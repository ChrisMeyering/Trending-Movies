package com.movies.chris.trendingmovies.data.tmdb.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class MovieList implements Parcelable
{

    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;
    @SerializedName("results")
    @Expose
    public List<MoviePoster> moviePosters = null;
    public final static Parcelable.Creator<MovieList> CREATOR = new Creator<MovieList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MovieList createFromParcel(Parcel in) {
            return new MovieList(in);
        }

        public MovieList[] newArray(int size) {
            return (new MovieList[size]);
        }

    }
    ;

    protected MovieList(Parcel in) {
        this.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.moviePosters, MoviePoster.class.getClassLoader());
    }

    public MovieList() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeValue(totalPages);
        dest.writeList(moviePosters);
    }
    private ContentValues[] getPosterListContentValues() {
        List<ContentValues> posterValues = new ArrayList<>();
        for (MoviePoster moviePoster : moviePosters) {
            posterValues.add(moviePoster.getPosterContentValues());
        }
        return posterValues.toArray(new ContentValues[posterValues.size()]);
    }

    public int getSize() {
        return moviePosters.size();
    }

    public int save(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            try {
                return contentResolver.bulkInsert(uri, getPosterListContentValues());
            } catch (Exception e) {
                Log.i("MovieList.save", "failed to save " + uri.toString());
            }
        }
        return -1;
    }
    public int describeContents() {
        return  0;
    }

}
