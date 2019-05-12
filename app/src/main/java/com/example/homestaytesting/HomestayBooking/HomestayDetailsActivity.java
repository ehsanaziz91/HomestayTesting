package com.example.homestaytesting.HomestayBooking;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomestayDetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

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
    private DatabaseReference databaseRef, databaseReference;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, imgUrl, hmName, hmLocation, hmPrice, hmDetails, hmContact, hmLat, hmLang, hmId,
            hmBathroom, hmBedrooms, hmPropertyType, hmFurnish, hmFacilities;

    private ImageView imgView;
    private TextView tvName, tvName1, tvLocation, tvPrice, tvDetails, tvContact, tvBed, tvBath, tvProperty, tvFurnish;
    private Button btnPay, btnBook;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton fAB1, fAB2, fAB3;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo = "+60197272594";
    String message = "I have a question for you!";

    NestedScrollView nestedScrollView;
    FrameLayout frameLayout;
    CoordinatorLayout rootLayout;
    BottomNavigationView bottomNavigationView;

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
        tvName1 = findViewById(R.id.tvName1);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetails = findViewById(R.id.tvDetails);
        tvContact = findViewById(R.id.tvContact);
        tvBed = findViewById(R.id.tvBed);
        tvBath = findViewById(R.id.tvBath);
        tvProperty = findViewById(R.id.tvProperty);
        tvFurnish = findViewById(R.id.tvFurnish);
        btnPay = findViewById(R.id.buttonPay);
        btnBook = findViewById(R.id.buttonBook);
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
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        databaseReference = FirebaseDatabase.getInstance().getReference("Booking");

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
                    tvName1.setText(hmName);
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

        //PayPal Implementation
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPalPayment();
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);

        //Book now
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomestayDetailsActivity.this, HomestayBookingActivity.class);
                intent1.putExtra("PostKey", PostKey);
                startActivity(intent1);
                return;
            }
        });
    }

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
        Intent mainIntent = new Intent(HomestayDetailsActivity.this, HomestayListingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private void payPalPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(0.10), "MYR", "Homestay Booking",
                PayPalPayment.PAYMENT_INTENT_SALE);

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
                            Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();

                            HashMap payment = new HashMap();

                            payment.put("hmName", hmName);
                            payment.put("bookPayment", true);

                            Calendar calFordDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            String saveCurrentDate = currentDate.format(calFordDate.getTime());

                            Calendar calFordTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                            String saveCurrentTime = currentTime.format(calFordTime.getTime());

                            String postRandomName = saveCurrentDate + saveCurrentTime;

                            databaseReference.child(currentUserid + postRandomName).setValue(payment);
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
        mMap.setMyLocationEnabled(true);

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
}
