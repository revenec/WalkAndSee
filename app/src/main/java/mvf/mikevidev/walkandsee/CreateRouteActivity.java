package mvf.mikevidev.walkandsee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateRouteActivity extends AppCompatActivity {

    public static ArrayList<WalkAndSeePlace> lstPlacesSorted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_places);
        TextView tcMessage = findViewById(R.id.tvMessage);
        tcMessage.setText("Creating route...");
        lstPlacesSorted = new ArrayList<>();
        //method to sort the places based on proximity to the user
        sortingPlaces();
        finish();
    }

    private void sortingPlaces()
    {
        /*LoadingPlacesActivity.myPlaceToStartRoute, PlacesActivity.lstPlacesSelected*/

    }
}