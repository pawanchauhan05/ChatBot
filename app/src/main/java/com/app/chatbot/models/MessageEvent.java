package com.app.chatbot.models;

/**
 * Created by pawansingh on 21/05/18.
 */

public class MessageEvent {
    private boolean update;

    public MessageEvent(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
