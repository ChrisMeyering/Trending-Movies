package com.movies.chris.trendingmovies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.activity.MovieDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FavoritesWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = getFavoritesRemoteView(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateFavoritesWidgets(Context context, AppWidgetManager appWidgetManager,
                                              int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static RemoteViews getFavoritesRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorites_widget);
        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        Intent detailIntent = new Intent(context, MovieDetailActivity.class);
        PendingIntent detailPendingIntent = PendingIntent.getActivity(context, 0, detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, detailPendingIntent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        FavoritesWidgetService.startActionUpdateFavorites(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        FavoritesWidgetService.startActionUpdateFavorites(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

