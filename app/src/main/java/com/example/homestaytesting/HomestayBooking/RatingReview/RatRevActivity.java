package com.example.homestaytesting.HomestayBooking.RatingReview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.homestaytesting.HomestayBooking.HomestayDetailsActivity;
import com.example.homestaytesting.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RatRevActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef, databaseReference, UserRef, dbRef, dbRefrence;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, hmId;
    private RatingBar ratings;
    private EditText editTextReviews;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rat_rev);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Write a Review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Prevent the keyboard from displaying on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        dbRefrence = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey).child("hmRatings");
        dbRef = FirebaseDatabase.getInstance().getReference("Ratings&Reviews");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmId = dataSnapshot.child("hmId").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ratings = findViewById(R.id.ratings);
        editTextReviews = findViewById(R.id.editTextReviews);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalStars = "Total Stars:: " + ratings.getNumStars();
                String rating = "Rating :: " + ratings.getRating();
                String review = "Review :: " + editTextReviews.getText();
                //Toast.makeText(getApplicationContext(), totalStars + "\n" + rating + "\n" + review, Toast.LENGTH_LONG).show();
                ratingreviewsubmit();
            }
        });
    }

    private void ratingreviewsubmit() {
        Float rat = ratings.getRating();
        String review = editTextReviews.getText().toString();
        ratingreviewsubmits(rat, review);
    }

    private void ratingreviewsubmits(Float rats, String review) {
        rats = ratings.getRating();
        review = editTextReviews.getText().toString();

        final Float finalRats = rats;
        final String finalReview = review;

        HashMap ratreview = new HashMap();
        ratreview.put("hmRatings", rats);
        ratreview.put("hmReviews", review);
        ratreview.put("hmPostKey", PostKey);
        ratreview.put("uID", currentUserid);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = currentTime.format(calFordTime.getTime());

        String postRandomName = saveCurrentDate + saveCurrentTime;

        dbRef.child(PostKey).child(currentUserid).setValue(ratreview).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    databaseRef.child("hmRatings").child(currentUserid).setValue(finalRats);
                    databaseRef.child("hmReviews").child(currentUserid).setValue(finalReview);
                    if(task.isSuccessful()){
                        submitRating();
                    }
                }
                else{
                    Toast.makeText(RatRevActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

/*        databaseRef.child("hmRatings").child(currentUserid).setValue(rats).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(RatRevActivity.this, "Successfully send your feedback",Toast.LENGTH_SHORT).show();
                    //submitRating();
                }
                else{
                    Toast.makeText(RatRevActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

/*        databaseRef.child("hmReviews").child(currentUserid).setValue(review).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(RatRevActivity.this, "Successfully send your feedback",Toast.LENGTH_SHORT).show();
                    submitRating();
                }
                else{
                    Toast.makeText(RatRevActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    public void submitRating() {
        try {
            databaseRef.child("hmRatings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    float total = 0;
                    float count = 0;
                    float average = 0;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        float rating = ds.getValue(Float.class);
                        total = total + rating;
                        count = count + 1;
                        average = total / count;

                        String a = String.valueOf(average);
                        Toast.makeText(RatRevActivity.this,"Ratings Average ::" + a,Toast.LENGTH_SHORT).show();
                    }

                    final DatabaseReference newRef = databaseRef.child("hmRatings");
                    newRef.child("hmAverageRat").setValue(average);
                    databaseRef.child("hmAverageRat").setValue(average);
                    dbRef.child("hmAverageRat").setValue(average);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        } catch (Exception e) {
            Toast.makeText(RatRevActivity.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        finish();
    }
}
