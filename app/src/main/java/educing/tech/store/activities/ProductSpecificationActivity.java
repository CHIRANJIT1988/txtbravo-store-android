package educing.tech.store.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import educing.tech.store.R;
import educing.tech.store.alert.CustomAlertDialog;
import educing.tech.store.helper.GenerateUniqueId;
import educing.tech.store.helper.Helper;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.SyncProduct;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.activities.UploadProductImageActivity.MEDIA_TYPE_IMAGE;
import static educing.tech.store.configuration.Configuration.MEDIA_DIRECTORY_NAME;


public class ProductSpecificationActivity extends AppCompatActivity implements OnTaskCompleted, View.OnClickListener, AdapterView.OnItemSelectedListener
{

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    private AppCompatSpinner spinnerProductCategory, spinnerProductSubCategory;
    private EditText editProductName, editProductDescription, editWeight, editUnit, editPrice, editDiscountPrice;
    private ImageView product_image;
    private CheckBox chkDiscount;
    private SwitchCompat switchDelete;

    private List<Product> productCategoryList = new ArrayList<>();
    private List<Product> productSubCategoryList = new ArrayList<>();

    private int selected_category_id = 0, selected_sub_category_id = 0;
    private String image_path = "", image_name = "";

    private DecimalFormat df = new DecimalFormat("0.00");
    private String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MEDIA_DIRECTORY_NAME + "/";

    private Product product;

    private SQLiteDatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_specification);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Add Product");
        findViewById();

        helper = new SQLiteDatabaseHelper(this);

        spinnerProductCategory.setOnItemSelectedListener(this);
        spinnerProductSubCategory.setOnItemSelectedListener(this);

        populateProductCategorySpinner();


        chkDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editDiscountPrice.setText(editPrice.getText().toString());
                } else {
                    editDiscountPrice.getText().clear();
                }
            }
        });


        if(getIntent().getBooleanExtra("MODIFY", false))
        {

            product = (Product) getIntent().getSerializableExtra("PRODUCT");

            image_name = product.product_thumbnail;
            image_path = file_path + product.product_thumbnail;

            displayProductSpecification(product);
            setTitle("Edit Product");
        }
    }


    private void displayProductSpecification(Product product) {

        editProductName.setText(product.product_name);
        editProductDescription.setText(product.description);
        editWeight.setText(String.valueOf(product.weight));
        editUnit.setText(product.unit.toLowerCase());
        editPrice.setText(df.format(product.price));
        editDiscountPrice.setText(df.format(product.discount_price));

        int index = Product.findIndex(productCategoryList, product.category_id);
        spinnerProductCategory.setSelection(index + 1);

        /*if(index != -1)
        {

            productSubCategoryList = helper.getProductSubCategory(product.category_id);
            populateSubProductCategorySpinner(productSubCategoryList);

            index = Product.findIndex(product.sub_category_id, productSubCategoryList);
            spinnerProductSubCategory.setSelection(index + 1);
        }*/


        if(product.price == product.discount_price)
        {
            chkDiscount.setChecked(true);
        }

        if(!product.product_thumbnail.equals(""))
        {

            if(fileExist(product.product_thumbnail))
            {

                try
                {

                    // bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // downsizing image as it throws OutOfMemory Exception for larger images
                    options.inSampleSize = 4; //power of 2 like 2, 4, 8, 16

                    final Bitmap bitmap = BitmapFactory.decodeFile(file_path + product.product_thumbnail, options);

                    product_image.setImageBitmap(bitmap);
                }

                catch (Exception e)
                {
                    int res = getResources().getIdentifier("no_image", "drawable", getPackageName());
                    product_image.setImageResource(res);
                }
            }

            else
            {
                int res = getResources().getIdentifier("no_image", "drawable", getPackageName());
                product_image.setImageResource(res);
            }
        }

        else
        {
            int res = getResources().getIdentifier("no_image", "drawable", getPackageName());
            product_image.setImageResource(res);
        }
    }


    private boolean fileExist(String file_name)
    {

        File imgFile = new File(file_path + file_name);
        {
            return imgFile.exists();
        }
    }


    private void findViewById()
    {

        spinnerProductCategory = (AppCompatSpinner) findViewById(R.id.spinnerProductCategory);
        spinnerProductSubCategory = (AppCompatSpinner) findViewById(R.id.spinnerProductSubCategory);

        editProductName = (EditText) findViewById(R.id.editProductName);
        editProductDescription = (EditText) findViewById(R.id.editProductDescription);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editUnit = (EditText) findViewById(R.id.editUnit);
        editPrice = (EditText) findViewById(R.id.editPrice);
        editDiscountPrice = (EditText) findViewById(R.id.editDiscountPrice);
        product_image = (ImageView) findViewById(R.id.product_image);
        chkDiscount = (CheckBox) findViewById(R.id.chkDiscount);
    }


    private void populateProductCategorySpinner()
    {

        // Spinner Drop down elements
        productCategoryList = helper.getActiveProductCategory();

        List<String> categoryList = new ArrayList<>();
        categoryList.add(0, "Select Category");

        for(Product product: productCategoryList)
        {
            categoryList.add(Helper.toCamelCase(product.getCategoryName()));
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerProductCategory.setAdapter(dataAdapter);
    }



    private void populateSubProductCategorySpinner(List<Product> productSubCategoryList)
    {

        List<String> subCategoryList = new ArrayList<>();

        subCategoryList.add(0, "Select Sub Category");

        for(Product product: productSubCategoryList)
        {
            subCategoryList.add(Helper.toCamelCase(product.getSubCategoryName()));
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subCategoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerProductSubCategory.setAdapter(dataAdapter);
    }


    private boolean validate()
    {

        if(selected_category_id == 0)
        {
            makeSnackbar("Please select product category");
            return false;
        }

        if(selected_sub_category_id == 0)
        {
            makeSnackbar("Please select product sub category");
            return false;
        }

        if(editProductName.getText().toString().trim().length() < 3)
        {
            makeSnackbar("Name must be at least 3 characters long");
            return false;
        }

        if(editWeight.getText().toString().trim().length() == 0)
        {
            makeSnackbar("Please enter weight");
            return false;
        }

        if(editUnit.getText().toString().trim().length() == 0)
        {
            makeSnackbar("Please enter unit");
            return false;
        }

        if(editPrice.getText().toString().trim().length() == 0)
        {
            makeSnackbar("Please enter price");
            return false;
        }

        if(editDiscountPrice.getText().toString().trim().length() == 0)
        {
            makeSnackbar("Please enter discount price");
            return false;
        }

        if(Double.parseDouble(editPrice.getText().toString()) < Double.parseDouble(editDiscountPrice.getText().toString()))
        {
            makeSnackbar("Invalid discount price");
            return false;
        }

        return true;
    }


    private void reset()
    {

        editProductName.getText().clear();
        editProductDescription.getText().clear();
        editWeight.getText().clear();
        editUnit.getText().clear();
        editPrice.getText().clear();
        editDiscountPrice.getText().clear();
        chkDiscount.setChecked(false);
        spinnerProductCategory.setSelection(0);
        spinnerProductSubCategory.setSelection(0);

        selected_category_id = 0;
        selected_sub_category_id = 0;

        image_name = "";
        image_path = "";

        populateProductCategorySpinner();

        try
        {

            int res = getResources().getIdentifier("no_image", "drawable", getPackageName());
            product_image.setImageResource(res);
        }

        catch (Exception e)
        {

        }
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.linearMain), msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.myPrimaryColor));
        snackbar.show();
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnSave:

                if(validate())
                {

                    product = initProductObject();

                    if(helper.dbProductCount(product) > 0)
                    {
                        new CustomAlertDialog(this, ProductSpecificationActivity.this).showUpdateConfirmationDialog("Already Exists", "This product already exists. Do you want to update", "product");
                    }

                    else
                    {

                        if(!getIntent().getBooleanExtra("MODIFY", false))
                        {

                            if(helper.insertProduct(product))
                            {

                                helper.insertProductImage(product);
                                new SyncProduct(getApplicationContext()).execute();

                                reset();

                                Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Failed to save product", Toast.LENGTH_LONG).show();
                            }
                        }

                        else
                        {
                            onTaskCompleted(true, 200, "update");
                        }
                    }
                }

                break;

            case R.id.product_image:

                if(permissionCheckerStorage())
                {
                    Intent intent = new Intent(ProductSpecificationActivity.this, UploadProductImageActivity.class);
                    intent.putExtra("FILE_NAME", "PRODUCT_" + GenerateUniqueId.generateRandomString());
                    startActivityForResult(intent, MEDIA_TYPE_IMAGE);
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        if(getIntent().getBooleanExtra("MODIFY", false))
        {

            getMenuInflater().inflate(R.menu.menu_delete_product, menu);

            // Get widget's instance
            switchDelete = (SwitchCompat) menu.findItem(R.id.myswitch).getActionView();
            switchDelete.setText("");
            addSwitchEvent();
        }

        return super.onCreateOptionsMenu(menu);
    }


    private void addSwitchEvent()
    {

        final Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

        if (product.status == 1)
        {
            switchDelete.setChecked(true);
        }

        else
        {
            switchDelete.setChecked(false);
        }


        switchDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    helper.updateProduct(product.product_code, 1);
                    new SyncProduct(getApplicationContext()).execute();
                    Toast.makeText(getApplicationContext(), "Product is activated", Toast.LENGTH_LONG).show();
                } else {
                    helper.updateProduct(product.product_code, 0);
                    new SyncProduct(getApplicationContext()).execute();
                    Toast.makeText(getApplicationContext(), "Product is de activated", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try
        {

            if(requestCode == MEDIA_TYPE_IMAGE && resultCode == MEDIA_TYPE_IMAGE)
            {

                image_path = data.getStringExtra("PATH");
                image_name = data.getStringExtra("FILE_NAME");


                // bitmap factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger images
                options.inSampleSize = 4; //power of 2 like 2, 4, 8, 16

                final Bitmap bitmap = BitmapFactory.decodeFile(image_path, options);

                product_image.setImageBitmap(bitmap);
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id)
    {

        AppCompatSpinner spinner = (AppCompatSpinner) parent;

        switch (spinner.getId())
        {

            case R.id.spinnerProductCategory:

                if(position > 0)
                {

                    selected_category_id = productCategoryList.get(position-1).category_id;
                    selected_sub_category_id = 0;

                    productSubCategoryList = helper.getProductSubCategory(selected_category_id);
                    populateSubProductCategorySpinner(productSubCategoryList);
                }

                break;

            case R.id.spinnerProductSubCategory:

                if(position > 0)
                {
                    selected_sub_category_id = productSubCategoryList.get(position - 1).sub_category_id;
                }

                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

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
        }

        return super.onOptionsItemSelected(item);
    }


    private Product initProductObject()
    {

        if(!getIntent().getBooleanExtra("MODIFY", false)) {
            product = new Product();
            product.setProductCode(GenerateUniqueId.generateProductCode(getStoreDetails().getMobileNo()));

        }

        product.setCategoryId(selected_category_id);
        product.setSubCategoryId(selected_sub_category_id);
        product.setProductName(editProductName.getText().toString());
        product.setProductDescription(editProductDescription.getText().toString());
        product.setProductThumbnail(image_name);
        product.setProductThumbnailPath(image_path);
        product.setPrice(Double.parseDouble(editPrice.getText().toString()));
        product.setDiscountPrice(Double.parseDouble(editDiscountPrice.getText().toString()));
        product.setWeight(Integer.parseInt(editWeight.getText().toString()));
        product.setUnit(editUnit.getText().toString());

        return product;
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


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        if(code == 200)
        {

            product.setStatus(1);
            helper.updateProduct(product, 0);
            helper.insertProductImage(product);
            new SyncProduct(getApplicationContext()).execute();

            reset();
            Toast.makeText(getApplicationContext(), "Product updated successfully.", Toast.LENGTH_LONG).show();
        }
    }


    private boolean checkPermissionStorage()
    {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private void requestPermissionStorage(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            makeToast("Storage permission allows us to read or write data onto memory. Please allow in App Settings for read or write data.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


    private boolean permissionCheckerStorage() {

        if (!checkPermissionStorage())
        {
            requestPermissionStorage();
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}