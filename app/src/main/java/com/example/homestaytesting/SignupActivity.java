package com.example.homestaytesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.homestaytesting.Modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private RadioGroup radioGroupRoles;
    private RadioButton radioButtonRolesoption;
    private ProgressBar progressBar;

    private FirebaseAuth hmAuth;
    private String role;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPhone = findViewById(R.id.edit_text_phone);
        progressBar = findViewById(R.id.progressbar);

        radioGroupRoles = (RadioGroup) findViewById(R.id.registerRoleRadioGroup);
        radioGroupRoles.setOnCheckedChangeListener((new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonRolesoption = radioGroupRoles.findViewById(i);
                switch (i){
                    case R.id.radioButton_agent:
                        role = radioButtonRolesoption.getText().toString();
                        break;
                    case R.id.radioButton_customer:
                        role = radioButtonRolesoption.getText().toString();
                        break;

                    default:
                }
            }
        }));

        int i = radioGroupRoles.getCheckedRadioButtonId();
        radioButtonRolesoption = (RadioButton) findViewById(i);

        hmAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_register).setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            SendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hmAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        role = radioButtonRolesoption.getText().toString();
        final String profileimages2 = "https://firebasestorage.googleapis.com/v0/b/dreamhome-a806d.appspot.com/o/profile%20Images%2Fnophoto.png?alt=media&token=724e2036-5419-4eef-bbeb-d8b0083ea123";
        final String profiledescription = "";
        final String devicetoken = FirebaseInstanceId.getInstance().getToken();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.error_field_required));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_field_required));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_field_required));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.error_field_required));
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError(getString(R.string.permission_rationale));
            editTextPhone.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        hmAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {


                        User user = new User(
                                name,
                                email,
                                phone,
                                role,
                                profileimages2,
                                profiledescription,
                                devicetoken

                        );

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                    finish();
                                    SendUserToLoginActivity();

                                } else {
                                    //display a failure message
                                }
                            }
                        });

                    } else {
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

    }

    private void SendUserToLoginActivity() {
        Intent mainIntent = new Intent(SignupActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

/*        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference xx = db.getReference();

        xx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User usersData = dataSnapshot.child("Users").child(uid).getValue(User.class);

                    if(usersData.getRole().equals("People"))
                    {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(usersData.getRole().equals("Owner"))
                    {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
        });*/
    }

/*    public void next(View view)
    {
        Intent homeIntent = new Intent(Register2.this, TestFireBaseActivity.class);
        startActivity(homeIntent);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                registerUser();
                break;
            case R.id.tvSignIn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
