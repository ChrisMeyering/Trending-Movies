package com.movies.chris.trendingmovies.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.activity.UI.ReviewAdapter;
import com.movies.chris.trendingmovies.activity.UI.TrailerAdapter;
import com.movies.chris.trendingmovies.data.tmdb.model.detail.MovieDetail;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncTask;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncUtils;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.movies.chris.trendingmovies.utils.MovieUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.movies.chris.trendingmovies.utils.MovieUtils.deleteFromFavorites;
import static com.movies.chris.trendingmovies.utils.MovieUtils.getFavoriteImageResource;
import static com.movies.chris.trendingmovies.utils.MovieUtils.isFavorite;
import static com.movies.chris.trendingmovies.utils.MovieUtils.saveToFavorites;
import static com.movies.chris.trendingmovies.utils.MovieUtils.setFavoriteImageResource;
import static com.movies.chris.trendingmovies.utils.MovieUtils.swapFavoriteImageResource;


public class MovieDetailActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<MovieDetail>,
//        YouTubePlayer.OnInitializedListener,
 implements       TrailerAdapter.TrailerAdapterClickHandler
{
    AppBarLayout appBarLayout;
    ProgressBar pbLoadingBackdrop;
    ImageView ivBackdrop;
    NestedScrollView svParent;
    ProgressBar pbLoadingPoster;
    ImageView ivMoviePoster;
    TextView tvReleaseDate;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    MenuItem favoriteMenu;
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

    private int movieID;
    private String transitionName;
//    private boolean isFavorite;
//    private ReviewAdapter reviewAdapter;
//    private TrailerAdapter trailerAdapter;
//    private ActivityMovieDetailBinding movieDetailBinding;
//    private String currentTrailerKey;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null)
                switch (intent.getAction()) {
                    case MoviesSyncTask.EVENT_MOVIE_DETAIL_RECEIVED:
                        LocalBroadcastManager.getInstance(MovieDetailActivity.this).unregisterReceiver(receiver);
                        if (intent.hasExtra(getString(R.string.key_movie_detail))) {
                            movieDetail = intent.getParcelableExtra(getString(R.string.key_movie_detail));
                            if (movieDetail == null)
                                showError();
                            else
                                bindMovieInfo();
                        }
                        invalidateOptionsMenu();

                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_detail);
        supportPostponeEnterTransition();
        initView();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,
                new IntentFilter(MoviesSyncTask.EVENT_MOVIE_DETAIL_RECEIVED));
        Intent startIntent = getIntent();

        if (savedInstanceState != null) {
            movieDetail = savedInstanceState.getParcelable(getString(R.string.key_movie_detail));
            bindMovieInfo();
            final int[] position = savedInstanceState.getIntArray(getString(R.string.key_scroll_view_postition));
            if (position != null && position.length == 2) {
                svParent.post(new Runnable() {
                    @Override
                    public void run() {
                        svParent.scrollTo(position[0], position[1]);
                        if (position[1] != 0) {
                            appBarLayout.setExpanded(false);
                        }
                    }
                });
            }
        } else if (startIntent != null) {
            if (startIntent.hasExtra(getString(R.string.transition_movie_poster)))
                transitionName = startIntent.getStringExtra(getString(R.string.transition_movie_poster));
            if (startIntent.hasExtra(getString(R.string.key_movie_id))) {
                movieID = startIntent.getIntExtra(getString(R.string.key_movie_id),0);
                pbLoadingDetails.setVisibility(View.VISIBLE);
                MoviesSyncUtils.getTmdbMovieDetail(this, movieID);
            }
        }
//        if (movieDetail == null){
//            showError();
//        } else {
//            bindMovieInfo();
//        }
    }

    private void initView() {
        bindViews();
        initReviewsRV();
        initTrailersRV();
    }

    public void toggleFavorite() {
        Log.i(TAG, "toggle favorite");
        int resID = swapFavoriteImageResource(this, fabFavorite,
                movieDetail.getId(),
                movieDetail.getPosterPath());
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.i(TAG, "onPrepareOptionMenu");
        favoriteMenu.setIcon(fabFavorite.getDrawable());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail, menu);
        favoriteMenu = menu.findItem(R.id.action_favorites);
        favoriteMenu.setIcon(getFavoriteImageResource(this, movieID));
        return true;
    }

    private void bindViews() {
        appBarLayout = findViewById(R.id.app_bar_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        svParent = findViewById(R.id.sv_parent);
        rvReviews = findViewById(R.id.rv_reviews);
        rvTrailers = findViewById(R.id.rv_trailers);
        ivBackdrop = findViewById(R.id.iv_backdrop);
        pbLoadingBackdrop = findViewById(R.id.pb_loading_backdrop);
        ivMoviePoster = findViewById(R.id.iv_movie_poster);
        pbLoadingPoster = findViewById(R.id.pb_loading_poster);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
//        collapsingToolbarLayout.setExpandedTitleColor(
//                getResources().getColor(android.R.color.transparent));

        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.START);
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.BOTTOM|Gravity.END);

        pbLoadingDetails = findViewById(R.id.pb_loading_details);
        fabFavorite = findViewById(R.id.fab_favorite);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
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
        rvReviews.setHasFixedSize(false);
        rvReviews.getLayoutManager().setAutoMeasureEnabled(true);
        reviewAdapter = new ReviewAdapter(this);
        rvReviews.setAdapter(reviewAdapter);

    }

    private void initTrailersRV(){
        rvTrailers.setHasFixedSize(true);
        rvTrailers.setFocusable(false);
        rvTrailers.setNestedScrollingEnabled(true);
//        final RecyclerView.LayoutManager trailerLayoutManager =
//                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        rvTrailers.setLayoutManager(trailerLayoutManager);
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
        switch(item.getItemId()) {
            // When the home button is pressed, take the user back to the VisualizerActivity
            case android.R.id.home:
                Toast.makeText(MovieDetailActivity.this, "transitionName = " +transitionName,
                        Toast.LENGTH_SHORT).show();
                supportFinishAfterTransition();
//                super.onBackPressed();
                return true;
            case R.id.action_favorites:
                toggleFavorite();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishAfterTransition() {
        Intent data = new Intent();
        data.putExtra(getString(R.string.transition_movie_poster), transitionName);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
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
//        pbLoadingPoster.setVisibility(View.VISIBLE);
        pbLoadingPoster.setVisibility(View.INVISIBLE);
    }

    protected void bindMovieInfo() {
        MovieUtils.setFavoriteImageResource(this, fabFavorite,  movieDetail.getId());
        ViewCompat.setTransitionName(ivMoviePoster, transitionName);
        collapsingToolbarLayout.setTitle(movieDetail.getTitle());
        tvReleaseDate.setText(movieDetail.getReleaseDate());
        tvGenres.setText(movieDetail.getGenreNames());
        tvRating.setText(String.valueOf(movieDetail.getVoteAverage()) + "/10");
        tvMovieInfo.setText(movieDetail.getOverview());

        if (isFavorite(this, movieDetail.getId()))
            fabFavorite.setImageResource(R.drawable.ic_star_orange_500_24dp);
        else
            fabFavorite.setImageResource(R.drawable.ic_star_border_orange_500_24dp);

        reviewAdapter.updateData(movieDetail.getReviewList().getReviews());
        if (reviewAdapter.getItemCount() > 0) {
            findViewById(R.id.reviews_layout).setVisibility(View.VISIBLE);
        }
        String [] trailers = movieDetail.getTrailerKeys();
        if (trailers!= null && trailers.length > 0) {
            Log.i(TAG, "number of trailers = " + trailers.length);
            trailerAdapter.setData(trailers);
        } else {
            rvTrailers.setVisibility(View.GONE);
        }
        //    movieDetailBinding.youtubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, this);
        //    movieDetailBinding.youtubePlayerView.setFocusable(true);
        //}else {
        //    movieDetailBinding.youtubePlayerView.setVisibility(View.GONE);
        //    Toast.makeText(this, "No trailers avaliable", Toast.LENGTH_LONG).show();
        //}
        showMovieInfo();
        showImageProgressBars();
        Picasso.get()
                .load(MediaUtils.buildPosterURL(movieDetail.getPosterPath(),
                        MediaUtils.measureWidth(ivMoviePoster)))
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.error)
                .noFade()
                .into(ivMoviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadingPoster.setVisibility(View.INVISIBLE);
                        supportStartPostponedEnterTransition();
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        pbLoadingPoster.setVisibility(View.INVISIBLE);
                        supportStartPostponedEnterTransition();
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

                        Bitmap bitmap = ((BitmapDrawable) ivBackdrop.getDrawable()).getBitmap();
                        if (bitmap != null) {
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette p) {
                                    applyPalette(p);
                                }
                            });
                        }
                        pbLoadingBackdrop.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        pbLoadingBackdrop.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85;
        return Color.HSVToColor(hsv);
    }
    private void applyPalette(Palette p) {

        //Toolbar
        int primary = getResources().getColor(R.color.colorPrimary);
        int dominantColor = p.getDominantColor(p.getMutedColor(primary));
        int darkDominantColor = darkenColor(dominantColor);

        Window window = getWindow();
        if (getWindow() != null) {
            window.setStatusBarColor(darkDominantColor);
        }
        collapsingToolbarLayout.setContentScrimColor(dominantColor);
        collapsingToolbarLayout.setBackgroundColor(dominantColor);
        //FAB
        int lightVibrantColor = p.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = p.getVibrantColor(getResources().getColor(R.color.colorAccent));
        fabFavorite.setRippleColor(lightVibrantColor);
        fabFavorite.setBackgroundTintList(ColorStateList.valueOf(darkenColor(vibrantColor)));

    }

    /*
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
            if (!b) {
                movieDetailBinding.youtubePlayerView.setVisibility(View.VISIBLE);
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
                        movieDetailBinding.youtubePlayerView.setVisibility(View.GONE);
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
*/
    @Override
    public void watchTrailer(String trailerKey) {
        Intent intent = new Intent(this, WatchTrailerActivity.class);
        intent.putExtra(getString(R.string.key_trailer_id), trailerKey);
        startActivity(intent);
    }
}