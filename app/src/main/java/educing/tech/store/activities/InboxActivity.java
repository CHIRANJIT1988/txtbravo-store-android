package educing.tech.store.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;

import java.util.List;

import educing.tech.store.R;
import educing.tech.store.adapter.InboxRecyclerAdapter;
import educing.tech.store.model.Order;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static educing.tech.store.CommonUtilities.EXTRA_MESSAGE;


public class InboxActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{

    private RecyclerView recyclerView;
    private InboxRecyclerAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    private Context context = null;

    private List<Order> list;

    private boolean first_load = true;

    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int pageCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setTitle("All Orders");
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.quickreturn_list);


        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        context = InboxActivity.this;


        // Instantiate NotificationManager Class
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Cancel notification
        mNotificationManager.cancel(2);

        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        fetchOrders();
    }


    private void fetchOrders()
    {

        loading = true;
        pageCount = 1;

        list = new SQLiteDatabaseHelper(getApplicationContext()).getAllOrders(0, 20);

        adapter = new InboxRecyclerAdapter(this, list);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new InboxRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ORDER", list.get(position));
                startActivity(intent);
            }
        });


        swipeDelete();
        loadMoreOnScroll();
        first_load = false;
    }


    @Override
    public void onResume()
    {

        for (Order order: new SQLiteDatabaseHelper(context).getAllOrders(0, pageCount * 20))
        {

            int index = Order.findIndex(list, order.order_no);

            if (index != -1)
            {
                list.get(index).setOrderStatus(order.order_status);
                list.get(index).setReadStatus(order.read_status);
            }
        }

        adapter.notifyDataSetChanged();

        super.onResume();
    }


    @Override
    public void onRefresh()
    {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                finish();
                return true;

            case R.id.action_delete:

                new SQLiteDatabaseHelper(context).deleteAllRow(SQLiteDatabaseHelper.TABLE_ORDERS);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void swipeDelete()
    {

        SwipeableRecyclerViewTouchListener swipeTouchListener =

                new SwipeableRecyclerViewTouchListener(recyclerView,

                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                            @Override
                            public boolean canSwipe(int position)
                            {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                if(!first_load)
                                {

                                    for (int position : reverseSortedPositions)
                                    {
                                        new SQLiteDatabaseHelper(getApplicationContext()).deleteOrder(list.get(position).order_no);
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                if(!first_load)
                                {

                                    for (int position : reverseSortedPositions)
                                    {
                                        new SQLiteDatabaseHelper(getApplicationContext()).deleteOrder(list.get(position).order_no);
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }


    private void loadMoreOnScroll()
    {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                loading = true;

                int totalRecord = new SQLiteDatabaseHelper(getApplicationContext()).dbRowCount(SQLiteDatabaseHelper.TABLE_ORDERS);
                int totalPage;

                if (totalRecord % 10 == 0) {
                    totalPage = (totalRecord / 20);
                } else {
                    totalPage = (totalRecord / 20) + 1;
                }

                if (pageCount < totalPage) {

                    if (dy > 0) //check for scroll down
                    {

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {

                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {

                                loading = false;
                                Log.v("onScrolled: ", "Last Item Wow ! " + pageCount);

                                List<Order> tempList = new SQLiteDatabaseHelper(getApplicationContext()).getAllOrders(pageCount * 20, 20);


                                for (Order order : tempList) {
                                    list.add(list.size(), order);
                                }

                                adapter.notifyDataSetChanged();

                                recyclerView.scrollToPosition(totalItemCount - 1);
                                pageCount++;
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy()
    {


        try
        {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        }

        catch (Exception e)
        {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }

        super.onDestroy();
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {


                String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);


                /**
                 * Take appropriate action on this message
                 * depending upon your app requirement
                 * For now i am just displaying it on the screen
                 * */


                // Showing received message
                // lblMessage.append(newMessage + "\n");

                JSONObject jsonObj = new JSONObject(newMessage);

                String message_type = jsonObj.getString("message_type");

                if(message_type.equals("order"))
                {

                    String order_no = jsonObj.getString("order_no");
                    int user_id = jsonObj.getInt("user_id");
                    double delivery_charge = jsonObj.getDouble("delivery_charge");

                    String customer_name = jsonObj.getString("customer_name");
                    String phone_no = jsonObj.getString("phone_no");
                    String landmark = jsonObj.getString("landmark");
                    String address = jsonObj.getString("address");
                    String city = jsonObj.getString("city");
                    String state = jsonObj.getString("state");
                    String country = jsonObj.getString("country");
                    String pincode = jsonObj.getString("pincode");


                    Order order = new Order(user_id, order_no, String.valueOf(System.currentTimeMillis()), delivery_charge, "RECEIVED");

                    order.setCustomerName(customer_name);
                    order.setPhoneNo(phone_no);
                    order.setLandmark(landmark);
                    order.setAddress(address);
                    order.setCity(city);
                    order.setState(state);
                    order.setCountry(country);
                    order.setPincode(pincode);

                    list.add(0, order);
                    adapter.notifyDataSetChanged();
                }


                // Waking up mobile if it is sleeping
                // WakeLocker.acquire(context);


                // Releasing wake lock
                // WakeLocker.release();
            }

            catch(Exception e)
            {

            }
        }
    };
}