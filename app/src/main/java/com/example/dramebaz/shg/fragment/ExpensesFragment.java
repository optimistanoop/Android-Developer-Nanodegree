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
import com.google.firebase.crash.FirebaseCrash;
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
        if(type.equals( getResources().getString(R.string.group).toLowerCase())){
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
                if(type.equals( getResources().getString(R.string.group).toLowerCase())){
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
        Integer currentUserId =  pref.getInt( getResources().getString(R.string.current_user_id), 0);
        expenses = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(getActivity(), expenses, currentUserId);

        final ListView lvExpenses = (ListView) view.findViewById(R.id.lvExpenses);
        lvExpenses.setAdapter(expensesAdapter);

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();

        client.getExpenses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<Expense> expenses = Expense.fromJSONArray(json.getJSONArray( getResources().getString(R.string.expenses)));
                    Log.i( getResources().getString(R.string.get_expenses), json.toString());
                    List<Expense> requiredExp = new ArrayList<Expense>();
                    for(Expense e :expenses){
                        if(e.deleted_at.equals("null")){
                            requiredExp.add(e);
                        }
                    }
                    Button noExpDataWarning = (Button) getActivity().findViewById(R.id.noExpDataWarning);
                    if(requiredExp.size()== 0){
                        noExpDataWarning.setVisibility(View.VISIBLE);
                    }else {
                        noExpDataWarning.setVisibility(View.INVISIBLE);
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
                            openChooseActionDialog(expense);
                            return false;
                        }
                    });
                } catch (JSONException e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.get_expenses), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }, groupId, null, null, friendId);
    }

    public void openChooseActionDialog(final Expense expense){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getResources().getString(R.string.choose_action_exp));

        alertDialogBuilder.setPositiveButton( getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.edit),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openEditExpenseDialog(type,expense);
            }
        });
        alertDialogBuilder.setNeutralButton(getResources().getString(R.string.delete),new DialogInterface.OnClickListener() {
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
                title.setText(getResources().getString(R.string.edit_expense));
                EditText cost = (EditText) f.findViewById(R.id.cost);
                EditText description = (EditText) f.findViewById(R.id.description);
                cost.setText(expense.cost);
                description.setText(expense.description);
                if (type.equals( getResources().getString(R.string.group).toLowerCase())){
                    TextView disclaimer = (TextView) f.findViewById(R.id.disclaimer);
                    disclaimer.setText( getResources().getString(R.string.cost_share_grp));
                }

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText cost = (EditText) f.findViewById(R.id.cost);
                        final EditText description = (EditText) f.findViewById(R.id.description);
                        if (cost.getText().toString().trim().equals("") || !(Float.parseFloat(cost.getText().toString().trim())>0)) {
                            cost.setError( getResources().getString(R.string.invalid_no));
                            return;
                        } else if (description.getText().toString().trim().equals("")) {
                            description.setError( getResources().getString(R.string.this_is_required));
                            return;
                        }
                        // before sending any data to add expense , plz make sure for the firend and group members sharing cost equally

                        final Map userShareMap = new LinkedHashMap<>();
                        // get current user id
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getContext());
                        final int currentUserId =  pref.getInt( getResources().getString(R.string.current_user_id), 0);
                        final float costvalue = Float.parseFloat(cost.getText().toString().trim());
                        // check type
                        if(type.equals( getResources().getString(R.string.group).toLowerCase())){
                            // get all members id in case of group
                            SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
                            client.getGroup(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                                    try {
                                        Log.i( getResources().getString(R.string.get_group), json.toString());
                                        // divide cost between all members
                                        // paid share will be of current user id
                                        JSONObject group = json.getJSONObject( getResources().getString(R.string.group).toLowerCase());
                                        JSONArray members = group.getJSONArray( getResources().getString(R.string.members));
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

                                        userShareMap.put( getResources().getString(R.string.cost),costvalue);
                                        userShareMap.put( getResources().getString(R.string.group_id),groupOrFrndId);
                                        userShareMap.put( getResources().getString(R.string.id), expense.id);
                                        updateExpense(expense.id,description.getText().toString().trim(), userShareMap);
                                        d.dismiss();
                                    } catch (Exception e) {
                                        FirebaseCrash.report(e);
                                        Log.e(getResources().getString(R.string.get_group), getResources().getString(R.string.json_parsing), e);
                                        Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, groupOrFrndId);

                        }else{
                            userShareMap.put(getResources().getString(R.string.users__0__user_id), currentUserId);
                            userShareMap.put(getResources().getString(R.string.users__0__paid_share), costvalue);
                            userShareMap.put(getResources().getString(R.string.users__0__owed_share), costvalue/2);
                            userShareMap.put(getResources().getString(R.string.users__1__user_id), groupOrFrndId);
                            userShareMap.put(getResources().getString(R.string.users__1__paid_share), 0);
                            userShareMap.put(getResources().getString(R.string.users__1__owed_share), costvalue/2);
                            userShareMap.put(getResources().getString(R.string.id), expense.id);
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
        alertDialogBuilder.setMessage(getResources().getString(R.string.confirm_del_exp));

        alertDialogBuilder.setPositiveButton( getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteExpense(expenseId);
            }
        });

        alertDialogBuilder.setNegativeButton( getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
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
                    Log.i( getResources().getString(R.string.delete_expense), json.toString());
                    if(json.getBoolean( getResources().getString(R.string.success))){
                        Toast.makeText(getActivity(), getResources().getString(R.string.exp_updated),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.delete_expense), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
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
                    Log.i( getResources().getString(R.string.update_expense), json.toString());
                    JSONArray expenses = json.getJSONArray( getResources().getString(R.string.expenses));
                    if(expenses.length()>0){
                        Toast.makeText(getActivity(), getResources().getString(R.string.exp_updated),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.update_expense), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }, expenseId,description,params);

    }
}
