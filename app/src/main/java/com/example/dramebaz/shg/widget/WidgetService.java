package com.example.dramebaz.shg.widget;

/**
 * Created by dramebaz on 31/10/16.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return (new ListProvider(this.getApplicationContext(), intent));
    }

}