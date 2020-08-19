package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button savebtn;
    private EditText usernameET, bionameET;
    private ImageView profileimage;
    private static int gallerypick=1;
    private Uri Imageuri;
    private StorageReference userProfileImgRef;
    private String downloadUrl;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide(); //hide the title bar


        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        savebtn = findViewById(R.id.save_settings);
        usernameET = findViewById(R.id.username_settings);
        bionameET = findViewById(R.id.bio_settings);
        profileimage = findViewById(R.id.settings_profile_picture);
        progressDialog = new ProgressDialog(this);


        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Galleryintent = new Intent();
                Galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                Galleryintent.setType("image/*");
                startActivityForResult(Galleryintent,gallerypick);

            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserdata();
            }
        });
        retrieveUserInfo();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallerypick && resultCode==RESULT_OK && data!=null){
            Imageuri=data.getData();
            profileimage.setImageURI(Imageuri);
        }
    }

    private void saveUserdata()
    {
        final String getUsername=usernameET.getText().toString();
        final String getUserstatus=bionameET.getText().toString();

        if(Imageuri==null)
        {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image"))
                    {
                        saveifonlywithoutImage();

                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this, "Please Select User Profile Image", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else if(getUsername.equals("")){
            Toast.makeText(this,"Username is Required",Toast.LENGTH_SHORT).show();
        }
        else if(getUserstatus.equals("")){
            Toast.makeText(this,"User Bio is Required",Toast.LENGTH_SHORT).show();
        }
        else{

            progressDialog.setTitle("Profile Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();

            final StorageReference filepath= userProfileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask =filepath.putFile(Imageuri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    downloadUrl=filepath.getDownloadUrl().toString();
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {

                        downloadUrl=task.getResult().toString();
                        HashMap<String, Object> profilemap =new HashMap<>();
                        profilemap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profilemap.put("name",getUsername);
                        profilemap.put("status",getUserstatus);
                        profilemap.put("image",downloadUrl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent intent=new Intent(SettingsActivity.this,ContactsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this,"Profile Settings have been updated",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                }
            });
        }
    }

    private void saveifonlywithoutImage() {
        final String getUsername=usernameET.getText().toString();
        final String getUserstatus=bionameET.getText().toString();



        if(getUsername.equals("")){
            Toast.makeText(this,"Username is Required",Toast.LENGTH_SHORT).show();
        }
        else if(getUserstatus.equals("")){
            Toast.makeText(this,"User Bio is Required",Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("Profile Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();

            HashMap<String, Object> profilemap =new HashMap<>();
            profilemap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            profilemap.put("name",getUsername);
            profilemap.put("status",getUserstatus);


            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Intent intent=new Intent(SettingsActivity.this,ContactsActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this,"Profile Settings have been updated",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }
    private void retrieveUserInfo()
    {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String imageDb=dataSnapshot.child("image").getValue().toString();
                    String nameDb=dataSnapshot.child("name").getValue().toString();
                    String bioDb=dataSnapshot.child("status").getValue().toString();

                    usernameET.setText(nameDb);
                    bionameET.setText(bioDb);
                    Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(profileimage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
