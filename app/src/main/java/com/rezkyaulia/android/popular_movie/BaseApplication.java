package com.rezkyaulia.android.popular_movie;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 6/30/2017.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        AndroidNetworking.initialize(getApplicationContext());
    }
}
