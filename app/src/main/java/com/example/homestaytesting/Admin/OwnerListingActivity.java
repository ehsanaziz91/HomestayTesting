package com.example.homestaytesting.Admin;

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

import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.HomestayPost.PostUpdateActivity;
import com.example.homestaytesting.Main2Activity;
import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.Modal.User;
import com.example.homestaytesting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class OwnerListingActivity extends AppCompatActivity {

    private DatabaseReference UsersRef,Postsref,LikesRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    private RecyclerView postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("List of Owner's");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postList = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");
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

        Query SortAgentPost = UsersRef.orderByChild("role").startAt("Owner").endAt("Owner" + "\uf8ff");
        //Query SortAgentPost = Postsref.orderByChild("hmPrice");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(SortAgentPost, User.class).build();

        FirebaseRecyclerAdapter<User,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<User, OwnerListingActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull OwnerListingActivity.PostsViewHolder holder, final int position, @NonNull User model)
            {

                final String PostKey = getRef(position).getKey();

                holder.userName.setText(model.getName());
                holder.userProfile.setText(model.getProfiledescription());
                holder.userContact.setText(model.getPhone());
                Picasso.with(OwnerListingActivity.this)
                        .load(model.getProfileimage2())
                        .fit()
                        .centerCrop()
                        .into(holder.userImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        // Untuk dpat Id dalam table post
                        //String PostKey = getSnapshots().getSnapshot(position).getKey();

                        Intent click_post = new Intent(OwnerListingActivity.this, OwnerHomestayActivity.class);
                        click_post.putExtra("PostKey", PostKey);
                        //Toast.makeText(getBaseContext(), "Location changed : Lat: " + PostKey, Toast.LENGTH_SHORT).show();
                        startActivity(click_post);
                    }
                });

                holder.userHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerListingActivity.this, OwnerHomestayActivity.class);
                        intent.putExtra("PostKey", PostKey);
                        startActivity(intent);
                    }
                });

                holder.userDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UsersRef.child(PostKey).removeValue();
                    }
                });


            }

            @NonNull
            @Override
            public OwnerListingActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_owner_listing, viewGroup, false);
                OwnerListingActivity.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userProfile, userContact, userHome, userDelete;
        ImageView userImage;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            userDelete = itemView.findViewById(R.id.tvDelete);
            userHome = itemView.findViewById(R.id.tvHomestay);
            userContact = itemView.findViewById(R.id.tvContact);
            userProfile = itemView.findViewById(R.id.tvProfileDesc);
            userName = itemView.findViewById(R.id.tvName);
            userImage = itemView.findViewById(R.id.imgView);

        }
    }
}
