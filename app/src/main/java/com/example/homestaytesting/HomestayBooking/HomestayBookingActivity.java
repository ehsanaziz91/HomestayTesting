package com.example.homestaytesting.HomestayBooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class HomestayBookingActivity extends AppCompatActivity implements SlyCalendarDialog.Callback {

    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef, databaseReference;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, imgUrl, hmName, hmLocation, hmPrice, hmDetails, hmContact, hmLat, hmLang, hmId,
            hmBathroom, hmBedrooms, hmPropertyType, hmFurnish, hmFacilities, uid;

    TextView tvName, tvPrice, tvHomeId, tvOwnerId;
    EditText editTextDate;
    Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_booking);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Booking Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvHomeId = findViewById(R.id.tvHomeId);
        tvOwnerId = findViewById(R.id.tvOwnerId);
        editTextDate = findViewById(R.id.edtTextCalendar);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomestayBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        editTextDate.setText(selectedDay+"-"+selectedMonth+"-"+selectedYear);

                        currentDate.set(selectedYear,selectedMonth,selectedDay);
                    }
                }, year, month, day);
                datePickerDialog.show();*/
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setCallback(HomestayBookingActivity.this)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        databaseReference = FirebaseDatabase.getInstance().getReference("Booking");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    hmName = dataSnapshot.child("hmName").getValue().toString();
                    hmPrice = dataSnapshot.child("hmPrice").getValue().toString();
                    hmId = dataSnapshot.child("hmId").getValue().toString();
                    uid = dataSnapshot.child("uid").getValue().toString();

                    //Toast.makeText(HomestayBookingActivity.this, hmId, Toast.LENGTH_SHORT).show();

                    tvName.setText(hmName);
                    //tvPrice.setText("RM " + hmPrice + " per night");
                    tvHomeId.setText(hmId);
                    tvOwnerId.setText(uid);
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
        finish();
    }

    @Override
    public void onCancelled() {
        //Nothing
    }

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours);
                firstDate.set(Calendar.MINUTE, minutes);

                String firstdate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime());
                //String seconddate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(secondDate.getTime());
                //String thirddatetime = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime());

                editTextDate.setText(firstdate);

            } else {

                String firstdate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime());
                String seconddate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(secondDate.getTime());
                //String thirddatetime = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime());

                String a = firstdate.replaceAll("\\D+","");
                String b = seconddate.replaceAll("\\D+","");

                editTextDate.setText(firstdate + " - " + seconddate);

                String price = hmPrice;

                double c = Double.parseDouble(b) - Double.parseDouble(a);
                double hmPrice = Double.parseDouble(price);
                double d = c * hmPrice;
                String e = String.valueOf(d);

                tvPrice.setText("RM "+e+"0");

                //Toast.makeText(HomestayBookingActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
