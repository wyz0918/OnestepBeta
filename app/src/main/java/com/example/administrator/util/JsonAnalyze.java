package com.example.administrator.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonAnalyze {

    public static String getJsonString(String jsonData)
    {
        String answer = new String();
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            answer = jsonObject.getString("res");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  answer;
    }
}
