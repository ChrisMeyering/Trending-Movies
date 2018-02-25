package com.movies.chris.trendingmovies.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chris on 10/31/17.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.chris.popularMovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECENT = "recent";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_MOST_POPULAR = "most_popular";
    public static final String PATH_UPCOMING = "upcoming";
    public static final String PATH_NOW_PLAYING = "now_playing";
    public static final String PATH_MOVIES = "all_movies";

    public static final String PATH_GENRE_IDS = "genre_ids";

    public static final String MOVIE_ID = "movie_id";
    public static final String POSTER_PATH = "poster_path";
    public static final String PAGE_NUMBER = "page_number";

    public static final class GenreIdsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_GENRE_IDS)
                .build();
        public static final String TABLE_NAME = PATH_GENRE_IDS;
        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_GENRE_NAME = "genre_name";
        public static Uri buildGenreIdsPageUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOW_PLAYING)
                .build();
        public static final String PARAM_LIMIT = "limit";
        public static final String PARAM_OFFSET = "offset";
        public static final String PARAM_SELECTION = "selection";

        public static final String TABLE_NAME = PATH_MOVIES;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_GENRE_ID = "genre_ids";
        public static final String COLUMN_GENRE_NAME = "genre_names";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_IS_RECENT = "is_recent";
        public static final String COLUMN_IS_NOW_PLAYING = "is_now_playing";
        public static final String COLUMN_IS_UPCOMING = "is_upcoming";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";
        public static final String COLUMN_IS_MOST_POPULAR = "is_most_popular";
        public static Uri buildMovieUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
        public static Uri buildSortedMovieListUri(String selection, int limit, int offset) {
            return CONTENT_URI.buildUpon().appendQueryParameter(PARAM_LIMIT, String.valueOf(limit))
                    .appendQueryParameter(PARAM_OFFSET, String.valueOf(offset))
                    .appendQueryParameter(PARAM_SELECTION, selection)
                    .build();
        }
    }
    public static final class NowPlayingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOW_PLAYING)
                .build();
        public static final String TABLE_NAME = PATH_NOW_PLAYING;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_PAGE_NUMBER = PAGE_NUMBER;
        public static Uri buildNowPlayingPageUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

    public static final class UpcomingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_UPCOMING)
                .build();
        public static final String TABLE_NAME = PATH_UPCOMING;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_PAGE_NUMBER = PAGE_NUMBER;
        public static Uri buildUpcomingPageUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }


    public static final class MostPopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOST_POPULAR)
                .build();
        public static final String TABLE_NAME = PATH_MOST_POPULAR;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_PAGE_NUMBER = PAGE_NUMBER;
        public static Uri buildMostPopularPageUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

    public static final class TopRatedEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();
        public static final String TABLE_NAME = PATH_TOP_RATED;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_PAGE_NUMBER = PAGE_NUMBER;
        public static Uri buildTopRatedPageUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        public static final String TABLE_NAME = PATH_FAVORITES;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static Uri buildFavoritesUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

    public static final class RecentEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECENT)
                .build();
        public static final String TABLE_NAME = PATH_RECENT;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static Uri buildRecentUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }
}
