package com.example.administrator.util;

public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}