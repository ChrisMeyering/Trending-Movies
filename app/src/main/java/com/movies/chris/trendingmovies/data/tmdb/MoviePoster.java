package com.movies.chris.trendingmovies.data.tmdb;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.movies.chris.trendingmovies.data.provider.MoviesContract;

/**
 * Created by chris on 9/27/17.
 */

public class MoviePoster implements Parcelable {
    public static final Creator<MoviePoster> CREATOR = new Creator<MoviePoster>() {
        @Override
        public MoviePoster createFromParcel(Parcel in) {
            return new MoviePoster(in);
        }

        @Override
        public MoviePoster[] newArray(int size) {
            return new MoviePoster[size];
        }
    };
    private final int id;
    private final String posterPath;
    ImageView posterImage;

    public MoviePoster(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    protected MoviePoster(Parcel in) {
        id = in.readInt();
        posterPath = in.readString();
    }

    public void setPosterImage(ImageView image) {
        posterImage = image;
    }

    public int getId() {
        return id;
    }

    public void saveToFavorites(Context context) {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID, id);
        context.getContentResolver().insert(MoviesContract.FavoritesEntry.CONTENT_URI, posterValues);
    }

    public void deleteFromFavorites(Context context) {
        Uri uri = MoviesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
        context.getContentResolver().delete(uri, null, null);
    }

    public void saveToRecents(Context context) {
        ContentValues posterValues = new ContentValues();
        posterValues.put(MoviesContract.RecentEntry.COLUMN_MOVIE_ID, id);
        context.getContentResolver().insert(MoviesContract.RecentEntry.CONTENT_URI, posterValues);
    }

//    public void saveToCurrent(Context context) {
//        ContentValues posterValues = new ContentValues();
//        posterValues.put(MoviesContract.CurrentPageEntry.COLUMN_POSTER_PATH, posterPath);
//        posterValues.put(MoviesContract.CurrentPageEntry.COLUMN_MOVIE_ID, id);
//        context.getContentResolver().insert(MoviesContract.CurrentPageEntry.CONTENT_URI, posterValues);
//
//    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
    }

}
