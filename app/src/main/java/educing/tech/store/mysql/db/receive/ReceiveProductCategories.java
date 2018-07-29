package educing.tech.store.mysql.db.receive;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import educing.tech.store.app.MyApplication;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import static educing.tech.store.configuration.Configuration.API_URL;


public class ReceiveProductCategories
{

	private OnTaskCompleted listener;
	
	private String URL = "";

	private Context context;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;
	private SQLiteDatabaseHelper helper;

	
	public ReceiveProductCategories(Context _context, OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = _context;
		this.helper = new SQLiteDatabaseHelper(context);
		this.URL = API_URL + "product-category.php";
	}


	public void execute()
	{

		// Volley's json array request object
		JsonArrayRequest req = new JsonArrayRequest(URL,

				new Response.Listener<JSONArray>()
				{

					@Override
					public void onResponse(JSONArray response)
					{

						Log.v("response: ", response.toString());

						if (response.length() > 0)
						{

							// looping through json and adding to movies list
							for (int i = 0; i < response.length(); i++)
							{

								try
								{

									JSONObject complainObj = response.getJSONObject(i);

									int category_id = complainObj.getInt("id");
									String name = complainObj.getString("name");
									String image = complainObj.getString("image");

									Product product = new Product(category_id, name, image);

									if(!helper.insertProductCategory(product))
									{
										helper.updateCategory(product, 1);
									}
								}

								catch (Exception e)
								{

								}
							}

							listener.onTaskCompleted(true, 200, "success");
						}
					}
				}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{

				try
				{

					if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
					{

						execute();

						ATTEMPTS_COUNT ++;

						Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
						return;
					}

					listener.onTaskCompleted(false, 500, "fail");
					Log.v("Error : ", "" + error);
				}

				catch (Exception e)
				{

				}
			}
		});


		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(req);
	}
}