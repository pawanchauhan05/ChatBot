package com.app.chatbot.models;

import com.orm.SugarRecord;

/**
 * Created by pawansingh on 20/05/18.
 */

public class Message extends SugarRecord<Message> {
    private String chatBotName;
    private int chatBotID;
    private String message;
    private String emotion;
    private String type;
    private boolean sent;
    private String storeTime;

    public Message() {
    }

    public String getChatBotName() {
        return chatBotName;
    }

    public void setChatBotName(String chatBotName) {
        this.chatBotName = chatBotName;
    }

    public int getChatBotID() {
        return chatBotID;
    }

    public void setChatBotID(int chatBotID) {
        this.chatBotID = chatBotID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(String storeTime) {
        this.storeTime = storeTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "chatBotName='" + chatBotName + '\'' +
                ", chatBotID=" + chatBotID +
                ", message='" + message + '\'' +
                ", emotion='" + emotion + '\'' +
                ", type='" + type + '\'' +
                ", sent=" + sent +
                '}';
    }
}
