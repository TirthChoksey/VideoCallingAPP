package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignUpPage extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button sign;
    private FirebaseAuth mFirebaseAuth;
    private EditText usrname,email,password,repassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_main);


        loginButton = findViewById(R.id.login_button);
        mFirebaseAuth=FirebaseAuth.getInstance();
        usrname=findViewById(R.id.usr);
        email=findViewById(R.id.emailid);
        password=findViewById(R.id.pass);
        repassword=findViewById(R.id.repass);
        sign=findViewById(R.id.btn_sign);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid=email.getText().toString();
                String pwd=password.getText().toString();
                String repwd= repassword.getText().toString();
                String user= usrname.getText().toString();
                if(emailid.isEmpty()){
                    email.setError("Please Enter Email ID");
                    email.requestFocus();

                }
                else if (user.isEmpty()){
                    usrname.setError("Please Enter Username");
                    usrname.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please Enter Password");
                    password.requestFocus();
                }
                else if (!(pwd.equals(repwd))){
                    repassword.setError("Passwords don't match");
                    repassword.requestFocus();
                }
                else if (emailid.isEmpty() && pwd.isEmpty() && repwd.isEmpty() && user.isEmpty()){
                    Toast.makeText(SignUpPage.this,"Fields are Empty",Toast.LENGTH_SHORT).show();
                }
                else if (!(pwd.isEmpty() && emailid.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(emailid,pwd).addOnCompleteListener(SignUpPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUpPage.this,"SignUp Unsuccessful:" + message,Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(SignUpPage.this, ContactsActivity.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignUpPage.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            Intent homeIntent =new Intent(SignUpPage.this, ContactsActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestcode, resultcode, data);
        super.onActivityResult(requestcode, resultcode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                Toast.makeText(SignUpPage.this, "User Logged out", Toast.LENGTH_LONG).show();
            } else
                loaduserProfile(currentAccessToken);

        }
    };

    private void loaduserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();




            }
        }
