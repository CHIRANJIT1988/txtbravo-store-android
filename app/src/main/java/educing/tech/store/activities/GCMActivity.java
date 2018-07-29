package educing.tech.store.activities;

import static educing.tech.store.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static educing.tech.store.CommonUtilities.EXTRA_MESSAGE;
import static educing.tech.store.CommonUtilities.SENDER_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import educing.tech.store.R;
import educing.tech.store.ServerUtilities;
import educing.tech.store.WakeLocker;
import educing.tech.store.model.Store;
import educing.tech.store.network.InternetConnectionDetector;

import static educing.tech.store.configuration.Configuration.SHARED_PREF;


public class GCMActivity extends AppCompatActivity
{

	// label to display gcm messages
	TextView lblMessage;
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;

	public static Store store;

	SharedPreferences prefs = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_gcm);


		//Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		//setSupportActionBar(mToolbar);


		prefs = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);


		if (!new InternetConnectionDetector(getApplicationContext()).isConnected())
		{
			makeToast("Internet Connection Fail");
			return;
		}
		
		
		// Getting name, email from intent
		Intent i = getIntent();
		

		store = (Store) i.getSerializableExtra("STORE_OBJ");


		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		
		lblMessage = (TextView) findViewById(R.id.lblMessage);
		
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		
		// Check if regid already presents
		if (regId.equals("")) 
		{

			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, SENDER_ID);
		} 
		
		else 
		{

			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this))
			{
			
				// Skips registration.				
				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} 
			
			else
			{
			
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				
				mRegisterTask = new AsyncTask<Void, Void, Void>() 
				{

					@Override
					protected Void doInBackground(Void... params) 
					{
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, store, regId);
						return null;
					}

					
					@Override
					protected void onPostExecute(Void result) 
					{
						mRegisterTask = null;
					}

				};
				
				mRegisterTask.execute(null, null, null);
			}
		}
	}		

	
	
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() 
	{
	
		@Override
		public void onReceive(Context context, Intent intent) 
		{

			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			
			// Showing received message
			lblMessage.append(newMessage + "\n");


			if (newMessage.equalsIgnoreCase("From Server: Successfully added device!")
					&& !GCMRegistrar.getRegistrationId(context).equals(""))
			{

					prefs.edit().putBoolean("hasGCM", true).apply();
					startActivity(new Intent(context, RegisterActivity.class));

					finish();

			}

			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	
	
	@Override
	protected void onDestroy() 
	{
	
		if (mRegisterTask != null) 
		{
			mRegisterTask.cancel(true);
		}
		
		
		try
		{
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} 
		
		catch (Exception e)
		{
			Log.e("UnRegister Error", "> " + e.getMessage());
		}
		
		super.onDestroy();
	}


	private void makeToast(String msg)
	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onBackPressed()
	{

	}
}