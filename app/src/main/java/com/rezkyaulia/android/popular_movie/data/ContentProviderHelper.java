package com.rezkyaulia.android.popular_movie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.database.contract.MovieContract;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class ContentProviderHelper extends ContentProvider{

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    public static final int CODE_GENRE = 200;
    public static final int CODE_GENRE_WITH_ID = 201;

    public static final int CODE_MOVIE_GENRE = 300;
    public static final int CODE_MOVIE_GENRE_WITH_ID = 301;

    public static final int CODE_FAVORITE = 400;
    public static final int CODE_FAVORITE_WITH_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataPath.getInstance().CONTENT_AUTHORITY;

        matcher.addURI(authority, DataPath.getInstance().PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, DataPath.getInstance().PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        matcher.addURI(authority, DataPath.getInstance().PATH_GENRE, CODE_GENRE);
        matcher.addURI(authority, DataPath.getInstance().PATH_GENRE + "/#", CODE_GENRE_WITH_ID);

        matcher.addURI(authority, DataPath.getInstance().PATH_MOVIE_GENRE, CODE_MOVIE_GENRE);
        matcher.addURI(authority, DataPath.getInstance().PATH_MOVIE_GENRE + "/#", CODE_MOVIE_GENRE_WITH_ID);

        matcher.addURI(authority, DataPath.getInstance().PATH_FAVORITE, CODE_FAVORITE);
        matcher.addURI(authority, DataPath.getInstance().PATH_FAVORITE+ "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = DbHelper.getInstance(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CODE_MOVIE:
                retCursor =  db.query(mDbHelper.getMovieContract().TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIE_GENRE:

                retCursor = db.rawQuery("SELECT * FROM GenreTbl g" +
                        "                LEFT JOIN MovieGenreRelation gp ON gp.GenreId = g.Id" +
                        "                WHERE gp.MovieId = " + selection, null);

                break;

            case CODE_FAVORITE:
                retCursor =  db.query(mDbHelper.getFavoriteContract().TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITE_WITH_ID :
                Timber.e("select star");
                String id = uri.getPathSegments().get(1);
                Timber.e("select star with id "+id);

                retCursor =  db.query(mDbHelper.getFavoriteContract().TABLE_NAME,
                        projection,
                        mDbHelper.getFavoriteContract().ID + "=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long movieId =
                                value.getAsLong(mDbHelper.getMovieContract().ID);

                        long _id = db.insertWithOnConflict(mDbHelper.getMovieContract().TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case CODE_GENRE:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insertWithOnConflict(mDbHelper.getGenreContract().TABLE_NAME, null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case CODE_FAVORITE:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insertWithOnConflict(mDbHelper.getFavoriteContract().TABLE_NAME,
                                null, value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        long id = 0;

        switch (match) {
            case CODE_MOVIE_GENRE:
                id = db.insert(mDbHelper.getMovieGenreContract().TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(mDbHelper.getMovieGenreContract().CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case CODE_FAVORITE:
                id = db.insert(mDbHelper.getFavoriteContract().TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(mDbHelper.getFavoriteContract().CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;


            default:
                Timber.e("Unknow uri : "+uri);
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted; // starts as 0

        switch (match) {
            case CODE_MOVIE_GENRE:
                tasksDeleted = db.delete(mDbHelper.getMovieGenreContract().TABLE_NAME, null, null);
                break;

            case CODE_FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(mDbHelper.getFavoriteContract().TABLE_NAME,
                        mDbHelper.getFavoriteContract().ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
