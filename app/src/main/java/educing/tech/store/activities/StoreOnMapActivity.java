package educing.tech.store.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import educing.tech.store.R;
import educing.tech.store.helper.Helper;


public class StoreOnMapActivity extends FragmentActivity implements View.OnClickListener
{

    private GoogleMap mMap;

    private TextView markerText;

    private LatLng center;
    private LinearLayout markerLayout;
    private List<Address> addresses;

    private TextView address;
    private ProgressBar progressBar;

    private double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_on_map);


        markerText = (TextView) findViewById(R.id.locationMarkertext);
        address = (TextView) findViewById(R.id.address);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable())
        {
            finish();
        }

        latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded()
    {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);

            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
                setUpMap(latitude, longitude);
            }
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    private void setUpMap(double latitude, double longitude)
    {

        LatLng position = new LatLng(latitude, longitude);

        // Instantiating MarkerOptions class
        MarkerOptions options = new MarkerOptions();

        // Setting position for the MarkerOptions
        options.position(position);


        // Check if we were successful in obtaining the map.
        if (mMap != null)
        {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position).zoom(15f).tilt(50).build();

            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Clears all the existing markers
            mMap.clear();


            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0)
                {

                    center = mMap.getCameraPosition().target;

                    markerText.setText(String.valueOf("Set Store Location"));
                    mMap.clear();
                    markerLayout.setVisibility(View.VISIBLE);

                    try
                    {

                        new GetLocationAsync(center.latitude, center.longitude).execute();

                    }

                    catch (Exception e)
                    {

                    }
                }
            });

            markerLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {

                    try
                    {

                        LatLng latLng1 = new LatLng(center.latitude, center.longitude);

                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(" Set your Location ")
                                .snippet("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
                        m.setDraggable(true);

                        markerLayout.setVisibility(View.GONE);
                    }

                    catch (Exception e)
                    {

                    }

                }
            });

            mMap.setTrafficEnabled(false);
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnDone:

                Intent intent = new Intent();
                intent.putExtra("LATITUDE", center.latitude);
                intent.putExtra("LONGITUDE", center.longitude);
                setResult(1, intent);

                finish();
        }
    }


    private boolean isGooglePlayServicesAvailable()
    {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }

        else
        {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    private class GetLocationAsync extends AsyncTask<String, Void, String>
    {

        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude)
        {
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            address.setText(String.valueOf("Fetching Address ..."));
        }

        @Override
        protected String doInBackground(String... params)
        {

            try
            {

                Geocoder geocoder = new Geocoder(StoreOnMapActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();

                if (Geocoder.isPresent())
                {

                    Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();

                    str.append(localityString + " ");
                    str.append(city + " " + region_code + " ");
                    str.append(zipcode + " ");

                }
            }

            catch (IOException e)
            {
                Log.e("tag", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {

            try
            {
                address.setText(String.valueOf(Helper.toCamelCase(addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getAddressLine(1) + " ")));
                progressBar.setVisibility(View.GONE);
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}