package com.example.administrator.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.administrator.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalityFragment extends Fragment {

    private String[] data={"我的心愿单","我的分享","我的收藏","设置"};

    public PersonalityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personality,container,false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,data);
        ListView listView= view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        return view;
    }


}
