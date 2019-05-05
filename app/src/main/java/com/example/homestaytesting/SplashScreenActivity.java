package com.example.homestaytesting;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.homestaytesting.Modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth hmAuth;
    private DatabaseReference UserRef;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 4000; //splash screen will be shown for 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        hmAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

/*        if(hmAuth.getCurrentUser() !=null)
        {
            DetermineRole();
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

/*    public void DetermineRole(){
        final FirebaseUser user = hmAuth.getCurrentUser();
        final String uid = user.getUid();
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //replace noti when tukar device
        //UserRef.child(uid).child("devicetoken").setValue(deviceToken);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference xx = db.getReference();

*//*        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();*//*

        xx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User usersData = dataSnapshot.child("Users").child(uid).getValue(User.class);

                    if(usersData.getRole().equals("Guest"))
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(usersData.getRole().equals("Owner"))
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/
}
