package com.indiaapps.myfirebaseapplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile_Activity extends AppCompatActivity
{

    private int screenWidth,screenHeight;
    private ImageView profile_pic;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private Button profile_pic_change;

    private TextView user_name_txt,user_email_id_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        screenWidth=getResources().getDisplayMetrics().widthPixels;
        screenHeight=getResources().getDisplayMetrics().heightPixels;

        mAuth=FirebaseAuth.getInstance();
        Log.e("msg","user id "+mAuth.getCurrentUser().getUid());
        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mDatabaseUser.keepSynced(true);

        Toolbar toolbar=(Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_email_id_txt=(TextView)findViewById(R.id.user_email_id_txt);
        user_name_txt=(TextView)findViewById(R.id.user_name_txt);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        profile_pic =(ImageView) findViewById(R.id.profile_pic);
        profile_pic.getLayoutParams().height=(screenWidth*50)/100;
        profile_pic.getLayoutParams().width=(screenWidth*50)/100;


        mDatabaseUser.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user1 = dataSnapshot.getValue(User.class);
                Glide.with(Profile_Activity.this).load(Uri.parse(user1.getUser_pic_url())).into(profile_pic);
                user_email_id_txt.setText(user1.getUser_email());
                user_name_txt.setText(user1.getUser_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        profile_pic_change=(Button)findViewById(R.id.profile_pic_change);
        profile_pic_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
