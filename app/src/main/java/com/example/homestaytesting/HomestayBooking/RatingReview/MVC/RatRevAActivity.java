package com.example.homestaytesting.HomestayBooking.RatingReview.MVC;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.homestaytesting.Modal.Review;
import com.example.homestaytesting.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatRevAActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RatRevDetailsAdapter ratRevDetailsAdapter;

    private DatabaseReference databaseRef;
    private List<Review> Reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rat_rev_a);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Reviews = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference("Ratings&Reviews");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Review review = ds.getValue(Review.class);
                    Reviews.add(review);
                }
                ratRevDetailsAdapter = new RatRevDetailsAdapter(RatRevAActivity.this, Reviews);
                recyclerView.setAdapter(ratRevDetailsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RatRevAActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
