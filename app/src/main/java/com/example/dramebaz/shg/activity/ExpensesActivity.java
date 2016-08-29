package com.example.dramebaz.shg.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.ExpensesFragment;

public class ExpensesActivity extends AppCompatActivity {

    private ListView lvGroupList;
    private LinearLayout llLeftDrawer;
    private DrawerLayout mDrawerLayout;
    private CharSequence mActivityTitle;
    private CharSequence mTitle;
    private SplitwiseRestClient client;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        client = RestApplication.getSplitwiseRestClient();

        lvGroupList = (ListView)findViewById(R.id.group_list);
        llLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        type = i.getStringExtra("type");
        String name = i.getStringExtra("name");
        int id = i.getIntExtra("id",0);
        loadExpense(id, type,name);

    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(type.equals("group")){

            getMenuInflater().inflate(R.menu.expensemenugrp, menu);
        }else {
            getMenuInflater().inflate(R.menu.expensemenufrn, menu);
        }
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void loadExpense(Integer id, String type, String name) {
        // update the main content by replacing fragment

        Fragment fragment = null;
        if(id != null) {
            setTitle(name);
            fragment = ExpensesFragment.newInstance(id, type);
        }
        else {
            setTitle(mActivityTitle);
            fragment = ExpensesFragment.newInstance(null, null);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
