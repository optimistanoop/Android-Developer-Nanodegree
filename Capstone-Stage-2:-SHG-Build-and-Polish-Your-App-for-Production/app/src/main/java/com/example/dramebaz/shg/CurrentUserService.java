package com.example.dramebaz.shg;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.User;
import com.google.firebase.crash.FirebaseCrash;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by dramebaz on 30/10/16.
 */
public class CurrentUserService extends IntentService {

    Handler mHandler ;

    public CurrentUserService() {
        super("CurrentUserService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getCurrentUser();
            }
        });
    }

    private void getCurrentUser(){
        SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {

                    Log.i(getResources().getString(R.string.get_current_user), json.toString());
                    User user = User.fromJSONObject(json.getJSONObject(getResources().getString(R.string.user)));

                    ContentValues values = new ContentValues();

                    values.put(UserProvider.NAME, user.firstName);

                    values.put(UserProvider.USERID, user.id);

                    Uri uri = getContentResolver().insert( UserProvider.CONTENT_URI, values);


                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    Log.e(getResources().getString(R.string.get_current_user), getResources().getString(R.string.json_parsing), e);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}