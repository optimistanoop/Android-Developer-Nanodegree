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
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpensesActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private CharSequence mActivityTitle;
    private CharSequence mTitle;
    private String type;
    String name;
    int id;
    String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mActivityTitle = getTitle().toString();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        type = i.getStringExtra(getResources().getString(R.string.type));
        name = i.getStringExtra(getResources().getString(R.string.name));
        id = i.getIntExtra(getResources().getString(R.string.id),0);
        if(type.equals(getResources().getString(R.string.friend).toLowerCase())){
            balance = i.getStringExtra(getResources().getString(R.string.balance_key)).replace("-","");
        }
        loadExpenseFrag(id, type,name);

    }

    public void noExpDataWarning(View v){
        openAddExpenseDialog(type);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(type.equals(getResources().getString(R.string.group).toLowerCase())){
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
                return true;
            case R.id.miCalculate:
                calculateInterest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAddGrpMemberDialog() {
        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, getResources().getString(R.string.add_frn_to_grp));
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.click), payload);
        //Firebase analytics - Track User Flows
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
                title.setText(getResources().getString(R.string.add_grp_member));
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText name = (EditText) f.findViewById(R.id.username);
                        EditText email = (EditText) f.findViewById(R.id.email);
                        if (name.getText().toString().trim().equals("")) {
                            name.setError(getResources().getString(R.string.this_is_required));
                            return;
                        } else if (!email.getText().toString().trim().equals("") && !Presenter.isValidEmail(email.getText().toString().trim())) {
                            email.setError(getResources().getString(R.string.not_a_valid_email));
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
        alertDialogBuilder.setMessage(getResources().getString(R.string.r_u_sure_del)+" "+name+" ?");

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(type.equals(getResources().getString(R.string.group).toLowerCase())){
                    deleteGroup(id);
                }else {
                    deleteFriend(id);
                }
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openAddExpenseDialog(final String type) {

        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, getResources().getString(R.string.add_expense));
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.click), payload);
        //Firebase analytics - Track User Flows

        final Integer groupId;
        if(type.equals(getResources().getString(R.string.group).toLowerCase())){
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
                if(type.equals(getResources().getString(R.string.group).toLowerCase())){
                    TextView title = (TextView) f.findViewById(R.id.title);
                    title.setText(getResources().getString(R.string.add_grp_expense));
                    TextView disclaimer = (TextView) f.findViewById(R.id.disclaimer);
                    disclaimer.setText(getResources().getString(R.string.cost_share_grp));
                }
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final EditText cost = (EditText) f.findViewById(R.id.cost);
                        final EditText description = (EditText) f.findViewById(R.id.description);
                        if (cost.getText().toString().trim().equals("") || !(Float.parseFloat(cost.getText().toString().trim())>0)) {
                            cost.setError(getResources().getString(R.string.invalid_no));
                            return;
                        } else if (description.getText().toString().trim().equals("")) {
                            description.setError(getResources().getString(R.string.this_is_required));
                            return;
                        }
                        // before sending any data to add expense , plz make sure for the firend and group members sharing cost equally

                        final Map userShareMap = new LinkedHashMap<>();
                        // get current user id
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        final int currentUserId =  pref.getInt(getResources().getString(R.string.current_user_id), 0);
                        final float costvalue = Float.parseFloat(cost.getText().toString().trim());
                        // check type
                        if(type.equals(getResources().getString(R.string.group).toLowerCase())){
                            // get all members id in case of group
                            SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
                            client.getGroup(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                                    try {
                                        Log.i(getResources().getString(R.string.get_group), json.toString());
                                        // divide cost between all members
                                        // paid share will be of current user id
                                        JSONObject group = json.getJSONObject(getResources().getString(R.string.group).toLowerCase());
                                        JSONArray members = group.getJSONArray(getResources().getString(R.string.members));
                                        int length = members.length();
                                        for(int i=0;i<length;i++){
                                            JSONObject member = members.getJSONObject(i);
                                            if(member.getInt(getResources().getString(R.string.id))!= currentUserId){
                                                userShareMap.put(getResources().getString(R.string.users__0__user_id).replace("0",""+i), member.getInt(getResources().getString(R.string.id)));
                                                userShareMap.put(getResources().getString(R.string.users__0__paid_share).replace("0",""+i), 0);
                                                userShareMap.put(getResources().getString(R.string.users__0__owed_share).replace("0",""+i), costvalue/length);
                                            }else {
                                                userShareMap.put(getResources().getString(R.string.users__0__user_id).replace("0",""+i), member.getInt(getResources().getString(R.string.id)));
                                                userShareMap.put(getResources().getString(R.string.users__0__paid_share).replace("0",""+i), costvalue);
                                                userShareMap.put(getResources().getString(R.string.users__0__owed_share).replace("0",""+i), costvalue/length);
                                            }
                                        }

                                        userShareMap.put(getResources().getString(R.string.cost),costvalue);
                                        userShareMap.put(getResources().getString(R.string.group_id),groupId);
                                        addExpense(description.getText().toString().trim(), userShareMap);
                                        d.dismiss();
                                    } catch (Exception e) {
                                        FirebaseCrash.report(e);
                                        Log.e(getResources().getString(R.string.get_group), getResources().getString(R.string.json_parsing), e);
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, id);

                        }else{
                            userShareMap.put(getResources().getString(R.string.users__0__user_id), currentUserId);
                            userShareMap.put(getResources().getString(R.string.users__0__paid_share), costvalue);
                            userShareMap.put(getResources().getString(R.string.users__0__owed_share), costvalue/2);
                            userShareMap.put(getResources().getString(R.string.users__1__user_id), id);
                            userShareMap.put(getResources().getString(R.string.users__1__paid_share), 0);
                            userShareMap.put(getResources().getString(R.string.users__1__owed_share), costvalue/2);
                            userShareMap.put(getResources().getString(R.string.cost),costvalue);
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
                    Log.i(getResources().getString(R.string.create_expense), json.toString());
                    JSONArray expenses = json.getJSONArray(getResources().getString(R.string.expenses));
                    if(expenses.length()>0){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.expense_added),
                                Toast.LENGTH_SHORT).show();
                        loadExpenseFrag(id, type,name);
                    }else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.create_expense), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
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
                    Log.i(getResources().getString(R.string.add_user_to_group), json.toString());
                    if(json.getBoolean(getResources().getString(R.string.success))){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.grp_mem_added),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.add_user_to_group), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }, group_id,name, email);
    }

    public void deleteFriend(int friendId){

        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, getResources().getString(R.string.delete_friend));
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.click), payload);
        //Firebase analytics - Track User Flows

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.deleteFriend(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i(getResources().getString(R.string.delete_friend), json.toString());
                    if(json.getBoolean(getResources().getString(R.string.success))){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.friend_deleted),
                                Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(ExpensesActivity.this);
                    }else {
                        Toast.makeText(getBaseContext(), json.getString(getResources().getString(R.string.error)),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.delete_friend), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }, friendId);
    }

    public void deleteGroup(int groupId){

        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, getResources().getString(R.string.delete_group));
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.click), payload);
        //Firebase analytics - Track User Flows

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.deleteGroup(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i(getResources().getString(R.string.delete_group), json.toString());
                    if(json.getBoolean(getResources().getString(R.string.success))){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.grp_deleted),
                                Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(ExpensesActivity.this);
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.delete_group), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }, groupId);
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void loadExpenseFrag(Integer id, String type, String name) {
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

    public void calculateInterest(){

        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, getResources().getString(R.string.calc_int));
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.click), payload);
        //Firebase analytics - Track User Flows

        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(R.layout.calc_int_dialog)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                final Dialog f = (Dialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                EditText principle = (EditText)d.findViewById(R.id.principle);
                principle.setText(balance);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText principle = (EditText) f.findViewById(R.id.principle);
                        EditText rate = (EditText) f.findViewById(R.id.rate);
                        EditText time = (EditText) f.findViewById(R.id.time);

                        if (principle.getText().toString().trim().equals("") || !(Float.parseFloat(principle.getText().toString().trim())>0)) {
                            principle.setError(getResources().getString(R.string.invalid_no));
                            return;
                        }else if (rate.getText().toString().trim().equals("") || !(Float.parseFloat(rate.getText().toString().trim())>0)) {
                            rate.setError(getResources().getString(R.string.invalid_no));
                            return;
                        } else if (time.getText().toString().trim().equals("") || !(Float.parseFloat(time.getText().toString().trim())>0)) {
                            time.setError(getResources().getString(R.string.invalid_no));
                            return;
                        }

                        float p = Float.parseFloat(principle.getText().toString().trim());
                        float r = Float.parseFloat(rate.getText().toString().trim());
                        float t = Float.parseFloat(time.getText().toString().trim())/12;
                        float i = p*r*t/100;
                        d.dismiss();
                        showInterest(i, p);
                    }
                });
            }
        });
        d.show();
    }

    public void showInterest(float interest, float principle){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        float total = principle + interest;
        alertDialogBuilder.setMessage("Total interest- "+interest+". \nTotal amount- "+total+".");

        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
