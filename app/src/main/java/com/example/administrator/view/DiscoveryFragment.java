package com.example.administrator.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.administrator.R;
import com.example.administrator.util.fenxiangkuang;
import com.example.administrator.adapter.fenxiangkuangAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends Fragment implements View.OnTouchListener {

    private EditText start_time;
    private EditText end_time;
    private View vkuanti;
    private List<fenxiangkuang> fenxiangkuangList=new ArrayList<>();
    public DiscoveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery,container,false);
        vkuanti=inflater.inflate(R.layout.sharing_templet,container,false);
        inittest1();
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.fenxiangliebiao);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        fenxiangkuangAdapter adapter=new fenxiangkuangAdapter(fenxiangkuangList);
        recyclerView.setAdapter(adapter);

        start_time=(EditText)view.findViewById(R.id.start_time);
        end_time=(EditText)view.findViewById(R.id.end_time);
        start_time.setOnTouchListener(this);
        end_time.setOnTouchListener(this);
        return view;
    }

    private void inittest1(){
            fenxiangkuang a=new fenxiangkuang("旅法师","福州大学","2018.11.27","104","26",R.drawable.fuzhoudaxue,R.drawable.touxianglvfa,R.drawable.dianzan1,R.drawable.pinglun,R.drawable.shoucan);
            fenxiangkuangList.add(a);
            ImageView imageView = (ImageView)vkuanti.findViewById(R.id.findtupian);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),StrategyActivity.class);
                    startActivity(intent);
                }
            });
            fenxiangkuang b=new fenxiangkuang("利威尔兵短","闽江学院","2018.11.25","156","32",R.drawable.malatang,R.drawable.touxiangbinzhang,R.drawable.dianzan2,R.drawable.pinglun,R.drawable.shoucang);
            fenxiangkuangList.add(b);
            fenxiangkuang c=new fenxiangkuang("血大板","师大学生街","2018.11.21","170","46",R.drawable.naicha,R.drawable.longnvpu,R.drawable.dianzan2,R.drawable.pinglun,R.drawable.shoucang);
            fenxiangkuangList.add(c);
    }


    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.timeset, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            builder.setView(view);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            if (v.getId() == R.id.start_time) {
                final int inType = start_time.getInputType();
                start_time.setInputType(InputType.TYPE_NULL);
                start_time.onTouchEvent(event);
                start_time.setInputType(inType);
                start_time.setSelection(start_time.getText().length());

                builder.setTitle("选择起始时间");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        start_time.setText(sb);
                        end_time.requestFocus();
                        dialog.cancel();
                    }

                });
            } else if (v.getId() == R.id.end_time) {
                int inType = end_time.getInputType();
                end_time.setInputType(InputType.TYPE_NULL);
                end_time.onTouchEvent(event);
                end_time.setInputType(inType);
                end_time.setSelection(end_time.getText().length());
                builder.setTitle("选择结束时间");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        end_time.setText(sb);
                        dialog.cancel();
                    }
                });
            }
            Dialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
}
