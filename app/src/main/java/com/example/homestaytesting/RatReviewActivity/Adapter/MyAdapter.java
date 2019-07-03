package com.example.homestaytesting.RatReviewActivity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.homestaytesting.R;
import com.example.homestaytesting.RatReviewActivity.Model.Review;

import java.util.List;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class MyAdapter extends PagerAdapter {

    Context context;
    List<Review> reviewList;
    LayoutInflater inflater;

    public MyAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //Inflate view
        View view = inflater.inflate(R.layout.comment_slider,container,false);

        //View
        ColorRatingBar ratingBar = view.findViewById(R.id.rating);
        TextView textView = view.findViewById(R.id.tvComment);

        //Set data
        ratingBar.setRating(reviewList.get(position).getRatings());
        textView.setText(reviewList.get(position).getReviews());

        container.addView(view);
        return view;
    }
}
