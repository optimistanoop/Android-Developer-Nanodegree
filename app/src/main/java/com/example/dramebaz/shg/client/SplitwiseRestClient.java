package com.example.dramebaz.shg.client;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.example.dramebaz.shg.BuildConfig;
import com.example.dramebaz.shg.R;
import com.google.firebase.crash.FirebaseCrash;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.Api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SplitwiseRestClient extends OAuthBaseClient {
    private static Context context;
    public static final Class<? extends Api> REST_API_CLASS = SplitwiseApi.class;
    public static final String REST_URL = "https://secure.splitwise.com/api/v3.0";
    public static final String REST_CONSUMER_KEY = BuildConfig.REST_CONSUMER_KEY;
    public static final String REST_CONSUMER_SECRET = BuildConfig.REST_CONSUMER_SECRET;
    public static final String REST_CALLBACK_URL ="oauth://codepathtweets";

    public SplitwiseRestClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        this.context= context;
    }

    private static String getApiEndPoint(int id){
        return context.getResources().getString(id);
    }
    /**
     * http://dev.splitwise.com/dokuwiki/doku.php?id=get_current_user
     */
    public void getCurrentUser(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.get_current_user));
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    public void getGroup(JsonHttpResponseHandler handler, Integer groupId) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.get_group)+groupId);
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }
    /**
     * http://dev.splitwise.com/dokuwiki/doku.php?id=get_groups
     * @param handler
     */
    public void getGroups(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.get_groups));
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    /**
     * http://dev.splitwise.com/dokuwiki/doku.php?id=create_group
     */
    public void createGroup(JsonHttpResponseHandler handler, String groupName) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.create_group));
        RequestParams params = new RequestParams();
        if(groupName != null) {
            params.put(getApiEndPoint(R.string.name), groupName);
        }
        getClient().post(apiUrl, params,handler);
    }

    public void createExpense(JsonHttpResponseHandler handler, String description,Map userShareMap) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.create_expense));
        RequestParams params = new RequestParams();

        if(description != null) {
            params.put(getApiEndPoint(R.string.description), description);
        }

        Set<String> keys =userShareMap.keySet();
        List<Integer> values= new LinkedList<>(userShareMap.values());
        int i = 0;
        for(String key:keys){
            params.put(key, values.get(i));
            i++;
        }
        getClient().post(apiUrl, params,handler);
    }

    public void updateExpense(JsonHttpResponseHandler handler,Integer expenseID,String description,Map userShareMap) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.update_expense)+expenseID);
        RequestParams params = new RequestParams();
        if(description != null) {
            params.put(getApiEndPoint(R.string.description), description);
        }
        Set<String> keys =userShareMap.keySet();
        List<Integer> values= new LinkedList<>(userShareMap.values());
        int i = 0;
        for(String key:keys){
            params.put(key, values.get(i));
            i++;
        }
        getClient().post(apiUrl, params,handler);
    }

    public void addGroupMember(JsonHttpResponseHandler handler,Integer group_id,String firstName,String email) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.add_user_to_group));
        RequestParams params = new RequestParams();
        if(group_id != null) {
            params.put(getApiEndPoint(R.string.group_id), group_id);
        }
        if(email != null) {
            params.put(getApiEndPoint(R.string.email), email);
        }
        if(firstName != null) {
            params.put(getApiEndPoint(R.string.first_name), firstName);
        }
        getClient().post(apiUrl, params,handler);
    }

    public void createFriend(JsonHttpResponseHandler handler,String user_email, String user_first_name) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.create_friend));
        RequestParams params = new RequestParams();
        if(user_email != null) {
            params.put(getApiEndPoint(R.string.user_email), user_email);
        }
        if(user_first_name != null) {
            params.put(getApiEndPoint(R.string.user_first_name), user_first_name);
        }
        getClient().post(apiUrl, params,handler);
    }

    /**
     * http://dev.splitwise.com/dokuwiki/doku.php?id=get_expenses
     * @param handler
     */
    public void getExpenses(JsonHttpResponseHandler handler, Integer groupId, Integer limit, Integer offset, Integer friendId) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.get_expenses));
        RequestParams params = new RequestParams();
        if(groupId != null) {
            params.put(getApiEndPoint(R.string.group_id), groupId);
        }
        if(friendId != null) {
            params.put(getApiEndPoint(R.string.friend_id), friendId);
        }
        if(limit != null) {
            params.put(getApiEndPoint(R.string.limit), limit);
        }
        if(offset != null) {
            params.put(getApiEndPoint(R.string.offset), offset);
        }
        getClient().get(apiUrl, params, handler);
    }

    /**
     * http://dev.splitwise.com/dokuwiki/doku.php?id=get_friends
     * @param handler
     */
    public void getFriends(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.get_friends));
        RequestParams params = new RequestParams();
        try {

            getClient().get(apiUrl, params, handler);
        }catch (Exception e){
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
    }

    public void deleteFriend(JsonHttpResponseHandler handler, Integer friendId) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.delete_friend)+friendId);
        getClient().post(apiUrl, handler);
    }

    public void deleteGroup(JsonHttpResponseHandler handler, Integer groupId) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.delete_group)+groupId);
        getClient().post(apiUrl, handler);
    }

    public void deleteExpense(JsonHttpResponseHandler handler, Integer expenseId) {
        String apiUrl = getApiUrl(getApiEndPoint(R.string.delete_expense)+expenseId);
        getClient().post(apiUrl, handler);
    }
}
