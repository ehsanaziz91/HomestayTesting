package com.example.homestaytesting.HomestayPost;

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
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.homestaytesting.MainActivity;
import com.example.homestaytesting.R;
import com.example.homestaytesting.Modal.Upload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth hmAuth;
    private String currentUserid;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgView;
    private TextView tvPic, tvDetails, tvUploads;
    private EditText editTextName, editTextDetails, editTextLocation, editTextPrice, editTextContact, editTextLat, editTextLang;
    private Spinner msPropertyType, msBedrooms, msBathroom;
    private Button btnSubmit;
    private RadioGroup rgFurnished;
    private RadioButton rbFurnished, rbFully, rbPartially;
    private CheckBox cbFirst, cbSecond, cbThird, cbFourth;
    private ProgressBar progressBar;

    private String hmFurnish;

    private Uri imgUri;

    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask uploadTask;

    ArrayList<String> facilities = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post Your Homestay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Prevent the keyboard from displaying on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imgView = (ImageView) findViewById(R.id.imgView);
        tvUploads = (TextView) findViewById(R.id.tvUploads);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        msPropertyType = findViewById(R.id.msPropertyType);
        msBedrooms = findViewById(R.id.msBedrooms);
        msBathroom = findViewById(R.id.msBathroom);

        //Checkbox
        cbFirst = findViewById(R.id.cbAircond);
        cbSecond = findViewById(R.id.cbCooking);
        cbThird = findViewById(R.id.cbInternet);
        cbFourth = findViewById(R.id.cbWashingMachine);

        //Radiogroup
        rbFully = findViewById(R.id.rbFully);
        rbPartially = findViewById(R.id.rbPartially);

        rgFurnished = findViewById(R.id.rgFurnished);
        rgFurnished.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                rbFurnished = rgFurnished.findViewById(i);
                switch (i){
                    case R.id.rbFully:
                        hmFurnish = rbFurnished.getText().toString();
                        break;
                    case R.id.rbPartially:
                        hmFurnish = rbFurnished.getText().toString();
                        break;
                }
            }
        });

        int i = rgFurnished.getCheckedRadioButtonId();
        rbFurnished = findViewById(i);

        //Spinner Dropdown
        ArrayAdapter<CharSequence> adapterProperty = ArrayAdapter.createFromResource(this, R.array.propertyCat, android.R.layout.simple_spinner_item);
        adapterProperty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msPropertyType.setPrompt("Property Type");
        msPropertyType.setAdapter(adapterProperty);
        msPropertyType.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterBedrooms = ArrayAdapter.createFromResource(this, R.array.bedroomsCat, android.R.layout.simple_spinner_item);
        adapterBedrooms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msBedrooms.setPrompt("Number of Bedrooms");
        msBedrooms.setAdapter(adapterBedrooms);
        msBedrooms.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterBathroom = ArrayAdapter.createFromResource(this, R.array.bathroomCat, android.R.layout.simple_spinner_item);
        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msBathroom.setPrompt("Number of Bathroom");
        msBathroom.setAdapter(adapterBathroom);
        msBathroom.setOnItemSelectedListener(this);

        //testing lat lang
        editTextLat = (EditText) findViewById(R.id.editTextLat);
        editTextLang = (EditText) findViewById(R.id.editTextLang);

        storageRef = FirebaseStorage.getInstance().getReference("Uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        tvUploads.setOnClickListener(new View.OnClickListener() {
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
        Intent mainIntent = new Intent(FormActivity.this, MainActivity.class);
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

            Picasso.with(this).load(imgUri).into(imgView);
            //Glide.with(this).load(imgUri).into(imgView);
        }
    }

    //Checkbox
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.cbAircond:
                if (checked){
                    facilities.add("Aircond");
                }
                else{
                    facilities.remove("Aircond");
                }
                break;

            case R.id.cbCooking:
                if (checked){
                    facilities.add("Cooking Allowed");
                }
                else{
                    facilities.remove("Cooking Allowed");
                }
                break;
            case R.id.cbInternet:
                if (checked){
                    facilities.add("Internet");
                }
                else{
                    facilities.remove("Internet");
                }
                break;
            case R.id.cbWashingMachine:
                if (checked){
                    facilities.add("Washing Machine");
                }
                else{
                    facilities.remove("Washing Machine");
                }
                break;
        }
    }

/*    public void facilitiesSelection (View view){
        String facilitySelect = "";

        for (String Selections : facilities){
            facilitySelect = facilitySelect + Selections + "\n";
        }

    }*/

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
                                            //testing lat lang
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            Double.parseDouble(editTextLat.getText().toString().trim()),
                                            Double.parseDouble(editTextLang.getText().toString().trim()),
                                            msPropertyType.getSelectedItem().toString().trim(),
                                            msBedrooms.getSelectedItem().toString().trim(),
                                            msBathroom.getSelectedItem().toString().trim(),
                                            hmFurnish = rbFurnished.getText().toString().trim()

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

                                    //set new child for facility
                                    HashMap facility = new HashMap();

                                    if(cbFirst.isChecked())
                                    {
                                        facility.put("1", cbFirst.getText().toString());
                                    }
                                    if(cbSecond.isChecked())
                                    {
                                        facility.put("2", cbSecond.getText().toString());
                                    }
                                    if(cbThird.isChecked())
                                    {
                                        facility.put("3", cbThird.getText().toString());
                                    }
                                    if(cbFourth.isChecked())
                                    {
                                        facility.put("4", cbFourth.getText().toString());
                                    }

                                    databaseRef.child(currentUserid + postRandomName).child("hmFacilities").setValue(facility);
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
            Toast.makeText(this, "No image selected !", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, PostListingActivity.class);
        startActivity(intent);
    }

    //using for spinner dropdown
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
