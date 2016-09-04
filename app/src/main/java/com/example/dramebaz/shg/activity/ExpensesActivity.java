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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.fragment.ExpensesFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

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
                addExpenseToGroup();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAddGrpMemberDialog() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(R.layout.add_group_member)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Dialog f = (Dialog) dialog;
                        EditText name = (EditText) f.findViewById(R.id.username);
                        EditText email = (EditText) f.findViewById(R.id.email);
                        if (name.getText().toString().trim().equals("")) {
                            name.setError("This is required");
                            return;
                        } else if (!email.getText().toString().trim().equals("") && !isValidEmail(email.getText().toString().trim())) {
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
                Toast.makeText(ExpensesActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                finish();
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

    public void addExpenseToGroup(){
        // redirect to the expense activity
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.createExpense(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("create_expense", json.toString());

                } catch (Exception e) {
                    Log.e("FAILED create_expense", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }, 100, "mote", 0);
    }

    public void addGroupMember(int group_id,String name,String email){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.addGroupMember(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("add_user_to_group", json.toString());

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

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
