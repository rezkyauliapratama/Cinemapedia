package com.rezkyaulia.android.popular_movie.database.contract;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.google.gson.Gson;
import com.rezkyaulia.android.popular_movie.activity.BaseActivity;
import com.rezkyaulia.android.popular_movie.data.DataPath;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.Movie;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class MovieContract implements BaseColumns {

    DbHelper mDbHelper;
    
    public MovieContract(DbHelper helper) {
        this.mDbHelper = helper;
    }

    public void create(SQLiteDatabase sqLiteDatabase){
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID                 + " INTEGER PRIMARY KEY, "  +
                        POSTER_PATH        + " STRING, "      +
                        ADULT              + " INTEGER,"      +
                        OVERVIEW           + " STRING, "      +
                        RELEASE_DATE       + " STRING, "      +
                        ORIGINAL_TITLE     + " STRING, "      +
                        ORIGINAL_LANGUAGE  + " STRING, "      +
                        TITLE              + " STRING, "      +
                        BACKDROP_PATH      + " STRING, "      +
                        POPULARITY         + " REAL, "        +
                        VOTE_COUNT         + " INTEGER, "     +
                        VIDEO              + " INTEGER, "     +
                        VOTE_AVERAGE       + " REAL, "        +
                        " UNIQUE (" + ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public final String TABLE_NAME = "MovieTbl";

    public final String ID = "Id";
    public final String POSTER_PATH= "PosterPath";
    public final String ADULT= "Adult";
    public final String OVERVIEW = "Overview";
    public final String RELEASE_DATE= "ReleaseDate";
    public final String ORIGINAL_TITLE = "OriginalTitle";
    public final String ORIGINAL_LANGUAGE = "OriginalLanguage";
    public final String TITLE = "Title";
    public final String BACKDROP_PATH = "BackdropPath";
    public final String POPULARITY = "Popularity";
    public final String VOTE_COUNT = "VoteCount";
    public final String VIDEO = "Video";
    public final String VOTE_AVERAGE = "VoteAverage";

    /* The base CONTENT_URI used to query the Weather table from the content provider */
    public final Uri CONTENT_URI = DataPath.getInstance().BASE_CONTENT_URI.buildUpon()
            .appendPath(DataPath.getInstance().PATH_MOVIE)
            .build();


    public ContentValues contentValue(Movie movie) {
        ContentValues value = new ContentValues();
        value.put(ID, movie.getId());
        value.put(POSTER_PATH, movie.getPosterPath());
        value.put(ADULT, movie.isAdult());
        value.put(OVERVIEW, movie.getOverview());
        value.put(RELEASE_DATE, movie.getReleaseDate());
        value.put(ORIGINAL_TITLE, movie.getOriginalTitle());
        value.put(ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        value.put(TITLE, movie.getTitle());
        value.put(BACKDROP_PATH, movie.getBackdropPath());
        value.put(POPULARITY, movie.getPopularity());
        value.put(VOTE_COUNT, movie.getVoteCount());
        value.put(VIDEO, movie.getVideo());
        value.put(VOTE_AVERAGE, movie.getVoteAverage());
        return value;
    }


    public ContentValues [] contentValues(List<Movie> movies) {

        ContentValues [] contentValues = new ContentValues[movies.size()];
        int i = 0;
        for (Movie movie : movies){
            ContentValues value = new ContentValues();
            value.put(ID, movie.getId());
            value.put(POSTER_PATH, movie.getPosterPath());
            value.put(ADULT, movie.isAdult());
            value.put(OVERVIEW, movie.getOverview());
            value.put(RELEASE_DATE, movie.getReleaseDate());
            value.put(ORIGINAL_TITLE, movie.getOriginalTitle());
            value.put(ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
            value.put(TITLE, movie.getTitle());
            value.put(BACKDROP_PATH, movie.getBackdropPath());
            value.put(POPULARITY, movie.getPopularity());
            value.put(VOTE_COUNT, movie.getVoteCount());
            value.put(VIDEO, movie.getVideo());
            value.put(VOTE_AVERAGE, movie.getVoteAverage());
            contentValues[i] = value;
            i++;
        }

        return contentValues;
    }




}
