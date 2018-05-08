package com.movies.chris.trendingmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.movies.chris.trendingmovies.BuildConfig;
import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.utils.MediaUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WatchTrailerActivity
        extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {
    @BindView(R.id.youtube_player)
    YouTubePlayerView youtubePlayerView;
    YouTubePlayer youTubePlayer;
    String trailerKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_trailer);
        ButterKnife.bind(this);
        Intent startIntent = getIntent();
        trailerKey = startIntent.getStringExtra(getString(R.string.key_trailer_id));
        youtubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, this);
    }

    @Override
    protected void onDestroy() {
        if (youTubePlayer != null)
            youTubePlayer.release();
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
//        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
//        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(String s) {
                youTubePlayer.play();
            }

            @Override
            public void onAdStarted() {
            }

            @Override
            public void onVideoStarted() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
            }
        });
        if (trailerKey != null) {
            if (b) {
                youTubePlayer.play();
            } else {
                youTubePlayer.cueVideo(trailerKey);
            }
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String error = String.format(getString(R.string.youtube_player_error), youTubeInitializationResult.toString());
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        implicitWatchTrailer();
    }

    private void implicitWatchTrailer() {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, MediaUtils.buildYoutubeUri(trailerKey));
        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        } else {
            Toast.makeText(this, "Unable to complete request.", Toast.LENGTH_LONG);
        }
    }
}
