package educing.tech.store.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import educing.tech.store.R;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.ChangeStoreStatus;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.services.AlarmService;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.configuration.Configuration.PACKAGE_NAME_SELLER;
import static educing.tech.store.configuration.Configuration.PACKAGE_NAME_BUYER;

import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_ADDRESS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_PROFILE;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_DELIVERY_DETAILS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_CATEGORY;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.KEY_STATUS;
import static educing.tech.store.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static educing.tech.store.CommonUtilities.EXTRA_MESSAGE;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnTaskCompleted
{

    private ViewPager mViewPager;
    private SessionManager session;
    private TextView nav_user_name, nav_mobile_number;
    private SwitchCompat nav_switch;
    private Menu menu;
    private SQLiteDatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("txtBravo");

        this.session = new SessionManager(DashboardActivity.this); // Session Manager
        this.helper = new SQLiteDatabaseHelper(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        nav_user_name = (TextView) header.findViewById(R.id.nav_user_name);
        nav_mobile_number = (TextView) header.findViewById(R.id.nav_mobile_number);
        nav_switch = (SwitchCompat) header.findViewById(R.id.nav_switch);

        nav_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if(new InternetConnectionDetector(DashboardActivity.this).isConnected())
                {

                    session.is_online(isChecked);
                    nav_switch.setChecked(isChecked);

                    if(isChecked)
                    {
                        nav_switch.setText(String.valueOf("ONLINE"));
                        Toast.makeText(getApplicationContext(), "ONLINE", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        nav_switch.setText(String.valueOf("OFFLINE"));
                        Toast.makeText(getApplicationContext(), "OFFLINE", Toast.LENGTH_LONG).show();
                    }

                    new ChangeStoreStatus(getApplicationContext(), DashboardActivity.this).save(getStoreDetails());
                }
            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // Fixes bug for disappearing fragment content
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));


        Intent service = new Intent(getApplicationContext(), AlarmService.class);
        startService(service);
    }


    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mHandleMessageReceiver);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.nav_orders:

                displayView(0);
                break;

            case R.id.nav_configuration:

                displayView(1);
                break;

            case R.id.nav_change_password:

                displayView(2);
                break;

            case R.id.nav_logout:

                displayView(3);
                break;

            case R.id.nav_share_seller_app:

                displayView(4);
                break;

            case R.id.nav_share_buyer_app:

                displayView(7);
                break;

            case R.id.nav_rate:

                displayView(5);
                break;

            case R.id.nav_manage_product:

                displayView(6);
                break;

            case R.id.nav_faq:

                displayView(8);
                break;

            case R.id.nav_about_us:

                displayView(9);
                break;

            case R.id.nav_profile:

                displayView(10);
                break;

            case R.id.nav_notification:

                displayView(11);
                break;

            case R.id.nav_terms_and_conditions:

                displayView(12);
                break;

            case R.id.nav_promote:

                displayView(13);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void setupViewPager(ViewPager viewPager)
    {

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new ChatFragment(), "CHAT");
        adapter.addFrag(new ProductFragment(), "DASHBOARD");

        viewPager.setAdapter(adapter);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }


        public void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }


    private void displayView(int position)
    {

        switch (position)
        {

            case 0:

                startActivity(new Intent(DashboardActivity.this, InboxActivity.class));
                break;

            case 1:

                startActivity(new Intent(DashboardActivity.this, StoreConfigurationActivity.class));
                break;

            case 2:

                startActivity(new Intent(DashboardActivity.this, PasswordChangeActivity.class));
                break;

            case 3:

                session.logoutUser();
                startActivity(new Intent(DashboardActivity.this, RegisterActivity.class));
                finish();
                break;

            case 4:

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "I have been using txtBravo for some time and found it to be a handy app to find and connect with local businesses on the go via free chat. Download now https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_SELLER;
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "txtBravo Seller Android App");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, "Share Via"));
                break;

            case 5:

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME_SELLER)));
                break;

            case 6:

                if(helper.countActiveCategory() != 0)
                {
                    startActivity(new Intent(this, ProductSpecificationActivity.class));
                }

                else
                {
                    startActivity(new Intent(this, StoreConfigurationActivity.class));
                }

                break;

            case 7:

                Intent sharingIntent1 = new Intent(Intent.ACTION_SEND);
                sharingIntent1.setType("text/plain");
                String shareBody1 = "I have been using txtBravo for some time and found it to be a handy app to find and connect with local businesses on the go via free chat. Download now https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_BUYER;
                sharingIntent1.putExtra(Intent.EXTRA_SUBJECT, "txtBravo Android App");
                sharingIntent1.putExtra(Intent.EXTRA_TEXT, shareBody1);
                startActivity(Intent.createChooser(sharingIntent1, "Share Via"));
                break;

            case 8:

                startActivity(new Intent(this, FAQActivity.class));
                break;

            case 9:

                startActivity(new Intent(this, AboutUsActivity.class));
                break;

            case 10:

                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case 11:

                startActivity(new Intent(DashboardActivity.this, AdvertisementActivity.class));
                break;

            case 12:

                startActivity(new Intent(DashboardActivity.this, TermsAndConditionsActivity.class));
                break;

            case 13:

                startActivity(new Intent(DashboardActivity.this, PromoteActivity.class));
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        try
        {
            menu.clear();
        }

        catch (Exception e)
        {

        }

        finally
        {

            getMenuInflater().inflate(R.menu.menu_home, menu);

            this.menu = menu;

            MenuItem menuItemBidders = menu.findItem(R.id.action_advertisement);
            menuItemBidders.setIcon(buildCounterDrawable(new SQLiteDatabaseHelper(this).unreadMessageCount(), R.drawable.ic_bell_white_24dp));
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                finish();
                return true;

            case R.id.action_advertisement:

                displayView(11);
                break;
        }

        return super.onOptionsItemSelected(item);
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

            if(count_address == 0 || count_profile == 0 || delivery_details == 0 || count_category == 0)
            {
                startActivity(new Intent(DashboardActivity.this, StoreConfigurationActivity.class));
            }

            Store store = getStoreDetails();

            nav_user_name.setText(store.getStoreName().toUpperCase());
            nav_mobile_number.setText(store.getMobileNo());
            nav_switch.setChecked(store.getIsOnline());

            if(store.getIsOnline())
            {
                nav_switch.setText(String.valueOf("ONLINE"));
            }

            else
            {
                nav_switch.setText(String.valueOf("OFFLINE"));
            }

            onCreateOptionsMenu(menu);
        }

        catch (Exception e)
        {

        }
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
            storeObj.setIsOnline(Boolean.parseBoolean(store.get(SessionManager.IS_ONLINE)));
        }

        return storeObj;
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId)
    {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);


        if (count == 0)
        {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        }

        else if(count > 9)
        {
            count = 9;
        }


        TextView textView = (TextView) view.findViewById(R.id.count);
        textView.setText(String.valueOf(count));


        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());


        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            Store store = getStoreDetails();

            if(!flag)
            {
                session.is_online(!store.is_online);
                nav_switch.setChecked(!store.is_online);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception e)
        {

        }
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

            Log.v("gcm_message: ", "" + newMessage);

            if(newMessage == null)
            {
                return;
            }

            try
            {

                JSONObject jsonObj = new JSONObject(newMessage);


                if(jsonObj.getString("message_type").equalsIgnoreCase("advertisement"))
                {
                    onCreateOptionsMenu(menu);
                }

                // Waking up mobile if it is sleeping
                // WakeLocker.acquire(context);

                // Releasing wake lock
                // WakeLocker.release();
            }

            catch (Exception e)
            {

            }
        }
    };
}