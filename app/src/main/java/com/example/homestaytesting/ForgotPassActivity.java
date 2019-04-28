package com.example.homestaytesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    private Toolbar hmToolbar;

    private EditText editTextEmail;
    private Button btnReset;

    private FirebaseAuth hmAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        hmToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(hmToolbar);
        getSupportActionBar().setTitle("Reset Your Password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextEmail = findViewById(R.id.editTextEmail);
        btnReset = findViewById(R.id.btnReset);
        hmAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();

                if (email.equals("")){
                    Toast.makeText(ForgotPassActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                }else {
                    hmAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassActivity.this, "Password reset email sent !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                            }else {
                                Toast.makeText(ForgotPassActivity.this, "Error in sending password reset email !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            SendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToLoginActivity() {

        Intent mainIntent = new Intent(ForgotPassActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
