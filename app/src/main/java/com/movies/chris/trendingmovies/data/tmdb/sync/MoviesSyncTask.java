package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.content.Context;
import android.net.Uri;
import android.util.Log;


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

    synchronized public static void syncMovies(final Context context, final Uri uri, String sortBy, int pageNumber) {
            MovieApiInterface movieApiInterface = ApiUtils.getMovieApiInterface();
            Log.i(TAG + ".syncMovies" , "sortBt = " + sortBy + " || pageNumber = " + pageNumber);
            Log.i(TAG + ".syncMovies" , "uri = " + uri.toString());

            Call<MovieList> call = movieApiInterface.getSortedMovieList(sortBy, pageNumber);
            call.enqueue(new Callback<MovieList>() {
                             @Override
                             public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                                 MovieList movieList = response.body();
                                 if (movieList != null) {
                                     Log.i(TAG + ".onResponse", "Number of posters = " + movieList.getSize());
                                     movieList.save(context, uri);
                                 } else {
                                     Log.i(TAG + ".onResponse", "null response");
                                 }
                             }

                             @Override
                             public void onFailure(Call<MovieList> call, Throwable t) {
                                 t.printStackTrace();
                             }
                         });
    }
}
