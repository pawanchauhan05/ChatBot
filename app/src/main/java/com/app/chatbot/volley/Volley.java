package com.app.chatbot.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.app.chatbot.app.AppController;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Volley<T> {
    private static final String TAG = Volley.class.getSimpleName();
    private Class<T> clazz;
    private Map<String, String> authHeaders = new HashMap<>();

    public Volley(Map<String, String> authHeaders) {
        this.authHeaders = authHeaders;
    }

    public abstract String getURL();

    public abstract void onResponseString(String response);

    public abstract void onResponseJsonObject(JSONObject response);

    public Context getContext() {
        return null;
    }

    public Map<String, String> getAdditionalParams() {
        return new HashMap<>();
    }

    public void sendStringRequest(int methodType, final Class<T> clazz) {
        this.clazz = clazz;
        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseString(response);
                try {
                    T jsonModel = new Gson().fromJson(response, clazz);
                    JSONModel(jsonModel);
                } catch (Exception e) {
                    handleError(new VolleyError(e.getMessage()));
                    Log.e("Pawan", "onResponse: " + e.getMessage());

                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        };


        StringRequest postReq = new StringRequest(methodType, getURL(), listener, errorListener) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {


                for (Map.Entry<String, String> entry : response.headers.entrySet()) {

                }

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authHeaders;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> formParams = getAdditionalParams();

                return formParams;
            }

            /*@Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }*/


        };

        postReq.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postReq);


    }

    public void sendJsonObjectRequest(int methodType) {


        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Pawan", "onResponse: " + response);
                onResponseJsonObject(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(methodType, "", new JSONObject(), listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return getAdditionalParams();
            }


            /*@Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }*/

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);


    }

    public void handleError(VolleyError error) {
        Log.e("Pawan", "handleError: " + error);
    }

    public abstract void JSONModel(T jsonModel);
}

