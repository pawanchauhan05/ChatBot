package com.app.chatbot.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.app.chatbot.R;
import com.app.chatbot.adapters.ChatAdapter;
import com.app.chatbot.interfaces.NetworkCallback;
import com.app.chatbot.models.Message;
import com.app.chatbot.models.Response;
import com.app.chatbot.utils.Constants;
import com.app.chatbot.utils.NetworkCall;
import com.app.chatbot.utils.Utility;
import com.orm.query.Select;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView buttonSend;
    private EditText editTextMsg;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = Select.from(Message.class).list();

        initViews();
        initListeners();
    }

    private void initViews() {
        buttonSend = findViewById(R.id.buttonSend);
        editTextMsg = findViewById(R.id.editTextMsg);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new ChatAdapter(messageList, this);
        recyclerView.setAdapter(chatAdapter);

        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }

    }

    private void initListeners() {
        buttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend:

                if (TextUtils.isEmpty(editTextMsg.getText().toString().trim()))
                    return;

                sendMessage(editTextMsg.getText().toString().trim());
                editTextMsg.getText().clear();
                break;


        }
    }

    private void sendMessage(String msg) {
        NetworkCallback<Response> networkCallback = new NetworkCallback<Response>() {
            @Override
            public void onSuccess(Response response) {

                Message message = response.getMessage();
                message.setType(Constants.BOT);
                message.save();

                messageList.add(message);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Pawan", "onError: ");
            }
        };

        String textMessage = "";
        try {
            textMessage = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("apiKey", Constants.API_KEY);
        params.put("chatBotID", Constants.CHAT_BOT_ID);
        params.put("externalID", Constants.EXTERNAL_ID);
        params.put("message", textMessage);

        String url = String.format(Constants.SEND_CHAT + "%s", Utility.getInstance().urlEncodeUTF8(params));

        Message message = new Message();
        message.setMessage(msg);
        message.setType(Constants.USER);
        message.save();

        messageList.add(message);
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size() - 1);

        NetworkCall<Response> networkCall = new NetworkCall<>(url, params, Request.Method.GET, networkCallback, Response.class);
        networkCall.initiateCall();
    }
}
