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
import com.example.dramebaz.shg.splitwise.UserExpense;
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
    private static final String ARG_GROUP_FRN_ID = "groupOrFrndId";
    private static final String TYPE = "type";
    private Integer groupOrFrndId;
    private String type;
    private String friendshipId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        view = inflater.inflate(R.layout.fragment_expenses, container, false);
        // Setup handles to view objects here
        // etFoo = (EditText) view.findViewById(R.id.etFoo);

        groupOrFrndId = getArguments().getInt(ARG_GROUP_FRN_ID);
        type = getArguments().getString(TYPE);
        client = RestApplication.getSplitwiseRestClient();
        if(type.equals("group")){
            loadGroupExpenses();
        }else {
            loadFriendExpenses();
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

    private void loadGroupExpenses() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Integer currentUserId =  pref.getInt("currentUserId", 0);
        expenses = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(getActivity(), expenses, currentUserId);

        final ListView lvExpenses = (ListView) view.findViewById(R.id.lvExpenses);
        lvExpenses.setAdapter(expensesAdapter);

        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        Log.i("get_expenses", "groupOrFrndId " + groupOrFrndId);

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
        }, groupOrFrndId, null, null, null);
    }

    private void loadFriendExpenses() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Integer currentUserId =  pref.getInt("currentUserId", 0);
        expenses = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(getActivity(), expenses, currentUserId);

        final ListView lvExpenses = (ListView) view.findViewById(R.id.lvExpenses);
        lvExpenses.setAdapter(expensesAdapter);
        final List<Expense> finalExpenses = new ArrayList<Expense>();
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        Log.i("get_expenses", "groupOrFrndId " + groupOrFrndId);

        client.getExpenses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<Expense> expenses = Expense.fromJSONArray(json.getJSONArray("expenses"));
                    Log.i("get_expenses frnds", expenses.toString());
                    //expensesAdapter.addAll(expenses);
                    for(Expense e :expenses){
                        for( UserExpense u :e.userExpenses){
                            Log.d("anp userexp",u.user.id+" "+groupOrFrndId );

                               if(u.user.id.equals(groupOrFrndId)){
                                   Log.d("anp userexp match",u.user.id+" "+groupOrFrndId );
                                   finalExpenses.add(e);
                                  /* Log.d("anp userexp match",u.user.id+" "+groupOrFrndId );
                                   friendshipId = e.friendshipId;
                                   break;*/
                               }
                        }
                    }
                    // second call
                    expensesAdapter.addAll(finalExpenses);
                    //loadFrinddata();
                    // second call ends here
                } catch (JSONException e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Log.e("FAILED get_expenses service_call", "");
            }
        }, 0, 0, null, null);
    }

/*    public  void loadFrinddata(){
        client.getExpenses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<Expense> expenses = Expense.fromJSONArray(json.getJSONArray("expenses"));
                    Log.i("get_expenses frndshipid", expenses.toString());
                    expensesAdapter.addAll(expenses);

                } catch (JSONException e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Log.e("FAILED get_expenses service_call", "");
            }
        }, 0, 0, 0, Integer.parseInt(friendshipId));
    }*/
}
