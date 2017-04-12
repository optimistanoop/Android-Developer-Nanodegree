package com.example.dramebaz.shg;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.example.dramebaz.shg.client.SplitwiseRestClient;

public class RestApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		RestApplication.context = this;
		ActiveAndroid.initialize(this);

	}

    public static SplitwiseRestClient getSplitwiseRestClient() {
        return (SplitwiseRestClient) SplitwiseRestClient.getInstance(SplitwiseRestClient.class, RestApplication.context);
    }

}