package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private Button login;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText email, password;
    private TextView passforget, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.emailID);
        password = findViewById(R.id.passwrd);
        login = findViewById(R.id.loginbtn);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(LoginPage.this, "You are Logged In", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginPage.this, ContactsActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginPage.this, "Please Login", Toast.LENGTH_SHORT).show();
                }

            }
        };
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailid = email.getText().toString();
                String pwd = password.getText().toString();
                if (emailid.isEmpty()) {
                    email.setError("Please Enter Email ID");
                    email.requestFocus();

                } else if (pwd.isEmpty()) {
                    password.setError("Please Enter Password");
                    password.requestFocus();
                } else if (emailid.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(LoginPage.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                } else if (!(pwd.isEmpty() && emailid.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(emailid, pwd).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginPage.this, "Login Error:" + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intToHome = new Intent(LoginPage.this, ContactsActivity.class);
                                startActivity(intToHome);
                            }
                        }

                    });
                } else {
                    Toast.makeText(LoginPage.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }

            }

        });


        signup = findViewById(R.id.sign);
        String text = "Not Signed Up? Sign Up";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(LoginPage.this, "Please Sign Up Here", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan1, 15, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signup.setText(ss);
        signup.setMovementMethod(LinkMovementMethod.getInstance());
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
            }
        });

        /*TextView textView1 = findViewById(R.id.forgotpass);
        String text1 = "Forgot Password? Click Here";
        SpannableString ss1 = new SpannableString(text1);
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget1) {
                Toast.makeText(LoginPage.this, "Reset Password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ms) {
                super.updateDrawState(ms);
                ms.setColor(Color.RED);
                ms.setUnderlineText(false);
            }
        };
        ss1.setSpan(clickableSpan2, 17, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        passforget.setText(ss1);
        passforget.setMovementMethod(LinkMovementMethod.getInstance());
        passforget.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginPage.this, ResetPassword.class);
                startActivity(intent1);
            }
        });*/
        passforget = findViewById(R.id.forgotpass);
        passforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this,ResetPassword.class));
            }
        });
    }




    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

}












