package educing.tech.store.mysql.db.send;

import educing.tech.store.app.MyApplication;
import educing.tech.store.configuration.Configuration;
import educing.tech.store.helper.Security;
import educing.tech.store.session.SessionManager;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import static educing.tech.store.configuration.Configuration.API_URL;
import static educing.tech.store.configuration.Configuration.SECRET_KEY;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.KEY_CATEGORY_ID;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_CATEGORY ;


public class AttachStore
{

	private String URL = "";

	private Context context;
	private SQLiteDatabaseHelper helper;

	private SharedPreferences preferences;
	private SessionManager session;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public AttachStore(Context context)
	{
		this.context = context;
		this.preferences = context.getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
		this.session = new SessionManager(context);
		this.helper = new SQLiteDatabaseHelper(context);

		this.URL = API_URL + "attach-store.php";
	}


	public void execute()
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{

					JSONArray jsonArray = new JSONArray(response);

					for(int i=0; i<jsonArray.length(); i++)
					{

						JSONObject jsonObj = (JSONObject)jsonArray.get(i);

						Log.v("Response:", response);

						int id = jsonObj.getInt("id");
						int sync_status = jsonObj.getInt("sync_status");

						new SQLiteDatabaseHelper(context).updateSyncStatus(TABLE_PRODUCT_CATEGORY , KEY_CATEGORY_ID, id, sync_status);
					}
				}

				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
				}
			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String ,String> params=new HashMap<>();

				try
				{
					String data = String.valueOf(helper.storeCategoryJSONData());
					params.put("responseJSON", Security.encrypt(data, preferences.getString("key", null)));
					params.put("store", Security.encrypt(session.getStoreId(), SECRET_KEY));
				}

				catch (Exception e)
				{
					Log.v("error ", "" + e.getMessage());
				}

				finally
				{
					Log.v("params ", "" + params);
				}

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}