package com.rezkyaulia.android.popular_movie;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.app.infideap.stylishwidget.view.Stylish;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.util.PreferencesManager;

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
        PreferencesManager.init(this);

        String fontFolder = "fonts/Exo_2/Exo2-";
        Stylish.getInstance().set(
                fontFolder.concat("Regular.ttf"),
                fontFolder.concat("Medium.ttf"),
                fontFolder.concat("RegularItalic.ttf")
        );

        Stylish.getInstance().setFontScale(1.0f);

    }
}
