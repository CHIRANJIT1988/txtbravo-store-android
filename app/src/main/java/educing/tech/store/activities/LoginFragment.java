package educing.tech.store.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import educing.tech.store.R;
import educing.tech.store.ServerUtilities;
import educing.tech.store.model.Store;
import educing.tech.store.network.InternetConnectionDetector;


import static educing.tech.store.CommonUtilities.SENDER_ID;
import static educing.tech.store.activities.RegisterActivity.is_registration;
import static educing.tech.store.activities.RegisterActivity.message;
import static educing.tech.store.activities.RegisterActivity.store;


public class LoginFragment extends Fragment implements View.OnClickListener
{

    private Button btnNewStore, btnForgotPassword, btnLogin;
    private EditText editPhone, editPassword;
    private TextView tvStatus;
    private ProgressBar pBar;

    private RelativeLayout relative_main;

    private Context context = null;


    // AsyncTask
    private AsyncTask<Void, Void, Void> mRegisterTask;


    public LoginFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        context = this.getActivity();
        is_registration = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        findViewById(rootView);
        addClickListener();

        hideKeyboard(rootView);

        return rootView;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    private void findViewById(View rootView)
    {

        btnNewStore = (Button) rootView.findViewById(R.id.btnNewStore);
        btnForgotPassword = (Button) rootView.findViewById(R.id.btnForgotPassword);
        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        editPhone = (EditText) rootView.findViewById(R.id.editPhoneNumber);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        pBar = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        tvStatus = (TextView) rootView.findViewById(R.id.status);
        relative_main = (RelativeLayout) rootView.findViewById(R.id.relative_main);
    }


    private void addClickListener()
    {
        btnForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnNewStore.setOnClickListener(this);
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


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnNewStore:

                Fragment fragment = new RegisterFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                getActivity().setTitle("REGISTER");

                break;

            case R.id.btnForgotPassword:

                Fragment fragment1 = new ForgotPasswordFragment();

                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

                fragmentTransaction1.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
                fragmentTransaction1.replace(R.id.container_body, fragment1);
                fragmentTransaction1.commit();

                getActivity().setTitle("FORGOT PASSWORD");

                break;

            case R.id.btnLogin:

                if(validateForm())
                {

                    if (!new InternetConnectionDetector(getActivity()).isConnected())
                    {
                        makeSnackbar("Internet Connection Fail");
                        return;
                    }

                    pBar.setVisibility(View.VISIBLE);
                    tvStatus.setText(String.valueOf("Logging ... "));

                    store = initStoreObject();
                    gcm_registration();
                }
                break;
        }
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


    private Store initStoreObject()
    {

        WifiManager m_wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        Store store = new Store();

        store.setMobileNo(editPhone.getText().toString());
        store.setPassword(editPassword.getText().toString());
        store.setDeviceId(String.valueOf(m_wm.getConnectionInfo().getMacAddress()));

        return store;
    }


    private boolean validateForm()
    {

        if(editPhone.getText().toString().trim().length() != 10)
        {

            makeSnackbar("Invalid Phone Number");
            return false;
        }

        if(editPassword.getText().toString().trim().length() == 0)
        {

            makeSnackbar("Enter Password");
            return false;
        }

        return  true;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(relative_main, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
        snackbar.show();
    }


    private void hideKeyboard(final View rootView)
    {

        editPhone.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editPhone.getText().toString().trim().length() == 10) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
            }
        });
    }
}
