package com.example.homestaytesting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.Modal.Upload;
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
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
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
    private DatabaseReference UsersRef,Postsref,LikesRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, PostListingActivity.class);
                startActivity(intent);
                return;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        String markerText = "You're here !";

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker marker  = mMap.addMarker(new MarkerOptions().position(latLng).title(markerText));
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

        String b = cityName.toString();
        String c = stateName.toString();

/*        Toast.makeText(MapActivity.this, b, Toast.LENGTH_SHORT).show();
        Toast.makeText(MapActivity.this, c, Toast.LENGTH_SHORT).show();

        String s = longitude + "\n" + latitude + "\n\nMy Currrent City is: " + cityName;
        //editLocation.setText(s);

        Toast.makeText(MapActivity.this, s, Toast.LENGTH_SHORT).show();*/
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

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference();
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
                        String state = addresses.get(0).getAdminArea();
                        //Toast.makeText(MapActivity.this, state, Toast.LENGTH_SHORT).show();

                        String melakastring = "Malacca";
                        String melakastrings = "Melaka";

                        if (melakastring.equalsIgnoreCase(state) || melakastrings.equalsIgnoreCase(state)){
                            LatLng location = new LatLng(upload.getLat(),upload.getLang());
                            markerOptions.position(location);
                            markerOptions.title(upload.getHmName());
                            markerOptions.snippet("RM " + upload.getHmPrice()+"\n"+"1 h 52 m"+"\n"+"1 KM");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                            marker = mMap.addMarker(markerOptions);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

/*                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*/

/*                    LatLng location = new LatLng(upload.getLat(),upload.getLang());
                    markerOptions.position(location);
                    markerOptions.title(upload.getHmName());
                    markerOptions.snippet("RM " + upload.getHmPrice()+"\n"+"1 h 52 m"+"\n"+"1 KM");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                    marker = mMap.addMarker(markerOptions);*/

/*                    String b = location.toString();

                    Toast.makeText(MapActivity.this, b, Toast.LENGTH_SHORT).show();*/

/*                    markerOptions.visible(false);// We dont need to show, if its less than 100 meter we can show, otherwise we will just create and we will make it visble or not later

                    marker = mMap.addMarker(markerOptions);

                    if (SphericalUtil.computeDistanceBetween(location, marker.getPosition()) < 100) {
                        marker.setVisible(true);

                    }*/

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
                                            Intent intent = new Intent(MapActivity.this, PostDetailsActivity.class);
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
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext();

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
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
