package educing.tech.store.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import educing.tech.store.R;
import educing.tech.store.adapter.ProductRecyclerAdapter;
import educing.tech.store.alert.CustomAlertDialog;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.receive.ReceiveProduct;
import educing.tech.store.mysql.db.receive.ReceiveProductCategories;
import educing.tech.store.mysql.db.receive.ReceiveProductSubCategories;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;


public class ProductFragment extends Fragment implements OnTaskCompleted
{

    private RecyclerView recyclerView;
    private ProductRecyclerAdapter simpleRecyclerAdapter;
    private Context context;
    private ProgressDialog pDialog;
    private List<Product> productList;


    public ProductFragment()
    {

    }


    /*public ProductFragment(Context context)
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

        View rootView = inflater.inflate(R.layout.product_fragment, container, false);
        context = getActivity();
        pDialog = new ProgressDialog(context);
        setHasOptionsMenu(true);

        findViewById(rootView);

        displayAllProduct();

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
    }


    @Override
    public void onResume()
    {

        for (Product product: new SQLiteDatabaseHelper(context).getAllProduct())
        {

            int index = Product.findIndex(productList, product.product_code);

            if (index != -1)
            {

                productList.get(index).setCategoryId(product.category_id);
                productList.get(index).setSubCategoryId(product.sub_category_id);
                productList.get(index).setProductName(product.product_name);
                productList.get(index).setProductDescription(product.description);
                productList.get(index).setProductThumbnail(product.product_thumbnail);
                productList.get(index).setPrice(product.price);
                productList.get(index).setDiscountPrice(product.discount_price);
                productList.get(index).setWeight(product.weight);
                productList.get(index).setUnit(product.unit);
                productList.get(index).setStatus(product.status);
            }

            else
            {
                productList.add(product);
            }
        }

        simpleRecyclerAdapter.notifyDataSetChanged();
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_product, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.action_add_product:

                if(new SQLiteDatabaseHelper(getActivity()).countActiveCategory() != 0)
                {
                    startActivity(new Intent(getActivity(), ProductSpecificationActivity.class));
                }

                else
                {
                    startActivity(new Intent(getActivity(), StoreConfigurationActivity.class));
                }

                break;

            case R.id.action_sync_category:

                if(new InternetConnectionDetector(context).isConnected())
                {
                    initProgressDialog("Syncing Product Category ...");
                    new ReceiveProductCategories(context, this).execute();
                }

                else
                {
                    Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.action_sync_sub_category:

                if(new InternetConnectionDetector(context).isConnected())
                {
                    initProgressDialog("Syncing Product Sub Category ...");
                    new ReceiveProductSubCategories(context, this, 1).execute();
                }

                else
                {
                    Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.action_sync_product:

                if(new InternetConnectionDetector(context).isConnected())
                {
                    initProgressDialog("Syncing Product ...");
                    new ReceiveProduct(context, this).retrieve(getStoreDetails().store_id);
                }

                else
                {
                    Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initProgressDialog(String message)
    {

        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void displayAllProduct()
    {

        productList = new SQLiteDatabaseHelper(context).getAllProduct();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        simpleRecyclerAdapter = new ProductRecyclerAdapter(productList, context);
        recyclerView.setAdapter(simpleRecyclerAdapter);

        simpleRecyclerAdapter.SetOnItemClickListener(new ProductRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

                Intent intent = new Intent(getActivity(), ProductSpecificationActivity.class);
                intent.putExtra("PRODUCT", productList.get(position));
                intent.putExtra("MODIFY", true);
                startActivity(intent);
            }
        });

        simpleRecyclerAdapter.notifyDataSetChanged();
    }


    private Store getStoreDetails()
    {

        SessionManager session = new SessionManager(context);

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


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if(pDialog.isShowing())
            {
                pDialog.dismiss();
            }

            if(flag && (code == 200 || code == 400))
            {
                displayAllProduct();
                new CustomAlertDialog(context, this).showOKDialog("Synchronized", "Synchronization Successful", "success");
                return;
            }

            if(!flag && code == 500)
            {
                new CustomAlertDialog(context, this).showOKDialog("Fail", "Filed to Synchronize", "success");
            }
        }

        catch (Exception e)
        {

        }
    }
}