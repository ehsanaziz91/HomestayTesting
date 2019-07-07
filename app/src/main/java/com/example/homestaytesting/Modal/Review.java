package com.example.homestaytesting.Modal;

public class Review {
    private Float ratings;
    private String reviews;

    public Review (){
    }

    public Review(Float ratings, String reviews) {
        this.ratings = ratings;
        this.reviews = reviews;
    }

    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
}
