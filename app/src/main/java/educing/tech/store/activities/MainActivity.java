package educing.tech.store.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import educing.tech.store.R;
import educing.tech.store.alert.CustomAlertDialog;
import educing.tech.store.helper.GenerateUniqueId;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.ChatMessage;
import educing.tech.store.model.Order;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.SyncChatMessage;
import educing.tech.store.mysql.db.send.SyncOrderStatus;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnTaskCompleted
{

    private Order order;
    private SQLiteDatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new SQLiteDatabaseHelper(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Order Summery");


        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        // Fixes bug for disappearing fragment content
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);


        helper.setAsRead(order.order_no);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }


            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setupViewPager(ViewPager viewPager)
    {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        this.order = (Order) getIntent().getSerializableExtra("ORDER");

        adapter.addFrag(new DeliveryAddressFragment(getApplicationContext(), order, ContextCompat.getColor(getApplicationContext(), R.color.home_background)), "DELIVERY DETAILS");

        adapter.addFrag(new OrderFragment(getApplicationContext(), this, order,  ContextCompat.getColor(getApplicationContext(), R.color.home_background)), "ORDER DETAILS");

        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
                break;
            }

            case R.id.action_cancel:
            {

                new CustomAlertDialog(this, MainActivity.this).showConfirmationDialog("Are you sure want to cancel this order ?", "confirmation");
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_order_details, menu);
        return super.onCreateOptionsMenu(menu);
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
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


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        if(flag && code == 200)
        {
            updateOrderStatus("CANCELLED");
            composeChatMessage("Sorry!! Your order (ORDER NO: " + order.order_no  + ") is cancelled by us. For any enquiry feel free to contact with us. Thanks");
            Toast.makeText(getApplicationContext(), "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOrderStatus(String status)
    {

        order.setOrderStatus(status);

        helper.updateOrder(order);
        new SyncOrderStatus(this).execute();
    }

    private void composeChatMessage(String chat_message)
    {
        ChatMessage chat = new ChatMessage(GenerateUniqueId.generateMessageId(getStoreDetails().getMobileNo()), String.valueOf(order.getUserID()), chat_message, "", String.valueOf(System.currentTimeMillis()), 0, 1);

        helper.insertChatUser(new ChatMessage(String.valueOf(order.getUserID()), order.getCustomerName()));
        helper.insertChatMessage(chat, 0);
        new SyncChatMessage(getApplicationContext(), this).execute();
    }


    private Store getStoreDetails()
    {

        SessionManager session = new SessionManager(this);

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
}