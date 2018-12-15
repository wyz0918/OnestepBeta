package com.example.administrator.model;
import com.google.gson.annotations.SerializedName;
public class User {
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("user_pwd")
    private String user_pwd;
    @SerializedName("user_mail")
    private String user_mail;
    @SerializedName("user_ver")
    private String user_ver;
    public String getUser_ver() {
        return user_ver;
    }
    public void setUser_ver(String user_ver) {
        this.user_ver = user_ver;
    }
    public String getUser_mail() {
        return user_mail;
    }
    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_pwd() {
        return user_pwd;
    }
    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }
    public User(){
        user_name="";
        user_pwd="";
    }
}

