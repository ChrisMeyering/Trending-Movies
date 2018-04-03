package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.tmdb.model.MovieList;
import com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils;
import com.movies.chris.trendingmovies.data.tmdb.remote.MovieApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncTask {
    private static String TAG = MoviesSyncTask.class.getSimpleName();
    public static String EVENT_SYNC_COMPLETE = "com.movies.chris.trendingmovies.data.tmbd.sync.SYNC_COMPLETE";
    synchronized public static void syncMovies(final Context context, final Uri uri, String sortBy, int pageNumber) {
            MovieApiInterface movieApiInterface = ApiUtils.getMovieApiInterface();
            Log.i(TAG + ".syncMovies" , "sortBt = " + sortBy + " || pageNumber = " + pageNumber);
            Log.i(TAG + ".syncMovies" , "uri = " + uri.toString());

            Call<MovieList> call = movieApiInterface.getSortedMovieList(sortBy, pageNumber);
            call.enqueue(new Callback<MovieList>() {
                             @Override
                             public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                                 Intent intent = new Intent(EVENT_SYNC_COMPLETE);
                                 MovieList movieList = response.body();
                                 if (movieList != null) {
                                     Log.i(TAG + ".onResponse", "Number of posters = " + movieList.getSize());
                                     movieList.save(context, uri);
                                     intent.putExtra(context.getResources()
                                                     .getString(R.string.key_sync_success),
                                             true);
                                     LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                 } else {
                                     Log.i(TAG + ".onResponse", "null response");
                                     intent.putExtra(context.getResources()
                                                     .getString(R.string.key_sync_success),
                                             false);
                                 }
                             }

                             @Override
                             public void onFailure(Call<MovieList> call, Throwable t) {
                                 t.printStackTrace();
                                 Intent intent = new Intent(EVENT_SYNC_COMPLETE);
                                 intent.putExtra(context.getResources()
                                                 .getString(R.string.key_sync_success),
                                         false);
                                 LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                             }
                         });
    }
}
