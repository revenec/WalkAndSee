package mvf.mikevidev.walkandsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class PlacesActivity extends AppCompatActivity {

    public SimpleAdapter arrAdapter;
    public ListView lvPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        int radius = getIntent().getIntExtra("intRadius",0);
        setTitle("Places within "  + (radius < 1000 ? radius + " Mts" : (radius/1000) + " Kms"));
        lvPlaces = findViewById(R.id.rvPlaces);
        String[] from = {"imagePlace", "namePlace", "address"};
        int[] to = {R.id.imagePlace, R.id.namePlace, R.id.addressDistance};
        arrAdapter = new WalkAndSeePlaceAdapter(getApplicationContext(), LoadingPlacesActivity.lstMapLitViewAttributeToData, R.layout.places_view, from, to);
        lvPlaces.setAdapter(arrAdapter);
        arrAdapter.notifyDataSetChanged();
        lvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    //Simple Adapter customised methot to set all the data in the listview
    public class WalkAndSeePlaceAdapter extends SimpleAdapter {

        public WalkAndSeePlaceAdapter(Context context, List<? extends Map<String, ?>> data,int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.places_view,null);
            }

            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);

            ((TextView) convertView.findViewById(R.id.namePlace)).setText((String) data.get("namePlace"));
            Bitmap photo = (Bitmap) data.get("imagePlace");
            ((ImageView) convertView.findViewById(R.id.imagePlace)).setImageBitmap(photo);
            Log.i("DISTANCE","" + data.get("address"));
            ((TextView) convertView.findViewById(R.id.addressDistance)).setText((String) data.get("address"));
            return convertView;
        }

    }

}