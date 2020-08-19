package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button ResetButton;
    private EditText ResetEmail;
    private FirebaseAuth zauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide(); //hide the title bar
        zauth = FirebaseAuth.getInstance();


        ResetButton = (Button) findViewById(R.id.resetbtn);
        ResetEmail = (EditText) findViewById(R.id.resetEmail);

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = ResetEmail.getText().toString();
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ResetPassword.this, "Please Enter The Valid Email ID", Toast.LENGTH_SHORT).show();
                } else {
                    zauth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Please Confirm your Email Address If you want to reset your password", Toast.LENGTH_SHORT).show();
                                Intent mint = new Intent(ResetPassword.this, LoginPage.class);
                                startActivity(mint);

                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPassword.this, "Error Occurred:" +  message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }

}