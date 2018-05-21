package com.app.chatbot.activities;

import android.content.IntentFilter;
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
import com.app.chatbot.models.MessageEvent;
import com.app.chatbot.models.Response;
import com.app.chatbot.receivers.NetworkStateReceiver;
import com.app.chatbot.utils.Constants;
import com.app.chatbot.utils.NetworkCall;
import com.app.chatbot.utils.Utility;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, NetworkStateReceiver.NetworkStateReceiverListener {

    private ImageView buttonSend;
    private EditText editTextMsg;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private NetworkStateReceiver networkStateReceiver;

    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = Select.from(Message.class).list();
        initViews();
        initListeners();
    }


    private void storeData() {
        Message message = new Message();
        message.setSent(false);
        message.setMessage("i am user");
        message.setType(Constants.USER);
        message.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message.save();

        Message message1 = new Message();
        message1.setSent(false);
        message1.setMessage("who are you?");
        message1.setType(Constants.USER);
        message1.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message1.save();


        Message message2 = new Message();
        message2.setSent(false);
        message2.setMessage("are you a bot ?");
        message2.setType(Constants.USER);
        message2.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message2.save();


        /*Message message3 = new Message();
        message3.setSent(false);
        message3.setMessage("are you busy?");
        message3.setType(Constants.USER);
        message3.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        message3.save();*/
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
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
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

    private void sendMessage(final String msg) {
        NetworkCallback<Response> networkCallback = new NetworkCallback<Response>() {
            @Override
            public void onSuccess(Response response) {

                Message userMessage = new Message();
                userMessage.setMessage(msg);
                userMessage.setSent(true);
                userMessage.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                userMessage.setType(Constants.USER);

                Message message = response.getMessage();
                message.setType(Constants.BOT);
                message.setSent(true);
                userMessage.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                message.save();

                messageList.add(message);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Pawan", "onError: ");
                Message message = new Message();
                message.setMessage(msg);
                message.setType(Constants.USER);
                message.setStoreTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                message.setSent(false);
                message.save();
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

        messageList.add(message);
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size() - 1);

        NetworkCall<Response> networkCall = new NetworkCall<>(url, params, Request.Method.GET, networkCallback, Response.class);
        networkCall.initiateCall();
    }

    @Override
    public void networkAvailable() {
        Utility.getInstance().syncMessage();
    }

    @Override
    public void networkUnavailable() {
        Log.e("Pawan", "networkUnavailable: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event != null && event.isUpdate()) {
            messageList = Select.from(Message.class).list();
            chatAdapter.updateList(messageList);
            if(messageList != null && !messageList.isEmpty()) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        }
    }
}
