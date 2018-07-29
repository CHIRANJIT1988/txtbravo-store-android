package educing.tech.store.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import educing.tech.store.R;
import educing.tech.store.adapter.ProductCategoryRecyclerAdapter;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
import educing.tech.store.mysql.db.receive.ReceiveProductCategories;
import educing.tech.store.mysql.db.receive.ReceiveProductSubCategories;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_CATEGORY;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_SUB_CATEGORY;


public class ProductCategoryFragment extends Fragment implements OnTaskCompleted
{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductCategoryRecyclerAdapter simpleRecyclerAdapter;
    private Context context;
    private SQLiteDatabaseHelper helper;


    public ProductCategoryFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.product_fragment, container, false);
        context = getActivity();
        this.helper = new SQLiteDatabaseHelper(context);

        findViewById(rootView);


        if(helper.dbRowCount(TABLE_PRODUCT_CATEGORY) == 0)
        {

            if (new InternetConnectionDetector(context).isConnected())
            {
                progressBar.setVisibility(View.VISIBLE);
                new ReceiveProductCategories(context, this).execute();
            }
        }

        else
        {
            onTaskCompleted(true, 200, "success");
        }


        if(helper.dbRowCount(TABLE_PRODUCT_SUB_CATEGORY) == 0)
        {

            if (new InternetConnectionDetector(context).isConnected())
            {
                new ReceiveProductSubCategories(context, this, 1).execute();
            }
        }


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    private void findViewById(View rootView)
    {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dummyfrag_scrollableview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pbLoading);
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if (flag && code == 300)
            {

                StoreConfigurationActivity.ib_category.setBackgroundResource(R.drawable.ib_order_status_completed);
                StoreConfigurationActivity.ib_category.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check_white_24dp));

                Fragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
                return;
            }

            if (flag && code == 200)
            {

                if (simpleRecyclerAdapter == null)
                {

                    List<Product> productList = helper.getAllProductCategory();
                    simpleRecyclerAdapter = new ProductCategoryRecyclerAdapter(productList, context, this);
                    recyclerView.setAdapter(simpleRecyclerAdapter);
                    simpleRecyclerAdapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
            }
        }

        catch (Exception e)
        {

        }
    }
}