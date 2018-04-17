package com.movies.chris.trendingmovies.data.tmdb.remote;

import com.movies.chris.trendingmovies.data.tmdb.model.GenreList;
import com.movies.chris.trendingmovies.data.tmdb.model.detail.MovieDetail;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.PATH_PARAM_MOVIE_ID;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.PATH_PARAM_SORT_BY;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.QUERY_PARAM_APPEND_REVIEWS;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.QUERY_PARAM_APPEND_TO_RESPONSE;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.QUERY_PARAM_APPEND_VIDEOS;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.QUERY_PARAM_MOVIE_NAME;
import static com.movies.chris.trendingmovies.data.tmdb.remote.ApiUtils.QUERY_PARAM_PAGE_NUMBER;
/**
 * Created by chris on 3/2/18.
 */

public interface MovieApiInterface {

    @GET("movie/{"+ PATH_PARAM_SORT_BY +"}")
    Call<MovieList> getSortedMovieList(@Path(PATH_PARAM_SORT_BY) String sortBy,
                                       @Query(QUERY_PARAM_PAGE_NUMBER) int pageNumber);

    @GET("search/movie")
    Call<MovieList> searchByMovieName(@Query(QUERY_PARAM_MOVIE_NAME) String movieName,
                           @Query(QUERY_PARAM_PAGE_NUMBER) int pageNumber);

    @GET("movie/{" + PATH_PARAM_MOVIE_ID + "}?"
            + QUERY_PARAM_APPEND_TO_RESPONSE + "="
            + QUERY_PARAM_APPEND_VIDEOS + ","
            + QUERY_PARAM_APPEND_REVIEWS)
    Call<MovieDetail> getMovieDetail(@Path(PATH_PARAM_MOVIE_ID) int movieID);

    @GET("genre/movie/list")
    Call<GenreList> getGenreList();

}
