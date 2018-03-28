package com.movies.chris.trendingmovies.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chris on 10/31/17.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.movies.chris.trendingmovies.data.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECENT = "recent";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_UPCOMING = "upcoming";
    public static final String PATH_NOW_PLAYING = "now_playing";

    public static final String PATH_GENRE_IDS = "genre_ids";

    public static final String MOVIE_ID = "movie_id";
    public static final String POSTER_PATH = "poster_path";
    public static final String TITLE = "title";

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
        public static String getGenreNameSelection(){
            return COLUMN_GENRE_NAME + " = ?";
        }
        public static String[] getGenreNameSelectionArgs(String name) {
            return new String[] {name};
        }
    }

//
//    public static final class MovieDetailEntry implements BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
//                .appendPath(PATH_NOW_PLAYING)
//                .build();
//
//        public static final String TABLE_NAME = PATH_MOVIES;
//        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
//        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
//        public static final String COLUMN_TITLE = "title";
//        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
//        public static final String COLUMN_OVERVIEW = "overview";
//        public static final String COLUMN_GENRE_ID = "genre_ids";
//        public static final String COLUMN_GENRE_NAMES = "genre_names";
//        public static final String COLUMN_RELEASE_DATE = "release_date";
//        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
//        public static final String COLUMN_HOMEPAGE = "homepage";
//        public static final String COLUMN_POPULARITY = "popularity";
//        public static final String COLUMN_RUNTIME = "runtime";
//        public static final String COLUMN_TAGLINE = "tagline";
//        public static Uri buildMovieUriWithID(int id) {
//            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
//        }
//
//        public static String[] getProjection(){
//                return new String[]{COLUMN_MOVIE_ID, COLUMN_POSTER_PATH, COLUMN_TITLE,
//                        COLUMN_BACKDROP_PATH, COLUMN_OVERVIEW, COLUMN_GENRE_NAMES,
//                        COLUMN_RELEASE_DATE, COLUMN_VOTE_AVERAGE,
//                };
//        }
//
//        public static String getMovieListSelection(String sortBy) {
//            return sortBy + " <= ?";
//        }
//        public static String[] getMovieListSelectionArgs(int pageNumber) {return new String[]{String.valueOf(pageNumber)};}
//        public static String getMovieListSortOder(String sort_order, int limit, int offset) {
//            String limitOffset = " LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offset);
//            return (sort_order.isEmpty()) ? limitOffset :sort_order + limitOffset;
//        }
//
//        public static String getMovieListSortOder(String sort_order, int limit) {
//            String limitSring = " LIMIT " + String.valueOf(limit);
//            return (sort_order.isEmpty()) ? limitSring :sort_order + limitSring;
//        }
//    }
//
//

    public static final class NowPlayingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOW_PLAYING)
                .build();
        public static final String TABLE_NAME = PATH_NOW_PLAYING;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;
    }

    public static final class MostPopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR)
                .build();
        public static final String TABLE_NAME = PATH_POPULAR;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;
    }

    public static final class TopRatedEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();
        public static final String TABLE_NAME = PATH_TOP_RATED;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;
    }

    public static final class UpcomingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_UPCOMING)
                .build();
        public static final String TABLE_NAME = PATH_UPCOMING;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;
    }
    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        public static final String TABLE_NAME = PATH_FAVORITES;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;

        public static Uri buildFavoritesUriWithID(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
        public static String getFavoritesWithIdSelection() { return COLUMN_MOVIE_ID + " = ?";}
        public static String[] getFavoritesWithIdSelectionArgs(int movieId) {
            return new String[] {String.valueOf(movieId)};
        }
        public static String getSortOrder() {
            return _ID + " DESC";
        }

        public static String getWhereClause() {
            return COLUMN_MOVIE_ID + " = ?";
        }
    }

    public static final class RecentEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECENT)
                .build();
        public static final String TABLE_NAME = PATH_RECENT;
        public static final String COLUMN_MOVIE_ID = MOVIE_ID;
        public static final String COLUMN_POSTER_PATH = POSTER_PATH;
        public static final String COLUMN_TITLE = TITLE;

        public static String getSortOrder() {
            return _ID + " DESC";
        }
    }
}
