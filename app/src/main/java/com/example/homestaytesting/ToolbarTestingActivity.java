package com.example.homestaytesting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homestaytesting.HomestayPost.FormActivity;
import com.example.homestaytesting.HomestayPost.PostDetailsActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.HomestayPost.PostUpdateActivity;
import com.example.homestaytesting.Modal.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import lib.kingja.switchbutton.SwitchMultiButton;

public class ToolbarTestingActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private DatabaseReference UsersRef,Postsref,LikesRef;

    private FirebaseAuth hmAuth;
    private String currentUserid;

    private RecyclerView postList;

    //private Toolbar hmToolbar;
    private SwitchMultiButton switchMultiButton;

    String Map = "Map";
    String List = "List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_testing);
        //setContentView(R.layout.activity_post_listing);

        postList = findViewById(R.id.recycler_view);
/*        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);*/

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        switchMultiButton = findViewById(R.id.switchmultibutton2);

        switchMultiButton.setText(Map, List).setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {

                if (tabText.equalsIgnoreCase(Map)){
                    /*Intent intent = new Intent(ToolbarTestingActivity.this, FormActivity.class);
                    startActivity(intent);*/
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ToolbarTestingActivity.this);
                }else {
                    /*Intent intent = new Intent(ToolbarTestingActivity.this, PostListingActivity.class);
                    startActivity(intent);*/
                    postList = findViewById(R.id.recycler_view);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ToolbarTestingActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    postList.setLayoutManager(linearLayoutManager);
                }
            }
        });

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
        DatabaseReference UsersRef = Postsref.child("Uploads");
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Upload upload = ds.getValue(Upload.class);
                    double latitude = upload.getLat();
                    double longitude = upload.getLang();
                    Log.d("TAG", latitude + " / " +  longitude);

                    LatLng location=new LatLng(upload.getLat(),upload.getLang());
                    mMap.addMarker(new MarkerOptions().position(location).title(upload.getHmName())).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //UsersRef.addListenerForSingleValueEvent(eventListener);
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("hmPrice");

        FirebaseRecyclerOptions<Upload> options = new FirebaseRecyclerOptions.Builder<Upload>().setQuery(SortAgentPost, Upload.class).build();

        FirebaseRecyclerAdapter<Upload,ToolbarTestingActivity.PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Upload, ToolbarTestingActivity.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull ToolbarTestingActivity.PostsViewHolder holder, final int position, @NonNull Upload model)
            {

                final String PostKey = getRef(position).getKey();

                holder.hmName.setText(model.getHmName());
                //holder.hmDetails.setText(model.getHmDetails());
                holder.hmLocation.setText(model.getHmLocation());
                holder.hmPrice.setText("RM " + model.getHmPrice());
                //holder.hmContact.setText(model.getHmContact());
                //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);
                Picasso.with(ToolbarTestingActivity.this)
                        .load(model.getImgUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.hmImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        // Untuk dpat Id dalam table post
                        //String PostKey = getSnapshots().getSnapshot(position).getKey();


                        Intent click_post = new Intent(ToolbarTestingActivity.this, PostDetailsActivity.class);
                        click_post.putExtra("PostKey", PostKey);
                        startActivity(click_post);
                    }
                });

            }

            @NonNull
            @Override
            public ToolbarTestingActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
                ToolbarTestingActivity.PostsViewHolder viewHolder = new ToolbarTestingActivity.PostsViewHolder(view);

                return viewHolder;
            }
        };

        //postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView hmName, hmDetails, hmLocation, hmPrice, hmContact;
        ImageView hmImage;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            hmName = itemView.findViewById(R.id.tvName);
            //hmDetails = itemView.findViewById(R.id.tvDetails);
            hmLocation = itemView.findViewById(R.id.tvLocation);
            hmPrice = itemView.findViewById(R.id.tvPrice);
            //hmContact = itemView.findViewById(R.id.tvContact);
            hmImage = itemView.findViewById(R.id.imgView);

        }
    }
}
