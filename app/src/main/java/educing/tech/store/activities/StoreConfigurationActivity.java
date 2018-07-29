package educing.tech.store.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import educing.tech.store.R;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_ADDRESS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_PROFILE;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_DELIVERY_DETAILS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_CATEGORY;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.KEY_STATUS;


public class StoreConfigurationActivity extends AppCompatActivity implements View.OnClickListener
{

    public static ImageButton ib_category, ib_profile, ib_address;
    private SQLiteDatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_configuration);

        ib_category = (ImageButton) findViewById(R.id.ib_category);
        ib_profile = (ImageButton) findViewById(R.id.ib_profile);
        ib_address = (ImageButton) findViewById(R.id.ib_address);

        ib_category.setOnClickListener(this);
        ib_profile.setOnClickListener(this);
        ib_address.setOnClickListener(this);

        helper = new SQLiteDatabaseHelper(this);
        redirectFragment(new ProductCategoryFragment());
    }


    @Override
    public void onResume()
    {

        super.onResume();

        try
        {

            int count_address = helper.dbRowCount(TABLE_STORE_ADDRESS);
            int count_profile = helper.dbRowCount(TABLE_STORE_PROFILE);
            int delivery_details = helper.dbRowCount(TABLE_DELIVERY_DETAILS);
            int count_category = helper.dbRowCount(TABLE_PRODUCT_CATEGORY, KEY_STATUS, "1");


            if(count_category != 0)
            {
                ib_category.setBackgroundResource(R.drawable.ib_order_status_completed);
                ib_category.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_white_24dp));
            }

            if(count_profile !=0 && delivery_details != 0)
            {
                ib_profile.setBackgroundResource(R.drawable.ib_order_status_completed);
                ib_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_white_24dp));
            }

            if(count_address != 0)
            {
                ib_address.setBackgroundResource(R.drawable.ib_order_status_completed);
                ib_address.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_white_24dp));
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.ib_category:

                redirectFragment(new ProductCategoryFragment());
                break;

            case R.id.ib_profile:

                if(helper.dbRowCount(TABLE_PRODUCT_CATEGORY, KEY_STATUS, "1") != 0)
                {
                    redirectFragment(new ProfileFragment());
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Category not configured", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.ib_address:

                if(helper.dbRowCount(TABLE_STORE_PROFILE) != 0)
                {
                    redirectFragment(new AddressFragment(this));
                }


                else
                {
                    Toast.makeText(getApplicationContext(), "Profile not configured", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void redirectFragment(Fragment fragment)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }
}