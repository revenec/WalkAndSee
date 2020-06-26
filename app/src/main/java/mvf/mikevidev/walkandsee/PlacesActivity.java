package mvf.mikevidev.walkandsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlacesActivity extends AppCompatActivity {

    public WalkAndSeePlaceAdapter arrAdapter;
    public RecyclerView rvPlaces;
    public static ArrayList<WalkAndSeePlace> lstPlacesSelected;
    public BottomNavigationView bottomNavigation;
    public static String navBotTitle;
    public boolean isAllSelected = false;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.bottomnav_select_all:
                    selectAllItems(item);
                    break;
                case R.id.bottomnav_backtosearch:
                    moveToActivity(false);
                    break;
                case R.id.bottomnav_createroute:
                    moveToActivity(true);
                    break;
            }
            return false;
        }
    };
    //Methods from menu
    public void selectAllItems(MenuItem item)
    {

        if(isAllSelected == false)
        {
            item.setTitle("Deselect All");
            for(WalkAndSeePlace wasp : LoadingPlacesActivity.lstWalkAndSeePlaces)
            {
                wasp.setSelected(true);
            }
            arrAdapter.notifyDataSetChanged();
            isAllSelected = true;
        }
        else
        {
            item.setTitle("Select All");
            for(WalkAndSeePlace wasp : LoadingPlacesActivity.lstWalkAndSeePlaces)
            {
                wasp.setSelected(false);
                arrAdapter.notifyDataSetChanged();
            }

            isAllSelected = false;
        }


    }
    //Method to move to activities
    public void moveToActivity(boolean blnMoveToCreateRoute)
    {
        Intent intent;
        if(blnMoveToCreateRoute == false)
        {
               intent = new Intent(getApplicationContext(),SearchPlacesActivity.class);
               startActivity(intent);
               finish();
        }
        else
        {
            if(isAnyItemSelected() == false)
            {
                Utilities.toastMessage("You need to select one place at least",getApplicationContext());
            }
            else
                {
                lstPlacesSelected = new ArrayList<>();
                intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
                for (WalkAndSeePlace wasp : LoadingPlacesActivity.lstWalkAndSeePlaces) {
                    if (wasp.isSelected() == true) {
                        lstPlacesSelected.add(wasp);
                    }
                }
                startActivity(intent);
                finish();
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        int radius = getIntent().getIntExtra("intRadius",0);
        getSupportActionBar().hide();
        setTitle("Places within "  + (radius < 1000 ? radius + " Mts" : (radius/1000) + " Kms"));
        rvPlaces = findViewById(R.id.rvPlaces);
        bottomNavigation = findViewById(R.id.bottomNavigationBar);
        navBotTitle = "Select All";
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        arrAdapter = new WalkAndSeePlaceAdapter();
        rvPlaces.setAdapter(arrAdapter);
        arrAdapter.notifyDataSetChanged();

    }

    public class WalkAndSeePlaceAdapter extends RecyclerView.Adapter<WalkAndSeePlaceAdapter.MyViewHolder> {
        private List<WalkAndSeePlace> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView tvNamePlace;
            public TextView tvAddressPlace;
            public ImageView ivImagePlace;
            public TextView tvDistance;
            public CheckBox ivSelected;

            public MyViewHolder(View v) {
                super(v);

                this.tvNamePlace = (TextView) v.findViewById(R.id.namePlace);
                this.tvAddressPlace = (TextView) v.findViewById(R.id.tvAddress);
                this.ivImagePlace = (ImageView) v.findViewById(R.id.ivPlace);
                this.tvDistance = (TextView) v.findViewById(R.id.tvDistance);
                this.ivSelected = (CheckBox) v.findViewById(R.id.cbSelect);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public WalkAndSeePlaceAdapter() {
            mDataset = LoadingPlacesActivity.lstWalkAndSeePlaces;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public WalkAndSeePlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
        {
            // create a new view
            View container = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.places_view, parent, false);

            MyViewHolder vh = new MyViewHolder(container);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(WalkAndSeePlaceAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.tvNamePlace.setText(mDataset.get(position).getPlaceName());
            holder.tvAddressPlace.setText(mDataset.get(position).getPlaceAddress());
            holder.tvDistance.setText(mDataset.get(position).getPlaceDistance());
            holder.ivSelected.setChecked(false);
            if(isAllSelected)
            {
                holder.ivSelected.setChecked(true);
            }
            holder.ivSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    Log.i("INSIDE_CHECK","Inside check");
                    WalkAndSeePlace was = mDataset.get(position);
                    was.setSelected(isChecked);
                    if(was.isSelected() == true)
                    {
                        buttonView.setButtonDrawable(R.drawable.placeselectedlogo);
                    }
                    else
                    {
                        buttonView.setButtonDrawable(R.drawable.placeunselectedlogo);
                    }

                }
            });
            Bitmap photo = mDataset.get(position).getPlacePhoto();
            if(photo == null)
            {
                holder.ivImagePlace.setImageResource(R.drawable.empty_house);
            }
            else
            {
                Log.i("PHOTO",photo.toString());
                photo = photo.copy(Bitmap.Config.RGBA_F16, true);
                //photo.setWidth(50);
                //photo.setHeight(50);
                photo = photo.copy(Bitmap.Config.RGBA_F16, false);
                holder.ivImagePlace.setImageBitmap(photo);
            }


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

    public boolean isAnyItemSelected()
    {
        for(WalkAndSeePlace wasp : LoadingPlacesActivity.lstWalkAndSeePlaces)
        {
            Log.i("Item_sel","Item select " + wasp.isSelected());
            if(wasp.isSelected() == true)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingPlacesActivity.lstWalkAndSeePlaces.clear();
    }
}