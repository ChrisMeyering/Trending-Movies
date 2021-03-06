package com.movies.chris.trendingmovies.activity.UI;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.tmdb.model.list.MoviePoster;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.movies.chris.trendingmovies.utils.MovieUtils;
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
    private static final String FAB_TRANSITION_BASE = "fab ";
    private static final String POSTER_TRANSITION_BASE = "poster ";
    //    private Cursor favorites = null;
    private final Context context;
    private final MovieAdapterClickHandler clickHandler;
    private int lastPosition = -1;
    private Cursor moviePosters;

    public MovieListAdapter(Context context) {
        this.context = context;
        try {
            clickHandler = (MovieAdapterClickHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MovieAdapterClickHandler");
        }
    }
//    public void setFavorites(Cursor favorites) {
//        this.favorites = favorites;
//        if (favorites != null)
//            notifyDataSetChanged();
//    }

    public int getNextPageNumber() {
        return 1 + (getItemCount() / ITEMS_PER_PAGE);
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
            holder.bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return (moviePosters == null) ? 0 : moviePosters.getCount();
    }
//
//    @Override
//    public void onViewAttachedToWindow(@NonNull PosterViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        holder.setAnimation(holder.itemView, holder.getAdapterPosition());
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(@NonNull PosterViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
//    }

    public void resetLastPosition() {
        lastPosition = -1;
    }

    public void swapCursor(Cursor data) {
        moviePosters = data;
        notifyDataSetChanged();
    }


    public interface MovieAdapterClickHandler {
        void onClick(ImageView sharedImageView, MoviePoster poster);

        void onClick(FloatingActionButton fab, MoviePoster poster);
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_poster)
        ImageView ivPoster;
        @BindView(R.id.fab_favorite)
        FloatingActionButton fabFavorite;
        @BindView(R.id.pb_loading_movie_poster)
        ProgressBar pbLoadingPoster;
        MoviePoster poster;

        public PosterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (poster.isRecent(context))
                                poster.deleteFromRecents(context);
                            poster.saveToRecents(context);
                        }
                    });
                    clickHandler.onClick(ivPoster, poster);
                }
            });
            fabFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickHandler.onClick(fabFavorite, poster);

                }
            });

        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_anim_from_bottom);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        private void bind(int position) {
            moviePosters.moveToPosition(position);
            poster = MoviePoster.getPoster(moviePosters);
            ViewCompat.setTransitionName(ivPoster, POSTER_TRANSITION_BASE + position);
            ViewCompat.setTransitionName(fabFavorite, FAB_TRANSITION_BASE + position);
            MovieUtils.setFavoriteImageResource(context, fabFavorite, poster.id);
            pbLoadingPoster.setVisibility(View.VISIBLE);
            Picasso.get().load(MediaUtils.buildPosterURL(poster.posterPath, MediaUtils.measureWidth(ivPoster)))
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
            setAnimation(itemView, getAdapterPosition());
        }
    }

}

