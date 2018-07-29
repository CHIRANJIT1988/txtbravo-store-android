package educing.tech.store.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import educing.tech.store.R;
import educing.tech.store.helper.GenerateUniqueId;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.mysql.db.send.ForgotPassword;
import educing.tech.store.mysql.db.send.ResetPassword;
import educing.tech.store.network.InternetConnectionDetector;


public class ForgotPasswordFragment extends Fragment implements View.OnClickListener, OnTaskCompleted
{

    private BroadcastReceiver mIntentReceiver;

    private Button btnSubmit, btnConfirmationCode;
    private EditText editPhone;
    private TextView tvStatus;
    private ProgressBar pBar;

    private RelativeLayout relative_main;

    private Context context = null;
    private static String phone_no, new_password;
    private static boolean is_changing_password = false;


    public ForgotPasswordFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        context = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);

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


    /** Called when another activity is taking focus. */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("Inside : ", "onPause() event");

        getActivity().unregisterReceiver(this.mIntentReceiver);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    private void findViewById(View rootView)
    {

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        btnConfirmationCode = (Button) rootView.findViewById(R.id.btnConfirmationCode);
        editPhone = (EditText) rootView.findViewById(R.id.editPhoneNumber);
        pBar = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        tvStatus = (TextView) rootView.findViewById(R.id.status);
        relative_main = (RelativeLayout) rootView.findViewById(R.id.relative_main);
    }


    private void addClickListener()
    {
        btnSubmit.setOnClickListener(this);
        btnConfirmationCode.setOnClickListener(this);
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {

        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
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

                    if(msg.contains("login"))
                    {


                        String otp = msg.substring(Math.max(0, msg.length() - 6));

                        if(otp.equals(new_password))
                        {

                            if(!is_changing_password)
                            {
                                tvStatus.setText(String.valueOf("Changing Password ..."));
                                new ResetPassword(context, ForgotPasswordFragment.this).resetPassword(phone_no, new_password);
                            }

                            is_changing_password = true;
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


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnSubmit:

                if(validateForm())
                {

                    if (!new InternetConnectionDetector(getActivity()).isConnected())
                    {
                        makeSnackbar("Internet Connection Fail");
                        return;
                    }

                    pBar.setVisibility(View.VISIBLE);
                    tvStatus.setText(String.valueOf("Waiting for OTP"));

                    btnConfirmationCode.setVisibility(View.GONE);

                    new_password = String.valueOf(GenerateUniqueId.getRandomNo(999999, 100000));
                    phone_no = editPhone.getText().toString();

                    new ForgotPassword(context, this).forgotPassword(phone_no, new_password);
                    is_changing_password = false;
                }

                break;

            case R.id.btnConfirmationCode:

                displayConfirmationCodeDialog();
                break;
        }
    }


    private boolean validateForm()
    {

        if(editPhone.getText().toString().trim().length() != 10)
        {

            makeSnackbar("Invalid Phone Number");
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

                    if (new_password.equals(edittext.getText().toString()))
                    {

                        if(!is_changing_password)
                        {
                            tvStatus.setText(String.valueOf("Changing Password ..."));
                            new ResetPassword(context, ForgotPasswordFragment.this).resetPassword(phone_no, new_password);
                        }

                        is_changing_password = true;
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


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if (flag)
            {

                btnConfirmationCode.setVisibility(View.VISIBLE);

                if(code == 200)
                {

                    Fragment fragment = new LoginFragment();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();

                    getActivity().setTitle("LOGIN");
                }
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
}