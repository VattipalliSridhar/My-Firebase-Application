package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener
{

    private int screenWidth,screenHeight;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String commmet_post;

    private ImageView post_image;
    private TextView post_rev_title,post_rev_desc;
    private Comment mComment;

    private DatabaseReference mDatabasePost;
    private DatabaseReference mDatabaseComments;
    private DatabaseReference mDatabaseUser;
    private EditText mCommentEditTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        commmet_post = getIntent().getExtras().getString("postid");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        mDatabasePost= FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseComments= FirebaseDatabase.getInstance().getReference().child("comments").child(commmet_post);
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());


        mDatabasePost.keepSynced(true);
        mDatabaseComments.keepSynced(true);
        mDatabaseUser.keepSynced(true);
        //mDatabaseComments= FirebaseDatabase.getInstance().getReference().child("Post").child(commmet_post).child(firebaseUser.getUid()).child("comments");




        screenWidth=getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;





        Toolbar toolbar=(Toolbar)findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog=new ProgressDialog(this);

        post_image=(ImageView)findViewById(R.id.post_rev_img);
        post_image.getLayoutParams().width=screenWidth;
        post_image.getLayoutParams().height=(screenHeight*40)/100;


        post_rev_title=(TextView)findViewById(R.id.post_rev_title);
        post_rev_desc=(TextView)findViewById(R.id.post_rev_desc);


        mDatabasePost.child(commmet_post).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Post post= dataSnapshot.getValue(Post.class);
                post_rev_title.setText(post.getTitle());
                post_rev_desc.setText(post.getDesc());
                Glide.with(CommentActivity.this).load(Uri.parse(post.getImage())).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        mCommentEditTextView = (EditText) findViewById(R.id.et_comment);
        findViewById(R.id.iv_send).setOnClickListener(this);
        Log.e("msg","user id  :"+ firebaseUser.getUid());
        Log.e("msg","post id  :"+ commmet_post);


        initCommentSection();
    }

    private void initCommentSection()
    {
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));


        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.row_comment,
                CommentHolder.class,
                mDatabaseComments
        ) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                Glide.with(CommentActivity.this).load(model.getUserimage()).into(viewHolder.commentOwnerDisplay);
            }
        };

        commentRecyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.iv_send:
                sendComment();
        }
    }

    private void sendComment()
    {
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);


        mComment = new Comment();
        final String uid = firebaseUser.getUid();
        String strComment = mCommentEditTextView.getText().toString().trim();

        mComment.setCommentId(uid);
        mComment.setComment(strComment);
        mComment.setTimeCreated(System.currentTimeMillis());
        final DatabaseReference databaseReference=mDatabaseComments.push();
        if(!TextUtils.isEmpty(strComment))
        {
            progressDialog.show();
            mDatabaseUser.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    databaseReference.setValue(mComment);
                    databaseReference.child("userimage").setValue(dataSnapshot.child("user_pic_url").getValue());
                    databaseReference.child("username").setValue(dataSnapshot.child("user_name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                mCommentEditTextView.getText().clear();
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
        else
        {
            mCommentEditTextView.setError("Required user comments.");
        }


    }

    public static class CommentHolder extends RecyclerView.ViewHolder
    {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView)
        {
            super(itemView);
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_comment_owner_display);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            commentTextView = (TextView) itemView.findViewById(R.id.tv_comment);
        }

        public void setUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            commentTextView.setText(comment);
        }
    }




}
