package com.example.dramebaz.shg.widget;

/**
 * Created by dramebaz on 31/10/16.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.GroupMember;
import com.google.firebase.crash.FirebaseCrash;
import com.loopj.android.http.JsonHttpResponseHandler;


public class RemoteFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static ArrayList<ListItem> listItemList;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        getFriendsList();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getFriendsList(){
        SplitwiseRestClient client= RestApplication.getSplitwiseRestClient();
        client.getFriends(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    List<GroupMember> friends = new ArrayList<>();
                    Log.i(getResources().getString(R.string.get_friends), json.toString());
                    friends = GroupMember.fromJSONArray(json.getJSONArray(getResources().getString(R.string.friends)));

                    Log.i(getResources().getString(R.string.get_friends), friends.toString());

                    processResult(friends);

                } catch (JSONException e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.get_friends), getResources().getString(R.string.json_parsing), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processResult(List<GroupMember> friends) {
        listItemList = new ArrayList<ListItem>();
        for (int i = 0; i<friends.size();i++){
            GroupMember friend = friends.get(i);
            ListItem listItem = new ListItem();
            listItem.name = friend.user.firstName;
            listItem.balanceText = Presenter.getBalanceText(friend.balance.amount);
            listItem.amount = Presenter.getBalanceString(friend.balance);
            listItemList.add(listItem);

        }

        populateWidget();
    }

    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(SHGWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
}