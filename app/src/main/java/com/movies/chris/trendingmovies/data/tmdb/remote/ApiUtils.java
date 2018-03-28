package com.movies.chris.trendingmovies.data.tmdb.remote;

import com.movies.chris.trendingmovies.BuildConfig;

/**
 * Created by chris on 3/2/18.
 */

public class ApiUtils {
    static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

    static final String PATH_PARAM_MOVIE_ID = "movieID";
    static final String PATH_PARAM_SORT_BY = "sortBy";
    static final String QUERY_PARAM_MOVIE_NAME = "query";

    static final String QUERY_PARAM_PAGE_NUMBER = "page";
    static final String QUERY_PARAM_APPEND_TO_RESPONSE = "append_to_response";

    static final String QUERY_PARAM_APPEND_VIDEOS = "videos";
    static final String QUERY_PARAM_APPEND_REVIEWS = "reviews";


    public static MovieApiInterface getMovieApiInterface() {
        return RetrofitClient.getClient(MOVIE_BASE_URL).create(MovieApiInterface.class);
    }

}
