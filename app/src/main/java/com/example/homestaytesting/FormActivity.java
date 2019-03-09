package com.example.homestaytesting;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FormActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUserid;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgViewPic;
    private TextView textViewPic, textViewDetails, textViewUploads;
    private EditText editTextName, editTextDetails, editTextLocation, editTextPrice, editTextContact;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private Uri imgUri;

    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        imgViewPic = (ImageView) findViewById(R.id.imgViewPic);
        textViewUploads = (TextView) findViewById(R.id.textViewUploads);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        storageRef = FirebaseStorage.getInstance().getReference("Uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Fill Form");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgViewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        textViewUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(com.example.homestaytesting.FormActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            Picasso.with(this).load(imgUri).into(imgViewPic);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void submit() {
        if (imgUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
            + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //progressBar.setProgress(0);
                                }
                            },500);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(FormActivity.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                                    Upload upload = new Upload(editTextName.getText().toString().trim(),uri.toString(),
                                            editTextDetails.getText().toString().trim(),
                                            editTextLocation.getText().toString().trim(),
                                            editTextPrice.getText().toString().trim(),
                                            editTextContact.getText().toString().trim(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                                            //taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                            );
                                    //String uploadId = databaseRef.push().getKey();

                                    Calendar calFordDate = Calendar.getInstance();
                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String saveCurrentDate = currentDate.format(calFordDate.getTime());

                                    Calendar calFordTime = Calendar.getInstance();
                                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                                    String saveCurrentTime = currentTime.format(calFordTime.getTime());

                                    String postRandomName = saveCurrentDate + saveCurrentTime;

                                    databaseRef.child(currentUserid + postRandomName).setValue(upload);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FormActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
/*                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    })*/;
        }else {
            Toast.makeText(this, "No file selected !", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }
}
