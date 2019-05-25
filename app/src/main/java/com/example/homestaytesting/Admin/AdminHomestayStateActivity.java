package com.example.homestaytesting.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.homestaytesting.HomestayPost.FormActivity;
import com.example.homestaytesting.HomestayPost.PostListingActivity;
import com.example.homestaytesting.Main2Activity;
import com.example.homestaytesting.R;
import com.example.homestaytesting.UserProfile2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminHomestayStateActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference UsersRef,Postsref,LikesRef;
    private FirebaseAuth hmAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    RelativeLayout RL1, RL2, RL3, RL4, RL5, RL6, RL7, RL8, RL9, RL10, RL11, RL12, RL13, RL14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homestay_state);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homestay by State");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        hmAuth = FirebaseAuth.getInstance();
        currentUserid = hmAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //Postsref = FirebaseDatabase.getInstance().getReference().child("Uploads");

        findViewById(R.id.RL1).setOnClickListener(this);
        findViewById(R.id.RL2).setOnClickListener(this);
        findViewById(R.id.RL3).setOnClickListener(this);
        findViewById(R.id.RL4).setOnClickListener(this);
        findViewById(R.id.RL5).setOnClickListener(this);
        findViewById(R.id.RL6).setOnClickListener(this);
        findViewById(R.id.RL7).setOnClickListener(this);
        findViewById(R.id.RL8).setOnClickListener(this);
        findViewById(R.id.RL9).setOnClickListener(this);
        findViewById(R.id.RL10).setOnClickListener(this);
        findViewById(R.id.RL11).setOnClickListener(this);
        findViewById(R.id.RL12).setOnClickListener(this);
        findViewById(R.id.RL13).setOnClickListener(this);
        findViewById(R.id.RL14).setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RL1:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL2:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL3:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL4:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL5:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL6:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL7:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL8:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL9:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL10:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL11:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL12:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL13:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
            case R.id.RL14:
                startActivity(new Intent(this, AdminHomestayListingActivity.class));
                break;
        }
    }
}
