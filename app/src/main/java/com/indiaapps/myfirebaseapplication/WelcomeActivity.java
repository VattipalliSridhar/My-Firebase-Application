package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity
{
    public static int screenWidth,screenHeight;


    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private String mCurrentUserUid;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private RecyclerView post_recycler_view;
    private List<Post> postList;
    private ProgressDialog progressDialog;
    PostListAdapter postListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //postList = new ArrayList<>();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabase.keepSynced(true);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        Toolbar toolbar=(Toolbar)findViewById(R.id.wel_come_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wel Come");
        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            finish();
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
                    //setUserDetails();

                }
                else
                {
                    // User is signed out

                }
            }
        };

        post_recycler_view=(RecyclerView) findViewById(R.id.post_list_recyclerView);
        post_recycler_view.setHasFixedSize(true);
        post_recycler_view.setLayoutManager(new LinearLayoutManager(this));







    }

/*    private void setUserDetails()
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
    }*/

    private void setUserData(FirebaseUser user)
    {
        mCurrentUserUid = user.getUid();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.post_row,
                PostViewHolder.class,
                mDatabase)
        {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position)
            {
                final String post_key=getRef(position).getKey();
                //model=new Post();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUserimage(getApplicationContext(),model.getUserimage());
                viewHolder.setUsername(model.getUsername());


                Log.e("Setting","Data img = "+ model.getImage());
                Log.e("Setting","Data title = "+ model.getTitle());
                Log.e("Setting","Data desc= "+ model.getDesc());
                Log.e("Setting","Data user= "+ model.getUsername());
                Log.e("Setting","Data user name= "+ model.getUserimage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_SHORT).show();
                    }
                });

            }


        };

        post_recycler_view.setAdapter(firebaseRecyclerAdapter);

    }



    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public PostViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }

        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.post_rev_title);
            post_title.setText(title);

        }
        public void setDesc(String desc)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.post_rev_desc);
            post_desc.setText(desc);
        }
        public void setUsername(String username)
        {
            TextView post_user=(TextView)mView.findViewById(R.id.post_rev_user);
            post_user.setText(username);
        }
        public void setImage(Context context , String image)
        {
            ImageView imageView=(ImageView)mView.findViewById(R.id.post_rev_img);
            imageView.getLayoutParams().height=(screenWidth*50)/100;
            imageView.getLayoutParams().width=(screenWidth*50)/100;

            Glide.with(context).load(image).into(imageView);
        }

        public void setUserimage(Context context, String userimage)
        {
            ImageView imageView1=(ImageView) mView.findViewById(R.id.post_rev_user_img);
            imageView1.getLayoutParams().height=(screenWidth*10)/100;
            imageView1.getLayoutParams().width=(screenWidth*10)/100;
            Glide.with(context).load(userimage).into(imageView1);
        }

        private Bitmap StringToBitmap(String userimage)
        {
            try
            {
                byte [] encodeByte= Base64.decode(userimage,Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            }
            catch(Exception e)
            {
                e.getMessage();
                return null;
            }
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:

                Intent intent=new Intent(WelcomeActivity.this,Post_Activity.class);
                //intent.putExtra("userid",mCurrentUserUid);
                startActivity(intent);

                break;
            case R.id.action_profile:
                Intent pro_intent=new Intent(WelcomeActivity.this,Profile_Activity.class);
                startActivity(pro_intent);
                break;

            case R.id.action_log_out:
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
