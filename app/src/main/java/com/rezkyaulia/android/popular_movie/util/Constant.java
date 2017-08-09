package com.rezkyaulia.android.popular_movie.util;


/**
 * Created by Mutya Nayavashti on 06/11/2016.
 */

public class Constant {

    // Step 1: private static variable of INSTANCE variable
    private static volatile Constant INSTANCE;

    // Step 2: private constructor
    private Constant() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static Constant getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (Constant.class) {
                if(null == INSTANCE){
                    INSTANCE = new Constant();
                }
            }
        }
        return INSTANCE;
    }


    public final String QUERY_POPULAR = "popular";
    public final String QUERY_TOP_RATED = "top_rated";
    public final String QUERY_FAVORITE= "favorite";
    public final String QUERY_NOW_PLAYING= "now_playing";
    public final String QUERY_UPCOMING= "upcoming";
    public final String CATEGORY = "category";
    public final String ID = "id";
    public final String VIDEO = "videos";
    public final String API_KEY = "api_key";
    public final String PAGE = "page";
    public final String YOUTUBE = "vnd.youtube:" ;

    public final int TYPE_MAIN = 1;
    public final int TYPE_SECONDARY = 2;
    public final int TYPE_THIRD = 3;
    public final int TYPE_NULL = 4;





}
