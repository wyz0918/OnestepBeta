package com.example.administrator.util;

public class fenxiangkuang {
    private String name;
    private String address;
    private String time;
    private String dianzan;
    private String pinglun;
    private int dituid;
    private int touxiangid;
    private int dianzanid;
    private int pinglunid;
    private int shoucanid;

    public fenxiangkuang(String name, String address, String time, String dianzan, String pinglun, int dituid, int touxiangid, int dianzanid, int pinglunid, int shoucanid) {
        this.name=name;
        this.address=address;
        this.time=time;
        this.dianzan=dianzan;
        this.pinglun=pinglun;
        this.dituid=dituid;
        this.touxiangid=touxiangid;
        this.dianzanid=dianzanid;
        this.pinglunid=pinglunid;
        this.shoucanid=shoucanid;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public String getTime(){
        return time;
    }
    public String getDianzan(){
        return dianzan;
    }
    public String getPinglun(){
        return pinglun;
    }
    public int getDituid(){
        return dituid;
    }
    public int getTouxiangid(){
        return touxiangid;
    }
    public int getDianzanid(){
        return dianzanid;
    }
    public int getPinglunid(){
        return pinglunid;
    }
    public int getShoucanid(){
        return shoucanid;
    }
}
