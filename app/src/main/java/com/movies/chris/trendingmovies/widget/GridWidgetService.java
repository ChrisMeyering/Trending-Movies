package com.movies.chris.trendingmovies.widget;

import com.movies.chris.trendingmovies.R;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MoviePoster;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by chris on 12/4/17.
 */

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String TAG = GridRemoteViewsFactory.class.getSimpleName();

    private Context context;
    private Cursor cursor;

    public GridRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDataSetChanged() {

        if (cursor != null) {
            cursor.close();
        }
        Log.i(TAG, "Widget DataSet Changed");
        cursor = context.getContentResolver().query(
                MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.FavoritesEntry.getSortOrder()
        );
        Log.i(TAG, cursor.getCount() + " favorites found");
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount");
        if (cursor == null)
            return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        Log.i(TAG, "getViewAt" + i);
        if (cursor == null || cursor.getCount() == 0) return null;
        cursor.moveToPosition(i);
        Log.i(TAG, "populating view " + i);
        MoviePoster poster = MoviePoster.getPoster(cursor);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorites_widget_list_item);
        try {
            Bitmap b = Picasso.get().load(MediaUtils.buildPosterURL(poster.posterPath, 0)).get();
            views.setImageViewBitmap(R.id.iv_widget_movie_poster, b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.i(TAG, "getLoadingView");
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
