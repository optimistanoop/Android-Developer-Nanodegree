package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Pair;
import com.example.dramebaz.myapplication.backend.myApi.MyApi;
import com.example.viewjokesandroid.ViewJokesActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by dramebaz on 23/7/16.
 */
public class EndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {

        private static MyApi myApiService = null;
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://backend-1381.appspot.com/_ah/api/");

                // end options for devappserver

                myApiService = builder.build();
            }

            context = params[0].first;
            try {
                return myApiService.getJoke().execute().getData();


            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(context, ViewJokesActivity.class);
            intent.putExtra("JOKE", result);
            context.startActivity(intent);

        }
    }
