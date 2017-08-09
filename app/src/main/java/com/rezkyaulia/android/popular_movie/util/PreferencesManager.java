package com.rezkyaulia.android.popular_movie.util;

import android.content.Context;

import com.securepreferences.SecurePreferences;

/**
 * Created by Shiburagi on 09/07/2016.
 */
public class PreferencesManager {

    private static final String CURRENT_VERSION = "CURRENT_VERSION";


    private static PreferencesManager instance;
    private final Context context;
    private final SecurePreferences preference;

    public static PreferencesManager init(Context context) {
        instance = new PreferencesManager(context);

        return instance;
    }

    public static PreferencesManager getInstance() {
        return instance;
    }

    PreferencesManager(Context context) {
        this.context = context;
        preference = new SecurePreferences(context);

    }




    public void setCurrentVersion(int version) {
        SecurePreferences.Editor editor = preference.edit();
        editor.putInt(CURRENT_VERSION, version);
        editor.apply();
    }

    public int getCurrentVersion() {
        return preference.getInt(CURRENT_VERSION, 1);
    }


}
