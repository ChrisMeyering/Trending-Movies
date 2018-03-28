
package com.movies.chris.trendingmovies.data.tmdb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class MoviePoster implements Parcelable
{

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
    public void saveToFavorites(Context context) {
        saveMovie(context, MoviesContract.FavoritesEntry.CONTENT_URI);
    }

    public void saveToRecents(Context context) {
        saveMovie(context, MoviesContract.RecentEntry.CONTENT_URI);
    }
    public ContentValues getPosterContentValues() {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.POSTER_PATH, posterPath);
        posterValues.put(MoviesContract.MOVIE_ID, id);
        return posterValues;
    }

    public boolean isFavorite(Context context) {
        Cursor c = context.getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelection(),
                MoviesContract.FavoritesEntry.getFavoritesWithIdSelectionArgs(id),
                MoviesContract.FavoritesEntry.getSortOrder());
        return (c != null && c.getCount() > 0);

    }

    public void deleteFromFavorites(Context context) {
        Uri uri = MoviesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
        context.getContentResolver().delete(uri, null, null);
    }

    private void saveMovie(Context context, Uri contentUri) {
        context.getContentResolver().insert(contentUri, getPosterContentValues());

    }


    public int describeContents() {
        return  0;
    }

}
