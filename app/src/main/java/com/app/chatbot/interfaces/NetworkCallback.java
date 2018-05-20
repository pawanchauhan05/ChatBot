package com.app.chatbot.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by pawansingh on 20/05/18.
 */

public interface NetworkCallback<T> {
    void onSuccess(T t);

    void onError(VolleyError error);
}
