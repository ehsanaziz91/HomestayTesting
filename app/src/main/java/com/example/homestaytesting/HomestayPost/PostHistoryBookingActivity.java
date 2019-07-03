package com.example.homestaytesting.HomestayPost;

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

public class PostHistoryBookingActivity extends AppCompatActivity {

    private DatabaseReference UsersRef,Postsref,LikesRef, BookingRef;
    private FirebaseAuth hmAuth;
    private String currentUserid, PostKey;

    private Toolbar mToolbar;

    private RecyclerView postList;

    private String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("List of Booking Homestay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postList = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        PostKey = getIntent().getExtras().get("PostKey").toString();
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

        Query SortAgentPost = BookingRef.orderByChild("ownerId").startAt(PostKey).endAt(PostKey + "\uf8ff");
        //Query SortAgentPost = BookingRef.orderByChild("ownerId");

        FirebaseRecyclerOptions<Booking> options = new FirebaseRecyclerOptions.Builder<Booking>().setQuery(SortAgentPost, Booking.class).build();

        FirebaseRecyclerAdapter<Booking, PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Booking, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Booking model)
            {

                final String PostKey = getRef(position).getKey();

                holder.hmName.setText(model.getHmName());
                holder.hmBookingDate.setText(model.getBookDate());
                holder.uName.setText(model.getUserName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        // Untuk dpat Id dalam table post
                        //String PostKey = getSnapshots().getSnapshot(position).getKey();

                        Intent click_post = new Intent(PostHistoryBookingActivity.this, PostHistoryBookingActivity.class);
                        click_post.putExtra("PostKey", PostKey);
                        //Toast.makeText(PostHistoryBookingActivity.this, PostKey, Toast.LENGTH_SHORT).show();
                        startActivity(click_post);
                    }
                });

/*                holder.userHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerHomestayActivity.this, PostUpdateActivity.class);
                        intent.putExtra("PostKey", PostKey);
                        startActivity(intent);
                    }
                });

                holder.userDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UsersRef.child(PostKey).removeValue();
                    }
                });*/


            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_post_history_booking, viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmName, hmBookingDate, uName;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            uName = itemView.findViewById(R.id.tvUserName);
            hmBookingDate = itemView.findViewById(R.id.tvBookingDate);
            hmName = itemView.findViewById(R.id.tvName);
        }
    }
}
