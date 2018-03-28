package com.movies.chris.trendingmovies.data.tmdb.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenreList implements Parcelable
{

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    public final static Parcelable.Creator<GenreList> CREATOR = new Creator<GenreList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public GenreList createFromParcel(Parcel in) {
            return new GenreList(in);
        }

        public GenreList[] newArray(int size) {
            return (new GenreList[size]);
        }

    }
    ;

    protected GenreList(Parcel in) {
        in.readList(this.genres, (Genre.class.getClassLoader()));
    }

    public GenreList() {
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(genres);
    }

    public int describeContents() {
        return  0;
    }

}
