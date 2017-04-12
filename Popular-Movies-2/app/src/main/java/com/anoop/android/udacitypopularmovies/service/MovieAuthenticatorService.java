package com.anoop.android.udacitypopularmovies.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.anoop.android.udacitypopularmovies.app.MovieAuthenticator;

public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new MovieAuthenticator(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
