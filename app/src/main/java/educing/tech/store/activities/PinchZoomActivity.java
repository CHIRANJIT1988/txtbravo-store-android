package educing.tech.store.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import educing.tech.store.helper.TouchImageView;


public class PinchZoomActivity extends AppCompatActivity
{

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        //TouchImageView img = new TouchImageView(this);
        //img.setImageResource(R.drawable.zoom);
        //img.setMaxZoom(4f);
        //setContentView(img);

        if(permissionCheckerStorage())
        {
            previewCapturedImage(getIntent().getStringExtra("URL"));
        }
    }


    private void previewCapturedImage(final String path)
    {

        try
        {

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 4;

            final Bitmap bitmap = BitmapFactory.decodeFile(path, options);


            TouchImageView img = new TouchImageView(this);
            img.setImageBitmap(bitmap);
            img.setMaxZoom(4f);
            setContentView(img);
        }

        catch (NullPointerException e)
        {
            e.printStackTrace();
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
                    previewCapturedImage(getIntent().getStringExtra("URL"));
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