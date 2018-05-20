package com.app.chatbot.utils;

import android.os.Parcel;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.app.chatbot.interfaces.NetworkCallback;
import com.app.chatbot.volley.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkCall<T> {

    private static final String TAG = NetworkCall.class.getName();
    private String url;
    private Map<String, String> params = new HashMap<>();
    private int requestMethod;
    private NetworkCallback networkCallback;
    private Class<T> clazz;

    public NetworkCall(String url, Map<String, String> params, int requestMethod, NetworkCallback networkCallback, Class<T> clazz) {
        this.url = url;
        if (params == null)
            this.params = new HashMap<>();
        else
            this.params = params;
        this.requestMethod = requestMethod;
        this.networkCallback = networkCallback;
        this.clazz = clazz;
    }

    protected NetworkCall(Parcel in) {
        this.url = in.readString();
        int paramsSize = in.readInt();
        this.params = new HashMap<String, String>(paramsSize);
        for (int i = 0; i < paramsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.params.put(key, value);
        }
        this.requestMethod = in.readInt();
        this.clazz = (Class<T>) in.readSerializable();
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    private void setParams(Map<String, String> params) {
        this.params = params;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    private void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    private NetworkCallback getNetworkCallback() {
        return networkCallback;
    }

    private void setNetworkCallback(NetworkCallback networkCallback) {
        this.networkCallback = networkCallback;
    }

    public void initiateCall(String title, FragmentManager fragmentManager) {

        Volley<T> doorCoreVolley = new Volley<T>(Utility.getInstance().getHeaders()) {
            @Override
            public String getURL() {
                return url;
            }

            @Override
            public void onResponseString(String response) {

            }

            @Override
            public void onResponseJsonObject(JSONObject response) {

            }

            @Override
            public void handleError(VolleyError error) {
                try {
                    networkCallback.onError(error);

                    if (error != null) {
                        NetworkResponse networkResponse = error.networkResponse;

                        if (error.getClass().equals(TimeoutError.class)) {

                        }

                        if (error.getClass().equals(AuthFailureError.class)) {

                        }

                        if (error.getClass().equals(NoConnectionError.class)) {

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void JSONModel(T jsonModel) {
                try {

                    networkCallback.onSuccess(jsonModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Map<String, String> getAdditionalParams() {
                HashMap<String, String> params = (HashMap<String, String>) super.getAdditionalParams();
                if (getParams() != null) {
                    params.putAll(getParams());
                }
                Log.e("Pawan " + TAG, "getAdditionalParams: " + params);
                return params;
            }
        };

        doorCoreVolley.sendStringRequest(requestMethod, clazz);
    }

    /**
     * used for simple network call
     */
    public void initiateCall() {
        Volley<T> doorCoreVolley = new Volley<T>(Utility.getInstance().getHeaders()) {
            @Override
            public String getURL() {
                Log.e("Pawan " + TAG, "getURL: " + url);
                return url;
            }

            @Override
            public void onResponseString(String response) {

            }

            @Override
            public void onResponseJsonObject(JSONObject response) {

            }

            @Override
            public void handleError(VolleyError error) {
                try {
                    super.handleError(error);
                    networkCallback.onError(error);

                    if (error != null) {
                        NetworkResponse networkResponse = error.networkResponse;


                        if (error.getClass().equals(TimeoutError.class)) {

                        }

                        if (error.getClass().equals(AuthFailureError.class)) {

                        }

                        if (error.getClass().equals(NoConnectionError.class)) {

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void JSONModel(T jsonModel) {
                try {
                    networkCallback.onSuccess(jsonModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public Map<String, String> getAdditionalParams() {
                HashMap<String, String> params = (HashMap<String, String>) super.getAdditionalParams();
                if (getParams() != null) {
                    params.putAll(getParams());

                }

                Log.e("Pawan " + TAG, "getAdditionalParams: " + params);
                return params;
            }
        };

        doorCoreVolley.sendStringRequest(requestMethod, clazz);
    }


}
