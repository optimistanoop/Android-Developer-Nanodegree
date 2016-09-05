package com.example.dramebaz.shg.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.adapter.ExpensesAdapter;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.Expense;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpensesFragment extends Fragment {

    ExpensesAdapter expensesAdapter;
    List<Expense> expenses;
    private View view;
    private static final String ARG_GROUP_FRN_ID = "groupOrFrndId";
    private static final String TYPE = "type";
    private Integer groupOrFrndId;
    private String type;
    private SwipeRefreshLayout swipeContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        view = inflater.inflate(R.layout.fragment_expenses, container, false);
        // Setup handles to view objects here
        // etFoo = (EditText) view.findViewById(R.id.etFoo);

        groupOrFrndId = getArguments().getInt(ARG_GROUP_FRN_ID);
        type = getArguments().getString(TYPE);
        if(type.equals("group")){
            loadExpenses(groupOrFrndId, null);
        }else {
            loadExpenses(null,groupOrFrndId);
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if(type.equals("group")){
                    loadExpenses(groupOrFrndId, null);
                }else {
                    loadExpenses(null,groupOrFrndId);
                }
            }
        });

        return view;
    }

    public static Fragment newInstance(Integer groupOrFrndId, String type) {
        Fragment fragment
                = new ExpensesFragment();
        Bundle args = new Bundle();
        if(groupOrFrndId != null)
            args.putInt(ARG_GROUP_FRN_ID, groupOrFrndId);
        if(type != null)
            args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadExpenses(Integer groupId, Integer friendId) {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Integer currentUserId =  pref.getInt("currentUserId", 0);
        expenses = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(getActivity(), expenses, currentUserId);

        final ListView lvExpenses = (ListView) view.findViewById(R.id.lvExpenses);
        lvExpenses.setAdapter(expensesAdapter);

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();

        client.getExpenses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<Expense> expenses = Expense.fromJSONArray(json.getJSONArray("expenses"));
                    Log.i("SUCCESS get_expenses", json.toString());
                    List<Expense> requiredExp = new ArrayList<Expense>();
                    for(Expense e :expenses){
                        if(e.deleted_at.equals("null")){
                            requiredExp.add(e);
                        }
                    }
                    expensesAdapter.addAll(requiredExp);
                    expensesAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    // item click listner for edit expense
                    lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
                    // item log click listner for delete expense
                    lvExpenses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Expense expense = (Expense)lvExpenses.getItemAtPosition(i);
                            Log.d("item long clicked", expense.id.toString());
                            openChooseActionDialog(expense);
                            return false;
                        }
                    });
                } catch (JSONException e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                    Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, groupId, null, null, friendId);
    }

    public void openChooseActionDialog(final Expense expense){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Choose Action for expense");

        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        alertDialogBuilder.setNegativeButton("Edit",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openEditExpenseDialog(type,expense);
            }
        });
        alertDialogBuilder.setNeutralButton("Delete",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openDeleteDialog(expense.id);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openEditExpenseDialog(final String type, final Expense expense) {

        final AlertDialog    d = new AlertDialog.Builder(getContext())
                    .setView(R.layout.add_expense_dialog)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();



        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                final Dialog f = (Dialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                TextView title = (TextView) f.findViewById(R.id.title);
                title.setText("EDIT EXPENSE");
                EditText cost = (EditText) f.findViewById(R.id.cost);
                EditText description = (EditText) f.findViewById(R.id.description);
                cost.setText(expense.cost);
                description.setText(expense.description);
                if (type.equals("group")){
                    TextView disclaimer = (TextView) f.findViewById(R.id.disclaimer);
                    disclaimer.setText("*Cost will be shared equally across group.");
                }

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText cost = (EditText) f.findViewById(R.id.cost);
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
                                PreferenceManager.getDefaultSharedPreferences(getContext());
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
                                        userShareMap.put("group_id",groupOrFrndId);
                                        userShareMap.put("id", expense.id);
                                        updateExpense(expense.id,description.getText().toString().trim(), userShareMap);
                                        d.dismiss();
                                    } catch (Exception e) {
                                        Log.e("FAILED get_group", "json_parsing", e);
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            }, groupOrFrndId);

                        }else{
                            userShareMap.put("users__0__user_id", currentUserId);
                            userShareMap.put("users__0__paid_share", costvalue);
                            userShareMap.put("users__0__owed_share", costvalue/2);
                            userShareMap.put("users__1__user_id", groupOrFrndId);
                            userShareMap.put("users__1__paid_share", 0);
                            userShareMap.put("users__1__owed_share", costvalue/2);
                            userShareMap.put("cost",costvalue);
                            userShareMap.put("id", expense.id);
                            updateExpense(expense.id,description.getText().toString().trim(), userShareMap);
                            d.dismiss();
                        }
                    }
                });
            }
        });
        d.show();
    }

    public void openDeleteDialog(final Integer expenseId){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to delete this expense ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteExpense(expenseId);
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

    public void deleteExpense(Integer expenseId){

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();

        client.deleteExpense(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("SUCCESS delete_expense", json.toString());
                    if(json.getBoolean("success")){
                        Toast.makeText(getActivity(), "Expense deleted.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("FAILED delete_expense", "json_parsing", e);
                    Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, expenseId);

    }

    public void updateExpense(Integer expenseId,String description,Map params){

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();

        client.updateExpense(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("SUCCESS update_expense", json.toString());
                    JSONArray expenses = json.getJSONArray("expenses");
                    if(expenses.length()>0){
                        Toast.makeText(getActivity(), "Expense updated.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("FAILED update_expense", "json_parsing", e);
                    Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), "Unexpected error occurred! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }, expenseId,description,params);

    }
}
