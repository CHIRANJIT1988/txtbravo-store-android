package educing.tech.store.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import educing.tech.store.helper.LruBitmapCache;

/**
 * Created by Ravi on 13/05/15.
 */

public class MyApplication extends MultiDexApplication {


    private ImageLoader mImageLoader;


    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;


    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance()
    {
        return mInstance;
    }


    public RequestQueue getRequestQueue()
    {

        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }


    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag)
    {

        if (mRequestQueue != null)
        {
            mRequestQueue.cancelAll(tag);
        }
    }


    public ImageLoader getImageLoader()
    {

        getRequestQueue();

        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }

        return this.mImageLoader;
    }
}