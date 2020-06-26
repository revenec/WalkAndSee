package mvf.mikevidev.walkandsee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class CreateRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_places);
        TextView tcMessage = findViewById(R.id.tvMessage);
        tcMessage.setText("Creating route...");
    }
}