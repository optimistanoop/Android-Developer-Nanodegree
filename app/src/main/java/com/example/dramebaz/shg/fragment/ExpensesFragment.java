package com.example.dramebaz.shg.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.adapter.ExpensesAdapter;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.Expense;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Log.e("FAILED get_expenses service_call", "");
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
                openEditExpenseDialog(type,groupOrFrndId);
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

    public void openEditExpenseDialog(final String type, final Integer expenseId) {

        final AlertDialog    d = new AlertDialog.Builder(getContext())
                    .setView(R.layout.add_frnd_expense_dialog)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();



        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                final Dialog f = (Dialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                TextView title = (TextView) f.findViewById(R.id.title);
                TextView disclaimer = (TextView) f.findViewById(R.id.disclaimer);
                title.setText("EDIT EXPENSE");
                disclaimer.setText("*Cost will be shared equally.");
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
                        Map paramsMap = Presenter.getUsersShareMap(getContext(),Integer.parseInt(cost.getText().toString().trim()), type, groupOrFrndId);
                        paramsMap.put("cost", Integer.parseInt(cost.getText().toString().trim()));

                        updateExpense(expenseId,description.getText().toString().trim(), paramsMap);
                        d.dismiss();
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
                    //TODO deleted toast

                } catch (Exception e) {
                    Log.e("FAILED delete_expense", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Log.e("FAILED get_expenses service_call", "");
            }
        }, expenseId);

    }

    public void updateExpense(Integer expenseId,String description,Map params){

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();

        client.updateExpense(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.i("SUCCESS delete_expense", json.toString());
                    //TODO deleted toast

                } catch (Exception e) {
                    Log.e("FAILED delete_expense", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Log.e("FAILED get_expenses service_call", "");
            }
        }, expenseId,description,params);

    }
}
