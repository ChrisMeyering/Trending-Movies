
package com.movies.chris.trendingmovies.data.tmdb.model.detail;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.movies.chris.trendingmovies.data.tmdb.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail implements Parcelable
{

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("popularity")
    @Expose
    private Float popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("runtime")
    @Expose
    private Integer runtime;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("vote_average")
    @Expose
    private Float voteAverage;
    @SerializedName("reviews")
    @Expose
    private ReviewList reviewList;
    @SerializedName("videos")
    @Expose
    private VideoList videoList;
    public final static Parcelable.Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        public MovieDetail[] newArray(int size) {
            return (new MovieDetail[size]);
        }

    }
    ;

    protected MovieDetail(Parcel in) {
        this.backdropPath = ((String) in.readValue((String.class.getClassLoader())));
        Log.i("MovieDetail", "backdropPath = " + backdropPath);
        genres = new ArrayList<>();
        in.readList(this.genres,(Genre.class.getClassLoader()));
        this.homepage = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.overview = ((String) in.readValue((String.class.getClassLoader())));
        this.popularity = ((Float) in.readValue((Float.class.getClassLoader())));
        this.posterPath = ((String) in.readValue((String.class.getClassLoader())));
        this.releaseDate = ((String) in.readValue((String.class.getClassLoader())));
        this.runtime = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.tagline = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.voteAverage = ((Float) in.readValue((Float.class.getClassLoader())));
        this.reviewList = ((ReviewList) in.readValue((ReviewList.class.getClassLoader())));
        this.videoList = ((VideoList) in.readValue((VideoList.class.getClassLoader())));
    }

    public MovieDetail() {
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getGenreNames() {
        String names = "";
        for (Genre genre: genres) {
            String name = genre.getName();
            names += name;
            if (name.equals(genres.get(genres.size() - 1).getName())) {
                names += ".";
            } else {
                names += ", ";
            }
        }
        return names;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public ReviewList getReviewList() {
        return reviewList;
    }

    public void setReviewList(ReviewList reviewList) {
        this.reviewList = reviewList;
    }

    public VideoList getVideoList() {
        return videoList;
    }

    public void setVideoList(VideoList videoList) {
        this.videoList = videoList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(backdropPath);
        dest.writeList(genres);
        dest.writeValue(homepage);
        dest.writeValue(id);
        dest.writeValue(overview);
        dest.writeValue(popularity);
        dest.writeValue(posterPath);
        dest.writeValue(releaseDate);
        dest.writeValue(runtime);
        dest.writeValue(tagline);
        dest.writeValue(title);
        dest.writeValue(voteAverage);
        dest.writeValue(reviewList);
        dest.writeValue(videoList);
    }

    public int describeContents() {
        return  0;
    }

    public String[] getTrailerKeys() {
        int numVideos = videoList.getVideos().size();
        String[] trailerKeys = new String[numVideos];
        for (int i = 0; i < numVideos; i++) {
            trailerKeys[i] = videoList.getVideos().get(i).getKey();

        }

        return trailerKeys;
    }
}
