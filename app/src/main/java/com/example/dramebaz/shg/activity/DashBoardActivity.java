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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.FriendsFragment;
import com.example.dramebaz.shg.fragment.GroupsFragment;
import com.google.firebase.crash.FirebaseCrash;
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

    public void noDataWarning(View v){
        CharSequence noDataButton = ((Button) v).getText();
        if(noDataButton.equals(getResources().getString(R.string.add_friend))){
            openDialog(getResources().getString(R.string.add_friend));
        }else {
            openDialog(getResources().getString(R.string.add_group));
        }
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
                openDialog(getResources().getString(R.string.add_group));
                return true;
            case R.id.miAddFriend:
                openDialog(getResources().getString(R.string.add_friend));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openDialog(final String type){
        final AlertDialog d;
        if (type.equals(getResources().getString(R.string.add_friend))){
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
                        if(type.equals(getResources().getString(R.string.add_friend))){
                            EditText name = (EditText) f.findViewById(R.id.username);
                            EditText email = (EditText) f.findViewById(R.id.email);
                            if(name.getText().toString().trim().equals("")){
                                name.setError(getResources().getString(R.string.this_is_required)); return;
                            }else if(!email.getText().toString().trim().equals("") && !Presenter.isValidEmail(email.getText().toString().trim())){
                                email.setError(getResources().getString(R.string.not_a_valid_email)); return;
                            }
                            createFriend(email.getText().toString().trim(), name.getText().toString().trim());
                        }else{
                            EditText groupName = (EditText)f.findViewById(R.id.groupname);
                            if(groupName.getText().toString().trim().equals("")){
                                groupName.setError(getResources().getString(R.string.this_is_required)); return;
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
                    Log.i(getResources().getString(R.string.create_group), json.toString());
                    JSONObject group = json.getJSONObject(getResources().getString(R.string.group).toLowerCase());
                    if(group.getInt(getResources().getString(R.string.id))>0){
                        Toast.makeText(getBaseContext(), group.getString(getResources().getString(R.string.name))+" Created.",
                                Toast.LENGTH_SHORT).show();
                        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                        viewPager.setCurrentItem(1);
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.create_group), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }, name);
    }

    public void createFriend(String user_email, String user_first_name){
        final SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.createFriend(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i(getResources().getString(R.string.create_friend), json.toString());
                    JSONObject friend = json.getJSONObject(getResources().getString(R.string.friend).toLowerCase());
                    if(friend.getInt(getResources().getString(R.string.id))>0){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.friend_added),
                                Toast.LENGTH_SHORT).show();
                        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                        viewPager.setCurrentItem(0);
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.create_friend), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
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

}
