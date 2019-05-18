package com.example.homestaytesting.HomestayPost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.homestaytesting.R;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = "Debug";
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private Toolbar mToolbar;

    private DatabaseReference UsersRef,Postsref,LikesRef;

    private FirebaseAuth hmAuth;
    private FirebaseDatabase firebaseDb;
    private String currentUserid, PostKey, imgUrl, hmName, hmLocation, hmPrice, hmDetails, hmContact, hmLat, hmLang,
            hmBedroom, hmBathroom, hmProperty, hmFurnish, email, name, hmFacilities, hmFacilities2, hmFacilities3, hmFacilities4;

    private ImageView imgView;
    private TextView tvName, tvLocation, tvPrice, tvDetails, tvContact, tvBed, tvBath, tvFurnish, tvProperty, tvEmail, tvName1, tvAc, tvCa, tvInternet, tvWm;
    private ExpandableTextView expTv, expTv1, expTv2;

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
        //tvDetails = findViewById(R.id.tvDetails);
        tvContact = findViewById(R.id.tvContact);
        tvBed = findViewById(R.id.tvBed);
        tvBath = findViewById(R.id.tvBath);
        tvProperty = findViewById(R.id.tvProperty);
        tvFurnish = findViewById(R.id.tvFurnish);
        // sample code snippet to set the text content on the ExpandableTextView
        expTv = findViewById(R.id.expand_text_view);
        expTv1 = findViewById(R.id.expand_text_view1);
        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(getString(R.string.content));
        expTv2 = findViewById(R.id.expand_text_view2);
        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv2.setText(getString(R.string.content1));

        tvName1 = findViewById(R.id.tvName1);
        tvEmail = findViewById(R.id.tvEmail);
        tvAc = findViewById(R.id.tvAc);
        tvCa = findViewById(R.id.tvCa);
        tvInternet = findViewById(R.id.tvInternet);
        tvWm = findViewById(R.id.tvWm);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);

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
                    hmBedroom = dataSnapshot.child("hmBedrooms").getValue().toString();
                    hmBathroom = dataSnapshot.child("hmBathroom").getValue().toString();
                    hmProperty = dataSnapshot.child("hmPropertyType").getValue().toString();
                    hmFurnish = dataSnapshot.child("hmFurnish").getValue().toString();
                    hmLat = dataSnapshot.child("lat").getValue().toString();
                    hmLang = dataSnapshot.child("lang").getValue().toString();

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
                    tvLocation.setText(hmLocation);
                    tvPrice.setText("RM " + hmPrice + " per night");
                    //tvDetails.setText(hmDetails);
                    expTv.setText(hmDetails);
                    tvContact.setText(hmContact);
                    tvBed.setText(hmBedroom);
                    tvBath.setText(hmBathroom);
                    tvProperty.setText(hmProperty);
                    tvFurnish.setText(hmFurnish);

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

        Postsref.child("hmFacilities").addValueEventListener(new ValueEventListener() {
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

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();

                    tvName1.setText(name);
                    tvEmail.setText(email);
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

    @Override
    public void onLocationChanged(Location location) {

        PostKey = getIntent().getExtras().get("PostKey").toString();
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads").child(PostKey);
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

}
