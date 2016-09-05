package com.example.dramebaz.shg.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.ExpensesFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpensesActivity extends AppCompatActivity {

    private CharSequence mActivityTitle;
    private CharSequence mTitle;
    private String type;
    String name;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mActivityTitle = getTitle().toString();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        type = i.getStringExtra("type");
        name = i.getStringExtra("name");
        id = i.getIntExtra("id",0);
        loadExpense(id, type,name);

    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(type.equals("group")){
            getMenuInflater().inflate(R.menu.groupmenu, menu);
        }else {
            getMenuInflater().inflate(R.menu.frndmenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return  true;
            case R.id.addFrndToGrp:
                openAddGrpMemberDialog();
                return true;
            case R.id.deleteFrnd:
                openDeleteDialog();
                return true;
            case R.id.deleteGroup:
                openDeleteDialog();
                return true;
            case R.id.addExpense:
                openAddExpenseDialog(type);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAddGrpMemberDialog() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(R.layout.add_friend_dialog)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                final Dialog f = (Dialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                TextView title = (TextView) f.findViewById(R.id.title);
                title.setText("Add A GROUP MEMBER");
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText name = (EditText) f.findViewById(R.id.username);
                        EditText email = (EditText) f.findViewById(R.id.email);
                        if (name.getText().toString().trim().equals("")) {
                            name.setError("This is required");
                            return;
                        } else if (!email.getText().toString().trim().equals("") && !Presenter.isValidEmail(email.getText().toString().trim())) {
                            email.setError("Not a valid email");
                            return;
                        }
                        addGroupMember(id,name.getText().toString().trim(),email.getText().toString().trim());
                        d.dismiss();
                    }
                });
            }
        });
        d.show();
    }

    public void openDeleteDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to delete "+name+" ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(type.equals("group")){
                    deleteGroup(id);
                }else {
                    deleteFriend(id);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openAddExpenseDialog(final String type) {

        final Integer groupId;
        if(type.equals("group")){
            groupId = id;
        }else {
            groupId = null;
        }
        final AlertDialog d = new AlertDialog.Builder(this)
                    .setView(R.layout.add_expense_dialog)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                final Dialog f = (Dialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                if(type.equals("group")){
                    TextView title = (TextView) f.findViewById(R.id.title);
                    title.setText("ADD GROUP EXPENSE");
                    TextView disclaimer = (TextView) f.findViewById(R.id.disclaimer);
                    disclaimer.setText("*Cost will be shared equally across group.");
                }
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final EditText cost = (EditText) f.findViewById(R.id.cost);
                        final EditText description = (EditText) f.findViewById(R.id.description);
                        if (cost.getText().toString().trim().equals("") || !(Float.parseFloat(cost.getText().toString().trim())>0)) {
                            cost.setError("Invalid no.");
                            return;
                        } else if (description.getText().toString().trim().equals("")) {
                            description.setError("This is required");
                            return;
                        }
                        // before sending any data to add expense , plz make sure for the firend and group members sharing cost equally

                        final Map userShareMap = new LinkedHashMap<>();
                        // get current user id
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        final int currentUserId =  pref.getInt("currentUserId", 0);
                        final float costvalue = Float.parseFloat(cost.getText().toString().trim());
                        // check type
                        if(type.equals("group")){
                            // get all members id in case of group
                            SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
                            client.getGroup(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                                    try {
                                        Log.i("get_group", json.toString());
                                        // divide cost between all members
                                        // paid share will be of current user id
                                        JSONObject group = json.getJSONObject("group");
                                        JSONArray members = group.getJSONArray("members");
                                        int length = members.length();
                                        for(int i=0;i<length;i++){
                                            JSONObject member = members.getJSONObject(i);
                                            if(member.getInt("id")!= currentUserId){
                                                userShareMap.put("users__"+i+"__user_id", member.getInt("id"));
                                                userShareMap.put("users__"+i+"__paid_share", 0);
                                                userShareMap.put("users__"+i+"__owed_share", costvalue/length);
                                            }else {
                                                userShareMap.put("users__"+i+"__user_id", member.getInt("id"));
                                                userShareMap.put("users__"+i+"__paid_share", costvalue);
                                                userShareMap.put("users__"+i+"__owed_share", costvalue/length);
                                            }
                                        }

                                        userShareMap.put("cost",costvalue);
                                        userShareMap.put("group_id",groupId);
                                        addExpense(description.getText().toString().trim(), userShareMap);
                                        d.dismiss();
                                    } catch (Exception e) {
                                        Log.e("FAILED get_group", "json_parsing", e);
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            }, id);

                        }else{
                            userShareMap.put("users__0__user_id", currentUserId);
                            userShareMap.put("users__0__paid_share", cost);
                            userShareMap.put("users__0__owed_share", costvalue/2);
                            userShareMap.put("users__1__user_id", id);
                            userShareMap.put("users__1__paid_share", 0);
                            userShareMap.put("users__1__owed_share", costvalue/2);
                            userShareMap.put("cost",costvalue);
                            addExpense(description.getText().toString().trim(), userShareMap);
                            d.dismiss();
                        }

                    }
                });
            }
        });
        d.show();
    }


    public void addExpense(String description, Map userShareMap){
        // redirect to the expense activity
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.createExpense(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("create_expense", json.toString());
                    JSONArray expenses = json.getJSONArray("expenses");
                    if(expenses.length()>0){
                        Toast.makeText(getBaseContext(), "Expense added.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("FAILED create_expense", "json_parsing", e);
                    Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        },description, userShareMap);
    }

    public void addGroupMember(int group_id,String name,String email){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.addGroupMember(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("add_user_to_group", json.toString());
                    if(json.getBoolean("success")){
                        Toast.makeText(getBaseContext(), "Group member added.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("FAILED addGrpMember", "json_parsing", e);
                    Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, group_id,name, email);
    }

    public void deleteFriend(int friendId){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.deleteFriend(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("delete_friend", json.toString());
                    if(json.getBoolean("success")){
                        Toast.makeText(getBaseContext(), "Friend deleted.",
                                Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(ExpensesActivity.this);
                    }else {
                        Toast.makeText(getBaseContext(), json.getString("error"),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("FAILED delete_friend", "json_parsing", e);
                    Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, friendId);
    }

    public void deleteGroup(int groupId){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.deleteGroup(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("delete_group", json.toString());
                    if(json.getBoolean("success")){
                        Toast.makeText(getBaseContext(), "Group deleted.",
                                Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(ExpensesActivity.this);
                    }

                } catch (Exception e) {
                    Log.e("FAILED delete_group", "json_parsing", e);
                    Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, groupId);
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
}
