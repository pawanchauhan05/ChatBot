package com.app.chatbot.utils;

import android.app.ProgressDialog;

import java.util.HashMap;
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
}
