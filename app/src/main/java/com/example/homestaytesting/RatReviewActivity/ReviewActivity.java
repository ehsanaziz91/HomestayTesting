package com.example.homestaytesting.RatReviewActivity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.homestaytesting.R;
import com.example.homestaytesting.RatReviewActivity.Adapter.MyAdapter;
import com.example.homestaytesting.RatReviewActivity.Listener.IFirebaseLoadDone;
import com.example.homestaytesting.RatReviewActivity.Model.Review;
import com.example.homestaytesting.RatReviewActivity.Transformer.DepthPageTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity implements IFirebaseLoadDone {

    ViewPager viewPager;
    MyAdapter adapter;
    DatabaseReference databaseRef;
    IFirebaseLoadDone iFirebaseLoadDone;

    private FirebaseAuth hmAuth;
    private String currentUserid, PostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);

        iFirebaseLoadDone = this;
        loadReview();
        viewPager = findViewById(R.id.view_pager);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    private void loadReview() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Review> reviewList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    reviewList.add(ds.getValue(Review.class));
                iFirebaseLoadDone.onFirebaseLoadSuccess(reviewList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<Review> reviewList) {
        adapter = new MyAdapter(this, reviewList);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }
}
