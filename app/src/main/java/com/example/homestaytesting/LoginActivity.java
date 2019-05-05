package com.example.homestaytesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.homestaytesting.Modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth hmAuth;
    private DatabaseReference UserRef;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private ImageView loginlogo;
    private static int SPLASH_TIME_OUT = 3000;

    private Toolbar mToolbar;

    private RelativeLayout R1,R2;
    private Animation uptodown,downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hmAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if(hmAuth.getCurrentUser() !=null)
        {
            DetermineRole();
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.textResetPass).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

/*        loginlogo = findViewById(R.id.loginlogo);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fadein);
        loginlogo.startAnimation(anim);*/

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");

        R1 = (RelativeLayout) findViewById(R.id.R1);
        R2 = (RelativeLayout) findViewById(R.id.R2);

/*        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);

        R1.setAnimation(uptodown);
        R2.setAnimation(downtoup);*/
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String roles = "0";

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        hmAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful())
                    {
                        if (password.length() < 6)
                        {
                            editTextPassword.setError(getString(R.string.minimum_password));
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        DetermineRole();
                        //Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void DetermineRole(){
        final FirebaseUser user = hmAuth.getCurrentUser();
        final String uid = user.getUid();
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //replace noti when tukar device
        //UserRef.child(uid).child("devicetoken").setValue(deviceToken);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference xx = db.getReference();

/*        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
        startActivity(intent);
        finish();*/

        xx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User usersData = dataSnapshot.child("Users").child(uid).getValue(User.class);

                    if(usersData.getRole().equals("Guest"))
                    {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(usersData.getRole().equals("Owner"))
                    {
                        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.textViewSignup:
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.textResetPass:
                startActivity(new Intent(this, ForgotPassActivity.class));
                break;
        }

    }
}
