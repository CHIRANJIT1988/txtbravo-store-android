package educing.tech.store.session;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import educing.tech.store.model.Store;


public class SessionManager 
{
	
	SharedPreferences pref; // Shared Preferences
	
	Editor editor; // Editor for Shared preferences
	
	Context _context; // Context
	
	int PRIVATE_MODE = 0; // Shared pref mode
	
	
	private static final String PREF_NAME = "JaapyStorePref"; // Sharedpref file name
	
	private static final String IS_LOGIN = "IsLoggedIn"; // All Shared Preferences Keys
	public static final String IS_ONLINE = "IsOnline"; // All Shared Preferences Keys

	public static final String KEY_STORE_ID = "store_id";
	public static final String KEY_STORE_NAME = "store_name";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_MOBILE_NO = "mobile_no";
	
	
	@SuppressLint("CommitPrefEdits") 
	public SessionManager(Context context) // Constructor
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	
	public void createLoginSession(Store store)
	{
		
		editor.putBoolean(IS_LOGIN, true); // Storing login value as TRUE
		editor.putBoolean(IS_ONLINE, true); // Storing login value as TRUE

		editor.putString(KEY_STORE_ID, String.valueOf(store.store_id));
		editor.putString(KEY_STORE_NAME, store.name);
		editor.putString(KEY_PASSWORD, store.password);
		editor.putString(KEY_MOBILE_NO, store.mobileNo);
		
		editor.commit(); // commit changes
	}


	public void editProfile(String store_name) // Get Login State
	{
		editor.putString(KEY_STORE_NAME, store_name);
		editor.commit(); // commit changes
	}


	public void is_online(boolean flag)
	{
		editor.putBoolean(IS_ONLINE, flag);
		editor.commit(); // commit changes
	}


	public boolean checkLogin()
	{
		
		if(!this.isLoggedIn()) // Check login status
		{
			
			/*Intent i = new Intent(_context, MainActivity.class); // user is not logged in redirect him to Login Activity
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all the Activities
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new Flag to start new Activity
			
			_context.startActivity(i); // Staring Login Activity*/
			
			return false;
			
		}
		
		return true;
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	
	public HashMap<String, String> getStoreDetails()
	{
		
		HashMap<String, String> store = new HashMap<>();

		store.put(KEY_STORE_ID, pref.getString(KEY_STORE_ID, null)); // store id
		store.put(KEY_STORE_NAME, pref.getString(KEY_STORE_NAME, null)); // store name
		store.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null)); // password
		store.put(KEY_MOBILE_NO, pref.getString(KEY_MOBILE_NO, null)); // mobile no
		store.put(IS_ONLINE, String.valueOf(pref.getBoolean(IS_ONLINE, false))); // mobile no

		return store; // return user
	}


	public String getStoreId() // Get Login State
	{
		return pref.getString(KEY_STORE_ID, null);
	}


	/**
	 * Clear session details
	 * */
	
	public void logoutUser()
	{
		
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		
		// After logout redirect user to Loing Activity
		/*Intent i = new Intent(_context, MainActivity.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);*/
	
	}


	public void changePassword(String password) // Get Login State
	{
		editor.putString(KEY_PASSWORD, password);
		editor.commit(); // commit changes
	}
	
	/**
	 * Quick check for login
	 * ***/
	
	public boolean isLoggedIn() // Get Login State
	{
		return pref.getBoolean(IS_LOGIN, false);
	}
}