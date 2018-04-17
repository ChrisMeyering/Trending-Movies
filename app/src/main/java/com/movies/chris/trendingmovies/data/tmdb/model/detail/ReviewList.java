
package com.movies.chris.trendingmovies.data.tmdb.model.detail;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewList implements Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Review> reviews = null;
    public final static Parcelable.Creator<ReviewList> CREATOR = new Creator<ReviewList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ReviewList createFromParcel(Parcel in) {
            return new ReviewList(in);
        }

        public ReviewList[] newArray(int size) {
            return (new ReviewList[size]);
        }

    }
    ;

    protected ReviewList(Parcel in) {
        in.readList(this.reviews, (Review.class.getClassLoader()));
    }

    public ReviewList() {
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(reviews);
    }

    public int describeContents() {
        return  0;
    }

}
