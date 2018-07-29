package educing.tech.store.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import java.util.HashMap;

import educing.tech.store.R;
import educing.tech.store.helper.Helper;
import educing.tech.store.model.DeliveryDetails;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.SyncDeliveryDetails;
import educing.tech.store.mysql.db.send.SyncStoreProfile;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;


public class ProfileFragment extends Fragment implements View.OnClickListener
{

    private Context context;
    private SessionManager session;
    private SQLiteDatabaseHelper helper;
    private Store store;
    private DeliveryDetails delivery_details = new DeliveryDetails();

    private Button btnSave;
    private EditText editStoreName, editOwnerName, editAlternateMobileNo, editEmail, editAmount, editDeliveryCharge;
    private RadioButton radioHomeDeliveryAvailable, radioHomeDeliveryNotAvailable, radioFreeHomeDelivery, radioServiceBased, radioProductBased;
    private LinearLayout layout_delivery, layout_delivery_option;
    private RelativeLayout relativeLayout;


    public ProfileFragment()
    {

    }


    /*public ProfileFragment(Context context)
    {
        this.context = context;
    }*/


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fargment_profile, container, false);
        context = getActivity();
        session = new SessionManager(context);

        findViewById(rootView);
        btnSave.setOnClickListener(this);

        this.store = getStoreDetails();
        this.helper = new SQLiteDatabaseHelper(context);


        if(helper.dbRowCount(SQLiteDatabaseHelper.TABLE_STORE_PROFILE) != 0)
        {

            Store store = helper.getStoreProfile();

            editStoreName.setText(Helper.toCamelCase(store.name));
            editOwnerName.setText(Helper.toCamelCase(store.owner_name));
            editAlternateMobileNo.setText(store.alternate_mobile_no);
            editEmail.setText(store.email);
        }

        else
        {
            editStoreName.setText(store.name);
        }


        if(helper.dbRowCount(SQLiteDatabaseHelper.TABLE_DELIVERY_DETAILS) != 0)
        {

            delivery_details = helper.getDeliveryDetails();

            if(delivery_details.delivery_status == 2)
            {
                radioServiceBased.setChecked(true);
                layout_delivery_option.setVisibility(View.GONE);
            }

            else
            {

                radioProductBased.setChecked(true);
                layout_delivery_option.setVisibility(View.VISIBLE);

                if(delivery_details.delivery_status == 1 && delivery_details.amount == 0 && delivery_details.delivery_charge == 0)
                {
                    radioFreeHomeDelivery.setChecked(true);
                    layout_delivery.setVisibility(View.GONE);
                }

                else if(delivery_details.delivery_status == 1 && delivery_details.amount != 0 && delivery_details.delivery_charge != 0)
                {
                    radioHomeDeliveryAvailable.setChecked(true);
                    layout_delivery.setVisibility(View.VISIBLE);
                    editAmount.setText(String.valueOf(delivery_details.amount));
                    editDeliveryCharge.setText(String.valueOf(delivery_details.delivery_charge));
                }

                else
                {
                    radioHomeDeliveryNotAvailable.setChecked(true);
                    layout_delivery.setVisibility(View.GONE);
                }

            }
        }


        radioProductBased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    layout_delivery_option.setVisibility(View.VISIBLE);
                    layout_delivery.setVisibility(View.GONE);
                } else {
                    layout_delivery_option.setVisibility(View.GONE);
                    layout_delivery.setVisibility(View.GONE);
                }
            }
        });

        radioServiceBased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked())
                {

                    layout_delivery_option.setVisibility(View.GONE);

                    radioHomeDeliveryAvailable.setChecked(false);
                    radioHomeDeliveryNotAvailable.setChecked(false);
                    radioFreeHomeDelivery.setChecked(false);

                    editAmount.setText("");
                    editDeliveryCharge.setText("");
                }

                else
                {
                    layout_delivery_option.setVisibility(View.VISIBLE);
                }
            }
        });


        radioHomeDeliveryAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    layout_delivery.setVisibility(View.VISIBLE);
                } else {
                    layout_delivery.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    private void findViewById(View rootView)
    {

        btnSave = (Button) rootView.findViewById(R.id.btnSave);

        editOwnerName = (EditText) rootView.findViewById(R.id.editOwnerName);
        editAlternateMobileNo = (EditText) rootView.findViewById(R.id.editAlternatePhoneNumber);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        editStoreName = (EditText) rootView.findViewById(R.id.editStoreName);

        editAmount = (EditText) rootView.findViewById(R.id.editAmount);
        editDeliveryCharge = (EditText) rootView.findViewById(R.id.editDeliveryCharge);

        radioHomeDeliveryAvailable = (RadioButton) rootView.findViewById(R.id.radioHomeDeliveryAvailable);
        radioHomeDeliveryNotAvailable = (RadioButton) rootView.findViewById(R.id.radioHomeDeliveryNotAvailable);
        radioFreeHomeDelivery = (RadioButton) rootView.findViewById(R.id.radioFreeHomeDelivery);
        radioProductBased = (RadioButton) rootView.findViewById(R.id.radioProduct);
        radioServiceBased = (RadioButton) rootView.findViewById(R.id.radioService);

        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.layout_main);
        layout_delivery = (LinearLayout) rootView.findViewById(R.id.layout_delivery);
        layout_delivery_option = (LinearLayout) rootView.findViewById(R.id.layout_delivery_option);
    }


    public void onClick(View view)
    {

        if(validateForm())
        {

            Store store = initStoreObject();

            if(!helper.insertStoreProfile(store));
            {
                helper.updateStoreProfile(store);
                session.editProfile(store.name);
            }

            if(!helper.insertDeliveryDetails(delivery_details))
            {
                helper.updateDeliveryDetails(delivery_details);
            }


            new SyncStoreProfile(getActivity()).execute();
            new SyncDeliveryDetails(getActivity()).execute();

            redirectAddressFragment();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    public static boolean isValidEmail(CharSequence target)
    {

        if (target.toString().trim().length() == 0)
        {
            return true;
        }

        else
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    private boolean validateForm()
    {

        if(editStoreName.getText().toString().trim().length() < 3)
        {
            makeSnackbar("Store name should be at least 3 character long");
            return false;
        }

        if(editOwnerName.getText().toString().trim().length() < 3)
        {
            makeSnackbar("Owner name should be at least 3 character long");
            return false;
        }

        if(editAlternateMobileNo.getText().toString().trim().length() != 0 && editAlternateMobileNo.getText().toString().trim().length() < 10)
        {
            makeSnackbar("Invalid mobile number");
            return false;
        }

        if(!isValidEmail(editEmail.getText().toString()))
        {
            makeSnackbar("Invalid Email");
            return false;
        }

        if(!radioServiceBased.isChecked() && !radioProductBased.isChecked())
        {
            makeSnackbar("Select Business Type");
            return false;
        }

        if(radioProductBased.isChecked() && (!radioHomeDeliveryAvailable.isChecked() && !radioHomeDeliveryNotAvailable.isChecked() && !radioFreeHomeDelivery.isChecked()))
        {
            makeSnackbar("Select Delivery Option");
            return false;
        }

        if(radioHomeDeliveryAvailable.isChecked())
        {

            if(editAmount.getText().toString().trim().length() == 0)
            {
                makeSnackbar("Enter Minimum Delivery Amount");
                return false;
            }

            if(editDeliveryCharge.getText().toString().trim().length() == 0)
            {
                makeSnackbar("Enter Delivery Charge");
                return false;
            }
        }

        return  true;
    }


    private Store initStoreObject()
    {

        store.setStoreName(editStoreName.getText().toString());
        store.setOwnerName(editOwnerName.getText().toString());
        store.setAlternateMobileNo(editAlternateMobileNo.getText().toString());
        store.setEmail(editEmail.getText().toString());

        delivery_details.setStoreId(store.store_id);

        if(radioHomeDeliveryAvailable.isChecked())
        {
            delivery_details.setDeliveryStatus(1);
            delivery_details.setAmount(Integer.valueOf(editAmount.getText().toString()));
            delivery_details.setDeliveryCharge(Integer.valueOf(editDeliveryCharge.getText().toString()));
        }

        else if(radioHomeDeliveryNotAvailable.isChecked())
        {
            delivery_details.setDeliveryStatus(0);
            delivery_details.setAmount(0);
            delivery_details.setDeliveryCharge(0);
        }

        else if(radioFreeHomeDelivery.isChecked())
        {
            delivery_details.setDeliveryStatus(1);
            delivery_details.setAmount(0);
            delivery_details.setDeliveryCharge(0);
        }

        else if(radioServiceBased.isChecked())
        {
            delivery_details.setDeliveryStatus(2);
            delivery_details.setAmount(0);
            delivery_details.setDeliveryCharge(0);
        }

        return store;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
        snackbar.show();
    }


    private Store getStoreDetails()
    {

        Store storeObj = new Store();

        if (session.checkLogin())
        {

            HashMap<String, String> store = session.getStoreDetails();

            storeObj.setStoreId(Integer.parseInt(store.get(SessionManager.KEY_STORE_ID)));
            storeObj.setStoreName(store.get(SessionManager.KEY_STORE_NAME));
            storeObj.setMobileNo(store.get(SessionManager.KEY_MOBILE_NO));
            storeObj.setPassword(store.get(SessionManager.KEY_PASSWORD));
        }

        return storeObj;
    }


    private void redirectAddressFragment()
    {

        StoreConfigurationActivity.ib_profile.setBackgroundResource(R.drawable.ib_order_status_completed);
        StoreConfigurationActivity.ib_profile.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check_white_24dp));


        Fragment fragment = new AddressFragment(getActivity());
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }
}