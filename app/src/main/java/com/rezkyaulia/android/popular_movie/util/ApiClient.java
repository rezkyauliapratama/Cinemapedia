package com.rezkyaulia.android.popular_movie.util;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.rezkyaulia.android.popular_movie.BuildConfig;
import com.rezkyaulia.android.popular_movie.model.ApiResponse;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class ApiClient {
    private OnFetchDataListener mListener;
    String apiKey = BuildConfig.MOVIE_API_KEY;

    private static ApiClient mInstance;
    // Step 1: private static variable of INSTANCE variable
    private static volatile ApiClient INSTANCE;

    // Step 2: private constructor
    private ApiClient() {

    }

    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static ApiClient getInstance() {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (ApiClient.class) {
                if(null == INSTANCE){
                    INSTANCE = new ApiClient();
                }
            }
        }
        return INSTANCE;
    }

    private final String URL = "http://api.themoviedb.org/3/movie/";
    public final String URL_IMAGE = "https://image.tmdb.org/t/p/";
    public void getList(final String query, final OnFetchDataListener listener){
        mListener = listener;
        AndroidNetworking.get(URL.concat("{".concat(Constant.getInstance().CATEGORY).concat("}")))
                .addPathParameter(Constant.getInstance().CATEGORY,query)
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ApiResponse.class,new ParsedRequestListener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        // do anything with response
                        mListener.OnResponse(response);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        mListener.OnError(error);
                    }
                });


    }


    public void getDetail(final String query, final OnFetchDataListener listener){
        mListener = listener;
        AndroidNetworking.get(URL.concat("{".concat(Constant.getInstance().CATEGORY).concat("}")))
                .addPathParameter(Constant.getInstance().CATEGORY,query)
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ApiResponse.class,new ParsedRequestListener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        // do anything with response
                        mListener.OnResponse(response);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        mListener.OnError(error);
                    }
                });


    }


    public interface OnFetchDataListener {
        // TODO: Update argument type and name
        void OnResponse(ApiResponse response);

        void OnError(ANError error);
    }

}
