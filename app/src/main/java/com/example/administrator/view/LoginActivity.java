package com.example.administrator.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.example.administrator.R;
import com.example.administrator.util.JsonAnalyze;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity{
    private String email = new String("");
    private String password = new String("");
    private int state = 3;
    private Button login;
    private  TextView t1;
    private  TextView t2;
    private CheckBox ck;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView title = (TextView)findViewById(R.id.title_name);
        title.setText("登录账号");
        TextView forGetPwd = (TextView) findViewById(R.id.loginForgetPwd);
        forGetPwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(LoginActivity.this,ForgetPWDActivity.class);
                startActivity(intent);
            }
        });

        login = (Button)findViewById(R.id.sign_in);
        t1 = (TextView) findViewById(R.id.email);
        t2 =(TextView) findViewById(R.id.passwordL);
        ck = (CheckBox) findViewById(R.id.rememberPwd);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

                email = t1.getText().toString();
                password = t2.getText().toString();

                if (t1.getText().length()==0 ||t2.getText().length()==0)
                {
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                 t1.setText(email);
                 if(ck.isChecked())
                 {
                     t2.setText(password);
                 }

                 WorkThread sendMessage = new WorkThread();
                sendMessage.start();
                try {
                    sendMessage.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage.interrupt();

                state = 0;
                switch (state){
                    case 0 :{
                        Intent intent = new Intent(LoginActivity.this,FragmentItemSetsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2: {
                        Toast.makeText(LoginActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:{
                        Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return;
            }
        });

    }


    private  class WorkThread extends Thread
    {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();

                    JSONObject emailAndPwd = new JSONObject();
                    emailAndPwd.put("e-mail", email);
                    emailAndPwd.put("userPwd", password);

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                    RequestBody body = RequestBody.create(JSON, emailAndPwd.toString());

                    Request request = new Request.Builder().url("http://115.159.198.216/YibuTest/Login").post(body).build();
                    Response response = client.newCall(request).execute();

                    String responseData = new String("");
                    if (response.isSuccessful()) {
                        responseData = response.body().string();

                        String answer=JsonAnalyze.getJsonString(responseData);
                        Log.d("Login",answer);
                        if(answer.equals("Yes"))
                            state = 0;
                        else
                            state = 1;
                    } else {
                        state =2 ;
                        throw new IOException("无法连接到服务器，请检查网络连接");
                    }

                    //parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }


}







