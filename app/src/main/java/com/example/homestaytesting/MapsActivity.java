package com.example.homestaytesting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private DatabaseReference UsersRef,Postsref,LikesRef;

    private FirebaseAuth hmAuth;
    private String currentUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        DatabaseReference Postsref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference UsersRef = Postsref.child("Uploads");

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Upload upload = ds.getValue(Upload.class);
                    double latitude = upload.getLat();
                    double longitude = upload.getLang();
                    Log.d("TAG", latitude + " / " +  longitude);

                    LatLng location = new LatLng(upload.getLat(),upload.getLang());
                    MarkerOptions markerOptions = new MarkerOptions();
                    //mMap.addMarker(new MarkerOptions()
                    markerOptions.position(location);
                    markerOptions.title(upload.getHmName());
                    markerOptions.snippet("RM " + upload.getHmPrice()+"\n"+"1 h 52 m"+"\n"+"1 KM");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                    Marker locationMarker = mMap.addMarker(markerOptions);

                    final String name = locationMarker.getTitle();

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            UsersRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        String PostKey = ds.getKey();
                                        String string = ds.child("hmName").getValue().toString();
                                        Toast.makeText(MapsActivity.this, name, Toast.LENGTH_SHORT).show();
/*                                        Intent intent = new Intent(MapsActivity.this, PostDetailsActivity.class);
                                        intent.putExtra("PostKey", PostKey);
                                        startActivity(intent);*/
                                        return;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }


/*                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){

                    final Upload upload = locationSnapshot.getValue(Upload.class);

                    final Double tempLat = (upload.getLat());
                    final Double tempLng = (upload.getLang());
                    //final String uid = locations.getUid();
                    final DatabaseReference Postsref = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference UsersRef = Postsref.child("Uploads");
                    UsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //String name = dataSnapshot.child("hmName").getValue().toString();
                            //String business = dataSnapshot.child("hmPrice").getValue().toString();

                            LatLng allLatLang = new LatLng(tempLat,tempLng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(allLatLang);
                            markerOptions.title(upload.getHmName());
                            //markerOptions.snippet("RM " + upload.getHmPrice());
                            markerOptions.snippet("RM " + upload.getHmPrice()+"\n"+"1 h 52 m"+"\n"+"1 KM");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                            //locationMarker = mMap.addMarker(markerOptions);

                            markerOptions.visible(false);// We dont need to show, if its less than 100 meter we can show, otherwise we will just create and we will make it visble or not later

                            Marker locationMarker = mMap.addMarker(markerOptions);

                            if (SphericalUtil.computeDistanceBetween(allLatLang, locationMarker.getPosition()) < 100) {
                                locationMarker.setVisible(true);

                            }

                            final String name = locationMarker.getTitle();

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {


                                    UsersRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                String PostKey = ds.getKey();
                                                String string = ds.child("hmName").getValue().toString();


                                                Toast.makeText(MapsActivity.this, name, Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MapsActivity.this, string, Toast.LENGTH_SHORT).show();
                    *//*                                Intent intent = new Intent(MapsActivity.this, PostDetailsActivity.class);
                                                    intent.putExtra("PostKey", PostKey);
                                                    startActivity(intent);*//*




                                            }
                                            //return;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //UsersRef.addListenerForSingleValueEvent(eventListener);
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
        //mMap.setOnInfoWindowClickListener(this);
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

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        String markerText = "You're here !";

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker marker  = mMap.addMarker(new MarkerOptions().position(latLng).title(markerText));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

    //calculate distance
    public static double calcDistance(double lat1, double long1 , double lat2 , double long2){
        try {
            double dist = 0.0;
            double deltaLat = Math.toRadians(lat2 - lat1);
            double deltaLong = Math.toRadians(long2 - long1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            long1 = Math.toRadians(long1);
            long2 = Math.toRadians(long2);
            double earthRadius = 6371;
            double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            dist = earthRadius * c;
            //dist = dist / 1000;
            return dist;
        } catch (Exception e){
            return  0;
        }
    }

/*    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        //intent.putExtra("PostKey", PostKey);
        startActivity(intent);
    }*/
}
