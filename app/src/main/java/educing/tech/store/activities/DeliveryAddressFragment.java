package educing.tech.store.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import educing.tech.store.R;
import educing.tech.store.helper.GenerateUniqueId;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.ChatMessage;
import educing.tech.store.model.Order;
import educing.tech.store.model.Store;
import educing.tech.store.mysql.db.send.SyncChatMessage;
import educing.tech.store.mysql.db.send.SyncOrderStatus;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;


public class DeliveryAddressFragment extends Fragment implements View.OnClickListener, OnTaskCompleted
{

	private static final int CALL_PERMISSION_REQUEST_CODE = 1;

	private Context context;
	private int color;

	public TextView name, address, phone_no, tvDeliveryStatus;
	public ImageButton ibCall, ibOrderPacked, ibDeliveryBoy, ibOrderDelivered;
	public Order order;
	private SQLiteDatabaseHelper helper;


	public DeliveryAddressFragment()
	{

	}


	@SuppressLint("ValidFragment")
	public DeliveryAddressFragment(Context context, Order order, int color)
	{
		this.color = color;
		this.context = context;
		this.order = order;
		this.helper = new SQLiteDatabaseHelper(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

		View rootView = inflater.inflate(R.layout.fragment_delivery_address, container, false);

		final FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.dummyfrag_bg);
		frameLayout.setBackgroundColor(color);

		findViewById(rootView);

		ibCall.setOnClickListener(this);
		ibOrderPacked.setOnClickListener(this);
		ibDeliveryBoy.setOnClickListener(this);
		ibOrderDelivered.setOnClickListener(this);


		StringBuilder sAddress = new StringBuilder().append(order.address.toUpperCase()).append(", ")
				.append(order.landmark.toUpperCase()).append(", ").append(order.city.toUpperCase())
				.append(", ").append(order.state.toUpperCase()).append(", ").append(order.country.toUpperCase())
				.append(", ").append(order.pincode);


		name.setText(order.customer_name.toUpperCase());
		phone_no.setText(String.valueOf("#M " + order.phone_no));
		address.setText(sAddress);

		display_order_status(order.order_status);

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

		name = (TextView) rootView.findViewById(R.id.name);
		address = (TextView) rootView.findViewById(R.id.address);
		phone_no = (TextView) rootView.findViewById(R.id.mobile_no);
		tvDeliveryStatus = (TextView) rootView.findViewById(R.id.delivery_status);
		ibCall = (ImageButton) rootView.findViewById(R.id.ibCall);
		ibOrderPacked = (ImageButton) rootView.findViewById(R.id.ib_order_packed);
		ibDeliveryBoy = (ImageButton) rootView.findViewById(R.id.ib_order_handover);
		ibOrderDelivered = (ImageButton) rootView.findViewById(R.id.ib_order_delivered);
	}


	private void display_order_status(String status)
	{

		if(status.equals("PACKED"))
		{

			tvDeliveryStatus.setText(String.valueOf("ORDER PACKED AT STORE"));
			ibOrderPacked.setBackgroundResource(R.drawable.ib_order_status_completed);
		}

		if(status.equals("OUT"))
		{

			tvDeliveryStatus.setText(String.valueOf("OUT FOR DELIVERY"));
			ibOrderPacked.setBackgroundResource(R.drawable.ib_order_status_completed);
			ibDeliveryBoy.setBackgroundResource(R.drawable.ib_order_status_completed);
		}

		if(status.equals("DELIVERED"))
		{
			tvDeliveryStatus.setText(String.valueOf("SUCCESSFULLY DELIVERED"));
			ibOrderPacked.setBackgroundResource(R.drawable.ib_order_status_completed);
			ibDeliveryBoy.setBackgroundResource(R.drawable.ib_order_status_completed);
			ibOrderDelivered.setBackgroundResource(R.drawable.ib_order_status_completed);
		}

		if(status.equals("CANCELLED"))
		{
			tvDeliveryStatus.setText(String.valueOf("ORDER CANCELLED"));
			ibOrderPacked.setBackgroundResource(R.drawable.ib_order_cancelled);
			ibDeliveryBoy.setBackgroundResource(R.drawable.ib_order_cancelled);
			ibOrderDelivered.setBackgroundResource(R.drawable.ib_order_cancelled);
		}
	}


	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{

			case R.id.ibCall:

				try
				{
					if(permissionCheckerCall())
					{
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + order.phone_no));
						startActivity(callIntent);
					}
				}

				catch(Exception e)
				{
					Toast.makeText(getActivity(), "Unable to Call", Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.ib_order_packed:

				if(order.order_status.equals("RECEIVED"))
				{
					composeChatMessage("Your order (ORDER NO: " + order.order_no + ") is packed at Store. For any enquiry feel free to contact with us. Thanks");
					updateOrderStatus("PACKED");
				}

				break;

			case R.id.ib_order_handover:

				if(order.order_status.equals("RECEIVED") || order.order_status.equals("PACKED"))
				{
					composeChatMessage("Your order (ORDER NO: " + order.order_no + ") is out for delivery. Please be available. Thanks");
					updateOrderStatus("OUT");
				}

				break;

			case R.id.ib_order_delivered:

				if(order.order_status.equals("RECEIVED") || order.order_status.equals("PACKED") || order.order_status.equals("OUT"))
				{
					composeChatMessage("Your order (ORDER NO: " + order.order_no + ") is successfully delivered. Thank you for shopping with us");
					updateOrderStatus("DELIVERED");
				}

				break;
		}
	}


	private void updateOrderStatus(String status)
	{

		order.setOrderStatus(status);
		display_order_status(status);

		helper.updateOrder(order);
		new SyncOrderStatus(context).execute();
	}


	private void composeChatMessage(String chat_message)
	{

		ChatMessage chat = new ChatMessage(GenerateUniqueId.generateMessageId(getStoreDetails().getMobileNo()), String.valueOf(order.getUserID()), chat_message, "", String.valueOf(System.currentTimeMillis()), 0, 1);

		ChatMessage chatUserObj = new ChatMessage(String.valueOf(order.getUserID()), order.getCustomerName(), String.valueOf(System.currentTimeMillis()));

		if(!helper.insertChatUser(chatUserObj))
		{
			helper.updateChatUser(chatUserObj);
		}

		helper.insertChatMessage(chat, 0);
		new SyncChatMessage(context, this).execute();
	}


	private Store getStoreDetails()
	{

		SessionManager session = new SessionManager(getActivity());

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

		int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

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

		if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS))
		{
			makeToast("CALL permission allows us to call from app. Please allow in App Settings for call.");
		}

		else
		{
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, CALL_PERMISSION_REQUEST_CODE);
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
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
}