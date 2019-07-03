package com.example.homestaytesting.RatReviewActivity.Listener;

import com.example.homestaytesting.RatReviewActivity.Model.Review;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<Review> reviewList);
    void onFirebaseLoadFailed(String message);
}
