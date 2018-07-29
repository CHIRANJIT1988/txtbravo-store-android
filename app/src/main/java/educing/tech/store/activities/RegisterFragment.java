package educing.tech.store.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


import educing.tech.store.R;
import educing.tech.store.ServerUtilities;
import educing.tech.store.helper.GenerateUniqueId;
import educing.tech.store.model.Store;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.mysql.db.send.OTPVerification;
import educing.tech.store.network.InternetConnectionDetector;

import static educing.tech.store.CommonUtilities.SENDER_ID;
import static educing.tech.store.activities.RegisterActivity.is_registration;
import static educing.tech.store.activities.RegisterActivity.store;
import static educing.tech.store.activities.RegisterActivity.message;


public class RegisterFragment extends Fragment implements View.OnClickListener, OnTaskCompleted
{

    private BroadcastReceiver mIntentReceiver;

    private Button btnRegister, btnConfirmationCode;
    private EditText editStoreName, editMobileNo, editPassword, editConfirmPassword;
    private TextView tvStatus;
    private ProgressBar pBar;
    private LinearLayout relativeLayout;

    private static Context context = null;
    private static boolean is_gcm_registering = false;


    // AsyncTask
    private AsyncTask<Void, Void, Void> mRegisterTask;


    public RegisterFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        context = this.getActivity();
        is_registration = true;
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        findViewById(rootView);
        btnRegister.setOnClickListener(this);
        btnConfirmationCode.setOnClickListener(this);

        hideKeyboard(rootView);

        return rootView;
    }



    private void findViewById(View rootView)
    {

        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnConfirmationCode = (Button) rootView.findViewById(R.id.btnConfirmationCode);

        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editStoreName = (EditText) rootView.findViewById(R.id.editStoreName);
        editMobileNo = (EditText) rootView.findViewById(R.id.editPhoneNumber);
        pBar = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        tvStatus = (TextView) rootView.findViewById(R.id.status);
        relativeLayout = (LinearLayout) rootView.findViewById(R.id.linear_main);
    }



    /** Called when the activity is about to become visible. */
    @Override
    public void onStart() {

        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }



    /** Called when another activity is taking focus. */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("Inside : ", "onPause() event");

        getActivity().unregisterReceiver(this.mIntentReceiver);
    }


    /** Called when the activity is no longer visible. */
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {

        if (mRegisterTask != null)
        {
            mRegisterTask.cancel(true);
        }


        try
        {
            //context.unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(context);
        }

        catch (Exception e)
        {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }

        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.btnRegister:

                if(validateForm())
                {

                    if (!new InternetConnectionDetector(getActivity()).isConnected())
                    {
                        makeSnackbar("Internet connection fail");
                        return;
                    }

                    pBar.setVisibility(View.VISIBLE);
                    tvStatus.setText(String.valueOf("Waiting for OTP"));

                    store = initStoreObject();

                    btnConfirmationCode.setVisibility(View.GONE);
                    new OTPVerification(context, this).otpVerify(store);
                    is_gcm_registering = false;
                }

                break;

            case R.id.btnConfirmationCode:

                displayConfirmationCodeDialog();
                break;
        }
    }


    private boolean validateForm()
    {

        if(editStoreName.getText().toString().trim().length() < 3)
        {
            makeSnackbar("Store name should be at least 3 character long");
            return false;
        }

        if(editMobileNo.getText().toString().trim().length() < 10)
        {
            makeSnackbar("Invalid mobile number");
            return false;
        }

        if(editPassword.getText().toString().trim().length() < 6)
        {
            makeSnackbar("Password should be 6 to 15 character long");
            return false;
        }

        if(!editPassword.getText().toString().equals(editConfirmPassword.getText().toString()))
        {
            makeSnackbar("Password Confirmation Error");
            return false;
        }

        return  true;
    }


    private Store initStoreObject()
    {

        WifiManager m_wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        Store store = new Store();

        store.setStoreName(editStoreName.getText().toString());
        store.setMobileNo(editMobileNo.getText().toString());
        store.setPassword(editPassword.getText().toString());
        store.setConfirmationCode(String.valueOf(GenerateUniqueId.getRandomNo(999999, 100000)));
        store.setDeviceId(String.valueOf(m_wm.getConnectionInfo().getMacAddress()));

        return store;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
        snackbar.show();
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if (flag)
            {

                btnConfirmationCode.setVisibility(View.VISIBLE);
            }

            else
            {
                tvStatus.setText("");
                btnConfirmationCode.setVisibility(View.GONE);
                pBar.setVisibility(View.GONE);
                makeSnackbar(message);
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }


    private void hideKeyboard(final View rootView) {

        editMobileNo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editMobileNo.getText().toString().trim().length() == 10) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
            }
        });
    }



    @Override
    public void onResume()
    {

        super.onResume();

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");

        mIntentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)
            {

                try
                {

                    String msg = intent.getStringExtra("get_msg");

                    if(msg.contains("registration"))
                    {


                        String otp = msg.substring(Math.max(0, msg.length() - 6));

                        if(otp.equals(store.confirmation_code))
                        {

                            if(!is_gcm_registering)
                            {
                                tvStatus.setText(String.valueOf("Registering ..."));
                                gcm_registration();
                            }

                            is_gcm_registering = true;
                        }
                    }
                }

                catch (Exception e)
                {
                    Toast.makeText(context, "Registration Error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        getActivity().registerReceiver(mIntentReceiver, intentFilter);
    }


    private void gcm_registration()
    {

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(context);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(context);

        //context.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(context);

        // Check if regid already presents
        if (regId.equals(""))
        {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(context, SENDER_ID);
        }

        else
        {

            // Try to register again, but not in the UI thread.
            // It's also necessary to cancel the thread onDestroy(),
            // hence the use of AsyncTask instead of a raw thread.
            // final Context context = this;

            mRegisterTask = new AsyncTask<Void, Void, Void>()
            {

                @Override
                protected Void doInBackground(Void... params)
                {
                    // Register on our server
                    // On server creates a new user
                    ServerUtilities.register(context, store, regId);
                    return null;
                }


                @Override
                protected void onPostExecute(Void result)
                {

                    mRegisterTask = null;

                    makeSnackbar(message);
                    tvStatus.setText("");
                    pBar.setVisibility(View.GONE);
                }

            };

            mRegisterTask.execute(null, null, null);
        }
    }


    private void displayConfirmationCodeDialog()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final EditText edittext= new EditText(context);
        alert.setMessage("Enter OTP");
        alert.setCancelable(false);

        edittext.setHeight(50);
        edittext.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        //edittext.SetRawInputType(Android.Text.InputTypes.NumberFlagDecimal | Android.Text.InputTypes.ClassNumber);
        //edittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});


        alert.setView(edittext);


        alert.setPositiveButton("Verify", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {


                if (edittext.getText().toString().trim().length() == 6) {

                    if (store.getConfirmationCode().equals(edittext.getText().toString()))
                    {

                        if(!is_gcm_registering)
                        {
                            tvStatus.setText(String.valueOf("Registering ..."));
                            gcm_registration();
                        }

                        is_gcm_registering = true;
                    }

                    else
                    {
                        makeSnackbar("Verification Fail. Try Again");
                    }

                    dialog.dismiss();
                }
            }
        });


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });

        alert.show();
    }
}