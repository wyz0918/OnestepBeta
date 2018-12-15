package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.R;
import com.example.administrator.model.CommentDetails;

import java.util.List;

public class ReviewerAdapter extends ArrayAdapter<CommentDetails> {
    private int resourceid;
    public ReviewerAdapter(Context context, int textViewResourceId, List<CommentDetails>objects){
        super(context,textViewResourceId,objects);
        resourceid=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CommentDetails CommentDetails =getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
        ImageView pinglunzhetouxiang=(ImageView)view.findViewById(R.id.pingluntouxiang);
        TextView pinglunzhename=(TextView)view.findViewById(R.id.guestname);
        TextView pinglunneirong=(TextView)view.findViewById(R.id.comment);
        pinglunzhename.setText(CommentDetails.getPinglunname());
        pinglunzhetouxiang.setImageResource(CommentDetails.getPinglunid());
        pinglunneirong.setText(CommentDetails.getPingluncomment());
        return view;
    }
}
