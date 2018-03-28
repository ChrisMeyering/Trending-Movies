package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.movies.chris.trendingmovies.R;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncIntentService extends IntentService {

    private static final String TAG = MoviesSyncIntentService.class.getSimpleName();

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.key_query_uri)) &&
                    intent.hasExtra(getString(R.string.key_sort_by)))
                MoviesSyncTask.syncMovies(this,
                        Uri.parse(intent.getStringExtra(getResources().getString(R.string.key_query_uri))),
                        intent.getStringExtra(getResources().getString(R.string.key_sort_by)),
                        intent.getIntExtra(getResources().getString(R.string.key_page_number), 1)
                );
        }
    }
}
