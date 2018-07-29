package educing.tech.store.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import educing.tech.store.R;
import educing.tech.store.gps.GPSTracker;
import educing.tech.store.helper.Helper;
import educing.tech.store.helper.OnLocationFound;
import educing.tech.store.model.Address;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.SyncStoreAddress;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;


public class AddressFragment extends Fragment implements View.OnClickListener, OnLocationFound
{

	private Context context;

	private TextView editAddress;
	private TextView editCity;
	private TextView editState;
	private TextView editPincode;

	private ProgressDialog pDialog;
	private Button btnSave, btnStoreLocation;
	private RelativeLayout relativeLayout;

	private SessionManager session; // Session Manager Class
	private SQLiteDatabaseHelper helper;

	private Store store;
	private Address address = new Address();
	private GoogleMap mMap;

	private static final int GPS_PERMISSION_REQUEST_CODE = 1;


	public AddressFragment()
	{

	}


	@SuppressLint("ValidFragment")
	public AddressFragment(Context context)
	{
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

		View rootView = inflater.inflate(R.layout.fragment_store_address, container, false);

		findViewById(rootView);
		btnSave.setOnClickListener(this);
		btnStoreLocation.setOnClickListener(this);

		this.session = new SessionManager(context); // Session Manager
		this.pDialog = new ProgressDialog(context);

		this.store = getStoreDetails();
		this.helper = new SQLiteDatabaseHelper(context);


		if(helper.dbRowCount(SQLiteDatabaseHelper.TABLE_STORE_ADDRESS) != 0)
		{

			address = helper.getStoreAddress();

			editAddress.setText(Helper.toCamelCase(address.address));
			editCity.setText(Helper.toCamelCase(address.city));
			editState.setText(Helper.toCamelCase(address.state));
			editPincode.setText(address.pincode);
		}

		setUpMap();

		hideKeyboard(rootView);

		return rootView;
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


	private void findViewById(View rootView)
	{

		editAddress = (TextView) rootView.findViewById(R.id.editAddress);
		editCity = (TextView) rootView.findViewById(R.id.editCity);
		editState = (TextView) rootView.findViewById(R.id.editState);
		editPincode = (TextView) rootView.findViewById(R.id.editPincode);

		btnSave = (Button) rootView.findViewById(R.id.btnSaveAddress);
		btnStoreLocation = (Button) rootView.findViewById(R.id.btnStoreLocation);

		relativeLayout = (RelativeLayout) rootView.findViewById(R.id.layout_main);
	}


	public void onClick(View v)
	{

		switch (v.getId())
		{

			case R.id.btnSaveAddress:

				if(validateForm())
				{

					Address address = initAddressObject();

					if(address.getLatitude() == 0 || address.getLongitude() == 0)
					{

						makeSnackbar("Click on Map and Update Store Location");
					}

					else
					{
						saveAddress(address);
					}
				}

				break;

			case R.id.btnStoreLocation:

				if(permissionCheckerGPS())
				{
					displayMap();
				}

				break;
		}
	}


	private void displayMap()
	{
		if(address.getLatitude() == 0 || address.getLongitude() == 0)
		{

			GPSTracker gps = new GPSTracker(getActivity(), AddressFragment.this);

			if(gps.canGetLocation())
			{
				initProgressDialog();
			}

			else
			{
				gps.showSettingsAlert();
			}
		}

		else
		{
			Intent intent = new Intent(getActivity(), StoreOnMapActivity.class);
			intent.putExtra("LATITUDE", address.latitude);
			intent.putExtra("LONGITUDE", address.longitude);
			startActivityForResult(intent, 1);
		}
	}


	private void saveAddress(Address address)
	{

		if(!helper.insertStoreAddress(address));
		{
			helper.updateStoreAddress(address);
		}

		new SyncStoreAddress(getActivity()).execute();
		redirectConfirmationFragment();
	}


	private boolean validateForm()
	{

		if(editAddress.getText().toString().trim().length() < 10)
		{
			makeSnackbar("Address should be at least 10 character");
			return false;
		}

		if(editCity.getText().toString().trim().length() == 0)
		{
			makeSnackbar("Enter City");
			return false;
		}

		if(editState.getText().toString().trim().length() == 0)
		{
			makeSnackbar("Enter State");
			return false;
		}

		if(editPincode.getText().toString().trim().length() != 6)
		{
			makeSnackbar("Invalid Pincode");
			return false;
		}

		return true;
	}


	private void makeSnackbar(String msg)
	{

        /*Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();*/

		Snackbar snackbar = Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT);
		View snackBarView = snackbar.getView();
		snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.myPrimaryColor));
		snackbar.show();
	}


	private void initProgressDialog()
	{

		pDialog.setMessage("Fetching Location ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}


	private Store getStoreDetails()
	{

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


	private Address initAddressObject()
	{

		address.setStoreId(store.store_id);
		address.setAddress(editAddress.getText().toString());
		address.setCity(editCity.getText().toString());
		address.setState(editState.getText().toString());
		address.setPincode(editPincode.getText().toString());

		return address;
	}


	@Override
	public void onLocationFound(Location location)
	{

		if(pDialog.isShowing())
		{
			pDialog.dismiss();
		}

		address.setLatitude(location.getLatitude());
		address.setLongitude(location.getLongitude());

		Intent intent = new Intent(getActivity(), StoreOnMapActivity.class);
		intent.putExtra("LATITUDE", address.latitude);
		intent.putExtra("LONGITUDE", address.longitude);
		startActivityForResult(intent, 1);
	}


	/*@Override
	public void onTaskCompleted(boolean flag, int code, String message)
	{

		if(code == 199)
		{
			saveAddress(address);
			return;
		}

		if(code == 200)
		{

			Intent intent = new Intent(getActivity(), StoreOnMapActivity.class);
			intent.putExtra("LATITUDE", address.latitude);
			intent.putExtra("LONGITUDE", address.longitude);
			startActivityForResult(intent, 1);
		}
	}*/


	private void setUpMap()
	{


		try
		{

			//show error dialog if GoolglePlayServices not available
			if (!isGooglePlayServicesAvailable())
			{
				getActivity().finish();
			}


			// Do a null check to confirm that we have not already instantiated the map.
			if (mMap == null)
			{

				// Try to obtain the map from the SupportMapFragment.
				SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap));
				//(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);

				mMap = supportMapFragment.getMap();
				mMap.setMyLocationEnabled(false);

				LatLng position = new LatLng(address.latitude, address.longitude);

				// Instantiating MarkerOptions class
				MarkerOptions options = new MarkerOptions();

				// Setting position for the MarkerOptions
				options.position(position);


				// Check if we were successful in obtaining the map.
				if (mMap != null)
				{

					if(address.latitude != 0 || address.longitude != 0)
					{
						mMap.addMarker(options);
					}

					mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
					mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

					mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

						@Override
						public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

							marker.showInfoWindow(); // show marker info window on marker click
							return true;
						}
					});


					mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

						@Override
						public void onMapClick(LatLng point)
						{

							if(permissionCheckerGPS())
							{
								displayMap();
							}
						}
					});
				}

				else
				{
					Toast.makeText(context, "Unable to create Maps", Toast.LENGTH_SHORT).show();
				}
			}
		}

		catch (Exception e)
		{

		}
	}


	/**
	 * Receiving speech input
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == 1)
		{

			double latitude = data.getDoubleExtra("LATITUDE", 0);
			double longitude = data.getDoubleExtra("LONGITUDE", 0);

			if(latitude != address.latitude || longitude != address.longitude)
			{

				address.setLatitude(latitude);
				address.setLongitude(longitude);


				LatLng position = new LatLng(latitude, longitude);

				// Instantiating MarkerOptions class
				MarkerOptions options = new MarkerOptions();

				// Setting position for the MarkerOptions
				options.position(position);


				mMap.clear();
				mMap.addMarker(options);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
				mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
			}
		}
	}


	private boolean isGooglePlayServicesAvailable()
	{

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

		if (ConnectionResult.SUCCESS == status)
		{
			return true;
		}

		else
		{
			GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
			return false;
		}
	}


	private void redirectConfirmationFragment()
	{

		StoreConfigurationActivity.ib_address.setBackgroundResource(R.drawable.ib_order_status_completed);
		StoreConfigurationActivity.ib_address.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check_white_24dp));

		Fragment fragment = new ConfirmationFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
		fragmentTransaction.replace(R.id.container_body, fragment);
		fragmentTransaction.commit();
	}


	private boolean checkPermissionGPS()
	{

		int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

		if (result == PackageManager.PERMISSION_GRANTED)
		{
			return true;
		}

		else
		{
			return false;
		}
	}


	private boolean permissionCheckerGPS()
	{

		if (!checkPermissionGPS())
		{
			requestPermissionGPS();
			return false;
		}

		return true;
	}


	private void requestPermissionGPS(){

		if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
		{
			makeToast("GPS permission allows us to access location data. Please allow in App Settings for location.");
		}

		else
		{
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{

		switch (requestCode)
		{

			case GPS_PERMISSION_REQUEST_CODE:

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					makeToast("Permission Granted");
				}

				else
				{
					makeToast("Permission Denied");
				}

				break;
		}
	}


	private void makeToast(String msg)
	{
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}


	private void hideKeyboard(final View rootView) {

		editPincode.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (editPincode.getText().toString().trim().length() == 6) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
				}
			}
		});
	}
}