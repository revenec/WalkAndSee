package mvf.mikevidev.walkandsee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public void doLogin(View view)
    {
        EditText user = findViewById(R.id.etUsername);
        EditText pass = findViewById(R.id.etPassword);
        final String strUser = user.getText().toString();
        final String strPass = pass.getText().toString();

        if(Utilities.isBlank(strUser) || Utilities.isBlank(strPass))
        {
            Utilities.toastMessage("User and password are required",getApplicationContext());
        }
        else
        {

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goToStartMenu();

    }

    public void goToStartMenu()
    {
        Intent intent = new Intent(getApplicationContext(),SearchPlacesActivity.class);
        startActivity(intent);
    }
}