
package com.movies.chris.trendingmovies.data.tmdb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class MoviePoster implements Parcelable
{

    public static final String TAG = MoviePoster.class.getSimpleName();
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    public final static Parcelable.Creator<MoviePoster> CREATOR = new Creator<MoviePoster>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MoviePoster createFromParcel(Parcel in) {
            return new MoviePoster(in);
        }

        public MoviePoster[] newArray(int size) {
            return (new MoviePoster[size]);
        }

    }
    ;

    protected MoviePoster(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.posterPath = ((String) in.readValue((String.class.getClassLoader())));
    }

    public MoviePoster() {
    }
    public MoviePoster(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(posterPath);
    }

    public static MoviePoster getPoster(Cursor cursor) {
        return new MoviePoster(
                cursor.getInt(cursor.getColumnIndex(MoviesContract.MOVIE_ID)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.POSTER_PATH)));
    }
    public ContentValues getPosterContentValues() {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.POSTER_PATH, posterPath);
        posterValues.put(MoviesContract.MOVIE_ID, id);
        return posterValues;
    }

    public static boolean isFavorite (Context context, Cursor posters) {
        MoviePoster poster = new MoviePoster(
                posters.getInt(posters.getColumnIndex(MoviesContract.MOVIE_ID)),
                posters.getString(posters.getColumnIndex(MoviesContract.POSTER_PATH)));
        return  poster.isFavorite(context);
    }

    public boolean isFavorite(Context context) {
        Cursor c = context.getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelection(),
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelectionArgs(id),
                MoviesContract.FavoritesEntry.getSortOrder());
        boolean ret = (c != null && c.getCount() > 0);
        if (c != null)
            c.close();
        return ret;
    }

    public boolean isRecent(Context context) {
        Cursor c = context.getContentResolver().query(MoviesContract.RecentEntry.CONTENT_URI,
                null,
                MoviesContract.RecentEntry.getRecentsWithIdSelection(),
                MoviesContract.RecentEntry.getRecentsWithIdSelectionArgs(id),
                MoviesContract.RecentEntry.getSortOrder());
        boolean ret = (c != null && c.getCount() > 0);
        if (c != null)
            c.close();
        return ret;
    }

    public void deleteFromFavorites(Context context) {
        Uri uri = MoviesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
        Toast.makeText(context, "Movie removed from favorites", Toast.LENGTH_SHORT).show();
        context.getContentResolver().delete(uri, null, null);
    }

    public void saveToFavorites(Context context) {
        Toast.makeText(context, "Movie added to favorites", Toast.LENGTH_SHORT).show();
        saveMovie(context, MoviesContract.FavoritesEntry.CONTENT_URI);
    }

    public void deleteFromRecents(Context context) {
        Uri uri = MoviesContract.RecentEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
        context.getContentResolver().delete(uri, null, null);
    }
    public void saveToRecents(Context context) {
        saveMovie(context, MoviesContract.RecentEntry.CONTENT_URI);
    }

    private void saveMovie(Context context, Uri contentUri) {
        context.getContentResolver().insert(contentUri, getPosterContentValues());
    }


    public int describeContents() {
        return  0;
    }

}
