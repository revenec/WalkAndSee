package mvf.mikevidev.walkandsee;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class SearchPlacesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public SeekBar sbRadious;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location locationUser;
    public CheckBox cbShowAll;
    public CheckBox cbShowRestaurants;
    public CheckBox cbShowParks;
    public CheckBox cbShowMuseums;
    public CheckBox cbShowNightClubs;
    public CheckBox cbShowBars;

    public static final String MUSEUM_TYPE = "museum";
    public static final String PARK_TYPE = "park";
    public static final String RESTAURANT_TYPE = "restaurant";
    public static final String NIGHT_CLUB_TYPE = "night_club";
    public static final String BAR_TYPE = "bar";
    public static final int MAX_RADIUS = 5000;
    public static final int MIN_RADIUS = 100;
    public static final int INCREASE_RADIUS = 50;
    public static final float MIN_ZOOM = 11.8f;
    public static float MAX_ZOOM = 17.45f;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                // Add a marker in Sydney and move the camera
                sbRadious.setProgress(MIN_RADIUS);
                increaseAndDecreaseZoom(sbRadious.getProgress(),true);

            }
            else
            {
                Utilities.toastMessage("There is not signal. check your internet connection or your GPS and try again",getApplicationContext());
            }
        }
    }

    public void goFindPlaces(View view)
    {
        //Variable to hold the parameters
        ArrayList<String> params = new ArrayList<>();

        boolean isOptionSelected = false;
        if(cbShowAll.isChecked())
        {
            params.add(MUSEUM_TYPE);
            params.add(PARK_TYPE);
            params.add(RESTAURANT_TYPE);
            params.add(NIGHT_CLUB_TYPE);
            params.add(BAR_TYPE);
            isOptionSelected = true;
        }
        else
        {
            if(cbShowRestaurants.isChecked())
            {
                params.add(RESTAURANT_TYPE);
                isOptionSelected = true;
            }
            if(cbShowParks.isChecked())
            {
                params.add(PARK_TYPE);
                isOptionSelected = true;
            }
            if(cbShowMuseums.isChecked())
            {
                params.add(MUSEUM_TYPE);
                isOptionSelected = true;
            }
            if(cbShowNightClubs.isChecked())
            {
                params.add(NIGHT_CLUB_TYPE);
                isOptionSelected = true;
            }
            if(cbShowBars.isChecked())
            {
                params.add(BAR_TYPE);
                isOptionSelected = true;
            }
        }

        if(isOptionSelected == false)
        {
            Utilities.toastMessage("We need to know what you want to find, please select an option :)",getApplicationContext());
        }
        else
        {
            //Open Places Activity passing per parameter the radius and the options selected if "Show All is not selected"
            Intent intent = new Intent(getApplicationContext(),LoadingPlacesActivity.class);
            Log.i("PROGRESS","Progress: " + sbRadious.getProgress());
            intent.putExtra("intRadius",sbRadious.getProgress());
            intent.putExtra("placesType",params);
            startActivity(intent);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);
        //Get the value in the checkboxes
        cbShowAll = findViewById(R.id.allOpt);
        cbShowRestaurants = findViewById(R.id.restaurantsOpt);
        cbShowParks = findViewById(R.id.parksOpt);
        cbShowMuseums = findViewById(R.id.museumsOpt);
        cbShowNightClubs = findViewById(R.id.nightClubsOpt);
        cbShowBars = findViewById(R.id.barsOpt);
        screenAdjustments();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                locationUser = location;
                sbRadious.setProgress(MIN_RADIUS);
                increaseAndDecreaseZoom(sbRadious.getProgress(),true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Utilities.toastMessage("Check your internet connection, something is not working :(", getApplicationContext());
            }
        };
    }

    private void screenAdjustments()
    {
        //Get the max zoom based on the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point(); display. getSize(size);
        Log.i("screenAdjustments", "size" + size.x);
        switch(size.x)
        {
            case 1440:
                MAX_ZOOM = 17.8f;
                break;
            case 480:
                MAX_ZOOM = 17.3f;
                cbShowAll.setTextSize(12);
                cbShowRestaurants.setTextSize(12);
                cbShowParks.setTextSize(12);
                cbShowMuseums.setTextSize(12);
                cbShowNightClubs.setTextSize(12);
                cbShowBars.setTextSize(12);
                break;
            default:
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        sbRadious = findViewById(R.id.sbDistance);
        sbRadious.setMax(MAX_RADIUS);
        sbRadious.setMin(MIN_RADIUS);
        sbRadious.setProgress(INCREASE_RADIUS);
        sbRadious.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                //Get the progress and make zoom +/- based if the progress increase or decrease
                increaseAndDecreaseZoom(progress,false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                // Add a marker in Sydney and move the camera
                sbRadious.setProgress(MIN_RADIUS);
                increaseAndDecreaseZoom(sbRadious.getProgress(),true);

            }
            else
            {
                Utilities.toastMessage("There is not signal. check your internet connection or your GPS and try again",getApplicationContext());
            }
        }

    }
    //increase the zoom and the circle based on the distance
    public void increaseAndDecreaseZoom(int barProgress, boolean blnUseDynamic)
    {
        mMap.clear();
        LatLng currentLocation = new LatLng(locationUser.getLatitude(), locationUser.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.lognoletters1_logo)).anchor(0.5f,0.5f));
        if(blnUseDynamic)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,MAX_ZOOM));
        }
        else
        {
            //The zoom will be between 20 (max zoom allowed in Google maps) and the minimun which adjust to the screen
            /*float flZoomThreshold = MAX_ZOOM - MIN_ZOOM;
            float percentatgeProgress = (barProgress * 100) / (MAX_RADIUS - MIN_RADIUS);
            float substract = flZoomThreshold * (percentatgeProgress / 100);
            //Calculate the percentage to discount to the max zoom based on the percentage we increase the radius and considering the threshold
            float flzoom = MAX_ZOOM - (substract * substract);

            if(flzoom < MIN_ZOOM || barProgress == MAX_RADIUS)
            {
                flzoom = MIN_ZOOM;
            }
            if(flzoom > MAX_ZOOM || barProgress == MIN_RADIUS)
            {
                flzoom = MAX_ZOOM;
            }*/

            float flzoom = 0f;
            if(barProgress == MIN_RADIUS)
            {
                flzoom = MAX_ZOOM;
            }
            else if(barProgress > MIN_RADIUS && barProgress <= 150)
            {
                flzoom = MAX_ZOOM - 0.5f;
            }
            else if(barProgress > 150 && barProgress <= 500)
            {
                flzoom = MAX_ZOOM - 2.2f;
            }
            else if(barProgress > 500 && barProgress <= 700)
            {
                flzoom = MAX_ZOOM - 2.8f;
            }
            else if(barProgress > 700 && barProgress <= 1200)
            {
                flzoom = MAX_ZOOM - 3.5f;
            }
            else if(barProgress > 1200 && barProgress < 4900)
            {
                //The zoom will be between 20 (max zoom allowed in Google maps) and the minimun which adjust to the screen
                float flZoomThreshold = (MAX_ZOOM - 3.5f) - MIN_ZOOM;
                float percentatgeProgress = (barProgress * 100) / (MAX_RADIUS - MIN_RADIUS);
                float substract = flZoomThreshold * (percentatgeProgress / 100);
                //Calculate the percentage to discount to the max zoom based on the percentage we increase the radius and considering the threshold
                if(barProgress > 2500 && barProgress < 4500)
                {
                    substract += 0.2f;
                }
                flzoom = (MAX_ZOOM - 3.5f) - substract;
            }
            else
            {
                flzoom = MIN_ZOOM;
            }
            //Log.i("increaseAndDecreaseZoom","substract: " + substract);
            Log.i("increaseAndDecreaseZoom","Progress: " + barProgress);
            Log.i("increaseAndDecreaseZoom","flzoom: " + flzoom);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,flzoom));
        }

        mMap.addCircle(new CircleOptions()
                .center(currentLocation)
                .radius(barProgress)
                .strokeWidth(5)
                .strokeColor(Color.parseColor(Utilities.basicColorApp)));
    }

}