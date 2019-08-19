package com.example.homestaytesting.Modal;

public class Review {
    private Float hmAverageRat;
    private Float hmRatings;
    private String hmReviews;

    public Review (){
    }

    public Review(Float hmAverageRat, Float hmRatings, String hmReviews) {
        this.hmAverageRat = hmAverageRat;
        this.hmRatings = hmRatings;
        this.hmReviews = hmReviews;
    }

    public Float getHmAverageRat() {
        return hmAverageRat;
    }

    public void setHmAverageRat(Float hmAverageRat) {
        this.hmAverageRat = hmAverageRat;
    }

    public Float getHmRatings() {
        return hmRatings;
    }

    public void setHmRatings(Float hmRatings) {
        this.hmRatings = hmRatings;
    }

    public String getHmReviews() {
        return hmReviews;
    }

    public void setHmReviews(String hmReviews) {
        this.hmReviews = hmReviews;
    }
}
