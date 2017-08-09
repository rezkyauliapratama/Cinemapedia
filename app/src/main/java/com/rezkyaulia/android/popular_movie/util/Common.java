package com.rezkyaulia.android.popular_movie.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class Common {

    // Step 1: private static variable of INSTANCE variable
    private static volatile Common INSTANCE;

    // Step 2: private constructor
    private Common() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static Common getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (Common.class) {
                if(null == INSTANCE){
                    INSTANCE = new Common();
                }
            }
        }
        return INSTANCE;
    }


    public Calendar parseDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(simpleDateFormat.parse(date).getTime());
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public int getVersionCode(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null)
            return pInfo.versionCode;
        else
            return 1;
    }
}
