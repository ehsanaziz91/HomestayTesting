package com.example.homestaytesting.HomestayBooking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;

import com.example.homestaytesting.HomestayBooking.RatingReview.RatRevActivity;
import com.example.homestaytesting.HomestayBooking.RatingReview.RatRevDetailsActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.MainActivity;
import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class HomestayDetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        SlyCalendarDialog.Callback {

    private static final String TAG = "Debug";
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private ChildEventListener mChildEventListener;
    Marker marker;

    NavigationView navigationView;

    private Toolbar mToolbar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef, databaseReference, UserRef, dbRef, notiRef;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, imgUrl, hmName, hmLocation, hmPrice, hmDetails, hmContact, hmLat, hmLang, hmId,
            hmBathroom, hmBedrooms, hmPropertyType, hmFurnish, hmFacilities, hmFacilities2, hmFacilities3, hmFacilities4, name, phone, email, uid, hmReview;

    private ImageView imgView;
    private TextView tvName, tvName1, tvLocation, tvPrice, tvDetails, tvContact, tvBed, tvBath, tvProperty, tvFurnish, tvAc, tvCa, tvInternet, tvWm, tvTotalPrice, tvReview, tvMoreReview;
    private EditText edtTextCalendar,edtTextCalendar1, editTextReviews;
    private Button btnPay, btnAvailability, btnBook, btnSubmit;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton fAB1, fAB2, fAB3;
    private LinearLayout linearLayout;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo = "+60197272594";
    String message = "I have a question for you!";

    NestedScrollView nestedScrollView;
    FrameLayout frameLayout;
    CoordinatorLayout rootLayout;
    BottomNavigationView bottomNavigationView;

    private String firstdate;
    private String seconddate;
    private String e;
    private String dateRange;

    private RatingBar rating, ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_details);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homestay Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgView = findViewById(R.id.imgView);
        tvName = findViewById(R.id.tvName);
        //tvName1 = findViewById(R.id.tvName1);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetails = findViewById(R.id.tvDetails);
        tvContact = findViewById(R.id.tvContact);
        tvBed = findViewById(R.id.tvBed);
        tvBath = findViewById(R.id.tvBath);
        tvProperty = findViewById(R.id.tvProperty);
        tvFurnish = findViewById(R.id.tvFurnish);
        tvAc = findViewById(R.id.tvAc);
        tvCa = findViewById(R.id.tvCa);
        tvInternet = findViewById(R.id.tvInternet);
        tvWm = findViewById(R.id.tvWm);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnBook = findViewById(R.id.btnBook);
        //btnPay = findViewById(R.id.buttonPay);
        btnAvailability = findViewById(R.id.btnAvailability);
        edtTextCalendar = findViewById(R.id.edtTextCalendar);
        edtTextCalendar1 = findViewById(R.id.edtTextCalendar1);

        fabMenu = findViewById(R.id.fab);
        fAB1 = findViewById(R.id.fab1);
        fAB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Send email", "");
                String[] TO = {""};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    finish();
                    Log.i("Finished sending email...", "");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HomestayDetailsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fAB2= findViewById(R.id.fab2);
        fAB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("smsto:"));
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", new String(phoneNo));
                    i.putExtra("sms_body",message);
                    startActivity(Intent.createChooser(i, "Send sms via:"));
                }
                catch(Exception e){
                    Toast.makeText(HomestayDetailsActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fAB3 = findViewById(R.id.fab3);
        fAB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNo));
                startActivity(callIntent);
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        databaseReference = FirebaseDatabase.getInstance().getReference("Booking");
        dbRef = FirebaseDatabase.getInstance().getReference("Ratings&Reviews");
        notiRef = FirebaseDatabase.getInstance().getReference("Notifications");

        UserRef.goOffline();
        databaseRef.goOffline();
        databaseReference.goOffline();
        dbRef.goOffline();
        notiRef.goOffline();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {

                    imgUrl = dataSnapshot.child("imgUrl").getValue().toString();
                    hmName = dataSnapshot.child("hmName").getValue().toString();
                    //hmLocation = dataSnapshot.child("hmLocation").getValue().toString();
                    hmPrice = dataSnapshot.child("hmPrice").getValue().toString();
                    hmDetails = dataSnapshot.child("hmDetails").getValue().toString();
                    hmContact = dataSnapshot.child("hmContact").getValue().toString();
                    hmLat = dataSnapshot.child("lat").getValue().toString();
                    hmLang = dataSnapshot.child("lang").getValue().toString();
                    hmId = dataSnapshot.child("hmId").getValue().toString();
                    uid = dataSnapshot.child("uid").getValue().toString();

                    hmBathroom = dataSnapshot.child("hmBathroom").getValue().toString();
                    hmBedrooms = dataSnapshot.child("hmBedrooms").getValue().toString();
                    hmPropertyType = dataSnapshot.child("hmPropertyType").getValue().toString();
                    hmFurnish = dataSnapshot.child("hmFurnish").getValue().toString();
                    //hmFacilities = dataSnapshot.child("hmFacilities").getValue().toString();

                    //Toast.makeText(HomestayDetailsActivity.this, hmId, Toast.LENGTH_SHORT).show();

                    Double lat = Double.valueOf(hmLat);
                    Double lang = Double.valueOf(hmLang);

                    LatLng latLng = new LatLng(lat, lang);

                    /*----------to get Address from coordinates ------------- */
                    String address = null;
                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(lat, lang, 1);
                        if (addresses.size() > 0)
                            //System.out.println(addresses.get(0).getAddressLine(0));
                        address = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    tvName.setText(hmName);
                    tvLocation.setText(address);
                    tvPrice.setText("RM " + hmPrice + " per night");
                    tvDetails.setText(hmDetails);
                    tvContact.setText(hmContact);
                    tvBed.setText(hmBedrooms);
                    tvBath.setText(hmBathroom);
                    tvProperty.setText(hmPropertyType);
                    tvFurnish.setText(hmFurnish);

                    Picasso.with(HomestayDetailsActivity.this)
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

        databaseRef.child("hmFacilities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("1").exists()){
                    tvAc.setVisibility(View.GONE);
                } else {
                    hmFacilities = dataSnapshot.child("1").getValue().toString();
                    tvAc.setText(hmFacilities);
                }

                if(!dataSnapshot.child("2").exists()){
                    tvAc.setVisibility(View.GONE);
                } else {
                    hmFacilities2 = dataSnapshot.child("2").getValue().toString();
                    tvCa.setText(hmFacilities2);
                }

                if(!dataSnapshot.child("3").exists()){
                    tvInternet.setVisibility(View.GONE);
                }else {
                    hmFacilities3 = dataSnapshot.child("3").getValue().toString();
                    tvInternet.setText(hmFacilities3);
                }

                if(!dataSnapshot.child("4").exists()){
                    tvWm.setVisibility(View.GONE);
                }else{
                    hmFacilities4 = dataSnapshot.child("4").getValue().toString();
                    tvWm.setText(hmFacilities4);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //PayPal Implementation
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPalPayment();
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);

        //Book now
        /*btnAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomestayDetailsActivity.this, HomestayBookingActivity.class);
                intent1.putExtra("PostKey", PostKey);
                startActivity(intent1);
                return;
            }
        });*/

        btnAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setCallback(HomestayDetailsActivity.this)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        //change button availability to book pyament if date and price are set
        onButtonChanged();

        // ratings and review
        rating = findViewById(R.id.rating);
        tvReview = findViewById(R.id.tvReview);
/*        ratings = findViewById(R.id.ratings);
        editTextReviews = findViewById(R.id.editTextReviews);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalStars = "Total Stars:: " + ratings.getNumStars();
                String rating = "Rating :: " + ratings.getRating();
                String review = "Review :: " + editTextReviews.getText();
                //Toast.makeText(getApplicationContext(), totalStars + "\n" + rating + "\n" + review, Toast.LENGTH_LONG).show();
                ratingreviewsubmit();
            }
        });*/

        tvMoreReview = findViewById(R.id.tvMoreReview);
        tvMoreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click_post = new Intent(HomestayDetailsActivity.this, RatRevDetailsActivity.class);
                click_post.putExtra("PostKey", PostKey);
                startActivity(click_post);
            }
        });

        linearLayout = findViewById(R.id.tvRRLink);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click_post = new Intent(HomestayDetailsActivity.this, RatRevActivity.class);
                click_post.putExtra("PostKey", PostKey);
                startActivity(click_post);
            }
        });

        //retrieve the rating and review
        databaseRef.child("hmRatings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    for (final DataSnapshot s : dataSnapshot.getChildren()) {
                        float ratings = Float.parseFloat(s.getValue().toString());
                        rating.setRating(ratings);
                    }
                }
                else {
                    rating.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseRef.child("hmReviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    for(final DataSnapshot s : dataSnapshot.getChildren()) {
                        hmReview = s.getValue().toString();
                        tvReview.setText(hmReview);
                    }
                }
                else {
                    tvReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*        databaseRef.child("hmReviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sum = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    int size = (int) ds.getChildrenCount();
                    //int value = ds.getValue(Integer.class);
                    Log.e(ds.getKey(),ds.getChildrenCount() + "");
                    sum =+ sum + size;
                }

                String a = String.valueOf(sum);
                Toast.makeText(HomestayDetailsActivity.this,a,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

/*    private void ratingreviewsubmit() {
        Float rat = ratings.getRating();
        String review = editTextReviews.getText().toString();
        ratingreviewsubmits(rat, review);
    }

    private void ratingreviewsubmits(Float rat, String review) {
        rat = ratings.getRating();
        review = editTextReviews.getText().toString();

        HashMap ratreview = new HashMap();
        ratreview.put("rating", rat);
        ratreview.put("review", review);
        ratreview.put("hmID", hmId);

        dbRef.child(currentUserid).setValue(ratreview).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(HomestayDetailsActivity.this, "Successfully send your feedback",Toast.LENGTH_SHORT).show();
                    //submitRating();
                }
                else{
                    Toast.makeText(HomestayDetailsActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseRef.child("hmRatings").child(currentUserid).setValue(rat).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(HomestayDetailsActivity.this, "Successfully send your feedback",Toast.LENGTH_SHORT).show();
                    //submitRating();
                }
                else{
                    Toast.makeText(HomestayDetailsActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseRef.child("hmReviews").child(currentUserid).setValue(review).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(HomestayDetailsActivity.this, "Successfully send your feedback",Toast.LENGTH_SHORT).show();
                    submitRating();
                }
                else{
                    Toast.makeText(HomestayDetailsActivity.this, "An error on insert your feedback, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void submitRating() {
        try {
            databaseRef.child("hmRatings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    double total = 0.0;
                    double count = 0.0;
                    double average = 0.0;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        double rating = ds.getValue(Double.class);
                        total = total + rating;
                        count = count + 1;
                        average = total / count;

                        String a = String.valueOf(average);
                        Toast.makeText(HomestayDetailsActivity.this,"Ratings Average ::" + a,Toast.LENGTH_SHORT).show();
                    }

                    final DatabaseReference newRef = databaseRef.child("hmRatings");
                    newRef.child("Average").setValue(average);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

*//*            databaseRef.child("hmReviews").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int sum = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        int value = ds.getValue(Integer.class);
                        sum =+ sum + value;

                        String a = String.valueOf(sum);
                        Toast.makeText(HomestayDetailsActivity.this,"Review Total ::" + a,Toast.LENGTH_SHORT).show();
                    }

                    final DatabaseReference newRef = databaseRef.child("hmReviews");
                    newRef.child("Total").setValue(sum);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });*//*
        } catch (Exception e) {
            Toast.makeText(HomestayDetailsActivity.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }*/

    public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

        private int height;

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
            height = child.getHeight();
            return super.onLayoutChild(parent, child, layoutDirection);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                           BottomNavigationView child, @NonNull
                                                   View directTargetChild, @NonNull View target,
                                           int axes, int type)
        {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                   @NonNull View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed,
                                   @ViewCompat.NestedScrollType int type)
        {
            if (dyConsumed > 0) {
                slideDown(child);
            } else if (dyConsumed < 0) {
                slideUp(child);
            }
        }

        private void slideUp(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(0).setDuration(200);
        }

        private void slideDown(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(height).setDuration(200);
        }
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

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private void payPalPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(.10), "MYR", "Homestay Booking",
                PayPalPayment.PAYMENT_INTENT_SALE);
        //price hard code e

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try {
                        JSONObject jsonObj = new JSONObject(confirmation.toJSONObject().toString());
                        String paymentResponse = jsonObj.getJSONObject("response").getString("state");

                        if (paymentResponse.equals("approved")){

                            Calendar calFordDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            String saveCurrentDate = currentDate.format(calFordDate.getTime());

                            Calendar calFordTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                            String saveCurrentTime = currentTime.format(calFordTime.getTime());

                            String postRandomName = saveCurrentDate + " " +saveCurrentTime;

                            HashMap payment = new HashMap();

                            payment.put("uid", currentUserid);
                            payment.put("userName", name);
                            payment.put("userEmail", email);
                            payment.put("userContact", phone);
                            payment.put("hmId", hmId);
                            payment.put("ownerId", PostKey);
                            payment.put("hmName", hmName);
                            payment.put("totalPrice", e);
                            payment.put("bookDate", dateRange);
                            payment.put("paymentDate", postRandomName);

                            databaseReference.child(currentUserid + postRandomName).setValue(payment);

                            //Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();

                            //send notification to owner
                            HashMap<String, String> alertnotification = new HashMap<>();
                            alertnotification.put("from", currentUserid);
                            alertnotification.put("type", "request");

                            notiRef.child("241XoIOo9sYJym8kfoZnXUCGD1s2").push()
                                    .setValue(alertnotification)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Notification Send", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(), "Payment Unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {

        PostKey = getIntent().getExtras().get("PostKey").toString();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        lastLocation = location;
        //String markerText = "You're here !";

        Double lat = Double.valueOf(hmLat);
        Double lang = Double.valueOf(hmLang);

        LatLng latLng = new LatLng(lat, lang);
        Marker marker  = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        //to show marker info window without clicked .title(markerText)
        //marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //editLocation.setText("");
        //pb.setVisibility(View.INVISIBLE);
/*        Toast.makeText(getBaseContext(), "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + location.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + location.getLatitude();
        Log.v(TAG, latitude);*/

        /*----------to get City & State -Name from coordinates ------------- */
        String cityName = null;
        String stateName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            System.out.println(addresses.get(0).getAdminArea());
            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

/*        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference Postsref = UsersRef.child("Uploads");

        Postsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MarkerOptions markerOptions = new MarkerOptions();


                for(final DataSnapshot s : dataSnapshot.getChildren())
                {
                    final Upload upload = s.getValue(Upload.class);
                    double latitude = upload.getLat();
                    double longitude = upload.getLang();

                    //Geocoder geocoder;

                    //geocoder = new Geocoder(this, Locale.getDefault());
                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String city = addresses.get(0).getLocality();
                        //String state = addresses.get(0).getAdminArea();
                        //String country = addresses.get(0).getCountryName();
                        //String postalCode = addresses.get(0).getPostalCode();
                        //String knownName = addresses.get(0).getFeatureName();
                        //Toast.makeText(MapActivity.this, state, Toast.LENGTH_SHORT).show();

                        String melakastring = "Malacca";
                        String melakastrings = "Melaka";
                        String melakastringss = "Durian Tunggal";
                        String melakastringsss = "Ayer Keroh";
                        String melakastringssss = "Alor Gajah";

                        if (melakastringss.equalsIgnoreCase(city) || melakastringssss.equalsIgnoreCase(city)){
                            LatLng location = new LatLng(upload.getLat(),upload.getLang());
                            markerOptions.position(location);
                            markerOptions.title(upload.getHmName());
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.homestay));

                            marker = mMap.addMarker(markerOptions);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

*//*                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*//*

*//*                    LatLng location = new LatLng(upload.getLat(),upload.getLang());
                    markerOptions.position(location);
                    markerOptions.title(upload.getHmName());
                    markerOptions.snippet("RM " + upload.getHmPrice()+"\n"+"1 h 52 m"+"\n"+"1 KM");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                    marker = mMap.addMarker(markerOptions);*//*

*//*                    String b = location.toString();

                    Toast.makeText(MapActivity.this, b, Toast.LENGTH_SHORT).show();*//*

*//*                    markerOptions.visible(false);// We dont need to show, if its less than 100 meter we can show, otherwise we will just create and we will make it visble or not later

                    marker = mMap.addMarker(markerOptions);

                    if (SphericalUtil.computeDistanceBetween(location, marker.getPosition()) < 100) {
                        marker.setVisible(true);

                    }*//*

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(final Marker marker) {

                            final String a = marker.getTitle();

                            Postsref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                                        //Cara get parent dri children
                                        final String PostKey = ds.getKey();
                                        String string = ds.child("hmName").getValue().toString();
                                        //Toast.makeText(MapActivity.this, PostKey, Toast.LENGTH_SHORT).show();

                                        if(a.equalsIgnoreCase(string)){
                                            Intent intent = new Intent(HomestayDetailsActivity.this, PostDetailsActivity.class);
                                            intent.putExtra("PostKey", PostKey);
                                            startActivity(intent);
                                            return;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
/*        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);*/
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //public class for set date range
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

                firstdate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime());
                //String seconddate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(secondDate.getTime());
                //String thirddatetime = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime());

                edtTextCalendar.setText(firstdate);

            } else {

                firstdate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime());
                seconddate = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(secondDate.getTime());
                //String thirddatetime = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime());

                String a = firstdate.replaceAll("\\D+","");
                String b = seconddate.replaceAll("\\D+","");

                edtTextCalendar.setText(firstdate + " - " + seconddate);

                dateRange = (firstdate+" - "+seconddate);

                //check edtText then pergi method buttonchange
                edtTextCalendar1.setText(firstdate + " - " + seconddate);

                String price = hmPrice;

                double c = Double.parseDouble(b) - Double.parseDouble(a);
                double hmPrice = Double.parseDouble(price);
                double d = c * hmPrice;
                e = String.valueOf(d);

                tvTotalPrice.setText("RM "+e+"0");

                //Toast.makeText(HomestayBookingActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //button change when user set date and price
    private void onButtonChanged() {

        edtTextCalendar1 = new EditText(this);
        edtTextCalendar1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                btnBook.setVisibility(View.VISIBLE);
                btnAvailability.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
