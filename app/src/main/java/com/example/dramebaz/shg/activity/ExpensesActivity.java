package com.example.dramebaz.shg.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.ExpensesFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

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

                        EditText cost = (EditText) f.findViewById(R.id.cost);
                        EditText description = (EditText) f.findViewById(R.id.description);
                        if (cost.getText().toString().trim().equals("") || !(Integer.parseInt(cost.getText().toString().trim())>0)) {
                            cost.setError("Invalid no.");
                            return;
                        } else if (description.getText().toString().trim().equals("")) {
                            description.setError("This is required");
                            return;
                        }
                        // before sending any data to add expense , plz make sure for the firend and group members sharing cost equally
                        Map userShareMap = Presenter.getUsersShareMap(getBaseContext(),Integer.parseInt(cost.getText().toString().trim()), type, id);
                        userShareMap.put("cost",Integer.parseInt(cost.getText().toString().trim()));
                        userShareMap.put("group_id",groupId);
                        addExpense(description.getText().toString().trim(), userShareMap);
                        d.dismiss();
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
                    //TODO expense added
                } catch (Exception e) {
                    Log.e("FAILED create_expense", "json_parsing", e);
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
                    //TODO group member added

                } catch (Exception e) {
                    Log.e("FAILED addGrpMember", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
                    // TODO friend deleted

                } catch (Exception e) {
                    Log.e("FAILED delete_friend", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
                    // TODO group deleted

                } catch (Exception e) {
                    Log.e("FAILED delete_group", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
