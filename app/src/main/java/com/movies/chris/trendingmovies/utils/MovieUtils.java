package com.movies.chris.trendingmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class MovieUtils {

    public static boolean isFavorite (Context context, int movieID) {
        Cursor c = context.getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelection(),
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelectionArgs(movieID),
                MoviesContract.FavoritesEntry.getSortOrder());
        boolean ret = (c != null && c.getCount() > 0);
        if (c != null)
            c.close();
        return ret;
    }

    public static boolean isRecent (Context context, int movieID) {
        Cursor c = context.getContentResolver().query(MoviesContract.RecentEntry.CONTENT_URI,
                null,
                MoviesContract.RecentEntry.getRecentsWithIdSelection(),
                MoviesContract.RecentEntry.getRecentsWithIdSelectionArgs(movieID),
                MoviesContract.RecentEntry.getSortOrder());
        boolean ret = (c != null && c.getCount() > 0);
        if (c != null)
            c.close();
        return ret;
    }

    public static void saveToFavorites(Context context, int movieID, String posterPath) {
        context.getContentResolver()
                .insert(MoviesContract.FavoritesEntry.CONTENT_URI,
                        getPosterContentValues(movieID, posterPath));
    }

    public static void deleteFromFavorites(Context context, int movieID) {
        Uri uri = MoviesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movieID))
                .build();
        context.getContentResolver().delete(uri, null, null);
    }

    private static ContentValues getPosterContentValues(int movieID, String posterPath) {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.POSTER_PATH, posterPath);
        posterValues.put(MoviesContract.MOVIE_ID, movieID);
        return posterValues;
    }
}
