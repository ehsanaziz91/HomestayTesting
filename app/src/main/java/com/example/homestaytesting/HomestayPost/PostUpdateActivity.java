package com.example.homestaytesting.HomestayPost;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostUpdateActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar mToolbar;

    private DatabaseReference databaseRef;
    private FirebaseAuth hmAuth;
    private String currentUserid, PostKey, hmFurnish;

    private ImageView imgView;
    private EditText editTextName, editTextDetails, editTextLocation, editTextPrice, editTextContact;
    private Spinner msPropertyType, msBedrooms, msBathroom;
    private RadioGroup rgFurnished;
    private RadioButton rbFurnished, rbFully, rbPartially;
    private CheckBox cbFirst, cbSecond, cbThird, cbFourth;

    ArrayList<String> facilities = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Homestay Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Prevent the keyboard from displaying on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imgView = findViewById(R.id.imgView);
        editTextName = findViewById(R.id.editTextName);
        editTextDetails = findViewById(R.id.editTextDetails);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextContact = findViewById(R.id.editTextContact);
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

        findViewById(R.id.btnUpdate).setOnClickListener(this);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String hmName = dataSnapshot.child("hmName").getValue().toString();
                    String hmDetails = dataSnapshot.child("hmDetails").getValue().toString();
                    String hmLocation = dataSnapshot.child("hmLocation").getValue().toString();
                    String hmPrice = dataSnapshot.child("hmPrice").getValue().toString();
                    String hmContact = dataSnapshot.child("hmContact").getValue().toString();
                    String hmFurnish = dataSnapshot.child("hmFurnish").getValue().toString();
                    String hmFacility = dataSnapshot.child("hmFacilities").getValue().toString();

                    final List<String> property = new ArrayList<String>();
/*                    property.add("Bungalow / Villa");
                    property.add("Double Storey");
                    property.add("Single Storey");
                    property.add("Semi-D");*/
                    String hmPropertyType = dataSnapshot.child("hmPropertyType").getValue().toString();
                    property.add(hmPropertyType);

                    final List<String> bedroom = new ArrayList<String>();
                    String hmBedrooms = dataSnapshot.child("hmBedrooms").getValue().toString();
                    bedroom.add(hmBedrooms);

                    final List<String> bathroom = new ArrayList<String>();
                    String hmBathroom = dataSnapshot.child("hmBathroom").getValue().toString();
                    bathroom.add(hmBathroom);

                    editTextName.setText(hmName);
                    editTextDetails.setText(hmDetails);
                    editTextLocation.setText(hmLocation);
                    editTextPrice.setText(hmPrice);
                    editTextContact.setText(hmContact);

                    //radio button
                    if(hmFurnish.equalsIgnoreCase("Fully Furnished"))
                    {
                        rbFully.setChecked(true);
                    }
                    else if (hmFurnish.equalsIgnoreCase("Partially Furnished"))
                    {
                        rbPartially.setChecked(true);
                    }

                    //checkbox
                    if(cbFirst.isChecked())
                    {
                        cbFirst.setChecked(true);
                    }
                    if(hmFacility.equals("Cooking Allowed"))
                    {
                        cbSecond.setChecked(true);
                    }
                    if(hmFacility.equals("Internet"))
                    {
                        cbThird.setChecked(true);
                    }
                    if(hmFacility.equals("Washing Machine"))
                    {
                        cbFourth.setChecked(true);
                    }

                    //Spinner Dropdown
                    msPropertyType = findViewById(R.id.msPropertyType);
                    //ArrayAdapter<CharSequence> adapterProperty = ArrayAdapter.createFromResource(this, R.array.propertyCat, android.R.layout.simple_spinner_item);
                    ArrayAdapter<String> adapterProperty = new ArrayAdapter<String>(PostUpdateActivity.this, android.R.layout.simple_spinner_item, property);
                    adapterProperty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    msPropertyType.setAdapter(adapterProperty);
                    msPropertyType.setOnItemSelectedListener(PostUpdateActivity.this);

                    //Spinner Dropdown
                    msBedrooms = findViewById(R.id.msBedrooms);
                    ArrayAdapter<String> adapterBedroom = new ArrayAdapter<String>(PostUpdateActivity.this, android.R.layout.simple_spinner_item, bedroom);
                    adapterBedroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    msBedrooms.setAdapter(adapterBedroom);
                    msBedrooms.setOnItemSelectedListener(PostUpdateActivity.this);

                    //Spinner Dropdown
                    msBathroom = findViewById(R.id.msBathroom);
                    ArrayAdapter<String> adapterBathroom = new ArrayAdapter<String>(PostUpdateActivity.this, android.R.layout.simple_spinner_item, bathroom);
                    adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    msBathroom.setAdapter(adapterBathroom);
                    msBathroom.setOnItemSelectedListener(PostUpdateActivity.this);
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

    //using for spinner dropdown
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        Intent mainIntent = new Intent(PostUpdateActivity.this, PostListingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void UpdatePostInfo() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnUpdate:
                UpdatePostInfo();
                break;
        }
    }
}
