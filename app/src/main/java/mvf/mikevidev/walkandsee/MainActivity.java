package mvf.mikevidev.walkandsee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
            //for now if the user has not been signed up, we will sign the user up automatically
            ParseUser.logInInBackground(strUser, strPass, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e)
                {
                    if(e != null)
                    {
                        ParseUser parseUser = new ParseUser();
                        parseUser.setUsername(strUser);
                        parseUser.setPassword(strPass);
                        parseUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e)
                            {
                                if(e != null)
                                {
                                    Utilities.toastMessage(e.getMessage(),getApplicationContext());
                                }
                                else
                                {
                                    goToStartMenu();
                                }
                            }
                        });
                    }
                    else
                    {
                        goToStartMenu();
                    }
                }
            });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ParseUser.getCurrentUser() != null)
        {
            goToStartMenu();
        }
        ParseUser.logOut();
    }

    public void goToStartMenu()
    {
        Intent intent = new Intent(getApplicationContext(),SearchPlacesActivity.class);
        startActivity(intent);
    }
}