package com.example.dramebaz.shg.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.adapter.GroupsSideBarAdapter;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.ExpensesFragment;
import com.example.dramebaz.shg.splitwise.Group;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpensesActivity extends AppCompatActivity {

    private ListView lvGroupList;
    private LinearLayout llLeftDrawer;
    private GroupsSideBarAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private CharSequence mActivityTitle;
    private CharSequence mTitle;
    private SplitwiseRestClient client;
    private final List<Group> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        client = RestApplication.getSplitwiseRestClient();

        lvGroupList = (ListView)findViewById(R.id.group_list);
        llLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expenses, menu);
        return true;
    }

    private void addDrawerItems() {

        TextView tvAllExpenses = (TextView) findViewById(R.id.all_expenses);
        tvAllExpenses.setText("All Expenses");
        tvAllExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroup(null);
            }
        });

        mAdapter = new GroupsSideBarAdapter(this, groups);
        lvGroupList.setAdapter(mAdapter);

        lvGroupList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGroup(position);
            }
        });

        client.getGroups(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    mAdapter.addAll(Group.fromJSONArray(json.getJSONArray("groups")));
                    selectGroup(null);

                } catch (Exception e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                llLeftDrawer.bringToFront();
                mDrawerLayout.requestLayout();
                super.onDrawerOpened(drawerView);
		        getSupportActionBar().setTitle(R.string.menu);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectGroup(Integer position) {
        // update the main content by replacing fragment

        Fragment fragment = null;
        if(position != null) {
            setTitle(groups.get(position).name);
            fragment = ExpensesFragment.newInstance(groups.get(position).id);
        }
        else {
            setTitle(mActivityTitle);
            fragment = ExpensesFragment.newInstance(null);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        mDrawerLayout.closeDrawer(llLeftDrawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
