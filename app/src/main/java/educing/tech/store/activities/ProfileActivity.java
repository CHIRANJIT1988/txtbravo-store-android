package educing.tech.store.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import educing.tech.store.R;
import educing.tech.store.configuration.Configuration;
import educing.tech.store.mysql.db.send.SendProfilePictureToServer;
import educing.tech.store.helper.Helper;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Store;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.session.SessionManager;

import static educing.tech.store.activities.CameraActivity.MEDIA_TYPE_IMAGE;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted
{

    private TextView tv_name;
    private ImageView iv_profile_pic;
    private ProgressBar pbLoading;

    private SessionManager session;
    private SharedPreferences pref;
    private Store store;
    private String file_path = "";

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");


        session = new SessionManager(this);
        this.pref = getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = width;

        //CardView cardImage = (CardView) findViewById(R.id.card_profile_pic);
        //cardImage.getLayoutParams().height = height - 10;
        //cardImage.getLayoutParams().width = width - 10;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_picture);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, height - 10, width - 10, false);

        iv_profile_pic = (ImageView) findViewById(R.id.iv_profile_pic);
        iv_profile_pic.getLayoutParams().height = height - 10;
        iv_profile_pic.getLayoutParams().width = width - 10;
        iv_profile_pic.setImageBitmap(resized);


        ImageButton ib_edit_profile_name = (ImageButton) findViewById(R.id.ib_edit_profile_name);
        ImageButton ib_edit_profile_pic = (ImageButton) findViewById(R.id.ib_edit_profile_pic);
        tv_name = (TextView) findViewById(R.id.name);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);


        ib_edit_profile_name.setOnClickListener(this);
        ib_edit_profile_pic.setOnClickListener(this);


        this.store = getLoggedInStore();

        tv_name.setText(Helper.toCamelCase(store.getStoreName()));

        if(fileExist(pref.getString("profile_pic", "")))
        {
            previewCapturedImage(pref.getString("profile_pic", ""));
        }
    }


    private boolean fileExist(String file_name)
    {

        File imgFile = new File(file_name);
        {
            return imgFile.exists();
        }
    }


    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.ib_edit_profile_name:

                startActivity(new Intent(ProfileActivity.this, StoreConfigurationActivity.class));
                finish();
                break;

            case R.id.ib_edit_profile_pic:

                if(permissionCheckerCamera()) {

                    if (permissionCheckerStorage())
                    {

                        if(new InternetConnectionDetector(getApplicationContext()).isConnected())
                        {
                            Intent intent = new Intent(this, UploadProductImageActivity.class);
                            intent.putExtra("FILE_NAME", "STORE_" + store.getMobileNo());
                            startActivityForResult(intent, MEDIA_TYPE_IMAGE);// Activity is started with requestCode 2
                        }

                        else
                        {
                            makeToast("Internet Connection Failure");
                        }
                    }
                }

                //Intent intent = new Intent(this, CameraActivity.class);
                //intent.putExtra("ACTION", MEDIA_TYPE_IMAGE);
                //intent.putExtra("FILE_NAME", user.getPhoneNo());
                //startActivityForResult(intent, MEDIA_TYPE_IMAGE);// Activity is started with requestCode 2
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        try
        {

            if (requestCode == MEDIA_TYPE_IMAGE && resultCode == MEDIA_TYPE_IMAGE) {

                file_path = data.getStringExtra("PATH");
                String file_name = data.getStringExtra("FILE_NAME");

                if(new InternetConnectionDetector(getApplicationContext()).isConnected())
                {
                    pbLoading.setVisibility(View.VISIBLE);
                    new SendProfilePictureToServer(getApplicationContext(), this, file_path, file_name).upload();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
                }

                Log.v("file_name: ", file_name);
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private Store getLoggedInStore()
    {
        session.checkLogin();
        HashMap<String, String> user = session.getStoreDetails();
        return new Store(Integer.parseInt(user.get(SessionManager.KEY_STORE_ID)), user.get(SessionManager.KEY_STORE_NAME), user.get(SessionManager.KEY_MOBILE_NO));
    }


    private void previewCapturedImage(final String path)
    {

        try
        {

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;

            final Bitmap bitmap = BitmapFactory.decodeFile(path, options);

            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, 128, 128, false);

            iv_profile_pic.setImageBitmap(bitmap);
        }

        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            pbLoading.setVisibility(View.GONE);

            if(flag && code == 300)
            {
                pref.edit().putString("profile_pic", file_path).apply();
                previewCapturedImage(file_path);
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        catch (Exception e)
        {

        }

        finally
        {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkPermissionCamera()
    {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
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


    private void requestPermissionCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        {
            makeToast("Camera permission allows us to capture image or record video. Please allow in App Settings for capture image or record video.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    makeToast("Permission Granted");
                }

                else
                {
                    makeToast("Permission Denied");
                }

                break;


            case STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private boolean permissionCheckerCamera() {

        if (!checkPermissionCamera())
        {
            requestPermissionCamera();
            return false;
        }

        return true;
    }


    private boolean permissionCheckerStorage() {

        if (!checkPermissionStorage())
        {
            requestPermissionStorage();
            return false;
        }

        return true;
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}