package com.example.homestaytesting.HomestayBooking.RatingReview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.homestaytesting.Modal.Review;
import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class RatRevDetailsActivity extends AppCompatActivity {

    private RecyclerView postList;
    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef, databaseReference, UserRef, dbRef;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, userid;
    private TextView tvReview;
    private RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rat_rev_details);
        setContentView(R.layout.activity_post_listing);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Rating & Review Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Prevent the keyboard from displaying on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        postList = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        userid = hmAuth.getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads");
        dbRef = FirebaseDatabase.getInstance().getReference("Ratings&Reviews");
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

    @Override
    protected void onStart() {
        super.onStart();

        Query SortAgentPost = dbRef.orderByChild("hmPostKey").startAt(PostKey).endAt(PostKey + "\uf8ff");
        //Query SortAgentPost = dbRef.orderByChild("uID");

        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>().setQuery(SortAgentPost, Review.class).build();

        FirebaseRecyclerAdapter<Review, PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Review, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull Review model)
            {
                final String PostKey = getRef(position).getKey();
                holder.hmRatings.setRating(model.getHmRatings());
                holder.hmReviews.setText(model.getHmReviews());
            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_slider, viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmReviews;
        RatingBar hmRatings;

        public PostsViewHolder(View itemView)
        {
            super(itemView);

            hmReviews = itemView.findViewById(R.id.tvReview);
            hmRatings = itemView.findViewById(R.id.rating);

        }
    }
}
