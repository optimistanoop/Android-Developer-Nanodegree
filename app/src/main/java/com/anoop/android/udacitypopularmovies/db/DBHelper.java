package com.anoop.android.udacitypopularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.anoop.android.udacitypopularmovies.db.MovieContract.MovieEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " BOOL NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_POPULARITY + " NUMERIC NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VIDEO + " BOOL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL);";

        final String SQL_CREATE_DISCOVERY_TABLE = "CREATE TABLE " + MovieContract.DiscoverEntry.TABLE_NAME + " (" +
                MovieContract.DiscoverEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.DiscoverEntry.COLUMN_SORTING + " TEXT NOT NULL," +
                MovieContract.DiscoverEntry.COLUMN_ORDER + " INTEGER NOT NULL," +
                "UNIQUE(" + MovieContract.DiscoverEntry.COLUMN_SORTING + ", " + MovieContract.DiscoverEntry.COLUMN_ORDER + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +
                MovieContract.VideoEntry._ID +  " INTEGER PRIMARY KEY," +
                MovieContract.VideoEntry.COLUMN_API_ID + " TEXT NOT NULL," +
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.VideoEntry.COLUMN_KEY + " TEXT NOT NULL," +
                MovieContract.VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                MovieContract.VideoEntry.COLUMN_SITE + " TEXT NOT NULL," +
                MovieContract.VideoEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_TYPE + " TEXT NOT NULL);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID +  " INTEGER PRIMARY KEY," +
                MovieContract.ReviewEntry.COLUMN_API_ID + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_DISCOVERY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.DiscoverEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.VideoEntry.TABLE_NAME);
        onCreate(db);
    }
}
