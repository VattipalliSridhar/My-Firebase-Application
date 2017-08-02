package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Post_Activity extends AppCompatActivity
{
    private int screenWidth,screenHeight;
    private ImageButton imageButton;Button post_button;
    private EditText post_title_edit_txt,post_description_edit_txt;
    public static final int REQUEST_CODE = 2;
    private Uri postImageUri;

    private StorageReference postStorage;
    private DatabaseReference postDatabase;
    private ProgressDialog progressDialog;

    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_);
        /*if(getIntent().getExtras() != null )
        {
            UserId=getIntent().getExtras().getString("userid");
            Log.e("msg","user id  "+UserId);
        }*/


        progressDialog=new ProgressDialog(this);
        postStorage= FirebaseStorage.getInstance().getReference().child("Post Image");
        postDatabase= FirebaseDatabase.getInstance().getReference().child("Post");

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
        progressDialog.show();


        if(!TextUtils.isEmpty(post_title)&&!TextUtils.isEmpty(post_description)&& postImageUri!=null)
        {

            StorageReference reference=postStorage.child(postImageUri.getLastPathSegment());
            reference.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri dowmloadUri=taskSnapshot.getDownloadUrl();
                    DatabaseReference databaseReference=postDatabase.push();
                    databaseReference.child("title").setValue(post_title);
                    databaseReference.child("desc").setValue(post_description);
                    databaseReference.child("postic").setValue(dowmloadUri.toString());
                    //databaseReference.child("post_user_id").setValue(UserId.toString());
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
            postImageUri=data.getData();
            imageButton.setImageURI(postImageUri);
        }
    }
}
