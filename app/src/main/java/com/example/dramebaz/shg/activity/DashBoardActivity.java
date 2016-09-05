package com.example.dramebaz.shg.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.FriendsFragment;
import com.example.dramebaz.shg.fragment.GroupsFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        final ActionBar ab = getSupportActionBar();

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.sliding_tabs);
        tabStrip.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miAddGroup:
                openDialog("addAGroup");
                return true;
            case R.id.miAddFriend:
                openDialog("addAFriend");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openDialog(final String type){
        final AlertDialog d;
        if (type.equals("addAFriend")){
             d = new AlertDialog.Builder(this)
                    .setView(R.layout.add_friend_dialog)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

        }else{
            d = new AlertDialog.Builder(this)
                    .setView(R.layout.add_group_dialog)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }


        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Dialog f = (Dialog) dialog;
                        if(type.equals("addAFriend")){
                            EditText name = (EditText) f.findViewById(R.id.username);
                            EditText email = (EditText) f.findViewById(R.id.email);
                            if(name.getText().toString().trim().equals("")){
                                name.setError("This is required"); return;
                            }else if(!email.getText().toString().trim().equals("") && !isValidEmail(email.getText().toString().trim())){
                                email.setError("Not a valid email"); return;
                            }
                            createFriend(email.getText().toString().trim(), name.getText().toString().trim());
                        }else{
                            EditText groupName = (EditText)f.findViewById(R.id.groupname);
                            if(groupName.getText().toString().trim().equals("")){
                                groupName.setError("This is required"); return;
                            }
                            createGroup(groupName.getText().toString().trim());
                        }

                        d.dismiss();
                    }
                });
            }
        });
        d.show();
    }

    public void createGroup(String name){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.createGroup(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("Got createGroup", json.toString());
                    //TODO group created toast

                } catch (Exception e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }, name);
    }

    public void createFriend(String user_email, String user_first_name){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.createFriend(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("Got createFriend", json.toString());
                    //TODO friend added toast

                } catch (Exception e) {
                    Log.e("FAILED createFriend", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }, user_email,  user_first_name);
    }

        public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private String tabTitles[] = { "Friends", "Groups" };

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return FriendsFragment.newInstance(0);
            }else {
                return GroupsFragment.newInstance(1);
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
