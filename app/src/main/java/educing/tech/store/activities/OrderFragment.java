package educing.tech.store.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import educing.tech.store.R;
import educing.tech.store.adapter.OrderRecyclerAdapter;
import educing.tech.store.alert.CustomAlertDialog;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Order;
import educing.tech.store.mysql.db.send.FetchOrderDetails;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;


public class OrderFragment extends Fragment implements OnTaskCompleted
{

    private OrderRecyclerAdapter adapter;
    private Context context;
    private int color;
    private Order order;

    private ProgressBar pbLoading;
    private RecyclerView recyclerView;


    public OrderFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public OrderFragment(Context context, OnTaskCompleted listener, Order order, int color)
    {
        this.color = color;
        this.context = context;
        this.order = order;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
        frameLayout.setBackgroundColor(color);


        recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Order.orderList.clear();
        Order.orderList = new SQLiteDatabaseHelper(context).getOrderItems(order.order_no);


        if(Order.orderList.size() == 0)
        {

            if (!new InternetConnectionDetector(context).isConnected())
            {

                pbLoading.setVisibility(View.GONE);
                new CustomAlertDialog(context, this).showOKDialog("Network Error", "Internet Connection Failure", "network");
                return view;
            }

            new FetchOrderDetails(context, this).fetchOrder(order.order_no);
        }

        else
        {
            pbLoading.setVisibility(View.GONE);
        }


        adapter = new OrderRecyclerAdapter(context, Order.orderList, order.delivery_charge);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new OrderRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

            }
        });

        return view;
    }


    /** Called when the activity is about to become visible. */
    @Override
    public void onStart()
    {

        super.onStart();
        Log.d("Inside : ", "onCreate() event");
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



    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            pbLoading.setVisibility(View.GONE);

            if(flag && code == 200)
            {
                adapter.notifyDataSetChanged();
                return;
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        catch (Exception e)
        {

        }
    }
}