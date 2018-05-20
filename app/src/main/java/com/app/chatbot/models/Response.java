package com.app.chatbot.models;

/**
 * Created by pawansingh on 20/05/18.
 */

public class Response {
    private int success;
    private String errorMessage;
    private Message message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", message=" + message +
                '}';
    }
}
