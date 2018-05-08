package com.movies.chris.trendingmovies.data.tmdb.sync;

import android.content.ContentResolver;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class MoviesSyncJobService extends JobService {
    public static final String TAG = MoviesSyncJobService.class.getSimpleName();
    public static final String ACTION_START_DELETION = "com.movies.chris.trendingmoies.ACTION_START_DELETION";

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(TAG, "onStartJob");
        if (job.getTag().equals(ACTION_START_DELETION)) {
//            MoviesSyncUtils.clearTmdbData(this);
            ContentResolver cr = this.getContentResolver();
            cr.delete(MoviesContract.FavoritesEntry.CONTENT_URI, null, null);
            cr.delete(MoviesContract.TopRatedEntry.CONTENT_URI, null, null);
            cr.delete(MoviesContract.MostPopularEntry.CONTENT_URI, null, null);
            cr.delete(MoviesContract.NowPlayingEntry.CONTENT_URI, null, null);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
