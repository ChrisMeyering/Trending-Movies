package com.movies.chris.trendingmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.activity.UI.ReviewAdapter;
import com.movies.chris.trendingmovies.activity.UI.TrailerAdapter;
import com.movies.chris.trendingmovies.data.tmdb.model.detail.MovieDetail;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncUtils;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.movies.chris.trendingmovies.utils.MovieUtils.deleteFromFavorites;
import static com.movies.chris.trendingmovies.utils.MovieUtils.isFavorite;
import static com.movies.chris.trendingmovies.utils.MovieUtils.saveToFavorites;


public class MovieDetailActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<MovieDetail>,
//        YouTubePlayer.OnInitializedListener,
 implements       TrailerAdapter.TrailerAdapterClickHandler
{
    ProgressBar pbLoadingBackdrop;
    ImageView ivBackdrop;
    ScrollView svParent;
    ProgressBar pbLoadingPoster;
    ImageView ivMoviePoster;
    TextView tvReleaseDate;
    Toolbar toolbar;
    ProgressBar pbLoadingDetails;
    FloatingActionButton fabFavorite;
    TextView tvMovieError;
    TextView tvGenres;
    TextView tvRating;
    TextView tvMovieInfo;
    RecyclerView rvTrailers;
    TrailerAdapter trailerAdapter;
    RecyclerView rvReviews;
    ReviewAdapter reviewAdapter;

    String TAG = MovieDetailActivity.class.getSimpleName();
    MovieDetail movieDetail = null;

//    private int movieID;
//    private boolean isFavorite;
//    private ReviewAdapter reviewAdapter;
//    private TrailerAdapter trailerAdapter;
//    private ActivityMovieDetailBinding movieDetailBinding;
//    private String currentTrailerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_detail);
        initView();

        Intent startIntent = getIntent();

        if (savedInstanceState != null) {
            movieDetail = savedInstanceState.getParcelable(getString(R.string.key_movie_detail));
            final int[] position = savedInstanceState.getIntArray(getString(R.string.key_scroll_view_postition));
            if (position != null && position.length == 2) {
                svParent.post(new Runnable() {
                    @Override
                    public void run() {
                        svParent.scrollTo(position[0], position[1]);
                    }
                });
            }
        } else if (startIntent != null) {
            if (startIntent.hasExtra(getString(R.string.key_movie_detail)))
                movieDetail = startIntent.getParcelableExtra(getString(R.string.key_movie_detail));
        }
        if (movieDetail == null){
            showError();
        } else {
            bindMovieInfo();
        }
    }

    private void initView() {
        bindViews();
        initReviewsRV();
        initTrailersRV();
    }

    private void bindViews() {
        svParent = findViewById(R.id.sv_parent);
        rvReviews = findViewById(R.id.rv_reviews);
        rvTrailers = findViewById(R.id.rv_trailers);
        ivBackdrop = findViewById(R.id.iv_backdrop);
        pbLoadingBackdrop = findViewById(R.id.pb_loading_backdrop);
        ivMoviePoster = findViewById(R.id.iv_movie_poster);
        pbLoadingPoster = findViewById(R.id.pb_loading_poster);
        toolbar = findViewById(R.id.toolbar);
        pbLoadingDetails = findViewById(R.id.pb_loading_details);
        fabFavorite = findViewById(R.id.fab_favorite);
        tvReleaseDate = findViewById(R.id.tv_release_date);
        tvGenres = findViewById(R.id.tv_genres);
        tvRating = findViewById(R.id.tv_rating);
        tvMovieError = findViewById(R.id.tv_movie_error);
        tvMovieInfo = findViewById(R.id.tv_movie_info);
    }

    private void initReviewsRV() {
        rvReviews.setDrawingCacheEnabled(true);
        rvReviews.setFocusable(false);
        rvReviews.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(reviewLayoutManager);
        reviewLayoutManager.setAutoMeasureEnabled(true);
        reviewAdapter = new ReviewAdapter(this);
        rvReviews.setAdapter(reviewAdapter);
    }

    private void initTrailersRV(){
        rvTrailers.setHasFixedSize(true);
        rvTrailers.setFocusable(false);
        rvTrailers.setNestedScrollingEnabled(true);
        final RecyclerView.LayoutManager trailerLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrailers.setLayoutManager(trailerLayoutManager);
        trailerLayoutManager.setAutoMeasureEnabled(true);
        trailerAdapter =  new TrailerAdapter(this);
        rvTrailers.setAdapter(trailerAdapter);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rvTrailers);
    }

    private void getMovieDetails() {
        MoviesSyncUtils.getTmdbMovieDetail(this, movieDetail.getId());
    }

    private void showProgressBar() {
        pbLoadingDetails.setVisibility(View.VISIBLE);
//        detailsGroup.setVisibility(View.INVISIBLE);
    }

    private void showMovieInfo() {
//        detailsGroup.setVisibility(View.VISIBLE);
        tvMovieError.setVisibility(View.INVISIBLE);
        pbLoadingDetails.setVisibility(View.INVISIBLE);
    }

    private void showError() {
//        detailsGroup.setVisibility(View.INVISIBLE);
        pbLoadingDetails.setVisibility(View.INVISIBLE);
        tvMovieError.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.key_movie_detail), movieDetail);
        outState.putIntArray(getString(R.string.key_scroll_view_postition),
                new int[] {svParent.getScrollX(), svParent.getScrollY()});
    }

    private void showImageProgressBars() {
        pbLoadingBackdrop.setVisibility(View.VISIBLE);
        pbLoadingPoster.setVisibility(View.VISIBLE);
    }

    protected void bindMovieInfo() {
        toolbar.setTitle(movieDetail.getTitle());
        tvReleaseDate.setText(movieDetail.getReleaseDate());
        tvGenres.setText(movieDetail.getGenreNames());
        tvRating.setText(String.valueOf(movieDetail.getVoteAverage()) + "/10");
        tvMovieInfo.setText(movieDetail.getOverview());
        if (isFavorite(this, movieDetail.getId()))
            fabFavorite.setImageResource(R.drawable.ic_star_orange_500_24dp);
        else
            fabFavorite.setImageResource(R.drawable.ic_star_border_grey_600_24dp);

        reviewAdapter.updateData(movieDetail.getReviewList().getReviews());
        if (reviewAdapter.getItemCount() > 0) {
//            reviewsGroup.setVisibility(View.VISIBLE);
        }
        String [] trailers = movieDetail.getTrailerKeys();
        if (trailers!= null && trailers.length > 0) {
            Log.i(TAG, "number of trailers = " + trailers.length);
            trailerAdapter.setData(trailers);
        } else {
            rvTrailers.setVisibility(View.GONE);
        }
        //    movieDetailBinding.youtubePlayer.initialize(BuildConfig.YOUTUBE_API_KEY, this);
        //    movieDetailBinding.youtubePlayer.setFocusable(true);
        //}else {
        //    movieDetailBinding.youtubePlayer.setVisibility(View.GONE);
        //    Toast.makeText(this, "No trailers avaliable", Toast.LENGTH_LONG).show();
        //}
        showMovieInfo();
        showImageProgressBars();
        Picasso.get()
                .load(MediaUtils.buildPosterURL(movieDetail.getPosterPath(),
                        MediaUtils.measureWidth(ivMoviePoster)))
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.error)
                .into(ivMoviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadingPoster.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        pbLoadingPoster.setVisibility(View.INVISIBLE);
                    }
                });

        Picasso.get()
                .load(MediaUtils.buildBackdropURL(movieDetail.getBackdropPath(),
                        MediaUtils.measureWidth(ivBackdrop)))
                .placeholder(R.drawable.backdrop_placeholder)
                .error(R.drawable.error)
                .into(ivBackdrop, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadingBackdrop.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        pbLoadingBackdrop.setVisibility(View.INVISIBLE);
                    }
                });

    }
/*
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            movieDetailBinding.youtubePlayer.setVisibility(View.VISIBLE);
            youTubePlayer.cueVideo(currentTrailerKey);

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
                    youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                        @Override
                        public void onPlaying() {
                            movieDetailBinding.ibCloseYoutubePlayer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPaused() {
                            movieDetailBinding.ibCloseYoutubePlayer.setVisibility(View.VISIBLE);
                            movieDetailBinding.ibCloseYoutubePlayer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    closePlayer();
                                }
                            });
                        }

                        @Override
                        public void onStopped() {
                        }

                        @Override
                        public void onBuffering(boolean b) {

                        }

                        @Override
                        public void onSeekTo(int i) {

                        }
                    });

                }


                @Override
                public void onVideoEnded() {
                    closePlayer();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }

                private void closePlayer() {
                    movieDetailBinding.ibCloseYoutubePlayer.setVisibility(View.GONE);
                    movieDetailBinding.youtubePlayer.setVisibility(View.GONE);
                    youTubePlayer.release();
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String error = String.format(getString(R.string.youtube_player_error), youTubeInitializationResult.toString());
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        watchTrailer();
    }

    public void watchTrailer() {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + currentTrailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeUri(currentTrailerKey));
        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        } else {
            Toast.makeText(this, "Unable to complete request", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void watchTrailer(String trailerKey) {
        currentTrailerKey = trailerKey;
        movieDetailBinding.youtubePlayer.initialize(BuildConfig.YOUTUBE_API_KEY, this);

    }
*/
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_favorite:
                if (isFavorite(this, movieDetail.getId())) {
                    deleteFromFavorites(this, movieDetail.getId());
                    fabFavorite.setImageResource(R.drawable.ic_star_border_grey_600_24dp);
                } else {
                    saveToFavorites(this, movieDetail.getId(), movieDetail.getPosterPath());
                    fabFavorite.setImageResource(R.drawable.ic_star_orange_500_24dp);
                }
                break;
        }
    }

    @Override
    public void watchTrailer(String trailerKey) {

    }
}