package com.rezkyaulia.android.popular_movie.database;

import com.rezkyaulia.android.popular_movie.database.contract.MovieContract;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class ManageDB {

    // Step 1: private static variable of INSTANCE variable
    private static volatile ManageDB INSTANCE;

    // Step 2: private constructor
    private ManageDB() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static ManageDB getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (ManageDB.class) {
                if(null == INSTANCE){
                    INSTANCE = new ManageDB();
                }
            }
        }
        return INSTANCE;
    }




}
