package educing.tech.store.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import educing.tech.store.R;
import educing.tech.store.model.ChatMessage;


public class PromoteActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final int CALL_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.quickreturn_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnCall = (Button) findViewById(R.id.btnCall);
        Button btnChat = (Button) findViewById(R.id.btnChat);

        btnCall.setOnClickListener(this);
        btnChat.setOnClickListener(this);

        setTitle("Promote");
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnCall:

                try
                {
                    if(permissionCheckerCall())
                    {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + String.valueOf("+918136051516")));
                        startActivity(callIntent);
                    }
                }

                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Unable to Call", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnChat:

                Intent intent = new Intent(this, ChatWindowActivity.class);
                intent.putExtra("USER", new ChatMessage("0", "Support Team"));
                startActivity(intent);
                break;
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
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean permissionCheckerCall() {

        if (!checkPermissionCall()) {
            requestPermissionCall();
            return false;
        }

        return true;
    }


    private boolean checkPermissionCall()
    {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private void requestPermissionCall(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))
        {
            makeToast("CALL permission allows us to call from app. Please allow in App Settings for call.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, CALL_PERMISSION_REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case CALL_PERMISSION_REQUEST_CODE:

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