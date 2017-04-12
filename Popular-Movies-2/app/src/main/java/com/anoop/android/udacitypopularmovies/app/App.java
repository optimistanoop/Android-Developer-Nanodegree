package com.anoop.android.udacitypopularmovies.app;

import android.app.Application;
import android.util.Log;
import com.anoop.android.udacitypopularmovies.BuildConfig;
import timber.log.Timber;
import static timber.log.Timber.DebugTree;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
        else
        {
            Timber.plant(new ReleaseLoggingTree());
        }
    }

    private static class ReleaseLoggingTree extends DebugTree {

        @Override
        protected boolean isLoggable(int priority) {
            return !(priority == Log.VERBOSE || priority == Log.DEBUG);
        }
    }
}
