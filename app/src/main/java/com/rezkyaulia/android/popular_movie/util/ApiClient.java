package com.rezkyaulia.android.popular_movie.util;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.rezkyaulia.android.popular_movie.BuildConfig;
import com.rezkyaulia.android.popular_movie.model.ApiGenreResponse;
import com.rezkyaulia.android.popular_movie.model.ApiMovieResponse;
import com.rezkyaulia.android.popular_movie.model.ApiReviewResponse;
import com.rezkyaulia.android.popular_movie.model.ApiTrailerResponse;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class ApiClient {
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
    //b77a9c9af1b4434dcbbacdde72879e7c

    private final String URL_MOVIE = "http://api.themoviedb.org/3/movie/";
    private final String URL_GENRE = "http://api.themoviedb.org/3/genre/movie/list";
    public final String URL_IMAGE = "https://image.tmdb.org/t/p/";
    public final String URL_THUMBNAIL = "https://img.youtube.com/vi/";
    public final String URL_TRAILER = "http://api.themoviedb.org/3/movie/";
    public final String URL_YOUTUBE = "http://www.youtube.com/watch?v=";

    public void getListMovie(final String query,int page, final OnFetchDataListener<ApiMovieResponse> listener){
        AndroidNetworking.get(URL_MOVIE.concat("{".concat(Constant.getInstance().CATEGORY).concat("}")))
                .addPathParameter(Constant.getInstance().CATEGORY,query)
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .addQueryParameter(Constant.getInstance().PAGE,String.valueOf(page))
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Timber.e("BYTE : "+bytesDownloaded/totalBytes);
                    }
                })
                .getAsObject(ApiMovieResponse.class,new ParsedRequestListener<ApiMovieResponse>() {
                    @Override
                    public void onResponse(ApiMovieResponse response) {
                        // do anything with response
                        listener.OnResponse((ApiMovieResponse)response);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        listener.OnError(error);
                    }
                });


    }

    public void getListGenre(final OnFetchDataListener<ApiGenreResponse> listener){

        AndroidNetworking.get(URL_GENRE)
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Timber.e("BYTE : "+bytesDownloaded/totalBytes);
                    }
                })
                .getAsObject(ApiGenreResponse.class,new ParsedRequestListener<ApiGenreResponse>() {
                    @Override
                    public void onResponse(ApiGenreResponse response) {
                        // do anything with response
                        listener.OnResponse((ApiGenreResponse)response);

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        listener.OnError(error);
                    }
                });


    }



    public void getListTrailer(int id,final OnFetchDataListener<ApiTrailerResponse> listener){

        AndroidNetworking.get(URL_MOVIE.concat("{".concat(Constant.getInstance().ID).concat("}").concat("/videos")))
                .addPathParameter(Constant.getInstance().ID,String.valueOf(id))
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Timber.e("BYTE : "+bytesDownloaded/totalBytes);
                    }
                })
                .getAsObject(ApiTrailerResponse.class,new ParsedRequestListener<ApiTrailerResponse>() {
                    @Override
                    public void onResponse(ApiTrailerResponse response) {
                        // do anything with response
                        listener.OnResponse(response);

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        listener.OnError(error);
                    }
                });


    }

    public void getListReview (int id,final OnFetchDataListener<ApiReviewResponse> listener){

        AndroidNetworking.get(URL_MOVIE.concat("{".concat(Constant.getInstance().ID).concat("}").concat("/reviews")))
                .addPathParameter(Constant.getInstance().ID,String.valueOf(id))
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Timber.e("BYTE : "+bytesDownloaded/totalBytes);
                    }
                })
                .getAsObject(ApiReviewResponse.class,new ParsedRequestListener<ApiReviewResponse>() {
                    @Override
                    public void onResponse(ApiReviewResponse response) {
                        // do anything with response
                        listener.OnResponse(response);

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        listener.OnError(error);
                    }
                });


    }

/*

    public void getDetail(final String query, final OnFetchDataListener listener){
        mListener = listener;
        AndroidNetworking.get(URL_MOVIE.concat("{".concat(Constant.getInstance().CATEGORY).concat("}")))
                .addPathParameter(Constant.getInstance().CATEGORY,query)
                .addQueryParameter(Constant.getInstance().API_KEY,apiKey)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ApiMovieResponse.class,new ParsedRequestListener<ApiMovieResponse>() {
                    @Override
                    public void onResponse(ApiMovieResponse response) {
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
*/


    public interface OnFetchDataListener<T> {
        // TODO: Update argument type and name
        void OnResponse(T response);

        void OnError(ANError error);
    }

}
