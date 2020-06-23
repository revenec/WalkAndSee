package mvf.mikevidev.walkandsee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class PlacesActivity extends AppCompatActivity {

    public WalkAndSeePlaceAdapter arrAdapter;
    public RecyclerView rvPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        int radius = getIntent().getIntExtra("intRadius",0);
        setTitle("Places within "  + (radius < 1000 ? radius + " Mts" : (radius/1000) + " Kms"));
        rvPlaces = findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        arrAdapter = new WalkAndSeePlaceAdapter(LoadingPlacesActivity.lstMapLitViewAttributeToData);
        rvPlaces.setAdapter(arrAdapter);
        arrAdapter.notifyDataSetChanged();


    }

    public class WalkAndSeePlaceAdapter extends RecyclerView.Adapter<WalkAndSeePlaceAdapter.MyViewHolder> {
        private List<Map<String, Object>> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView tvNamePlace;
            public TextView tvAddressPlace;
            public ImageView ivImagePlace;
            public TextView tvDistance;
            public ImageButton ivSelected;

            public MyViewHolder(View v) {
                super(v);

                this.tvNamePlace = (TextView) v.findViewById(R.id.namePlace);
                this.tvAddressPlace = (TextView) v.findViewById(R.id.tvAddress);
                this.ivImagePlace = (ImageView) v.findViewById(R.id.ivPlace);
                this.tvDistance = (TextView) v.findViewById(R.id.tvDistance);
                this.ivSelected = (ImageButton) v.findViewById(R.id.ivSelect);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public WalkAndSeePlaceAdapter(List<Map<String, Object>> myDataset) {
            mDataset = myDataset;
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

            holder.tvNamePlace.setText((String) mDataset.get(position).get("namePlace"));
            holder.tvAddressPlace.setText((String) mDataset.get(position).get("address"));
            holder.tvDistance.setText((String) mDataset.get(position).get("distance"));
            holder.ivSelected.setImageResource(R.drawable.lognoletters1_logo);
            Bitmap photo = (Bitmap) mDataset.get(position).get("imagePlace");
            holder.ivImagePlace.setImageBitmap(photo);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }

}