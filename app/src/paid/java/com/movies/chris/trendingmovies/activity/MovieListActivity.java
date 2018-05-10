package com.movies.chris.trendingmovies.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.movies.chris.trendingmovies.BuildConfig;
import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.activity.UI.MovieListAdapter;
import com.movies.chris.trendingmovies.activity.UI.Utility;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MoviePoster;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncJobService;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncTask;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncUtils;
import com.movies.chris.trendingmovies.utils.MovieUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.concurrent.TimeUnit.HOURS;

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
    private static final int LOADER_MOVIE_NAME_ID = 17;

    private static final String SORT_BY_RATING = MoviesContract.PATH_TOP_RATED;
    private static final String SORT_BY_POPULARITY = MoviesContract.PATH_POPULAR;
    private static final String SORT_NOW_PLAYING = MoviesContract.PATH_NOW_PLAYING;
    private static final String SORT_UPCOMING = MoviesContract.PATH_UPCOMING;
    private static final String SORT_FAVORITES = MoviesContract.PATH_FAVORITES;
    private static final String SORT_RECENT = MoviesContract.PATH_RECENT;
    private static final int RC_SIGN_IN = 9001;
    Parcelable layoutManagerSavedState = null;
    SignInButton btnGoogleSignin;
    TextView tvUserEmail;
    TextView tvUserName;
    ProgressBar pbLoadingUserInfo;
    ImageView ivUserPicture;
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
    ImageView sharedView = null;
    private FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    private boolean isLoading = false;
    private boolean resetAnimation = true;
    private SharedPreferences preferences;
    private String sortBy;
    private MovieListAdapter movieAdapter;
    private int RV_VISIBLE_THRESHOLD = 7;
    private RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (!recyclerView.canScrollVertically(1) && !isLoading) {
                Log.i(TAG, "bottom reached, loading more");
                loadNewPage();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isLoading) {
                GridLayoutManager layoutManager =
                        (GridLayoutManager) rvMoviePosters.getLayoutManager();
                int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                int currentTotalCount = layoutManager.getItemCount();
                if ((dx == 0 && dy == 0)
                        || (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING))
                    return;
                if (dy > 0) {
                    if (currentTotalCount <= lastItem + RV_VISIBLE_THRESHOLD) {
                        Log.i(TAG, "visible threshold reached, loading more");
                        loadNewPage();
                    }
                }
            }
        }

        synchronized void loadNewPage() {
            switch (sortBy) {
                case SORT_FAVORITES:
                case SORT_RECENT:
                    break;
                case SORT_UPCOMING:
                case SORT_NOW_PLAYING:
                    int itemCount = movieAdapter.getItemCount();
                    //TMDB Upcoming and Now playing data is inconsistent and will have
                    //duplicate movie ids (it has at least two different lists for the same url
                    //aka: two identical urls can have different results
                    if (itemCount >= 100 &&
                            itemCount%Utility.numOfGridColumns(MovieListActivity.this) ==0)
                        break;
                default:
                    isLoading = true;
                    Log.i(TAG, "new query: \n" + getQueryUri() + "\n page number = " + movieAdapter.getItemCount() / 20
                            + "\n number of elements in adapter = " + movieAdapter.getItemCount());
                    MoviesSyncUtils.getTmdbMovieList(MovieListActivity.this,
                            getQueryUri(),
                            sortBy,
                            movieAdapter.getNextPageNumber());
                    break;
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null)
            switch (intent.getAction()) {
                case MoviesSyncTask.EVENT_SYNC_COMPLETE:
                    if (intent.getBooleanExtra(getString(R.string.key_sync_success), false)) {
                        Log.i(TAG, "Sync successful");
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG, "Error: Sync failed");
                    }
                    isLoading = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initJobDispatcher();
        initView();
        initGoogleSignIn();
        makeSortedMovieSearch();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initJobDispatcher() {
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job job = dispatcher.newJobBuilder()
                .setService(MoviesSyncJobService.class)
                .setTag(MoviesSyncJobService.ACTION_START_DELETION)
                .setRecurring(true)
                .setReplaceCurrent(false)
                .setTrigger(Trigger.executionWindow((int)HOURS.toSeconds(4),(int)HOURS.toSeconds(7)))
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_IDLE)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        dispatcher.mustSchedule(job);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            layoutManagerSavedState = savedInstanceState.
                    getParcelable(getString(R.string.state_key_layout_manager));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            updateUserInfo(user);
        }
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,
                new IntentFilter(MoviesSyncTask.EVENT_SYNC_COMPLETE));
        localBroadcastManager.registerReceiver(receiver,
                new IntentFilter(MoviesSyncTask.EVENT_MOVIE_DETAIL_RECEIVED));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        preferences.edit().putString(getString(R.string.pref_key_sort_by), sortBy).apply();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.state_key_layout_manager),
                rvMoviePosters.getLayoutManager().onSaveInstanceState());
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
                return LOADER_MOVIE_NAME_ID;
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
                return MoviesContract.MovieNameEntry.CONTENT_URI;
        }
    }

    private void makeSortedMovieSearch(){
        pbLoadingMovieList.setVisibility(View.VISIBLE);
        LoaderManager loaderManager = getSupportLoaderManager();
        int loaderID = getLoaderID();
        Loader<Cursor> cursorLoader = loaderManager.getLoader(loaderID);
        if (cursorLoader == null) {
            loaderManager.initLoader(loaderID, null, this);
        } else {
            loaderManager.restartLoader(loaderID, null, this);
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        View header = navView.getHeaderView(0);
        tvUserEmail = header.findViewById(R.id.tv_user_email);
        tvUserName = header.findViewById(R.id.tv_user_name);
        ivUserPicture = header.findViewById(R.id.iv_user_picture);
        pbLoadingUserInfo = header.findViewById(R.id.pb_loading_user_info);
        btnGoogleSignin = header.findViewById(R.id.btn_google_signin);
        btnGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieListActivity.this, "signin clicked", Toast.LENGTH_SHORT).show();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                pbLoadingUserInfo.setVisibility(View.VISIBLE);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        RV_VISIBLE_THRESHOLD = Utility.getVisibleThreshold(MovieListActivity.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortBy = preferences.getString(getString(R.string.pref_key_sort_by), SORT_BY_RATING);
        etSearchByName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ){
                    etSearchByName.clearFocus();
                    Utility.hideKeyboard(MovieListActivity.this, getCurrentFocus().getWindowToken());
                    String search = etSearchByName.getText().toString().trim();
                    if (!search.isEmpty()) {
                        if (!sortBy.equals(search)) {
                            Utility.deleteMovieNameTable(MovieListActivity.this);
                        }
                        Utility.uncheckAllMenuItems(navView);
                        sortBy = search;
                        resetRecyclerView();
                        makeSortedMovieSearch();
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        initDrawerLayout();
        initRecyclerView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        rvMoviePosters.setHasFixedSize(true);
        rvMoviePosters.setItemViewCacheSize(30);
        rvMoviePosters.setDrawingCacheEnabled(true);
        movieAdapter = new MovieListAdapter(this);
        rvMoviePosters.setAdapter(movieAdapter);
        final RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, Utility.numOfGridColumns(this));
        rvMoviePosters.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true);
        rvMoviePosters.scheduleLayoutAnimation();
        rvMoviePosters.addOnScrollListener(rvScrollListener);
        rvMoviePosters.setOnTouchListener(new RecyclerView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etSearchByName.clearFocus();
                Utility.hideKeyboard(MovieListActivity.this, getCurrentFocus().getWindowToken());
                return false;
            }
        });
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
                etSearchByName.clearFocus();
                Utility.hideKeyboard(MovieListActivity.this, getCurrentFocus().getWindowToken());
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
            default:
                etSearchByName.setText(sortBy);
        }
    }

    @Override
    public void onBackPressed() {
        etSearchByName.clearFocus();
        Utility.hideKeyboard(this, getCurrentFocus().getWindowToken());
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        etSearchByName.clearFocus();
        Utility.hideKeyboard(this, getCurrentFocus().getWindowToken());
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
                firebaseAuth.signOut();
                googleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateUserInfo(null);
                            }
                        });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean changeSortOption = false;
        switch (item.getItemId()) {
            case R.id.action_sort_pop:
                if (!sortBy.equals(SORT_BY_POPULARITY)) {
                    changeSortOption = true;
                    sortBy = SORT_BY_POPULARITY;
                }
                break;
            case R.id.action_sort_rating:
                if (!sortBy.equals(SORT_BY_RATING)) {
                    changeSortOption = true;
                    sortBy = SORT_BY_RATING;
                }
                break;
            case R.id.action_now_playing:
                if (!sortBy.equals(SORT_NOW_PLAYING)) {
                    changeSortOption = true;
                    sortBy = SORT_NOW_PLAYING;
                }
                break;
            case R.id.action_upcoming:
                if (!sortBy.equals(SORT_UPCOMING)) {
                    changeSortOption = true;
                    sortBy = SORT_UPCOMING;
                }
                break;
            case R.id.action_view_favorites:
                if (!sortBy.equals(SORT_FAVORITES)) {
                    changeSortOption = true;
                    sortBy = SORT_FAVORITES;
                }
                break;
            case R.id.action_view_recents:
                if (!sortBy.equals(SORT_RECENT)) {
                    changeSortOption = true;
                    sortBy = SORT_RECENT;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        getString(R.string.exception_drawer_item_not_implemented));
        }
        if (changeSortOption) {
            etSearchByName.setText(null);
            resetRecyclerView();
            makeSortedMovieSearch();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetRecyclerView() {
        rvMoviePosters.scrollToPosition(0);
        movieAdapter.resetLastPosition();
        resetAnimation = true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri = getQueryUri();
        String sortOrder = null;
        switch (id) {
            case LOADER_FAVORITES_ID:
                sortOrder = MoviesContract.FavoritesEntry.getSortOrder();
                break;
            case LOADER_RECENTS_ID:
                sortOrder = MoviesContract.RecentEntry.getSortOrder();
                break;
        }
        return new CursorLoader(MovieListActivity.this,
                queryUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Uri queryUri = getQueryUri();
        if (data != null && !data.isClosed()) {
            if (data.getCount() == 0) {
                switch (loader.getId()) {
                    case LOADER_FAVORITES_ID:
                        if (sortBy.equals(SORT_FAVORITES)) {
                            Toast.makeText(this, "No favorite movies!", Toast.LENGTH_LONG).show();
                            movieAdapter.swapCursor(data);
                        }
                        break;
                    case LOADER_RECENTS_ID:
                        if (sortBy.equals(SORT_RECENT)) {
                            Toast.makeText(this, "No recently viewed movies!", Toast.LENGTH_LONG).show();
                            movieAdapter.swapCursor(data);
                        }
                        break;
                    default:
                        MoviesSyncUtils.getTmdbMovieList(this, queryUri, sortBy);
                        break;
                }
            } else {
                boolean swapCursor = false;
                switch (loader.getId()) {
                    case LOADER_FAVORITES_ID:
                        if (sortBy.equals(SORT_FAVORITES))
                            swapCursor = true;
                        break;
                    case LOADER_RECENTS_ID:
                        if (sortBy.equals(SORT_RECENT))
                            swapCursor = true;
                        break;
                    default:
                        swapCursor = true;
                }
                if (swapCursor) {
                    data.setNotificationUri(getContentResolver(), queryUri);
                    movieAdapter.swapCursor(data);
                    if (resetAnimation) {
                        resetAnimation = false;
                        rvMoviePosters.startLayoutAnimation();
                    }
                    if (layoutManagerSavedState != null && data.getCount() > 0) {
                        rvMoviePosters.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
                        layoutManagerSavedState = null;
                    }
                }
            }
        }
        pbLoadingMovieList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pbLoadingMovieList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(final FloatingActionButton fab, final MoviePoster poster) {
        if (MovieUtils.swapFavoriteImageResource(this, fab, poster)){
            Snackbar snackbar = Snackbar
                    .make(rvMoviePosters, "Movie saved to Favorites", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MovieUtils.swapFavoriteImageResource(MovieListActivity.this, fab, poster);
                        }
                    });
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(rvMoviePosters, "Movie removed from Favorites", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MovieUtils.swapFavoriteImageResource(MovieListActivity.this, fab, poster);
                        }
                    });
            snackbar.show();
        }

    }

    @Override
    public void onClick(ImageView sharedView, MoviePoster poster) {
        etSearchByName.clearFocus();
        Utility.hideKeyboard(MovieListActivity.this, getCurrentFocus().getWindowToken());

        int viewId = sharedView.getId();
        switch (viewId) {
            case R.id.iv_poster:
                this.sharedView = sharedView;
                startDetailActivity(poster.id);
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void startDetailActivity(Integer id) {
        pbLoadingMovieList.setVisibility(View.VISIBLE);

        Intent detailIntent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        String imageTransitionName = ViewCompat.getTransitionName(sharedView);
        String progressTransitionName = ViewCompat.getTransitionName(pbLoadingMovieList);
        detailIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, id);
        detailIntent.putExtra(getString(R.string.transition_movie_poster), imageTransitionName);
        Pair<View, String> p1 = Pair.create((View)sharedView, imageTransitionName);
        Pair<View, String> p2 = Pair.create((View)pbLoadingMovieList, progressTransitionName);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MovieListActivity.this, p1, p2);
        startActivityForResult(detailIntent, MovieDetailActivity.REQUEST_MOVIE_DETAIL, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==MovieDetailActivity.REQUEST_MOVIE_DETAIL && resultCode != RESULT_OK) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    pbLoadingMovieList.setVisibility(View.INVISIBLE);
                }
            });
            Snackbar snackbar = Snackbar
                    .make(rvMoviePosters, "Unable to fetch details. Please try again.", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startDetailActivity(resultCode);
                        }
                    });
            snackbar.show();

        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUserInfo(null);
            }
        }
    }

    private void updateUserInfo(FirebaseUser user) {
        if(user != null) {
            btnGoogleSignin.setVisibility(View.GONE);
            tvUserName.setText(user.getDisplayName());
            tvUserEmail.setText(user.getEmail());
            Picasso.get()
                    .load(user.getPhotoUrl().toString())
                    .placeholder(R.drawable.backdrop_placeholder)
                    .error(R.mipmap.ic_launcher_round)
                    .into(ivUserPicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            ivUserPicture.setVisibility(View.VISIBLE);
                            tvUserName.setVisibility(View.VISIBLE);
                            tvUserEmail.setVisibility(View.VISIBLE);
                            pbLoadingUserInfo.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            ivUserPicture.setVisibility(View.GONE);
                            tvUserName.setVisibility(View.VISIBLE);
                            tvUserEmail.setVisibility(View.VISIBLE);
                            pbLoadingUserInfo.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            ivUserPicture.setVisibility(View.GONE);
            tvUserName.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
            btnGoogleSignin.setVisibility(View.VISIBLE);
            pbLoadingUserInfo.setVisibility(View.INVISIBLE);
        }
    }


    private void firebaseAuth(GoogleSignInAccount account) {
        Toast.makeText(this, "Authentiating with firebase", Toast.LENGTH_SHORT).show();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),  null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateUserInfo(firebaseAuth.getCurrentUser());
                } else {
                    updateUserInfo(null);
                }
            }
        });
    }

    @Override
    public void onActivityReenter(final int resultCode, final Intent data) {
        super.onActivityReenter(resultCode, data);
        Log.i(TAG, "onActivityReenter");
        supportPostponeEnterTransition();
        pbLoadingMovieList.setVisibility(View.INVISIBLE);
        if (resultCode == RESULT_OK) {
            sharedView = null;
            rvMoviePosters.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rvMoviePosters.getViewTreeObserver().removeOnPreDrawListener(this);
                    rvMoviePosters.requestLayout();
                    supportStartPostponedEnterTransition();
                    return true;
                }
            });
        }

    }

}