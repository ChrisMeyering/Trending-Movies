
package com.movies.chris.trendingmovies.data.tmdb.model.detail;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoList implements Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Video> videos = null;
    public final static Parcelable.Creator<VideoList> CREATOR = new Creator<VideoList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public VideoList createFromParcel(Parcel in) {
            return new VideoList(in);
        }

        public VideoList[] newArray(int size) {
            return (new VideoList[size]);
        }

    }
    ;

    protected VideoList(Parcel in) {
        in.readList(this.videos, (Video.class.getClassLoader()));
    }

    public VideoList() {
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(videos);
    }

    public int describeContents() {
        return  0;
    }

}
