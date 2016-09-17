package com.example.dramebaz.shg.widget;

/**
 * Created by dramebaz on 18/9/16.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.activity.LoginActivity;

public class SHGWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.shg_widget);
            Intent configIntent = new Intent(context, LoginActivity.class);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.wiButton, configPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }
}