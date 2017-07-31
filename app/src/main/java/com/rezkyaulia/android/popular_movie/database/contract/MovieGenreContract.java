package com.rezkyaulia.android.popular_movie.database.contract;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.rezkyaulia.android.popular_movie.data.DataPath;
import com.rezkyaulia.android.popular_movie.database.DbHelper;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class MovieGenreContract implements BaseColumns {
    DbHelper mDbHelper;

    public MovieGenreContract(DbHelper helper) {
        this.mDbHelper = helper;
    }

    public void create(SQLiteDatabase sqLiteDatabase){
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _id                 + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                        MOVIE_ID + " INTEGER NOT NULL, "      +
                        GENRE_ID + " INTEGER NOT NULL, "      +
                        "FOREIGN KEY (" + MOVIE_ID + ") REFERENCES " +
                            mDbHelper.getMovieContract().TABLE_NAME + "("+ mDbHelper.getMovieContract().ID + ")," +
                        "FOREIGN KEY (" + GENRE_ID + ") REFERENCES " +
                            mDbHelper.getGenreContract().TABLE_NAME + "(" + mDbHelper.getGenreContract().ID + ")"+
                        ");";

        sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public final String TABLE_NAME = "MovieGenreRelation";

    public final String _id = "_id";
    public final String MOVIE_ID = "MovieId";
    public final String GENRE_ID = "GenreId";

    /* The base CONTENT_URI used to query the Weather table from the content provider */
    public final Uri CONTENT_URI = DataPath.getInstance().BASE_CONTENT_URI.buildUpon()
            .appendPath(DataPath.getInstance().PATH_MOVIE_GENRE)
            .build();

    public ContentValues contentValue(int movieId, int genreId) {
        ContentValues value = new ContentValues();
        value.put(MOVIE_ID, movieId);
        value.put(GENRE_ID, genreId);
        return value;
    }

}
