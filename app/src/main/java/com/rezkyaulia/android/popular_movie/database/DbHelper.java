package com.rezkyaulia.android.popular_movie.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rezkyaulia.android.popular_movie.database.contract.FavoriteContract;
import com.rezkyaulia.android.popular_movie.database.contract.GenreContract;
import com.rezkyaulia.android.popular_movie.database.contract.MovieContract;
import com.rezkyaulia.android.popular_movie.database.contract.MovieGenreContract;
import com.rezkyaulia.android.popular_movie.model.Movie;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/29/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popular_movie.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
     * with the Android course for a while now. Believe it or not, older versions of Sunshine
     * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
     * versions of Sunshine could cause everything to break. Although that is certainly a rare
     * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
     * version your databases.
     */
    private static final int DATABASE_VERSION = 7;

    // Step 1: private static variable of INSTANCE variable
    private static volatile DbHelper INSTANCE;


    // Step 3: Provide public static getInstance() method returning INSTANCE after checking
    public static DbHelper getInstance(Context Context) {

        // double-checking lock
        if(null == INSTANCE){

            // synchronized block
            synchronized (DbHelper.class) {
                if(null == INSTANCE){
                    INSTANCE = new DbHelper(Context);
                }
            }
        }
        return INSTANCE;
    }

    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    //initialize contract
    private MovieContract movieContract;
    private GenreContract genreContract;
    private FavoriteContract favoriteContract;
    private MovieGenreContract movieGenreContract;



    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        //define contract
        movieContract = new MovieContract(this);
        genreContract = new GenreContract(this);
        favoriteContract = new FavoriteContract(this);
        movieGenreContract = new MovieGenreContract(this);

        //initialize on the end on this constractor
        sqLiteDatabase = getWritableDatabase();


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTrigger(db);
    }
    private void createTrigger(SQLiteDatabase database) {
        movieContract.create(database);
        genreContract.create(database);
        movieGenreContract.create(database);
        favoriteContract.create(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTrigger(db);
    }

    public SQLiteDatabase getDatabase() {
        return sqLiteDatabase;
    }

    public MovieContract getMovieContract(){
        return movieContract;
    }
    public GenreContract getGenreContract(){
        return genreContract;
    }
    public FavoriteContract getFavoriteContract(){
        return favoriteContract;
    }
    public MovieGenreContract getMovieGenreContract(){
        return movieGenreContract;
    }
}
