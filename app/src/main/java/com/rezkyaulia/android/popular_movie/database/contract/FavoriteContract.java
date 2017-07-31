package com.rezkyaulia.android.popular_movie.database.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.rezkyaulia.android.popular_movie.data.DataPath;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.Movie;

import java.util.List;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class FavoriteContract implements BaseColumns {

    DbHelper mDbHelper;

    public FavoriteContract(DbHelper helper) {
        this.mDbHelper = helper;
    }

    public void create(SQLiteDatabase sqLiteDatabase){
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID                 + " INTEGER PRIMARY KEY, "  +
                        POSTER_PATH        + " STRING NOT NULL, "      +
                        ADULT              + " INTEGER NOT NULL,"      +
                        OVERVIEW           + " STRING NOT NULL, "      +
                        RELEASE_DATE       + " STRING NOT NULL, "      +
                        ORIGINAL_TITLE     + " STRING NOT NULL, "      +
                        ORIGINAL_LANGUAGE  + " STRING NOT NULL, "      +
                        TITLE              + " STRING NOT NULL, "      +
                        BACKDROP_PATH      + " STRING NOT NULL, "      +
                        POPULARITY         + " REAL NOT NULL, "        +
                        VOTE_COUNT         + " INTEGER NOT NULL, "     +
                        VIDEO              + " INTEGER NOT NULL, "     +
                        VOTE_AVERAGE       + " REAL NOT NULL, "        +

                        " UNIQUE (" + ID + ") ON CONFLICT FAIL);";

        sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public final String TABLE_NAME = "FavoriteTbl";

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
            .appendPath(DataPath.getInstance().PATH_FAVORITE)
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


    public Movie assign(Cursor cursor) {

        Movie movie = new Movie();

        movie.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndex(POSTER_PATH)));
        movie.setAdult((cursor.getInt(cursor.getColumnIndex(ADULT))==1)?true:false);
        movie.setOverview(cursor.getString(cursor.getColumnIndex(OVERVIEW)));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(RELEASE_DATE)));
        movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(ORIGINAL_TITLE)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(BACKDROP_PATH)));
        movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(POPULARITY)));
        movie.setVoteCount(cursor.getInt(cursor.getColumnIndex(VOTE_COUNT)));
        movie.setVideo((cursor.getInt(cursor.getColumnIndex(VIDEO))==1)?true:false);
        movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(VOTE_AVERAGE)));

        return movie;
    }



}
