package com.movies.chris.trendingmovies.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.UI.MovieListAdapter;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.MovieList;
import com.movies.chris.trendingmovies.data.tmdb.model.MoviePoster;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncTask;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener,
        MovieListAdapter.MovieAdapterClickHandler
{


    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final int LOADER_FAVORITES_ID = 11;
    private static final int LOADER_RECENTS_ID = 12;
    private static final int LOADER_NOW_PLAYING_ID = 13;
    private static final int LOADER_POPULAR_ID = 14;
    private static final int LOADER_UPCOMING_ID = 15;
    private static final int LOADER_TOP_RATED_ID = 16;

    private static final String SORT_BY_RATING = MoviesContract.PATH_TOP_RATED;
    private static final String SORT_BY_POPULARITY = MoviesContract.PATH_POPULAR;
    private static final String SORT_NOW_PLAYING = MoviesContract.PATH_NOW_PLAYING;
    private static final String SORT_UPCOMING = MoviesContract.PATH_UPCOMING;
    private static final String SORT_FAVORITES = MoviesContract.PATH_FAVORITES;
    private static final String SORT_RECENT = MoviesContract.PATH_RECENT;

    private boolean isLoading = false;
    private int previousTotalCount = 0;
    private String sortBy = SORT_BY_RATING;
    private int pageCount = 1;
    private MovieListAdapter movieAdapter;
    private final int RV_VISIBLE_THRESHOLD = 7;
    @BindView(R.id.rv_movie_posters)
    RecyclerView rvMoviePosters;
    @BindView(R.id.pb_loading_movie_list)
    ProgressBar pbLoadingMovieList;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.et_search_by_name)
    EditText etSearchByName;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isLoading = false;
            if (intent.getBooleanExtra(getString(R.string.key_sync_success), false)) {
                Log.i(TAG, "Sync successful");
                pageCount++;
                makeSortedMovieSearch(getLoaderID());

            } else {
                Log.i(TAG, "Error: Sync failed");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initView();
        makeSortedMovieSearch(getLoaderID());
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(MoviesSyncTask.EVENT_SYNC_COMPLETE));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private int getLoaderID() {
        switch(sortBy) {
            case SORT_BY_RATING:
                return LOADER_TOP_RATED_ID;
            case SORT_BY_POPULARITY:
                return LOADER_POPULAR_ID;
            case SORT_NOW_PLAYING:
                return LOADER_NOW_PLAYING_ID;
            case SORT_UPCOMING:
                return LOADER_UPCOMING_ID;
            case SORT_FAVORITES:
                return LOADER_FAVORITES_ID;
            case SORT_RECENT:
                return LOADER_RECENTS_ID;
            default:
                throw new UnsupportedOperationException("Sort option not implemented: " + sortBy);
        }
    }
    private Uri getQueryUri() {
        switch(sortBy) {
            case SORT_BY_RATING:
                return MoviesContract.TopRatedEntry.CONTENT_URI;
            case SORT_BY_POPULARITY:
                return MoviesContract.MostPopularEntry.CONTENT_URI;
            case SORT_NOW_PLAYING:
                return MoviesContract.NowPlayingEntry.CONTENT_URI;
            case SORT_UPCOMING:
                return MoviesContract.UpcomingEntry.CONTENT_URI;
            case SORT_FAVORITES:
                return MoviesContract.FavoritesEntry.CONTENT_URI;
            case SORT_RECENT:
                return MoviesContract.RecentEntry.CONTENT_URI;
            default:
                throw new UnsupportedOperationException("URI option not implemented: " + sortBy);
        }
    }
    private void makeSortedMovieSearch(int loaderID){
        Log.i(TAG + ".makeSortedMovieSearch", "Initializing loader");
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> cursorLoader = loaderManager.getLoader(loaderID);
        if (cursorLoader == null) {
            Log.i(TAG + ".makeSortedMovieSearch", "Loader not found. Initializing loader" + loaderID);
            loaderManager.initLoader(loaderID, null, this);
        } else {
            Log.i(TAG + ".makeSortedMovieSearch", "Loader found. Restarting loader" + loaderID);
            loaderManager.restartLoader(loaderID, null, this);
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        initDrawerLayout();
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvMoviePosters.setHasFixedSize(true);
        rvMoviePosters.setItemViewCacheSize(30);
        rvMoviePosters.setDrawingCacheEnabled(true);
//        rvMoviePosters.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        movieAdapter = new MovieListAdapter(this, this, pageCount);
        rvMoviePosters.setAdapter(movieAdapter);
        final RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, 2);
        //TODO: Utility.numOfGridColumns(this));
        rvMoviePosters.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true);
        rvMoviePosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy >= 0) {
                    GridLayoutManager layoutManager =
                            (GridLayoutManager)rvMoviePosters.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + RV_VISIBLE_THRESHOLD) {
                        if (!isLoading) {
                            Log.i(TAG + ".onScrolled", "Loading new page.");
                            loadNewPage();
                        }
                    }
                }
            }

            private void loadNewPage() {
                isLoading = true;
                Log.i(TAG + ".loadNewPage",
                        "num items increased from " +movieAdapter.getItemCount()
                        + " to " + (movieAdapter.getItemCount() + 20));
                Log.i(TAG + ".loadNewPage",
                        "cursor size = " + movieAdapter.getCursorSize());
                if (movieAdapter.getItemCount() < movieAdapter.getCursorSize()) {
                    pageCount++;
                    rvMoviePosters.post(new Runnable() {
                        public void run() {
                            Log.i(TAG + ".loadNewPage", "setting page count");
                            movieAdapter.setPageCount(pageCount);
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
                    isLoading = false;
                } else {
                    MoviesSyncUtils.getTmdbMovieList(MovieListActivity.this,
                            getQueryUri(),
                            sortBy,
                            pageCount);
                }
            }

        });
//        rvMoviePosters.setOnTouchListener(new RecyclerView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                etSearchByName.clearFocus();
//                Utility.hideKeyboard(MovieListActivity.this, getCurrentFocus().getWindowToken());
//                return false;
//            }
//        });
    }

    private void initDrawerLayout() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
//                etSearchByName.clearFocus();
//                Utility.hideKeyboard(MainActivity.this, getCurrentFocus().getWindowToken());
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        switch (sortBy) {
            case SORT_BY_RATING:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_top_rated))
                        .setChecked(true);
                break;
            case SORT_BY_POPULARITY:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_popular))
                        .setChecked(true);
                break;
            case SORT_NOW_PLAYING:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_now_playing))
                        .setChecked(true);
                break;
            case SORT_UPCOMING:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_upcoming))
                        .setChecked(true);
                break;
            case SORT_RECENT:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_recents))
                        .setChecked(true);
                break;
            case SORT_FAVORITES:
                navView.getMenu()
                        .getItem(getResources().getInteger(R.integer.drawer_index_favorites))
                        .setChecked(true);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_pop:
                if (!sortBy.equals(SORT_BY_POPULARITY)) {
                    sortBy = SORT_BY_POPULARITY;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_POPULAR_ID);
                }
                break;
            case R.id.action_sort_rating:
                if (!sortBy.equals(SORT_BY_RATING)) {
                    sortBy = SORT_BY_RATING;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_TOP_RATED_ID);
                }
                break;
            case R.id.action_now_playing:
                if (!sortBy.equals(SORT_NOW_PLAYING)) {
                    sortBy = SORT_NOW_PLAYING;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_NOW_PLAYING_ID);
                }
                break;
            case R.id.action_upcoming:
                if (!sortBy.equals(SORT_UPCOMING)) {
                    sortBy = SORT_UPCOMING;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_UPCOMING_ID);
                }
                break;
            case R.id.action_view_favorites:
                if (!sortBy.equals(SORT_FAVORITES)) {
                    sortBy = SORT_FAVORITES;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_FAVORITES_ID);
                }
                break;
            case R.id.action_view_recents:
                if (!sortBy.equals(SORT_RECENT)) {
                    sortBy = SORT_RECENT;
                    resetRecyclerView();
                    makeSortedMovieSearch(LOADER_RECENTS_ID);
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        getString(R.string.exception_drawer_item_not_implemented));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetRecyclerView() {
        rvMoviePosters.scrollToPosition(0);
        pageCount = 1;
        previousTotalCount = 0;
        Log.i(TAG + ".resetRecyclerView", "setting page count");
        movieAdapter.setPageCount(pageCount);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri;
        String[] projection = null;
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        switch (id) {
            case LOADER_FAVORITES_ID:
                queryUri = MoviesContract.FavoritesEntry.CONTENT_URI;
                sortOrder = MoviesContract.FavoritesEntry.getSortOrder();
                break;
            case LOADER_RECENTS_ID:
                queryUri = MoviesContract.RecentEntry.CONTENT_URI;
                sortOrder = MoviesContract.RecentEntry.getSortOrder();
                break;
            case LOADER_POPULAR_ID:
                queryUri = MoviesContract.MostPopularEntry.CONTENT_URI;
                break;
            case LOADER_NOW_PLAYING_ID:
                queryUri = MoviesContract.NowPlayingEntry.CONTENT_URI;
                break;
            case LOADER_TOP_RATED_ID:
                queryUri = MoviesContract.TopRatedEntry.CONTENT_URI;
                break;
            case LOADER_UPCOMING_ID:
                queryUri = MoviesContract.UpcomingEntry.CONTENT_URI;
                break;
            default:
                throw new RuntimeException(getString(R.string.loader_not_implemented, id));
        }

        return new CursorLoader(MovieListActivity.this,
                queryUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && !data.isClosed()) {

            Log.i(TAG + ".onLoadFinished", "data not null");
            switch (loader.getId()) {
                case LOADER_FAVORITES_ID:
                case LOADER_RECENTS_ID:
                    break;
                default:
                    if (data.getCount() == 0) {
                        Log.i(TAG + ".onLoadFinished", "data empty");
                        Uri queryUri = null;
                        switch (loader.getId()) {
                            case LOADER_POPULAR_ID:
                                queryUri = MoviesContract.MostPopularEntry.CONTENT_URI;
                                break;
                            case LOADER_NOW_PLAYING_ID:
                                queryUri = MoviesContract.NowPlayingEntry.CONTENT_URI;
                                break;
                            case LOADER_TOP_RATED_ID:
                                queryUri = MoviesContract.TopRatedEntry.CONTENT_URI;
                                break;
                            case LOADER_UPCOMING_ID:
                                queryUri = MoviesContract.UpcomingEntry.CONTENT_URI;
                                break;
                        }
                        MoviesSyncUtils.getTmdbMovieList(this, queryUri, sortBy);
                    } else {
//                      showPosters();
                        data.setNotificationUri(getContentResolver(), getQueryUri());
                        movieAdapter.swapCursor(data);
                        Log.i(TAG + ".onLoadFinished", "setting page count");
                        movieAdapter.setPageCount(pageCount);
                        movieAdapter.notifyDataSetChanged();

                    }
                    break;
            }
        } else {
            Log.i(TAG + ".onLoadFinished", "data is null");
//            showError();
        }
        pbLoadingMovieList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onClick(View view, MoviePoster poster) {
        etSearchByName.clearFocus();
//        Utility.hideKeyboard(MainActivity.this, getCurrentFocus().getWindowToken());
        int id = view.getId();
        Toast.makeText(this, "Movie " + poster.id + " selected.",
                Toast.LENGTH_SHORT).show();
        switch (id) {
            case R.id.iv_poster:
//                Intent intent = new Intent(this, MovieDetailActivity.class);
//                intent.putExtra(getString(R.string.MOVIE_ID_KEY), movie.getId());
//                intent.putExtra(getString(R.string.IS_FAVORITE), isFavorite(movie.getId()));
//                startActivity(intent);
                break;
            case R.id.ib_favorite:
                if (poster.isFavorite(this)) {
                    poster.deleteFromFavorites(this);
                } else {
                    poster.saveToFavorites(this);
                }
                break;
        }
    }
}
