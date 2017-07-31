package com.rezkyaulia.android.popular_movie.database.contract;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.rezkyaulia.android.popular_movie.data.DataPath;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.Genre;

import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class GenreContract implements BaseColumns{


    DbHelper mDbHelper;

    public GenreContract(DbHelper helper) {
        this.mDbHelper = helper;
    }

    public void create(SQLiteDatabase sqLiteDatabase){
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID                 + " INTEGER PRIMARY KEY, "  +
                        NAME               + " STRING NOT NULL "      +
                        ");";

        sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public final String TABLE_NAME = "GenreTbl";

    public final String ID = "Id";
    public final String NAME = "Name";


    /* The base CONTENT_URI used to query the Weather table from the content provider */
    public final Uri CONTENT_URI = DataPath.getInstance().BASE_CONTENT_URI.buildUpon()
            .appendPath(DataPath.getInstance().PATH_GENRE)
            .build();



    public ContentValues contentValue(Genre genre) {
        ContentValues value = new ContentValues();
        value.put(ID, genre.getId());
        value.put(NAME, genre.getName());
        return value;
    }


    public ContentValues[] contentValues(List<Genre> genres) {

        ContentValues [] contentValues = new ContentValues[genres.size()];
        int i = 0;
        for (Genre genre : genres){
            ContentValues value = new ContentValues();
            value.put(ID, genre.getId());
            value.put(NAME, genre.getName());
            contentValues[i] = value;
            i++;
        }

        return contentValues;
    }



}
