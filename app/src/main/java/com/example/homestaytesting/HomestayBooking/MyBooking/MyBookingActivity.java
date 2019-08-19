package com.example.homestaytesting.HomestayBooking.MyBooking;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.Admin.OwnerHomestayActivity;
import com.example.homestaytesting.Admin.OwnerHomestayDetailsActivity;
import com.example.homestaytesting.Modal.Booking;
import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MyBookingActivity extends AppCompatActivity {

    private DatabaseReference UsersRef,Postsref,LikesRef, BookingRef;
    private FirebaseAuth hmAuth;
    private String currentUserid, PostKey;

    private Toolbar mToolbar;

    private RecyclerView postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postList = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        //PostKey = getIntent().getExtras().get("PostKey").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");
        BookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
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

        Query SortAgentPost = BookingRef.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        //Query SortAgentPost = Postsref.orderByChild("hmPrice");

        FirebaseRecyclerOptions<Booking> options = new FirebaseRecyclerOptions.Builder<Booking>().setQuery(SortAgentPost, Booking.class).build();

        FirebaseRecyclerAdapter<Booking,MyBookingActivity.PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Booking, MyBookingActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull MyBookingActivity.PostsViewHolder holder, final int position, @NonNull Booking model)
            {
                final String PostKey = getSnapshots().getSnapshot(position).getKey();

                holder.hmName.setText(model.getHmName());
                holder.hmBookDate.setText(model.getBookDate());
            }

            @NonNull
            @Override
            public MyBookingActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_booking_list, viewGroup, false);
                MyBookingActivity.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmName, hmBookDate;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            hmName = itemView.findViewById(R.id.tvHmName);
            hmBookDate = itemView.findViewById(R.id.tvBookingDate);
        }
    }
}
