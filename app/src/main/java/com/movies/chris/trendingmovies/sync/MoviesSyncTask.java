package com.movies.chris.trendingmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;


import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.JSONUtils;
import com.movies.chris.trendingmovies.data.tmdb.NetworkUtils;

import java.net.URL;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncTask {
    private static String TAG = MoviesSyncTask.class.getSimpleName();

    synchronized public static void syncMovies(Context context, String urlString) {
        try {
            URL movieRequestURL = new URL(urlString);
            String jsonQueryResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestURL);
            ContentValues[] posterValues = JSONUtils.getPosterContentValuesFromJSON(context, jsonQueryResponse);

            if (posterValues != null && posterValues.length != 0) {
                //TODO: if response != page1 data sync all data
                ContentResolver contentResolver = context.getContentResolver();
//                contentResolver.delete(MoviesContract.CurrentPageEntry.CONTENT_URI, null, null);
                contentResolver.bulkInsert(MoviesContract.AllMoviesEntry.CONTENT_URI, posterValues);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
