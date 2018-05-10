package com.movies.chris.trendingmovies.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.sync.MoviesSyncUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FavoritesWidgetService extends JobIntentService {
    private static final String TAG = FavoritesWidgetService.class.getSimpleName();
    private static final String ACTION_UPDATE_FAVORITES_WIDGET = "com.movies.chris.trendingmovies" +
            ".widget.action.UPDATE_FAVORITES";

    public FavoritesWidgetService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (ACTION_UPDATE_FAVORITES_WIDGET.equals(action)) {
            handleActionUpdateFavorites();
        }
    }

    public static void startActionUpdateFavorites(Context context) {
        Intent intent = new Intent(context, FavoritesWidgetService.class);
        intent.setAction(ACTION_UPDATE_FAVORITES_WIDGET);
        context.startService(intent);
    }

    private void handleActionUpdateFavorites() {
        Log.i(TAG, "Updating Favorites Widget");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FavoritesWidgetProvider.class));
        Log.i(TAG, "Notifying " + appWidgetIds.length + " widgets");
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
        FavoritesWidgetProvider.updateFavoritesWidgets(this, appWidgetManager, appWidgetIds);
    }
}
