package com.movies.chris.trendingmovies.activity.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.chris.trendingmovies.R;
import com.movies.chris.trendingmovies.data.tmdb.model.detail.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 11/7/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviews = null;

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
        holder.bind(reviews.get(position));

    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        else return reviews.size();
    }

    public void updateData(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_review_author)
        TextView tvAuthor;
        @BindView(R.id.tv_review_contents)
        TextView tvReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(Review review){
            tvAuthor.setText(review.getAuthor());
            tvReview.setText(review.getContent());
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Log.i("ReviewAdapter", String.valueOf(itemView.getMeasuredHeight()));
        }
    }
}
