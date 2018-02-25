package com.movies.chris.trendingmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.movies.chris.trendingmovies.R;

import java.net.URL;

/**
 * Created by chris on 11/2/17.
 */

public class MoviesSyncUtils {
    private static final String TAG = MoviesSyncUtils.class.getSimpleName();

    public static void startImmediateSync(@NonNull final Context context, URL url) {
        Intent intent = new Intent(context, MoviesSyncIntentService.class);
        intent.putExtra(context.getString(R.string.key_query_url), url.toString());
        context.startService(intent);
    }
}
