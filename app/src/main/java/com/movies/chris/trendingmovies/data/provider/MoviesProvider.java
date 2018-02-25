package com.movies.chris.trendingmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by chris on 10/31/17.
 */

public class MoviesProvider extends ContentProvider {

    private static final String TAG = MoviesProvider.class.getSimpleName();
    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_ID = 101;
    public static final int CODE_RECENT = 200;
    public static final int CODE_RECENT_WITH_ID = 201;
    public static final int CODE_TOP_RATED = 300;
    public static final int CODE_TOP_RATED_WITH_ID = 301;
    public static final int CODE_MOST_POPULAR = 400;
    public static final int CODE_MOST_POPULAR_WITH_ID = 401;
    public static final int CODE_NOW_PLAYING = 500;
    public static final int CODE_NOW_PLAYING_WITH_ID = 501;
    public static final int CODE_UPCOMING = 600;
    public static final int CODE_UPCOMING_WITH_ID = 601;
    public static final int CODE_ALL_MOVIES = 700;
    public static final int CODE_MOVIES_WITH_ID = 701;
    public static final int CODE_GENRE_IDS = 800;
    public static final int CODE_GENRE_IDS_WITH_ID = 801;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

//TODO
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_RECENT, CODE_RECENT);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_RECENT + "/#", CODE_RECENT_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_TOP_RATED, CODE_TOP_RATED);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_TOP_RATED + "/#", CODE_TOP_RATED_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOST_POPULAR, CODE_MOST_POPULAR);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOST_POPULAR + "/#", CODE_MOST_POPULAR_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_NOW_PLAYING, CODE_NOW_PLAYING);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_NOW_PLAYING + "/#", CODE_NOW_PLAYING_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_UPCOMING, CODE_UPCOMING);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_UPCOMING + "/#", CODE_UPCOMING_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_ALL_MOVIES, CODE_ALL_MOVIES);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_ALL_MOVIES + "/#", CODE_MOVIES_WITH_ID);

        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_GENRE_IDS, CODE_GENRE_IDS);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_GENRE_IDS + "/#", CODE_GENRE_IDS_WITH_ID);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor cursor;

        String[] selectionArguments;
        String tableName = getTableName(uri);
        switch(sUriMatcher.match(uri)) {
//            case CODE_FAVORITES:
//                cursor = mOpenHelper.getReadableDatabase().query(
//                        MoviesContract.FavoritesEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//                break;

            case CODE_FAVORITES_WITH_ID:
            case CODE_RECENT_WITH_ID:
            case CODE_MOVIES_WITH_ID:
            case CODE_GENRE_IDS_WITH_ID:
            case CODE_MOST_POPULAR_WITH_ID:
            case CODE_NOW_PLAYING_WITH_ID:
            case CODE_TOP_RATED_WITH_ID:
            case CODE_UPCOMING_WITH_ID:
                selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(
                        tableName,
                        projection,
                        MoviesContract.MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_GENRE_IDS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                cursor = mOpenHelper.getReadableDatabase().query(
                        tableName,
                        projection,
                        selection, // where PageNumber =
                        selectionArgs, // pageNumber
                        null,
                        null,
                        sortOrder);
                break;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private String getTableName(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_ALL_MOVIES:
            case CODE_MOVIES_WITH_ID:
                return MoviesContract.AllMoviesEntry.TABLE_NAME;
            case CODE_FAVORITES:
            case CODE_FAVORITES_WITH_ID:
                return MoviesContract.FavoritesEntry.TABLE_NAME;
            case CODE_GENRE_IDS:
            case CODE_GENRE_IDS_WITH_ID:
                return MoviesContract.GenreIdsEntry.TABLE_NAME;
            case CODE_MOST_POPULAR:
            case CODE_MOST_POPULAR_WITH_ID:
                return MoviesContract.MostPopularEntry.TABLE_NAME;
            case CODE_NOW_PLAYING:
            case CODE_NOW_PLAYING_WITH_ID:
                return MoviesContract.NowPlayingEntry.TABLE_NAME;
            case CODE_RECENT:
            case CODE_RECENT_WITH_ID:
                return MoviesContract.RecentEntry.TABLE_NAME;
            case CODE_TOP_RATED:
            case CODE_TOP_RATED_WITH_ID:
                return MoviesContract.TopRatedEntry.TABLE_NAME;
            case CODE_UPCOMING:
            case CODE_UPCOMING_WITH_ID:
                return MoviesContract.UpcomingEntry.TABLE_NAME;
            default:
                throw new SQLException("Unrecognized URI: " + uri);
        }
    }
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id <= 0) {
                    throw new SQLException("Failed to insert row into uri: " + uri);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if(rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName = getTableName(uri);

        long _id = db.insert(tableName,
                null,
                values);
        if (_id >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return uri.buildUpon().appendPath(String.valueOf(_id)).build();
        } else {
            throw new SQLException("Failed to insert row into uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        String[] selectionArguments;
        if (null == selection) selection = "1";
        String tableName = getTableName(uri);
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES_WITH_ID:
            case CODE_RECENT_WITH_ID:
            case CODE_MOVIES_WITH_ID:
            case CODE_GENRE_IDS_WITH_ID:
            case CODE_MOST_POPULAR_WITH_ID:
            case CODE_NOW_PLAYING_WITH_ID:
            case CODE_TOP_RATED_WITH_ID:
            case CODE_UPCOMING_WITH_ID:
                selectionArguments = new String[]{uri.getLastPathSegment()};
                numRowsDeleted = mOpenHelper.getWritableDatabase()
                        .delete(tableName,
                                MoviesContract.MOVIE_ID + " = ?",
                                selectionArguments);
                break;
            default:
                numRowsDeleted = mOpenHelper.getWritableDatabase()
                        .delete(tableName,
                                selection,
                                selectionArgs);
                break;
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
