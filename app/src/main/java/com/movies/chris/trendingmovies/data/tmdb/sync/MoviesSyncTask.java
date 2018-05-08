package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.detail.MovieDetail;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MovieList;
import com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils;
import com.movies.chris.trendingmovies.data.tmdb.remote.MovieApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncTask {
    public static final String EVENT_SYNC_COMPLETE = "com.movies.chris.trendingmovies.data.tmbd.sync.SYNC_COMPLETE";
    public static final String EVENT_MOVIE_DETAIL_RECEIVED = "com.movies.chris.trendingmovies.data.tmbd.sync.MOVIE_DETAIL_RECEIVED";
    private static final String TAG = MoviesSyncTask.class.getSimpleName();

    public static void getMovieDetail(final Context context, final int movieID) {
        MovieApiInterface movieApiInterface = ApiUtils.getMovieApiInterface();
        Call<MovieDetail> call = movieApiInterface.getMovieDetail(movieID);
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                Intent intent = new Intent(EVENT_MOVIE_DETAIL_RECEIVED);
                intent.putExtra(context.getString(R.string.key_movie_detail), response.body());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Intent intent = new Intent(EVENT_MOVIE_DETAIL_RECEIVED);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    synchronized public static void syncMovies(final Context context, final Uri uri, String sortBy, int pageNumber) {
        MovieApiInterface movieApiInterface = ApiUtils.getMovieApiInterface();
        Log.i(TAG + ".syncMovies", "sortBt = " + sortBy + " || pageNumber = " + pageNumber);
        Log.i(TAG + ".syncMovies", "uri = " + uri.toString());
        Call<MovieList> call;
        if (uri.equals(MoviesContract.MovieNameEntry.CONTENT_URI))
            call = movieApiInterface.searchByMovieName(sortBy, pageNumber);
        else
            call = movieApiInterface.getSortedMovieList(sortBy, pageNumber);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                MovieList movieList = response.body();
                if (movieList != null) {
                    Log.i(TAG + ".onResponse", "Number of posters = " + movieList.getSize());
                    try {
                        int numItemsAdded = movieList.save(context, uri);
                        broadcastExitStatus(numItemsAdded == 20);
                    } catch (Exception e) {
                        broadcastExitStatus(false);
                    }
                } else {
                    Log.i(TAG + ".onResponse", "null response");
                    broadcastExitStatus(false);
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                t.printStackTrace();
                broadcastExitStatus(false);
            }

            private void broadcastExitStatus(boolean success) {
                Intent intent = new Intent(EVENT_SYNC_COMPLETE);
                intent.putExtra(context.getResources()
                        .getString(R.string.key_sync_success), success);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    public static void clearMovies(Context context) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(MoviesContract.FavoritesEntry.CONTENT_URI, null, null);
        cr.delete(MoviesContract.TopRatedEntry.CONTENT_URI, null, null);
        cr.delete(MoviesContract.MostPopularEntry.CONTENT_URI, null, null);
        cr.delete(MoviesContract.NowPlayingEntry.CONTENT_URI, null, null);
    }
}
