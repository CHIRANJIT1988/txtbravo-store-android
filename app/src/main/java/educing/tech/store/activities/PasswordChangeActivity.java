package educing.tech.store.activities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.ChangePassword;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.session.SessionManager;
import educing.tech.store.R;


public class PasswordChangeActivity extends AppCompatActivity implements OnClickListener, OnTaskCompleted
{

    private Button btnChangePassword;
    private EditText editNewPassword, editConfirmPassword;

    private ProgressBar pBar;

    private SessionManager session;

    private LinearLayout layout_main;

    private Context context = null;
    private String new_password;

    private int back_pressed = 0;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setTitle("Change Password");
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = PasswordChangeActivity.this;
        session = new SessionManager(context); // Session Manager

        findViewById();

        btnChangePassword.setOnClickListener(this);
        hideKeyboard();
    }


    /** Called when the activity is about to become visible. */
    @Override
    public void onStart()
    {

        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }



    /** Called when the activity has become visible. */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("Inside : ", "onResume() event");
    }


    /** Called when another activity is taking focus. */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("Inside : ", "onPause() event");
    }


    /** Called when the activity is no longer visible. */
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }


    //** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    private void findViewById()
    {

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);

        pBar = (ProgressBar) findViewById(R.id.pbLoading);

        layout_main = (LinearLayout) findViewById(R.id.layout_main);
    }


    public void onClick(View v)
    {

        if(validateForm())
        {

            if (!new InternetConnectionDetector(context).isConnected())
            {
                makeSnackbar("Internet Connection Fail");
                return;
            }

            pBar.setVisibility(View.VISIBLE);
            new_password = editNewPassword.getText().toString();
            new ChangePassword(context, this).changePassword(storeDetails(), new_password);
        }
    }


    private boolean validateForm()
    {

        if(editNewPassword.getText().toString().trim().length() < 6)
        {
            makeSnackbar("Password should be at least 6 character");
            return false;
        }

        if(!editConfirmPassword.getText().toString().equals(editNewPassword.getText().toString()))
        {
            makeSnackbar("Password Confirmation Error");
            return false;
        }

        return  true;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(layout_main, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
        snackbar.show();
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            pBar.setVisibility(View.GONE);

            if (code == 200)
            {

                session.changePassword(new_password);

                editNewPassword.getText().clear();
                editConfirmPassword.getText().clear();
            }

            makeSnackbar(message);

        }

        catch (Exception e)
        {

        }
    }


    private Store storeDetails()
    {

        // Check for logged in
        session.checkLogin();

        // Get user data from session
        HashMap<String, String> employee = session.getStoreDetails();

        Store storeObj = new Store();

        storeObj.setMobileNo(employee.get(SessionManager.KEY_MOBILE_NO));
        storeObj.setPassword(employee.get(SessionManager.KEY_PASSWORD));
        storeObj.setStoreId(Integer.parseInt(employee.get(SessionManager.KEY_STORE_ID)));

        // Return user
        return storeObj;
    }


    private void hideKeyboard()
    {

        editConfirmPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editConfirmPassword.getText().toString().equals(editNewPassword.getText().toString()))
                {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editConfirmPassword.getWindowToken(), 0);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {

        if(back_pressed == 0)
        {
            back_pressed++;
            Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
        }

        else
        {
            finish();
        }
    }
}