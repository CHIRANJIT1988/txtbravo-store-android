package educing.tech.store.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import educing.tech.store.R;
import educing.tech.store.session.SessionManager;


public class SplashScreenActivity extends Activity
{

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        SessionManager session = new SessionManager(getApplicationContext()); // Session Manager

        if(session.isLoggedIn())
        {
            startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
            finish();
        }

        else
        {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    // This method will be executed once the timer is over
                    // Start your app main activity
                    startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
                    // close this activity
                    finish();
                }

            }, SPLASH_TIME_OUT);
        }
    }


    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart()
    {

        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }


    /** Called when the activity has become visible. */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("Inside : ", "onResume() event");
    }


    /** Called when another activity is taking focus. */
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("Inside : ", "onPause() event");
    }


    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    @Override
    public void onBackPressed()
    {

    }
}