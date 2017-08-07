package com.rezkyaulia.android.popular_movie.util;

/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class ImageSize {
    // Step 1: private static variable of INSTANCE variable
    private static volatile ImageSize INSTANCE;

    // Step 2: private constructor
    private ImageSize() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static ImageSize getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (ImageSize.class) {
                if(null == INSTANCE){
                    INSTANCE = new ImageSize();
                }
            }
        }
        return INSTANCE;
    }

    public final String ORI = "original";
    public final String SMALL = "w185";
    public final String MEDIUM = "w342";
    public final String NORMAL = "w500";
    public final String LARGE = "w780";

}
