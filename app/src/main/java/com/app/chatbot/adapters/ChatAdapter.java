package com.app.chatbot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.chatbot.R;
import com.app.chatbot.models.Message;
import com.app.chatbot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawansingh on 20/05/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messageList = new ArrayList<>();
    private Context context;
    public static final int LEFT = 0, RIGHT = 1;

    public ChatAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public void updateList(List<Message> messageList) {
        this.messageList =  messageList;
        notifyDataSetChanged();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case LEFT:
                view = inflater.inflate(R.layout.left_chat_msg_layout, parent, false);
                viewHolder = new LeftChatMsgViewHolder(view);
                break;

            case RIGHT:
                view = inflater.inflate(R.layout.right_chat_msg_layout, parent, false);
                viewHolder = new RightChatMsgViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        switch (holder.getItemViewType()) {
            case LEFT:
                ((LeftChatMsgViewHolder) holder).textViewMsg.setText(!TextUtils.isEmpty(message.getMessage()) ? message.getMessage().trim() : "Empty message");
                break;

            case RIGHT:
                ((RightChatMsgViewHolder) holder).textViewMsg.setText(!TextUtils.isEmpty(message.getMessage()) ? message.getMessage().trim() : "Empty message");
                break;

        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getType().equalsIgnoreCase(Constants.BOT)) {
            return LEFT;
        } else if (message.getType().equalsIgnoreCase(Constants.USER)) {
            return RIGHT;
        }

        return RIGHT;
    }

    class LeftChatMsgViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMsg;

        public LeftChatMsgViewHolder(View itemView) {
            super(itemView);
            textViewMsg = itemView.findViewById(R.id.textViewMsg);
        }
    }

    class RightChatMsgViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMsg;

        public RightChatMsgViewHolder(View itemView) {
            super(itemView);
            textViewMsg = itemView.findViewById(R.id.textViewMsg);
        }
    }
}
