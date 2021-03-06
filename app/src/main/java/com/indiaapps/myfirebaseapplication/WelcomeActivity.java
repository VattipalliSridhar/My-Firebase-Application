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
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class WelcomeActivity extends AppCompatActivity
{
    public static int screenWidth,screenHeight;


    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private String mCurrentUserUid;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private RecyclerView post_recycler_view;
    private List<String> postList;
    private ProgressDialog progressDialog;
    PostListAdapter postListAdapter;



    //like code
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseUser;
    private boolean mProceesLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();


        postList = new ArrayList<>();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseComment=FirebaseDatabase.getInstance().getReference().child("comments");

        mDatabase.keepSynced(true);
        mDatabase.keepSynced(true);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        Toolbar toolbar=(Toolbar)findViewById(R.id.wel_come_toolbar);
        setSupportActionBar(toolbar);

        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/



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

        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseUser.keepSynced(true);
        post_recycler_view=(RecyclerView) findViewById(R.id.post_list_recyclerView);
        post_recycler_view.setHasFixedSize(true);
        post_recycler_view.setLayoutManager(new LinearLayoutManager(this));


        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setTitle(""+dataSnapshot.child("user_name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







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
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position)
            {
                final String post_key=getRef(position).getKey();
                //model=new Post();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUserimage(getApplicationContext(),model.getUserimage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeButton(post_key);
                viewHolder.setCommenteButton(post_key);
                viewHolder.setPosttime(model.getPosttime());

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
                       // Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.like_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mProceesLike=true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if(mProceesLike)
                                {
                                    if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid()))
                                    {
                                        mDatabaseLike.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        mProceesLike=false;

                                    }
                                    else
                                    {
                                        mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                mDatabaseLike.child(post_key).child(firebaseAuth.getCurrentUser().getUid())
                                                        .setValue(dataSnapshot.child("user_name").getValue());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        mProceesLike=false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


                viewHolder.comment_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                            intent.putExtra("postid",post_key);
                            startActivity(intent);
                    }
                });


            }


        };

        post_recycler_view.setAdapter(firebaseRecyclerAdapter);

    }



    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        Button like_button,comment_button;
        TextView like_count_txt,comment_count_txt;

        DatabaseReference mDatabaseLike,mDatabaseComments;
        FirebaseAuth mAuth;

        public PostViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;

            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseComments=FirebaseDatabase.getInstance().getReference().child("comments");
            mDatabaseComments.keepSynced(true);
            mDatabaseLike.keepSynced(true);

            like_button=(Button)mView.findViewById(R.id.like_button);
            like_count_txt=(TextView)mView.findViewById(R.id.like_count_txt);

            comment_button=(Button)mView.findViewById(R.id.comment_button);
            comment_count_txt=(TextView)mView.findViewById(R.id.comment_count_txt);

        }

        public void setLikeButton(final String post_key)
        {
            mDatabaseLike.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid()))
                    {
                        like_button.setBackgroundResource(R.drawable.like);
                    }
                    else
                    {

                        like_button.setBackgroundResource(R.drawable.dislike);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    long countChildren = dataSnapshot.getChildrenCount();
                    Log.e("msg","count "+countChildren);
                    if(countChildren==0)
                    {
                        like_count_txt.setText("");
                    }
                    else
                    {
                        like_count_txt.setText(""+countChildren);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

        public void setCommenteButton(final String post_key)
        {
            mDatabaseComments.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    long countChildren = dataSnapshot.getChildrenCount();
                    Log.e("msg","count "+countChildren);
                    if(countChildren==0)
                    {
                        comment_count_txt.setText("");
                    }
                    else
                    {
                        comment_count_txt.setText(""+countChildren);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        public void setPosttime(long time)
        {
            TextView post_user=(TextView)mView.findViewById(R.id.post_time_txt);
            post_user.setText(DateUtils.getRelativeTimeSpanString(time));
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
