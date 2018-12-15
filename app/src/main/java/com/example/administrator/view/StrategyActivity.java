package com.example.administrator.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.administrator.R;
import com.example.administrator.adapter.ReviewerAdapter;
import com.example.administrator.model.CommentDetails;

import java.util.ArrayList;
import java.util.List;

public class StrategyActivity extends AppCompatActivity {
    private List<CommentDetails> commentDetailsList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy);
        initpinglunzhe();
        ReviewerAdapter adapter=new ReviewerAdapter(StrategyActivity.this,R.layout.comment_item, commentDetailsList);
        ListView listView=(ListView)findViewById(R.id.guestlist);
        listView.setAdapter(adapter);
    }
    private void initpinglunzhe(){
        CommentDetails guest1=new CommentDetails("小明","这地方真不错",R.drawable.touxiang1);
        commentDetailsList.add(guest1);
        CommentDetails guest2=new CommentDetails("小王","我觉得不行",R.drawable.touxiang2);
        commentDetailsList.add(guest2);
    }
}
