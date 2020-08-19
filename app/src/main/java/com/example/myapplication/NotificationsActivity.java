package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notifiations_list;
    private DatabaseReference friendRequestRef,contactsRef,usersRef;
    private FirebaseAuth mauth;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide(); //hide the title bar

        mauth=FirebaseAuth.getInstance();
        currentUserId=mauth.getCurrentUser().getUid();
        friendRequestRef= FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        notifiations_list=findViewById(R.id.notifications_list);
        notifiations_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
       FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(friendRequestRef.child(currentUserId),Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewHolder holder, int i, @NonNull Contacts contacts)
            {
                holder.acceptbtn.setVisibility(View.VISIBLE);
                holder.declinebtn.setVisibility(View.VISIBLE);

               final String listUserID= getRef(i).getKey();

                DatabaseReference requestTypeRef= getRef(i).child("request_type").getRef();
                requestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String type=dataSnapshot.getValue().toString();
                            if(type.equals("received"))
                            {
                                holder.cardView.setVisibility(View.VISIBLE);
                                usersRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if(dataSnapshot.hasChild("image"))
                                        {
                                            final String imageStr=dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(imageStr).into(holder.profileimage);

                                        }
                                        final String nameStr=dataSnapshot.child("name").getValue().toString();
                                        holder.username.setText(nameStr);
                                        holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                contactsRef.child(currentUserId).child(listUserID).child("Contacts").setValue("Saved")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    contactsRef.child(listUserID).child(currentUserId).child("Contacts").setValue("Saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        friendRequestRef.child(currentUserId).child(listUserID).removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if(task.isSuccessful())
                                                                                                        {
                                                                                                            friendRequestRef.child(listUserID).child(currentUserId).removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                                        {
                                                                                                                            if(task.isSuccessful())
                                                                                                                            {
                                                                                                                                Toast.makeText(NotificationsActivity.this, "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                            }

                                                                                                                        }
                                                                                                                    });
                                                                                                        }

                                                                                                    }
                                                                                                });

                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });

                                            }
                                        });
                                        holder.declinebtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                friendRequestRef.child(currentUserId).child(listUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    friendRequestRef.child(currentUserId).child(listUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(NotificationsActivity.this, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else
                            {
                                holder.cardView.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_design,parent,false);
                NotificationsViewHolder viewHolder= new NotificationsViewHolder(view);
                return viewHolder;
            }
        };

        notifiations_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        Button acceptbtn,declinebtn;
        ImageView profileimage;
        RelativeLayout cardView;


        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.name_notification);
            acceptbtn=itemView.findViewById(R.id.request_accept_btn);
            declinebtn=itemView.findViewById(R.id.request_decline_btn);
            profileimage=itemView.findViewById(R.id.image_notification);
            cardView=itemView.findViewById(R.id.card_view);
        }
    }




}
