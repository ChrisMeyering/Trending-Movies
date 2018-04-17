package com.movies.chris.trendingmovies.activity.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chris.popularMovies2.R;

/**
 * Created by chris on 11/7/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private MovieReview[] reviews = null;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        MovieReview movieReview = reviews[position];
        holder.tvAuthor.setText(movieReview.getAuthor());
        holder.tvReview.setText(movieReview.getReview());

    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        else return reviews.length;
    }

    public void updateData(MovieReview[] movieReviews) {
        reviews = movieReviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAuthor;
        private final TextView tvReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_review_author);
            tvReview = itemView.findViewById(R.id.tv_review_contents);
        }
    }
}
