package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.movies.chris.trendingmovies.R;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncUtils {
    private static final String TAG = MoviesSyncUtils.class.getSimpleName();

    public static void getTmdbMovieList(@NonNull final Context context, Uri uri, String sortBy, int pageNumber) {
        Log.i(TAG, "Building intent to fetch movies");
        Log.i(TAG, "URI = " + uri.toString()
                + "\nSort order = " + sortBy
                + "\nPage number = " + pageNumber);
        Intent intent = new Intent(context, MoviesSyncIntentService.class);
        intent.putExtra(context.getString(R.string.key_query_uri), uri.toString());
        intent.putExtra(context.getString(R.string.key_sort_by), sortBy);
        intent.putExtra(context.getString(R.string.key_page_number), pageNumber);
        context.startService(intent);
    }

    public static void getTmdbMovieList(@NonNull final Context context, Uri uri, String sortBy) {
        getTmdbMovieList(context, uri, sortBy, 1);
    }

    public static void getTmdbMovieDetail(@NonNull final Context context, int movieID) {
        Intent intent = new Intent(context, MoviesSyncIntentService.class);
        intent.putExtra(context.getString(R.string.key_movie_id), movieID);
        context.startService(intent);
    }
}
