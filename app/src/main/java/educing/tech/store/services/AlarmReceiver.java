package educing.tech.store.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.mysql.db.send.SyncChatMessage;
import educing.tech.store.mysql.db.send.SyncDeliveryDetails;
import educing.tech.store.mysql.db.send.SyncOrderStatus;
import educing.tech.store.mysql.db.send.SyncProduct;
import educing.tech.store.mysql.db.send.SyncStoreAddress;
import educing.tech.store.mysql.db.send.SyncStoreProfile;
import educing.tech.store.network.InternetConnectionDetector;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_DELIVERY_DETAILS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_ORDERS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_IMAGE;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_ADDRESS;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_STORE_PROFILE;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_CHAT_IMAGES;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_CHAT_MESSAGES;


public class AlarmReceiver extends BroadcastReceiver implements OnTaskCompleted
{
	
	Context context;


	@Override
	public void onReceive(Context context, Intent intent) 
	{
		
		this.context = context;
		SessionManager session = new SessionManager(context); // Session class instance

		if(!session.isLoggedIn())
		{
			return;
		}


		int alarm = intent.getExtras().getInt("alarm");

		if(alarm == 1)
		{

			if(new InternetConnectionDetector(context).isConnected())
			{

				//makeToast("Sync Alarm Received");
				syncData();
			}
		}
	}


	private void makeToast(String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}


	private void syncData()
	{

		SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);

		if(helper.dbSyncCount(TABLE_CHAT_MESSAGES) != 0)
		{
			new SyncChatMessage(context, this).execute();
		}

		if(helper.dbSyncCount(TABLE_CHAT_IMAGES) != 0)
		{
			new SyncChatMessage(context, this).getAllChatImage();
		}


		if(helper.dbSyncCount(TABLE_PRODUCT) != 0)
		{
			new SyncProduct(context).execute();
		}

		if(helper.dbSyncCount(TABLE_PRODUCT_IMAGE) != 0)
		{
			new SyncProduct(context).getAllProductImage();
		}

		if(helper.dbSyncCount(TABLE_STORE_PROFILE) != 0)
		{
			new SyncStoreProfile(context).execute();
		}

		if(helper.dbSyncCount(TABLE_STORE_ADDRESS) != 0)
		{
			new SyncStoreAddress(context).execute();
		}

		if(helper.dbSyncCount(TABLE_DELIVERY_DETAILS) != 0)
		{
			new SyncDeliveryDetails(context).execute();
		}

		if(helper.dbSyncCount(TABLE_ORDERS) != 0)
		{
			new SyncOrderStatus(context).execute();
		}
	}


	@Override
	public void onTaskCompleted(boolean flag, int code, String message)
	{

	}
}