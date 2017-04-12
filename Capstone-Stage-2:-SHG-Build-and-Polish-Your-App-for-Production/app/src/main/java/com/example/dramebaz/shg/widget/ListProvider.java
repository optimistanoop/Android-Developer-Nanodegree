package com.example.dramebaz.shg.widget;

/**
 * Created by dramebaz on 31/10/16.
 */

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.example.dramebaz.shg.R;

import java.util.ArrayList;

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
    private Context context = null;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        populateListItem();
    }

    private void populateListItem() {
        if(RemoteFetchService.listItemList !=null )
            listItemList = (ArrayList<ListItem>) RemoteFetchService.listItemList
                    .clone();
        else
            listItemList = new ArrayList<ListItem>();

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.wname, listItem.name);
        remoteView.setTextViewText(R.id.wbalancetext, listItem.balanceText);
        remoteView.setTextViewText(R.id.wamount, listItem.amount);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}