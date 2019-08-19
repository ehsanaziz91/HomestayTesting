package com.example.homestaytesting.HomestayBooking.RatingReview.MVC;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.homestaytesting.Modal.Review;
import com.example.homestaytesting.R;

import java.util.List;

public class RatRevDetailsAdapter extends RecyclerView.Adapter<RatRevDetailsAdapter.ImageViewHolder>{
    private android.content.Context Context;
    private List<com.example.homestaytesting.Modal.Review> Review;

    public RatRevDetailsAdapter(Context context, List<Review> review){
        Context = context;
        Review = review;
    }

    @NonNull
    @Override
    public RatRevDetailsAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(Context).inflate(R.layout.comment_slider, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatRevDetailsAdapter.ImageViewHolder holder, int position) {
        Review reviewCurrent = Review.get(position);
        holder.ratingBar.setRating(reviewCurrent.getHmRatings());
        holder.tvReview.setText(reviewCurrent.getHmReviews());
    }

    @Override
    public int getItemCount() {
        return Review.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public RatingBar ratingBar;
        public TextView tvReview;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating);
            tvReview = itemView.findViewById(R.id.tvReview);
        }
    }
}
