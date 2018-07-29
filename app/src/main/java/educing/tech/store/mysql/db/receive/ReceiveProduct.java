package educing.tech.store.mysql.db.receive;

import educing.tech.store.app.MyApplication;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import static educing.tech.store.configuration.Configuration.API_URL;


public class ReceiveProduct
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;

	private static final int MAX_ATTEMPTS = 5;
	private int ATTEMPTS_COUNT;
	private int store_id;
	private SQLiteDatabaseHelper helper;
	


	public ReceiveProduct(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
		this.helper = new SQLiteDatabaseHelper(context);

		this.URL = API_URL + "receive-all-product.php";
	}


	public void retrieve(int store_id)
	{
		this.store_id = store_id;
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

					Log.v("response: ", response.toString());

					JSONArray arr = new JSONArray(response);

					if(arr.length() > 0)
					{

						for (int i = 0; i < arr.length(); i++)
						{

							JSONObject jsonObj = (JSONObject) arr.get(i);

							String product_code = jsonObj.getString("product_code");
							int category_id = jsonObj.getInt("product_category_id");
							int sub_category_id = jsonObj.getInt("product_sub_category_id");
							String name = jsonObj.getString("product_name");
							String description = jsonObj.getString("product_description");
							String image = jsonObj.getString("product_image");
							double price = jsonObj.getDouble("price");
							double discount_price = jsonObj.getDouble("discount_price");
							int weight = jsonObj.getInt("weight");
							String unit = jsonObj.getString("unit");
							int status = jsonObj.getInt("status");

							Product product = new Product(product_code, category_id, sub_category_id, name, description, weight, unit, price, discount_price, image, status);

							if(!helper.insertProduct(product))
							{
								helper.updateProduct(product, 1);
							}
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(true, 200, "empty");
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

				Log.v("Product List: ", "" + error);

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
					return;
				}

				listener.onTaskCompleted(false, 500, "fail"); // Invalid User

			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				params.put("store_id", String.valueOf(store_id));

				Log.v("params", "" + params);
				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}