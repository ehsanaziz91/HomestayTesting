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

public class AdminHomestayListingActivity extends AppCompatActivity {

    private DatabaseReference UsersRef,Postsref,LikesRef;
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
        getSupportActionBar().setTitle("List of Homestays");
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
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");
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

        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(PostKey).endAt(PostKey + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("hmPrice");

        FirebaseRecyclerOptions<Upload> options = new FirebaseRecyclerOptions.Builder<Upload>().setQuery(SortAgentPost, Upload.class).build();

        FirebaseRecyclerAdapter<Upload,AdminHomestayListingActivity.PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Upload, AdminHomestayListingActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull AdminHomestayListingActivity.PostsViewHolder holder, final int position, @NonNull Upload model)
            {

                final String PostKey = getRef(position).getKey();

                holder.hmName.setText(model.getHmName());
                holder.hmLocation.setText(model.getHmLocation());
                Picasso.with(AdminHomestayListingActivity.this)
                        .load(model.getImgUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.hmImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        // Untuk dpat Id dalam table post
                        //String PostKey = getSnapshots().getSnapshot(position).getKey();

                        Intent click_post = new Intent(AdminHomestayListingActivity.this, OwnerHomestayDetailsActivity.class);
                        click_post.putExtra("PostKey", PostKey);
                        //Toast.makeText(OwnerHomestayActivity.this, PostKey, Toast.LENGTH_SHORT).show();
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
            public AdminHomestayListingActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_owner_homestay, viewGroup, false);
                AdminHomestayListingActivity.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmName, hmLocation, userProfile, userContact, userHome, userDelete;
        ImageView hmImage;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            //userDelete = itemView.findViewById(R.id.tvDelete);
            //userHome = itemView.findViewById(R.id.tvHomestay);
            //userContact = itemView.findViewById(R.id.tvContact);
            hmLocation = itemView.findViewById(R.id.tvLocation);
            hmName = itemView.findViewById(R.id.tvName);
            hmImage = itemView.findViewById(R.id.imgView);

        }
    }
}
