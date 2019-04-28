package com.example.homestaytesting.HomestayPost;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.homestaytesting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostDetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference UsersRef,Postsref,LikesRef;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, imgUrl, hmName, hmLocation, hmPrice, hmDetails, hmContact;

    private ImageView imgView;
    private TextView tvName, tvLocation, tvPrice, tvDetails, tvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homestay Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgView = findViewById(R.id.imgView);
        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetails = findViewById(R.id.tvDetails);
        tvContact = findViewById(R.id.tvContact);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);

        Postsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {

                    imgUrl = dataSnapshot.child("imgUrl").getValue().toString();
                    hmName = dataSnapshot.child("hmName").getValue().toString();
                    hmLocation = dataSnapshot.child("hmLocation").getValue().toString();
                    hmPrice = dataSnapshot.child("hmPrice").getValue().toString();
                    hmDetails = dataSnapshot.child("hmDetails").getValue().toString();
                    hmContact = dataSnapshot.child("hmContact").getValue().toString();

                    tvName.setText(hmName);
                    tvLocation.setText(hmLocation);
                    tvPrice.setText("RM " + hmPrice);
                    tvDetails.setText(hmDetails);
                    tvContact.setText(hmContact);

                    Picasso.with(PostDetailsActivity.this)
                            .load(imgUrl)
                            //.placeholder(R.drawable.profile)
                            .fit()
                            .centerCrop()
                            .into(imgView);

/*                  Glide.with(PostDetailsActivity.this).load(imgUrl).into(imgView);*/
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        Intent mainIntent = new Intent(PostDetailsActivity.this, PostListingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
