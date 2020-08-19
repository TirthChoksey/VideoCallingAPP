package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {
    BottomNavigationView navView;
    RecyclerView mycontactslist;
    ImageView findpeoplebtn;
    private DatabaseReference contactsRef,usersRef;
    private FirebaseAuth mauth;
    private String currentUserId;
    private String username="",profileimage="";
    private String calledby="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mauth=FirebaseAuth.getInstance();
        currentUserId=mauth.getCurrentUser().getUid();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        findpeoplebtn=findViewById(R.id.search_people);
        mycontactslist=findViewById(R.id.contact_list);
        mycontactslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        findpeoplebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent findIntent=new Intent(ContactsActivity.this,FindPeopleActivity.class);
                startActivity(findIntent);

            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    Intent mainIntent=new Intent(ContactsActivity.this, ContactsActivity.class);
                    startActivity(mainIntent);
                    break;

                case R.id.navigation_settings:
                    Intent settingsIntent=new Intent(ContactsActivity.this,SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;

                case R.id.navigation_notifications:
                    Intent notificationsIntent=new Intent(ContactsActivity.this,NotificationsActivity.class);
                    startActivity(notificationsIntent);
                    break;

                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent logoutIntent=new Intent(ContactsActivity.this,LoginPage.class);
                    startActivity(logoutIntent);
                    finish();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        checkForReceivingCall();



        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsRef.child(currentUserId), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int i, @NonNull Contacts contacts)
            {
                final String listUserID= getRef(i).getKey();

                usersRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            username=dataSnapshot.child("name").getValue().toString();
                            profileimage=dataSnapshot.child("image").getValue().toString();

                            holder.usernametxt.setText(username);
                            Picasso.get().load(profileimage).into(holder.profileimageview);

                        }
                        holder.callbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent callingIntent= new Intent(ContactsActivity.this, CallingActivity.class);
                                callingIntent.putExtra("visit_user_id", listUserID);
                                startActivity(callingIntent);


                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                ContactsViewHolder viewHolder= new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        mycontactslist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView usernametxt;
        Button callbtn;
        ImageView profileimageview;



        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            usernametxt=itemView.findViewById(R.id.name_contact);
            callbtn=itemView.findViewById(R.id.video_call_btn);
            profileimageview=itemView.findViewById(R.id.image_contact);
        }
    }

    private void checkForReceivingCall()
    {
        usersRef.child(currentUserId).child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild("ringing"))
                        {
                            calledby= dataSnapshot.child("ringing").getValue().toString();

                            Intent callingIntent= new Intent(ContactsActivity.this, CallingActivity.class);
                            callingIntent.putExtra("visit_user_id", calledby);
                            startActivity(callingIntent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }




    }


