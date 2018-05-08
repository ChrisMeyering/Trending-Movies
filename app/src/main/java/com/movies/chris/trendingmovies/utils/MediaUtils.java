package com.movies.chris.trendingmovies.utils;

import android.net.Uri;
import android.view.View;

/**
 * Created by chris on 10/23/17.
 */

public class MediaUtils {
    private static final String TAG = MediaUtils.class.getSimpleName();

    private static final String YOUTUBE_IMG_BASE_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String PARAM_YOUTUBE_QUERY_KEY = "v";

    private static final String TMDB_IMG_BASE_URL = "https://image.tmdb.org/t/p/";


    public static Uri buildYoutubeUri(String video_key) {
        return Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_YOUTUBE_QUERY_KEY, video_key)
                .build();
    }

    public static String buildYoutubeImageURL(String trailerId) {
        return YOUTUBE_IMG_BASE_URL + trailerId + "/0.jpg";
    }


    public static String buildBackdropURL(String path, int width) {
        String imgSize = "w";
        if (width <= 10)
            imgSize += "780";
        else if (width <= 300)
            imgSize += "300";
        else if (width <= 780)
            imgSize += "780";
        else if (width <= 1280)
            imgSize += "1280";
        else
            imgSize = "original";
        return TMDB_IMG_BASE_URL + imgSize + path;
    }

    public static String buildPosterURL(String path, int width) {
        String imgSize = "w";
        if (width <= 10)
            imgSize += "342";
        else if (width <= 92)
            imgSize += "92";
        else if (width <= 154)
            imgSize += "154";
        else if (width <= 185)
            imgSize += "185";
        else if (width <= 342)
            imgSize += "342";
        else if (width <= 500)
            imgSize += "500";
        else if (width <= 780)
            imgSize += "780";
        else
            imgSize = "original";
        return TMDB_IMG_BASE_URL + imgSize + path;
    }

    public static int measureWidth(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }
}
