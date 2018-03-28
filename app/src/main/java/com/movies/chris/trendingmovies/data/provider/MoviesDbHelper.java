package com.movies.chris.trendingmovies.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by chris on 10/31/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TAG = MoviesDbHelper.class.getSimpleName();

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private ArrayList<String> generateSQL() {
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + MoviesContract.FavoritesEntry.TABLE_NAME + " ("
                        + MoviesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_RECENTS_TABLE =
                "CREATE TABLE " + MoviesContract.RecentEntry.TABLE_NAME + " ("
                        + MoviesContract.RecentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.RecentEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.RecentEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_TOP_RATED_TABLE =
                "CREATE TABLE " + MoviesContract.TopRatedEntry.TABLE_NAME + " ("
                        + MoviesContract.TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.TopRatedEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_MOST_POPULAR_TABLE =
                "CREATE TABLE " + MoviesContract.MostPopularEntry.TABLE_NAME + " ("
                        + MoviesContract.MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.MostPopularEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.MostPopularEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_UPCOMING_TABLE =
                "CREATE TABLE " + MoviesContract.UpcomingEntry.TABLE_NAME + " ("
                        + MoviesContract.UpcomingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.UpcomingEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.UpcomingEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_NOW_PLAYING_TABLE =
                "CREATE TABLE " + MoviesContract.NowPlayingEntry.TABLE_NAME + " ("
                        + MoviesContract.NowPlayingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.NowPlayingEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.NowPlayingEntry.COLUMN_POSTER_PATH + " STRING NOT NULL);";
        final String SQL_CREATE_GENRE_IDS_TABLE =
                "CREATE TABLE " + MoviesContract.GenreIdsEntry.TABLE_NAME + " ("
                        + MoviesContract.GenreIdsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoviesContract.GenreIdsEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL UNIQUE, "
                        + MoviesContract.GenreIdsEntry.COLUMN_GENRE_NAME + " STRING NOT NULL);";

        ArrayList<String> SQLInstructions = new ArrayList<>();
        SQLInstructions.add(SQL_CREATE_FAVORITES_TABLE);
        SQLInstructions.add(SQL_CREATE_RECENTS_TABLE);
        SQLInstructions.add(SQL_CREATE_TOP_RATED_TABLE);
        SQLInstructions.add(SQL_CREATE_MOST_POPULAR_TABLE);
        SQLInstructions.add(SQL_CREATE_UPCOMING_TABLE);
        SQLInstructions.add(SQL_CREATE_NOW_PLAYING_TABLE);
        SQLInstructions.add(SQL_CREATE_GENRE_IDS_TABLE);

        return SQLInstructions;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        ArrayList<String> SQL = generateSQL();
        for (String instruction : SQL) {
            db.execSQL(instruction);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoritesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.RecentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TopRatedEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MostPopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.UpcomingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.NowPlayingEntry.TABLE_NAME);
        onCreate(db);
    }
}