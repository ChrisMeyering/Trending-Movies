package com.movies.chris.trendingmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MoviePoster;

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

    public static int getFavoriteImageResource(Context context, int movieId) {
        return isFavorite(context, movieId) ?
                R.drawable.ic_star_orange_500_24dp:
                R.drawable.ic_star_border_orange_500_24dp;
    }
    public static void setFavoriteImageResource(Context context, FloatingActionButton fab, int movieId){
        if (isFavorite(context, movieId)) {
            fab.setImageResource(R.drawable.ic_star_orange_500_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_star_border_orange_500_24dp);
        }
    }
    public static boolean swapFavoriteImageResource(Context context, FloatingActionButton fab, MoviePoster moviePoster){
        return swapFavoriteImageResource(context, fab, moviePoster.id, moviePoster.posterPath) == R.drawable.ic_star_orange_500_24dp;
    }

    public static int swapFavoriteImageResource(Context context, FloatingActionButton fab, int movieId, String posterPath){
        int resourceID;
        if (isFavorite(context, movieId)) {
            fab.setImageResource(R.drawable.ic_star_border_orange_500_24dp);
            deleteFromFavorites(context, movieId);
            resourceID = R.drawable.ic_star_border_orange_500_24dp;
        } else {
            fab.setImageResource(R.drawable.ic_star_orange_500_24dp);
            saveToFavorites(context, movieId, posterPath);
            resourceID = R.drawable.ic_star_orange_500_24dp;
        }
        return resourceID;
    }

    private static ContentValues getPosterContentValues(int movieID, String posterPath) {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.POSTER_PATH, posterPath);
        posterValues.put(MoviesContract.MOVIE_ID, movieID);
        return posterValues;
    }
}
