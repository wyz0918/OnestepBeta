package com.example.administrator.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.R;
import com.example.administrator.util.fenxiangkuang;

import java.util.List;

public class fenxiangkuangAdapter extends RecyclerView.Adapter<fenxiangkuangAdapter.ViewHolder> {
    private List<fenxiangkuang> mtest1List;
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView touxiang;
        ImageView dianzan;
        ImageView pinglun;
        ImageView shoucan;
        ImageView ditu;
        TextView address;
        TextView name;
        TextView time;
        TextView dianzanshu;
        TextView pinglunshu;

        public ViewHolder(View view) {
            super(view);
            ditu = (ImageView) view.findViewById(R.id.findtupian);
            touxiang = (ImageView) view.findViewById(R.id.touxiang);
            dianzan = (ImageView) view.findViewById(R.id.dianzan);
            pinglun = (ImageView) view.findViewById(R.id.pinglun);
            shoucan = (ImageView) view.findViewById(R.id.shoucan);
            address = (TextView) view.findViewById(R.id.address);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            dianzanshu = (TextView) view.findViewById(R.id.dianzanshu);
            pinglunshu = (TextView) view.findViewById(R.id.pinglunshu);
        }
    }

    public fenxiangkuangAdapter(List<fenxiangkuang>test1List){
        mtest1List=test1List;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sharing_templet,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        fenxiangkuang fenxiangkuang=mtest1List.get(position);
        holder.touxiang.setImageResource(fenxiangkuang.getTouxiangid());
        holder.dianzan.setImageResource(fenxiangkuang.getDianzanid());
        holder.shoucan.setImageResource(fenxiangkuang.getShoucanid());
        holder.pinglun.setImageResource(fenxiangkuang.getPinglunid());
        holder.ditu.setImageResource(fenxiangkuang.getDituid());
        holder.name.setText(fenxiangkuang.getName());
        holder.address.setText(fenxiangkuang.getAddress());
        holder.dianzanshu.setText(fenxiangkuang.getDianzan());
        holder.pinglunshu.setText(fenxiangkuang.getPinglun());
        holder.time.setText(fenxiangkuang.getTime());
    }
    @Override
    public int getItemCount(){
        return mtest1List.size();
    }
}
