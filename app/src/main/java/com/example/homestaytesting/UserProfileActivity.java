package com.example.homestaytesting;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private DatabaseReference databaseRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;

    //private ImageView imgView;
    private TextView tvEmail;
    private EditText editTextName, editTextEmail, editTextContc;

    // Code gambar
    private CircleImageView circleImage;
    final static int gallerypick = 1;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Code gambar
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        circleImage = findViewById(R.id.imgView);

        //imgView = findViewById(R.id.imgView);
        editTextName = findViewById(R.id.editTextName);
        tvEmail = findViewById(R.id.tvEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContc = findViewById(R.id.editTextContc);

        findViewById(R.id.btnUpdate).setOnClickListener(this);
        findViewById(R.id.imgView).setOnClickListener(this);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userEmail = dataSnapshot.child("email").getValue().toString();
                    String userContact = dataSnapshot.child("phone").getValue().toString();

                    editTextName.setText(userName);
                    tvEmail.setText(userEmail);
                    editTextContc.setText(userContact);
                }
                else {
                    Log.d("LOGGER", "No such document");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Display image from database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image = dataSnapshot.child("profileimage2").getValue().toString();
                    Picasso.with(UserProfileActivity.this)
                            .load(image)
                            .fit()
                            .centerCrop()
                            .into(circleImage);
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
        hmAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = hmAuth.getCurrentUser();
        String uid = user.getUid();

        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallerypick && resultCode == RESULT_OK && data!= null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Toast.makeText(UserProfileActivity.this, "enter request code", Toast.LENGTH_LONG).show();

            if (resultCode == RESULT_OK) {
                Toast.makeText(UserProfileActivity.this, "enter result", Toast.LENGTH_LONG).show();
                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserid + ".jpg");

                //save the crop inside firebase storage
                //save the link inside firebase
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {


                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String downloadUrl = String.valueOf(filePath.getDownloadUrl());
                                    Log.i("downloadUrl", downloadUrl);

                                    //Important msukkan url ke dlam profileimage2
                                    String uriurl = uri.toString();
                                    Log.i("uri", uri.toString());

                                    Toast.makeText(UserProfileActivity.this, "Your Image Successfully Uploaded ", Toast.LENGTH_SHORT).show();

                                    HashMap userMap = new HashMap();
                                    userMap.put("profileimage2", uriurl);
                                    //Pilihan untuk update
                                    //UsersRef.update(userMap).addOnCompleteListener(new OnCompleteListener() {

                                    databaseRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(UserProfileActivity.this, "Link update successfully ", Toast.LENGTH_SHORT).show();
                                                Intent selfIntent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(UserProfileActivity.this, "Profile Image store into database are success", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(UserProfileActivity.this, "update image link error ", Toast.LENGTH_SHORT).show();
                                                String message = task.getException().getMessage();
                                                Toast.makeText(UserProfileActivity.this, "Error Occured" + message, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

            } else {
                Toast.makeText(UserProfileActivity.this, "Error Occured: Image cant be crop, try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void UpdateProfileInfo() {
        String name = editTextName.getText().toString();
        //String email = editTextEmail.getText().toString();
        String phone = editTextContc.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        }
/*        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email address...", Toast.LENGTH_SHORT).show();
        }*/
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
        }
        else{
            //UpdateProfileInfo(name,email,phone);
            UpdateProfileInfo(name,phone);
        }
    }

    private void UpdateProfileInfo(String name, String phone) {
        HashMap userMap = new HashMap();
        userMap.put("name", name);
        //userMap.put("email", email);
        userMap.put("phone", phone);

        databaseRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(UserProfileActivity.this, "Profile update successfully",Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();
                }
                else{
                    Toast.makeText(UserProfileActivity.this, "An error on profile update, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnUpdate:
                UpdateProfileInfo();
                break;
            case R.id.imgView:
                toGallery();
                break;
        }
    }
}
