package com.movies.chris.trendingmovies.activity.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.utils.MediaUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 11/11/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private String[] trailerIds;
    private final Context context;
    private final TrailerAdapterClickHandler clickHandler;

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(view);
    }



    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {

        if (trailerIds == null)
            return 0;
        else
            return trailerIds.length;
    }

    public void setData(String[] trailers) {
        trailerIds = trailers;
        notifyDataSetChanged();
    }

    public void releaseLoaders() {

    }

    public interface TrailerAdapterClickHandler {
        void watchTrailer(String trailerKey);
    }

    public TrailerAdapter(Context context) {
        this.context = context;
        try {
            clickHandler = (TrailerAdapterClickHandler) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement TrailerAdapterClickHandler");
        }
    }


    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.youtube_thumb_view)
        ImageView youtubeThumbView;
        @BindView(R.id.pb_loading_thumb)
        ProgressBar pbLoadingThumb;
        public TrailerViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            youtubeThumbView.setOnClickListener(this);
        }

        public void bind() {
            Log.i(TrailerAdapter.class.getSimpleName(), "Binding tailer #" + getAdapterPosition());
            Picasso.get()
                    .load(MediaUtils.buildYoutubeImageURL(trailerIds[getAdapterPosition()]))
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.error)
                    .into(youtubeThumbView, new Callback() {
                        @Override
                        public void onSuccess() {
                            pbLoadingThumb.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            pbLoadingThumb.setVisibility(View.INVISIBLE);
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            clickHandler.watchTrailer(trailerIds[getAdapterPosition()]);
        }


    }
}
