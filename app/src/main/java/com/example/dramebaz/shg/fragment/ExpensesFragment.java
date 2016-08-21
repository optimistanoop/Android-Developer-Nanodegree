package com.example.dramebaz.shg.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.adapter.ExpensesAdapter;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.Expense;
import com.example.dramebaz.shg.splitwise.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpensesFragment extends Fragment {

    ExpensesAdapter expensesAdapter;
    List<Expense> expenses;
    private SplitwiseRestClient client;
    private View view;
    private static final String ARG_GROUP_ID = "group_id";
    private Integer groupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        view = inflater.inflate(R.layout.fragment_expenses, container, false);
        // Setup handles to view objects here
        // etFoo = (EditText) view.findViewById(R.id.etFoo);

        groupId = getArguments().getInt(ARG_GROUP_ID);

        client = RestApplication.getSplitwiseRestClient();
        client.getCurrentUser(new CurrentUserHandler());

        return view;
    }

    public static Fragment newInstance(Integer groupId) {
        Fragment fragment
                = new ExpensesFragment();
        Bundle args = new Bundle();
        if(groupId != null)
            args.putInt(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }


    private class CurrentUserHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
            try {
                Log.i("SUCCESS current_user", json.toString());
                SharedPreferences pref =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = pref.edit();
                User u = User.fromJSONObject(json.getJSONObject("user"));
                edit.putInt("currentUserId", u.id);
                edit.commit();
                loadExpenses();
            } catch (JSONException e) {
                Log.e("FAILED current_user", "json_parsing", e);
            }
        }
    }


    private void loadExpenses() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Integer currentUserId =  pref.getInt("currentUserId", 0);
        expenses = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(getActivity(), expenses, currentUserId);

        final ListView lvExpenses = (ListView) view.findViewById(R.id.lvExpenses);
        lvExpenses.setAdapter(expensesAdapter);

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        Log.i("get_expenses", "group_id " + groupId);
        client.getExpenses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<Expense> expenses = Expense.fromJSONArray(json.getJSONArray("expenses"));
                    Log.i("SUCCESS get_expenses", expenses.toString());
                    expensesAdapter.addAll(expenses);
                    lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        }, groupId, null, null);
    }
}
