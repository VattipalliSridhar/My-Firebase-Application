package com.indiaapps.myfirebaseapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity
{
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private String mCurrentUserUid;
    //view objects
    private TextView textView_User_name;
    private ImageView profile_pic;
    private Button buttonLogout;
    private DatabaseReference mFirebasereference;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        textView_User_name = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        profile_pic=(ImageView)findViewById(R.id.profile_pic);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebasereference=FirebaseDatabase.getInstance().getReference().child("users");
        mFirebasereference.keepSynced(true);
        //initializing firebase authentication object


        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null)
        {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {


                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    setUserData(user);
                    setUserDetails();

                }
                else
                {
                    // User is signed out

                }
            }
        };





        //adding listener to button
        buttonLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });
    }

    private void setUserDetails()
    {
        mFirebasereference.child(mCurrentUserUid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user1 = dataSnapshot.getValue(User.class);
                textView_User_name.setText("Welcome "+user1.getUser_name());
                Glide.with(WelcomeActivity.this).load(Uri.parse(user1.getUser_pic_url())).into(profile_pic);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setUserData(FirebaseUser user)
    {
        mCurrentUserUid = user.getUid();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();


        if (mAuthListener != null)
        {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }

    }
}
