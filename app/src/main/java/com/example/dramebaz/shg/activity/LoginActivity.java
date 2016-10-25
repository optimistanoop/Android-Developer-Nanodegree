package com.example.dramebaz.shg.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.RestApplication;
import com.example.dramebaz.shg.UserProvider;
import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActionBarActivity<SplitwiseRestClient> implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportLoaderManager().initLoader(1, null, this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        getCurrentUser();
        Intent i = new Intent(this, DashBoardActivity.class);
        startActivity(i);
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
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

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String URL = getResources().getString(R.string.provider_url);
        Uri students = Uri.parse(URL);

        return new CursorLoader(this, students, null, null, null, getResources().getString(R.string._id));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {

        if (c.moveToLast()) {

            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(getResources().getString(R.string.current_user_id), Integer.parseInt(c.getString(c.getColumnIndex(UserProvider.USERID))));
            edit.commit();

            //Firebase analytics - Track User Flows
            Bundle payload = new Bundle();
            payload.putString(getResources().getString(R.string.current_user_id), c.getString(c.getColumnIndex(UserProvider.USERID)));
            mFirebaseAnalytics.logEvent(getResources().getString(R.string.get), payload);
            //Firebase analytics - Track User Flows

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
