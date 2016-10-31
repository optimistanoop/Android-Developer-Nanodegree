package com.example.dramebaz.shg.widget;

/**
 * Created by dramebaz on 31/10/16.
 */

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import com.example.dramebaz.shg.R;

import com.google.firebase.analytics.FirebaseAnalytics;


public class ConfigActivity extends Activity  {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        assignAppWidgetId();

    }

    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        //Firebase analytics - Track User Flows
        Bundle payload = new Bundle();
        payload.putString(getResources().getString(R.string.widgetopen),appWidgetId+"");
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.widgetopen), payload);
        //Firebase analytics - Track User Flows
        startWidget();
    }

    private void startWidget() {

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(Activity.RESULT_OK, intent);

        Intent serviceIntent = new Intent(this, RemoteFetchService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startService(serviceIntent);

        this.finish();

    }

}