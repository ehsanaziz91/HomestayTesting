package com.example.homestaytesting;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.homestaytesting.Admin.AdminHomestayStateActivity;
import com.example.homestaytesting.Admin.AnalysisActivity;
import com.example.homestaytesting.Admin.OwnerListingActivity;
import com.example.homestaytesting.HomestayPost.FormActivity;
import com.example.homestaytesting.HomestayPost.PostHistoryActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.Modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;

    private LinearLayout linearVisible1, linearVisible2, linearVisible3, linearVisible4;

    //private ImageView imgView;
    private TextView tvName, tvEmail;
    private EditText editTextName, editTextEmail, editTextContc;
    private Dialog dialog;
    private ImageView close;

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

        if(hmAuth.getCurrentUser() !=null)
        {
            DetermineRole();
        }

        // Code gambar
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        circleImage = findViewById(R.id.imgView);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        linearVisible1 = findViewById(R.id.linearVisible1);
        linearVisible2 = findViewById(R.id.linearVisible2);
        linearVisible3 = findViewById(R.id.linearVisible3);
        linearVisible4 = findViewById(R.id.linearVisible4);

        findViewById(R.id.L1).setOnClickListener(this);
        findViewById(R.id.L2).setOnClickListener(this);
        findViewById(R.id.L3).setOnClickListener(this);
        findViewById(R.id.L4).setOnClickListener(this);
        findViewById(R.id.L5).setOnClickListener(this);
        findViewById(R.id.L6).setOnClickListener(this);
        findViewById(R.id.L7).setOnClickListener(this);
        findViewById(R.id.L8).setOnClickListener(this);
        findViewById(R.id.L9).setOnClickListener(this);
        findViewById(R.id.L10).setOnClickListener(this);
        
        dialog = new Dialog(this);

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

    public void DetermineRole(){
        final FirebaseUser user = hmAuth.getCurrentUser();
        final String uid = user.getUid();
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //replace noti when tukar device
        //UserRef.child(uid).child("devicetoken").setValue(deviceToken);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference xx = db.getReference();

/*        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
        startActivity(intent);
        finish();*/

        xx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User usersData = dataSnapshot.child("Users").child(uid).getValue(User.class);

                    if(usersData.getRole().equals("Owner"))
                    {
                        linearVisible3.setVisibility(View.GONE);
                        linearVisible4.setVisibility(View.GONE);
                    }
                    else {
                        linearVisible1.setVisibility(View.GONE);
                        linearVisible2.setVisibility(View.GONE);
                    }
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
                showPopupDialog();
                break;
            case R.id.L2:
                startActivity(new Intent(this, PostListingActivity.class));
                break;
            case R.id.L3:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.L4:
                startActivity(new Intent(this, PostHistoryActivity.class));
                break;
            case R.id.L5:
                startActivity(new Intent(this, Main2Activity.class));
                break;
            case R.id.L6:
                startActivity(new Intent(this, AdminHomestayStateActivity.class));
                break;
            case R.id.L7:
                startActivity(new Intent(this, OwnerListingActivity.class));
                break;
            case R.id.L8:
                startActivity(new Intent(this, AnalysisActivity.class));
                break;
            case R.id.L9:
                startActivity(new Intent(this, UserProfile2Activity.class));
                break;
            case R.id.L10:
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

    private void showPopupDialog() {
        dialog.setContentView(R.layout.about_us_popup);
        close = dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
