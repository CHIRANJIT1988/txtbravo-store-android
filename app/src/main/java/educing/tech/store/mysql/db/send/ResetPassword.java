package educing.tech.store.mysql.db.send;

import educing.tech.store.app.MyApplication;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.helper.Security;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import static educing.tech.store.configuration.Configuration.API_URL;
import static educing.tech.store.configuration.Configuration.SECRET_KEY;


public class ResetPassword
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	
	private String phone_number, new_password;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;
	
	
	public ResetPassword(Context _context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = _context;

		this.URL = API_URL + "reset-store-password.php";
	}
	
	
	public void resetPassword(String phone_number, String new_password)
	{

		this.phone_number = phone_number;
		this.new_password = new_password;

		execute();
	}


	public void execute()
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{

					JSONObject jsonObj = new JSONObject(response);

					int error_code = jsonObj.getInt("error_code");
					String message = jsonObj.getString("message");

					Log.v("Response: " , "" + response);


					if (error_code == 200) // checking for error node in json
					{
						listener.onTaskCompleted(true, error_code, message); // Successful
					}

					else
					{

						if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
						{

							execute();

							ATTEMPTS_COUNT ++;

							Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
							return;
						}

						listener.onTaskCompleted(false, error_code, message); // Unsuccessful
					}
				}

				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{
				listener.onTaskCompleted(false, 500, "Internet connection fail. Try Again");
			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				params.put("mobile_no", Security.encrypt(phone_number, SECRET_KEY));
				params.put("code", Security.encrypt(new_password, SECRET_KEY));

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}