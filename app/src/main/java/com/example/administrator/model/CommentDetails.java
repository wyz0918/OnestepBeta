package com.example.administrator.model;

import java.io.Serializable;

public class CommentDetails implements Serializable {
    private String pinglunname;
    private String pingluncomment;
    private int pinglunid;
    public CommentDetails(String pinglunname, String pingluncomment, int pinglunid){
        this.pinglunname=pinglunname;
        this.pingluncomment=pingluncomment;
        this.pinglunid=pinglunid;
    }
    public String getPinglunname(){
        return pinglunname;
    }
    public String getPingluncomment(){
        return pingluncomment;
    }
    public int getPinglunid(){
        return pinglunid;
    }
}
