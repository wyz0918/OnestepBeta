package com.example.administrator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.administrator.R;

public class FragmentItemSetsActivity extends AppCompatActivity {

    private HomeActivity fragment1;
    private DiscoveryFragment fragment2;
    private PersonalityFragment fragment3;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    hideFragment(transaction);
                    //fragment1 = new HomeActivity();
                    transaction.show(fragment1);
                    transaction.commit();
                    return true;
                case R.id.navigation_find:
                    Log.d("test1","1111");
                    hideFragment(transaction);
                    // fragment2 = new DiscoveryFragment();
                    transaction.show(fragment2);
                    transaction.commit();
                    return true;
                case R.id.navigation_person:
                     hideFragment(transaction);
                    //fragment3 = new PersonalityFragment();
                    transaction.show(fragment3);
                    transaction.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_itemsets);
        fragment1 = new HomeActivity();
        fragment2 = new DiscoveryFragment();
        fragment3 = new PersonalityFragment();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content,fragment1);
        transaction.add(R.id.content,fragment2);
        transaction.hide(fragment2);
        transaction.add(R.id.content,fragment3);
        transaction.hide(fragment3);
        transaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);//隐藏方法也可以实现同样的效果，不过我一般使用去除
            // transaction.remove(fragment1);
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
            // transaction.remove(fragment2);
        }
        if (fragment3 != null) {
            transaction.hide(fragment3);
            // transaction.remove(fragment3);
        }


    }

}
