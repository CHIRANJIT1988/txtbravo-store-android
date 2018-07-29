package educing.tech.store.mysql.db.send;

import educing.tech.store.app.MyApplication;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Order;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import static educing.tech.store.configuration.Configuration.API_URL;


public class FetchOrderDetails
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;
	
	private String order_no;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;
	


	public FetchOrderDetails(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;

		this.URL = API_URL + "fetch-order-details.php";
	}
	


	public void fetchOrder(String order_no)
	{

		this.order_no = order_no;
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

					Log.v("Response: ", response);

					JSONArray arr = new JSONArray(response);

					if(arr.length() > 0)
					{

						for (int i = 0; i < arr.length(); i++)
						{

							JSONObject jsonObj = (JSONObject) arr.get(i);

							int product_id = jsonObj.getInt("product_id");
							String product_name = jsonObj.getString("product_name");
							String product_image = jsonObj.getString("product_image");
							int weight = jsonObj.getInt("weight");
							String unit = jsonObj.getString("unit");
							double price = jsonObj.getDouble("price");
							double discount_price = jsonObj.getDouble("discount_price");
							int quantity = jsonObj.getInt("quantity");

							Order order = new Order(order_no, product_id, product_name, product_image, weight, unit, price, discount_price, quantity);
							Order.orderList.add(order);

							new SQLiteDatabaseHelper(context).insertOrderDetails(order);
						}

						listener.onTaskCompleted(true, 200, "success");
					}

					else
					{
						listener.onTaskCompleted(false, 500, "No items found");
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

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
					return;
				}

				listener.onTaskCompleted(false, 500, "Internet Connection Failure. Try Again");

			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				params.put("order_no", order_no);

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}