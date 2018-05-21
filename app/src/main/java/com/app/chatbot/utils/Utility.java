package com.app.chatbot.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.app.chatbot.interfaces.NetworkCallback;
import com.app.chatbot.models.Message;
import com.app.chatbot.models.MessageEvent;
import com.app.chatbot.models.Response;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pawansingh on 20/05/18.
 */

public class Utility {
    private static final Utility ourInstance = new Utility();

    public static Utility getInstance() {
        return ourInstance;
    }

    private Utility() {
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headerMap = new HashMap<>();
        return headerMap;
    }

    public String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(String.format("%s=%s", entry.getKey().toString(),
                    entry.getValue().toString()
            ));
        }
        return sb.toString();
    }

    /**
     * this function is used to show default progress dialog.
     *
     * @param title          - progress dialog title
     * @param message        - progress dialog message
     * @param progressDialog - progress dialog object (not be null)
     */
    public static void showProgressBar(String title, String message, ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.show();
            progressDialog.setCancelable(true);
        }
    }

    /**
     * this function is used to hide progress dialog.
     *
     * @param progressDialog - progress dialog object (not be null)
     */
    public static void hideProgressBar(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.cancel();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        protected String doInBackground(String... params) {
            sendToServer(message);
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }


    public void syncMessage() {
        List<Message> messageList = Select.from(Message.class).where(Condition.prop("sent").eq("0")).list();
        if (messageList != null && !messageList.isEmpty()) {
            sendToServer(messageList.get(0));
        }

    }


    private void sendToServer(final Message offlineMessage) {

        NetworkCallback<Response> networkCallback = new NetworkCallback<Response>() {
            @Override
            public void onSuccess(Response response) {

                Log.e("Pawan", "onSuccess: AyncTask " + offlineMessage.getMessage());

                Message message = Select.from(Message.class).where(Condition.prop("store_time").eq(offlineMessage.getStoreTime())).first();
                message.setSent(true);
                message.save();

                Message serverReply = response.getMessage();
                serverReply.setType(Constants.BOT);
                serverReply.setSent(true);
                serverReply.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                serverReply.save();

                EventBus.getDefault().post(new MessageEvent(true));

                syncMessage();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Pawan", "onError: ");
            }
        };

        String textMessage = "";
        try {
            textMessage = URLEncoder.encode(offlineMessage.getMessage(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("apiKey", Constants.API_KEY);
        params.put("chatBotID", Constants.CHAT_BOT_ID);
        params.put("externalID", Constants.EXTERNAL_ID);
        params.put("message", textMessage);

        String url = String.format(Constants.SEND_CHAT + "%s", Utility.getInstance().urlEncodeUTF8(params));


        NetworkCall<Response> networkCall = new NetworkCall<>(url, params, Request.Method.GET, networkCallback, Response.class);
        networkCall.initiateCall();
    }
}
