package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Post_Activity extends AppCompatActivity
{
    private int screenWidth,screenHeight;
    private ImageButton imageButton;Button post_button;
    private EditText post_title_edit_txt,post_description_edit_txt;
    public static final int REQUEST_CODE = 2;
    private Uri postImageUri;

    private StorageReference postStorage;
    private DatabaseReference postDatabase;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        /*if(getIntent().getExtras() != null )
        {
            UserId=getIntent().getExtras().getString("userid");
            Log.e("msg","user id  "+UserId);
        }*/


        progressDialog=new ProgressDialog(this);
        postStorage= FirebaseStorage.getInstance().getReference().child("Post Image");
        postDatabase= FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        imageButton=(ImageButton)findViewById(R.id.imageButton);
        imageButton.getLayoutParams().height=(screenWidth*60)/100;
        imageButton.getLayoutParams().width=(screenWidth*60)/100;

        Toolbar toolbar=(Toolbar)findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        post_title_edit_txt=(EditText)findViewById(R.id.post_title_edit_txt);
        post_description_edit_txt=(EditText)findViewById(R.id.post_description_edit_txt);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),REQUEST_CODE);
            }
        });


        post_button=(Button)findViewById(R.id.post_button);
        post_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                posting_method();
            }
        });

    }

    private void posting_method()
    {
        final String post_title=post_title_edit_txt.getText().toString().trim();
        final String post_description=post_description_edit_txt.getText().toString().trim();
        progressDialog.setMessage("Posting.........");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);



        if(!TextUtils.isEmpty(post_title)&&!TextUtils.isEmpty(post_description)&& postImageUri!=null)
        {
            progressDialog.show();
            StorageReference reference=postStorage.child(postImageUri.getLastPathSegment());
            reference.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    final Uri dowmloadUri=taskSnapshot.getDownloadUrl();
                    final DatabaseReference databaseReference=postDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            databaseReference.child("title").setValue(post_title);
                            databaseReference.child("desc").setValue(post_description);
                            databaseReference.child("image").setValue(dowmloadUri.toString());
                            databaseReference.child("uid").setValue(firebaseUser.getUid());
                            databaseReference.child("posttime").setValue(System.currentTimeMillis());
                            databaseReference.child("userimage").setValue(dataSnapshot.child("user_pic_url").getValue());
                            databaseReference.child("username").setValue(dataSnapshot.child("user_name").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(Post_Activity.this,WelcomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    /*Post post = new Post(post_title,post_description, taskSnapshot.getDownloadUrl().toString());

                    //Save image info in to firebase database
                    String uploadId = postDatabase.push().getKey();
                    postDatabase.child(uploadId).setValue(post);*/

                    progressDialog.dismiss();





                }
            });
        }
        else
        {
            Toast.makeText(Post_Activity.this,"Please fill ttile and description,image",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri imgUri = data.getData();
            Log.e("msg img getdata","crop img uri"+imgUri);
            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                postImageUri=result.getUri();
                imageButton.setImageURI(postImageUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Log.e("msg error","crop img uri"+error);
            }
        }
    }
}
