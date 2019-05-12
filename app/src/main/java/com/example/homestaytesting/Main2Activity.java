package com.example.homestaytesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.homestaytesting.HomestayPost.FormActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;

    private LinearLayout L1;

    //private ImageView imgView;
    private TextView tvName, tvEmail;
    private EditText editTextName, editTextEmail, editTextContc;

    // Code gambar
    private CircleImageView circleImage;
    final static int gallerypick = 1;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);

        // Code gambar
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        circleImage = findViewById(R.id.imgView);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        findViewById(R.id.L1).setOnClickListener(this);
        findViewById(R.id.L2).setOnClickListener(this);
        findViewById(R.id.L3).setOnClickListener(this);
        findViewById(R.id.L4).setOnClickListener(this);
        findViewById(R.id.L5).setOnClickListener(this);
        findViewById(R.id.L6).setOnClickListener(this);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userEmail = dataSnapshot.child("email").getValue().toString();
                    String image = dataSnapshot.child("profileimage2").getValue().toString();

                    tvName.setText(userName);
                    tvEmail.setText(userEmail);

                    Picasso.with(Main2Activity.this)
                            .load(image)
                            .fit()
                            .centerCrop()
                            .into(circleImage);
                }
                else {
                    Log.d("LOGGER", "No such document");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.L1:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.L2:
                startActivity(new Intent(this, PostListingActivity.class));
                break;
            case R.id.L3:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.L4:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.L5:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.L6:
                logout();
                break;
        }
    }

    private void logout() {
        hmAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
