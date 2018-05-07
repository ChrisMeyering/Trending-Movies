package com.movies.chris.trendingmovies.activity.UI;

import android.content.ContentResolver;
import android.content.Context;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import com.movies.chris.trendingmovies.data.provider.MoviesContract;

public class Utility {
    public static int numOfGridColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return Math.max((int) (dpWidth / 130), 3);
    }

    public static void hideKeyboard(Context context, IBinder binder) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binder, 0);
    }

    public static void deleteMovieNameTable(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            contentResolver.delete(MoviesContract.MovieNameEntry.CONTENT_URI, null, null);
        }
    }

    public static void uncheckAllMenuItems(NavigationView navView) {
        final Menu menu = navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }

    public static int getVisibleThreshold(Context context) {
        return 3 * numOfGridColumns(context);
    }
}