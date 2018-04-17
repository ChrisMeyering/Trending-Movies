package com.movies.chris.trendingmovies.activity.UI;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.provider.MoviesContract;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MoviePoster;
import com.movies.chris.trendingmovies.utils.MovieUtils;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 9/27/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.PosterViewHolder> {
    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private static final int ITEMS_PER_PAGE = 20;

    private Cursor moviePosters;
//    private Cursor favorites = null;
    private final Context context;
    private final MovieAdapterClickHandler clickHandler;

    public int getNextPageNumber() {
        return 1 + (getItemCount() /ITEMS_PER_PAGE);
    }

//    public void setFavorites(Cursor favorites) {
//        this.favorites = favorites;
//        if (favorites != null)
//            notifyDataSetChanged();
//    }

    public interface MovieAdapterClickHandler {
        void onClick(View view, MoviePoster poster);
    }

    public MovieListAdapter(Context context, MovieAdapterClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieListAdapter.PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        if (!moviePosters.isClosed()) {
            moviePosters.moveToPosition(position);
            holder.bind();
        }
    }

    @Override
    public int getItemCount() {
        return (moviePosters == null) ? 0 : moviePosters.getCount();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.iv_poster)
        ImageView ivPoster;
        @BindView(R.id.ib_favorite)
        ImageButton ibFavorite;
        @BindView(R.id.pb_loading_movie_poster)
        ProgressBar pbLoadingPoster;
        int width;
        public PosterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ivPoster.setOnClickListener(this);
            ibFavorite.setOnClickListener(this);
            width = MediaUtils.measureWidth(view);

        }

        @Override
        public void onClick(View view) {
            moviePosters.moveToPosition(getAdapterPosition());
            MoviePoster poster = MoviePoster.getPoster(moviePosters);
            if (view.getId() == R.id.ib_favorite) {
                if (poster.isFavorite(context)) {
                    ibFavorite.setImageResource(R.drawable.ic_star_border_grey_600_24dp);
                    poster.deleteFromFavorites(context);
                }
                else {
                    ibFavorite.setImageResource(R.drawable.ic_star_orange_500_24dp);
                    poster.saveToFavorites(context);
                }
            } else {
                if (poster.isRecent(context))
                    poster.deleteFromRecents(context);
                poster.saveToRecents(context);
                clickHandler.onClick(view, poster);
            }
        }

        private void bind() {
            int movieId = moviePosters.getInt(moviePosters.getColumnIndex(MoviesContract.MOVIE_ID));
            String posterPath = moviePosters.getString(moviePosters.getColumnIndex(MoviesContract.POSTER_PATH));
            if (MovieUtils.isFavorite(context, movieId)) {
                ibFavorite.setImageResource(R.drawable.ic_star_orange_500_24dp);
            } else {
                ibFavorite.setImageResource(R.drawable.ic_star_border_grey_600_24dp);
            }
            pbLoadingPoster.setVisibility(View.VISIBLE);
            Picasso.get().load(MediaUtils.buildPosterURL(posterPath, width))
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.error)
                    .into(ivPoster, new Callback() {
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
        }
    }

    public void swapCursor(Cursor data) {
        moviePosters = data;
        notifyDataSetChanged();
    }

}

