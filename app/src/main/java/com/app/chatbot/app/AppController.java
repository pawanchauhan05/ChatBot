package com.app.chatbot.app;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.orm.SugarApp;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by pawansingh on 20/05/18.
 */

public class AppController extends SugarApp {
    public static final String TAG = AppController.class.getName();
    private static AppController appController;
    public DefaultHttpClient mDefaultHttpClient;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    @SuppressWarnings("deprecation")
    public RequestQueue getRequestQueue() {


        if (mRequestQueue == null) {
            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);
            mDefaultHttpClient = new DefaultHttpClient();
            final ClientConnectionManager mClientConnectionManager = mDefaultHttpClient.getConnectionManager();
            final HttpParams mHttpParams = mDefaultHttpClient.getParams();
            final ThreadSafeClientConnManager mThreadSafeClientConnManager = new ThreadSafeClientConnManager(mHttpParams, mClientConnectionManager.getSchemeRegistry());
            mDefaultHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager, mHttpParams);
            final HttpStack httpStack = new HttpClientStack(mDefaultHttpClient);
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), httpStack);
        }

        return mRequestQueue;
    }

    public static AppController getInstance() {
        return appController;
    }

}

