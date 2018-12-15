package com.example.administrator.view;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.R;
import com.example.administrator.model.User;
import com.example.administrator.util.JsonAnalyze;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private int stateR = 2;
    private int stateR2= 3;
    private String email = new String();
    private String pwd = new String();
    private String Rpwd = new String();
    private  String icode = new String();
    private String userName = new String();
    private  TextView tMail;
    private  TextView tPwd;
    private  TextView tRpwd;
    private  TextView tName;
    private  TextView tCode;
    private Button geticode;
    private TimeCount time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        TextView titleR = (TextView)findViewById(R.id.title_name);
        titleR.setText("注册账号");
//        Button back = (Button)findViewById(R.id.title_button1);
//        back.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });
        time = new TimeCount(60000, 1000);
       geticode = (Button) findViewById(R.id.geticode_button);
       geticode.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               email = tMail.getText().toString();
               if(email.length()==0)
               {
                   Toast.makeText(RegisterActivity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
               else
               {
                   WorkThread2 sendMessage2 = new RegisterActivity.WorkThread2();
                   sendMessage2.start();
                   try {
                       sendMessage2.join();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   sendMessage2.interrupt();
               }
               time.start();
           }
       });

        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateR = judgeInput();
                if(stateR == 0)
                {   //创建子线程发送请求

                    WorkThread sendMessage = new RegisterActivity.WorkThread();
                    sendMessage.start();
                    try {
                        sendMessage.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendMessage.interrupt();

                    switch (stateR2){
                        case 0 :{
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 1: {
                            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 2: {
                            Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default:{
                            Toast.makeText(RegisterActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        });
    }
    //初始化
    void init()
    {
        tMail = (TextView)findViewById(R.id.email_register);
        tPwd = (TextView)findViewById(R.id.passwordR);
        tRpwd = (TextView)findViewById(R.id.confirm_password);
        tName = (TextView)findViewById(R.id.register_name);
        tCode = (TextView)findViewById(R.id.identify_code);
    }

   //判断输入是否合法
    private int judgeInput()
    {
        email = tMail.getText().toString();
        pwd = tPwd.getText().toString();
        Rpwd = tRpwd.getText().toString();
        userName = tName.getText().toString();
        icode = tCode.getText().toString();
        if(email.length()==0)
        {
            Toast.makeText(RegisterActivity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(pwd.length()==0)
        {
            Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(pwd.length()<6||pwd.length()>16)
        {
            Toast.makeText(RegisterActivity.this,"密码长度不符合规定",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(!pwd.equals(Rpwd))
        {
            Toast.makeText(RegisterActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(userName.length()==0)
        {
            Toast.makeText(RegisterActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(userName.length()>10)
        {
            Toast.makeText(RegisterActivity.this,"昵称过长",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(icode.length()==0)
        {
            Toast.makeText(RegisterActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return 1;
        }
        return 0;
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

                JSONObject userInformation = new JSONObject();
                userInformation.put("user_name",userName);
                userInformation.put("user_pwd", pwd);
                Log.d("Register",pwd);
                userInformation.put("user_mail", email);
                userInformation.put("user_ver",icode);
                User user=new User();
                user.setUser_mail(email);
                user.setUser_pwd(pwd);
                user.setUser_name(userName);
                user.setUser_ver(icode);
                Gson gson = new Gson();
                String json = gson.toJson(user);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, json);
                Log.d("Register",json);
                Request request = new Request.Builder().url("http://115.159.198.216/YibuTest/Register").post(body).build();

                Response response = client.newCall(request).execute();
                String responseData = new String("");
                responseData = response.body().string();
                Log.d("Register",responseData);
                if (response.isSuccessful()) {
                    String answer=JsonAnalyze.getJsonString(responseData);
                    if(answer.equals("Yes"))
                        stateR2 = 0;
                    if(answer.equals("Ver_Wrong"))
                        stateR2 = 1;
                    if(answer.equals("User_Exist"))
                        stateR2 = 2;
                } else {
                    throw new IOException("无法连接到服务器，请检查网络连接");
                }

                //parseJSONWithJSONObject(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private  class WorkThread2 extends Thread
    {
        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();

                JSONObject userInformation = new JSONObject();
                userInformation.put("user_name",null);
                userInformation.put("user_mail", email);
                userInformation.put("user_pwd", null);
                userInformation.put("user_ver",null);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, userInformation.toString());

                Request request = new Request.Builder().url("http://115.159.198.216/YibuTest/Verified").post(body).build();
                Response response = client.newCall(request).execute();

                String responseData = new String("");
                if (response.isSuccessful()) {
                    responseData = response.body().string();
                    Log.d("Register",responseData);
                } else {
                    throw new IOException("无法连接到服务器，请检查网络连接");
                }
                //parseJSONWithJSONObject(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            geticode.setClickable(false);
            geticode.setText("("+millisUntilFinished / 1000 +") ");
        }

        @Override
        public void onFinish() {
            geticode.setText("获取验证码");
            geticode.setClickable(true);

        }
    }
}



