package com.example.dramebaz.shg.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dramebaz.shg.FriendsAdapter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.GroupMember;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TotalBalance extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private ArrayAdapter<GroupMember> friendAdapter;
    private List<GroupMember> friends;
    private int mPage;
    private SplitwiseRestClient client;
    private ListView lvFriends;
    private SwipeRefreshLayout swipeContainer;

    public TotalBalance() {
        // Required empty public constructor
    }

    private String title;

    public static TotalBalance newInstance(int page) {
        TotalBalance fragment = new TotalBalance();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.balance_per_contact, container, false);
        client = RestApplication.getSplitwiseRestClient();
        friends = new ArrayList<>();
        friendAdapter = new FriendsAdapter(getContext(), friends);
        lvFriends = (ListView) view.findViewById(R.id.lvBalanceAll);
        lvFriends.setAdapter(friendAdapter);
        getFriendsList();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getFriendsList();
            }
        });

        return view;
    }


    private void getFriendsList(){
        client.getFriends(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    friendAdapter.clear();
                    Log.i("Got something", json.toString());
                    friends = GroupMember.fromJSONArray(json.getJSONArray("friends"));
                    Log.i("SUCCESS get_friends", friends.toString());
                    for (int i = 0; i<friends.size();i++){
                        GroupMember friend = friends.get(i);
                        if (mPage == 1 && friend.balance.amount != null && Double.valueOf(friend.balance.amount) < 0) {
                            friendAdapter.add(friend);
                        }
                        if (mPage == 2 && friend.balance.amount != null && Double.valueOf(friend.balance.amount) > 0){
                            friendAdapter.add(friend);
                        }
                        if(mPage == 0) {
                            friendAdapter.add(friend);
                        }

                    }
                    swipeContainer.setRefreshing(false);
                    friendAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e("FAILED get_expenses", "json_parsing", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

}
