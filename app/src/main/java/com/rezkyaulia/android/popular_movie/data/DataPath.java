package com.rezkyaulia.android.popular_movie.data;

import android.net.Uri;

import com.rezkyaulia.android.popular_movie.BuildConfig;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class DataPath {
    private static DataPath mInstance;
    // Step 1: private static variable of INSTANCE variable
    private static volatile DataPath INSTANCE;

    // Step 2: private constructor
    private DataPath() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static DataPath getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (DataPath.class) {
                if(null == INSTANCE){
                    INSTANCE = new DataPath();
                }
            }
        }
        return INSTANCE;
    }

    public final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "MOVIE";
    public static final String PATH_GENRE = "GENRE";
    public static final String PATH_MOVIE_GENRE = "GENRE_MOVIE";
    public static final String PATH_FAVORITE = "FAVORITE";

}
