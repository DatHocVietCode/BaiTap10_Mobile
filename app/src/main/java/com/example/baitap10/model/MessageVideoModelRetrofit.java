package com.example.baitap10.model;

import java.io.Serializable;
import java.util.List;

public class MessageVideoModelRetrofit implements Serializable {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public MessageVideoModelRetrofit() {
    }

    public MessageVideoModelRetrofit(boolean success, String message, List<VideoModelRetrofit> result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<VideoModelRetrofit> getResult() {
        return result;
    }

    public void setResult(List<VideoModelRetrofit> result) {
        this.result = result;
    }

    private String message;
    private List<VideoModelRetrofit> result;
}
