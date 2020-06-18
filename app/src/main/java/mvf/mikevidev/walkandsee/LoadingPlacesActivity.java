package mvf.mikevidev.walkandsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class LoadingPlacesActivity extends AppCompatActivity {

    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location locationUser;
    public static List<Map<String, Object>> lstMapLitViewAttributeToData;
    public Map<String, Float> mapPlaceIdToDistanceCurrentLocation;
    public String strSpecificPlace;
    public int intRadiusFromScreen;
    public ArrayList<String> strPlaceTypeFromScreen;
    public List<WalkAndSeePlace> lstWalkAndSeePlaces;
    public static Map<String,WalkAndSeePlace> mapPlaceIdToWalkAndSeePlace;
    public static WalkAndSeePlace walkAndSeePlace;
    public int totalResults;
    public TextView tvMessage;

    public class DownloadData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urls[0]).openStream()));
                String input;
                StringBuffer stringBuffer = new StringBuffer();
                while ((input = in.readLine()) != null) {
                    stringBuffer.append(input);
                }
                in.close();
                result = stringBuffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //after execution fill up the list view with the locations
            for (String placeId : getPlaceIds(result)) {
                Log.i("PLACE_ID_REQUEST: ",placeId);
                getPlace(placeId);
            }
            Log.i("Places: ", result);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3, locationListener);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_places);
        mapPlaceIdToDistanceCurrentLocation = new HashMap<>();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        intRadiusFromScreen = getIntent().getIntExtra("intRadius",0);
        strPlaceTypeFromScreen = (ArrayList<String>)getIntent().getSerializableExtra("placesType");
        strSpecificPlace = null;
        lstMapLitViewAttributeToData = new ArrayList<>();
        lstWalkAndSeePlaces = new ArrayList<>();
        tvMessage = findViewById(R.id.tvMessage);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Utilities.toastMessage("Check your internet connection, something is not working :(", getApplicationContext());
            }
        };
        initPlaceSearch();
    }

    public void initPlaceSearch() {
        //Check if the user grants permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                Build.VERSION.SDK_INT < 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            //if the user has already give permission, the process will jump the pop up
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3, locationListener);
            //Get the current location at the begin and add a list of the places
            locationUser = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationUser == null) {
                locationUser = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (locationUser != null) {
                getPlacesFromGoogleMaps(locationUser, intRadiusFromScreen, strPlaceTypeFromScreen, strSpecificPlace);
            }
            else
            {
                Utilities.toastMessage("There is not signal. check your internet connection or your GPS and try again",getApplicationContext());
            }
        }

    }
    private void getPlacesFromGoogleMaps(Location location, int radius, ArrayList<String> lstPlaceTypes, String specificPlace) {
        DownloadData download = new DownloadData();
        ArrayList<String> urlsArray = new ArrayList<>();

        try {

            for(int i = 0; i< lstPlaceTypes.size(); i++)
            {
                StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

                url.append("location=").append(location.getLatitude()).append(",").append(location.getLongitude());
                url.append("&radius=").append(radius);

                if (!Utilities.isBlank(lstPlaceTypes.get(i))) {
                    url.append("&type=").append(lstPlaceTypes.get(i));
                }

                if (!Utilities.isBlank(specificPlace)) {
                    url.append("&keyword=").append(specificPlace);
                }

                url.append("&key=" + Utilities.key);
                Log.i("url: ", url.toString());
                urlsArray.add(url.toString());
            }
            String[] urlsArrayToSend = new String[urlsArray.size()];
            for(int i = 0; i < urlsArrayToSend.length; i++)
            {
                urlsArrayToSend[i] = urlsArray.get(i);
            }
            download.execute(urlsArrayToSend).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void getPlace(String placeId) {

        Trace.beginSection("SessionGetPlaces");

        //Set the information we want to retrieve from google
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        //Init places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), Utilities.key);
        }
        //Create client places and request places
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Map<String, Object> mapData = new HashMap<>();
            Log.i("TAG", "Place found: " + place);

            if (place.getLatLng() != null)
            {
                //Get list of places to operate in the future
                String distanceFormatted = String.valueOf((Math.round((mapPlaceIdToDistanceCurrentLocation.get(place.getId()) / 1000)* 100.00) / 100.00));
                try
                {
                    Log.i("DISTANCE_STR",distanceFormatted);
                    Log.i("DISTANCE_STR_L",String.valueOf(distanceFormatted.split(".").length));
                    if(distanceFormatted.split("\\.").length == 1)
                    {
                        Log.i("DISTANCE_STR","inside option 1");
                        distanceFormatted += ".00";
                    }
                    else if(distanceFormatted.split("\\.").length == 2 && distanceFormatted.split("\\.")[1].length() < 2)
                    {
                        Log.i("DISTANCE_STR","inside option 2");
                        distanceFormatted += "0";
                    }
                }
                catch(Exception e)
                {
                    distanceFormatted = "No distance available";
                }
                mapData.put("distance_sort",mapPlaceIdToDistanceCurrentLocation.get(place.getId()));
                mapData.put("address", place.getAddress() + " \nDistance: " + distanceFormatted + " Km");
                Log.i("PLACE_ID_REQUEST_2: ",place.getId());
                walkAndSeePlace = new WalkAndSeePlace(place.getName(), place.getId(), place.getLatLng(), null, place.getAddress() + " \nDistance: " + (Math.round(mapPlaceIdToDistanceCurrentLocation.get(place.getId())* 100.00) / 100.00) + " Mls",mapPlaceIdToDistanceCurrentLocation.get(place.getId()));

                if (place.getPhotoMetadatas() != null && place.getPhotoMetadatas().get(0) != null) {
                    //Generate photo metadata
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(place.getPhotoMetadatas().get(0))
                            .setMaxWidth(100) // Optional.
                            .setMaxHeight(100) // Optional.
                            .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        Log.i("PHOTO_BITMAP_DOWNLOADED", "Photo: " + fetchPhotoResponse.getBitmap());

                        Bitmap photo = fetchPhotoResponse.getBitmap();
                        walkAndSeePlace.setPlacePhoto(photo);
                        mapData.put("imagePlace", photo);

                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("ERROR_PHOTO", "Photo not found: " + statusCode);
                            //Icons made by <a href="https://www.flaticon.com/authors/eucalyp" title="Eucalyp">Eucalyp</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
                            walkAndSeePlace.setPlacePhoto(BitmapFactory.decodeResource(getResources(), R.drawable.empty_house));
                            mapData.put("imagePlace", BitmapFactory.decodeResource(getResources(), R.drawable.empty_house));
                        }
                    });
                } else {
                    //Icons made by <a href="https://www.flaticon.com/authors/eucalyp" title="Eucalyp">Eucalyp</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
                    walkAndSeePlace.setPlacePhoto(BitmapFactory.decodeResource(getResources(), R.drawable.empty_house));
                    mapData.put("imagePlace", BitmapFactory.decodeResource(getResources(), R.drawable.empty_house));
                }

                Log.i("INFO_GOOGLE_GET", walkAndSeePlace.toString());
                lstWalkAndSeePlaces.add(walkAndSeePlace);

                String strPlaceName = walkAndSeePlace.getPlaceName();
                if (Utilities.isBlank(strPlaceName) || "null".equals(strPlaceName)) {
                    strPlaceName = "Place not found";
                }
                else if(strPlaceName.length() > 38)
                {
                    strPlaceName = strPlaceName.substring(0,38) + "...";
                }
                mapData.put("namePlace",strPlaceName);
                lstMapLitViewAttributeToData.add(mapData);

                //Create data to be showed in the list view
                Log.i("Places2: ", walkAndSeePlace.toString());

            }


        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.i("TAG", "Place not found: " + exception.getMessage());
            }
        }).addOnCompleteListener((task) -> {

            if(this.totalResults == lstWalkAndSeePlaces.size())
            {
                Log.i("TAG", "Inside adapter and notify change");
                //Sort results and return to view
                List<Map<String,Object>> lstTemp = new ArrayList<>();
                lstTemp.addAll(lstMapLitViewAttributeToData);
                lstMapLitViewAttributeToData.clear();
                List<Float> lstDistance = new ArrayList<>();
                for(Map<String,Object> record : lstTemp)
                {
                    lstDistance.add((Float)record.get("distance_sort"));
                }
                Collections.sort(lstDistance);
                Log.i("TAG", "Inside adapter and notify change: " + lstDistance.toString());
                for(Float dst : lstDistance)
                {
                    for(Map<String,Object> record : lstTemp)
                    {
                        if(dst == (Float)record.get("distance_sort"))
                        {
                            lstMapLitViewAttributeToData.add(record);
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(),PlacesActivity.class);
                startActivity(intent);
                finish();
                //arrAdapter.notifyDataSetChanged();
            }

        });
        Trace.endSection();
    }

    //Get place id and distance from current location, so we can sort places by proximity
    public ArrayList<String> getPlaceIds(String JSONString)
    {
        Log.i("TAG", "getPlaceIds ");
        ArrayList<String> lstPlaceIds = new ArrayList<>();
        Map<Float,Integer> mapDistanceToPositionList = new TreeMap<>();
        Map<Integer,String> mapPositionListToPlaceId = new HashMap<>();
        int pos = 0;
        lstWalkAndSeePlaces.clear();
        //arrAdapter.notifyDataSetChanged();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return lstPlaceIds;
        }
        Location locationEndPoint = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(locationEndPoint == null)
        {
            locationEndPoint =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray arrResults = jsonObject.getJSONArray("results");
            for(int i = 0; i < arrResults.length();i++)
            {
                JSONObject info = arrResults.getJSONObject(i).getJSONObject("geometry");
                JSONObject infoLoc = info.getJSONObject("location");
                double latitude = (double) infoLoc.get("lat");
                double longitude = (double) infoLoc.get("lng");
                locationEndPoint.setLongitude(longitude);
                locationEndPoint.setLatitude(latitude);
                Log.i("LOCATION_USER",locationUser.toString());
                Log.i("LOCATION_END_POINT",locationEndPoint.toString());
                mapDistanceToPositionList.put(locationUser.distanceTo(locationEndPoint),pos);
                mapPositionListToPlaceId.put(pos,arrResults.getJSONObject(i).getString("place_id"));
                pos++;
            }

            for(Float key : mapDistanceToPositionList.keySet())
            {
                lstPlaceIds.add(mapPositionListToPlaceId.get(mapDistanceToPositionList.get(key)));
                mapPlaceIdToDistanceCurrentLocation.put(mapPositionListToPlaceId.get(mapDistanceToPositionList.get(key)),key);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Get this value to find out when the process will finish, so we can order the places for distance in ascending order
        this.totalResults = lstPlaceIds.size();
        return lstPlaceIds;
    }
}