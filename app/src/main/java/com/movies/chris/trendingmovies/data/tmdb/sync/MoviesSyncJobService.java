package com.movies.chris.trendingmovies.data.tmdb.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MoviesSyncJobService extends JobService {
    public static final String ACTION_START_DELETION = "com.movies.chris.trendingmoies.ACTION_START_DELETION";
    @Override
    public boolean onStartJob(JobParameters job) {
        MoviesSyncUtils.clearTmdbData(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
