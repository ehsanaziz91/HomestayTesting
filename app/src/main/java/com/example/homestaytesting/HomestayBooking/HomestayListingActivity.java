package com.example.homestaytesting.HomestayBooking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.HomestayPost.PostUpdateActivity;
import com.example.homestaytesting.MainActivity;
import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomestayListingActivity extends AppCompatActivity {

    private RecyclerView postList;

    private DatabaseReference UsersRef,Postsref,LikesRef;

    private FirebaseAuth hmAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_listing);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homestays Available");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postList = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomestayListingActivity.this, MainActivity.class);
                HomestayListingActivity.this.startActivity(intent);
            }
        });
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
        Intent mainIntent = new Intent(HomestayListingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("hmPrice");

        FirebaseRecyclerOptions<Upload> options = new FirebaseRecyclerOptions.Builder<Upload>().setQuery(SortAgentPost, Upload.class).build();

        FirebaseRecyclerAdapter<Upload,HomestayListingActivity.PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Upload, HomestayListingActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull HomestayListingActivity.PostsViewHolder holder, final int position, @NonNull Upload model)
            {

                final String PostKey = getRef(position).getKey();

                holder.hmName.setText(model.getHmName());
                //holder.hmDetails.setText(model.getHmDetails());
                holder.hmLocation.setText(model.getHmLocation());
                holder.hmPrice.setText("RM " + model.getHmPrice()+ " per night");
                holder.hmPropertyType.setText(model.getHmPropertyType());
                holder.hmFurnish.setText(model.getHmFurnish());
                //holder.hmContact.setText(model.getHmContact());
                //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);
                Picasso.with(HomestayListingActivity.this)
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

                        Intent click_post = new Intent(HomestayListingActivity.this, HomestayDetailsActivity.class);
                        click_post.putExtra("PostKey", PostKey);
                        startActivity(click_post);
                    }
                });

            }

            @NonNull
            @Override
            public HomestayListingActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_items, viewGroup, false);
                HomestayListingActivity.PostsViewHolder viewHolder = new HomestayListingActivity.PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmName, hmDetails, hmLocation, hmPrice, hmContact, hmPropertyType, hmFurnish;
        ImageView hmImage;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            hmFurnish = itemView.findViewById(R.id.tvFurnish);
            hmPropertyType = itemView.findViewById(R.id.tvProperty);
            hmName = itemView.findViewById(R.id.tvName);
            //hmDetails = itemView.findViewById(R.id.tvDetails);
            hmLocation = itemView.findViewById(R.id.tvLocation);
            hmPrice = itemView.findViewById(R.id.tvPrice);
            //hmContact = itemView.findViewById(R.id.tvContact);
            hmImage = itemView.findViewById(R.id.imgView);

        }
    }
}
