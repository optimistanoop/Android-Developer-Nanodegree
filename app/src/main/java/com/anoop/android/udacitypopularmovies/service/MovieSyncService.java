package com.anoop.android.udacitypopularmovies.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.anoop.android.udacitypopularmovies.adapters.SyncAdapter;

public class MovieSyncService extends Service {
    public static final Object sSyncAdapterLock = new Object();
    public static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock)
        {
            if(sSyncAdapter == null)
            {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
