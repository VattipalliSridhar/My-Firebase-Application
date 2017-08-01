package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText sign_up_email,sign_up_password,sign_up__name;
    Button sign_up_button,upload_button;
    ImageView user_pic;



    private ProgressDialog progressDialog;

    String Name,Email,Password;
    TextView mLoginPageBack;


    public static final String FB_STORAGE_PATH = "user_profile_pic/";
    public static final int REQUEST_CODE = 1;
    private Uri imgUri;
    private String user_pic_uri;

    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private StorageReference mStorageRef;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
/*
        Toolbar toolbar=(Toolbar)findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sample Signup");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        //Get the storage reference
       // mStorageRef = mStorageRef.child(FB_STORAGE_PATH + Name + "." + getImageExt(imgUri));

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RegisterActivity.this.finish();
            }
        });*/

        sign_up__name=(EditText)findViewById(R.id.sign_up__name);
        sign_up_email=(EditText)findViewById(R.id.sign_up_email);
        sign_up_password=(EditText)findViewById(R.id.sign_up_password);


        progressDialog = new ProgressDialog(this);

        sign_up_button=(Button)findViewById(R.id.sign_up_button);
        sign_up_button.setOnClickListener(this);

        user_pic=(ImageView)findViewById(R.id.user_pic);

        upload_button=(Button)findViewById(R.id.upload_button);
        upload_button.setOnClickListener(this);




        mLoginPageBack = (TextView)findViewById(R.id.loginBackbtn);
        mLoginPageBack.setOnClickListener(this);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sign_up_button:

                UserRegister();

                break;

            case R.id.loginBackbtn:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;

            case R.id.upload_button:
                /*Name = sign_up__name.getText().toString().trim();
                if (TextUtils.isEmpty(Name))
                {
                    Toast.makeText(RegisterActivity.this, "Enter User name", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),REQUEST_CODE);

                break;


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


            /*if (imgUri != null)
            {

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Uploading image");
                dialog.show();

                //Get the storage reference
                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + (sign_up__name.getText().toString().trim()+System.currentTimeMillis()) + "." + getImageExt(imgUri));

                //Add file to reference

                ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        //Dimiss dialog when success
                        dialog.dismiss();
                        //Display success toast msg
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        user_pic_uri=taskSnapshot.getDownloadUrl().toString();//ignore that error

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //Dimiss dialog when error
                                dialog.dismiss();
                                //Display err toast msg
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                        {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                            {

                                //Show upload progress
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();

            }*/



            /*try
            {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                user_pic.setImageBitmap(bm);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }*/
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                 resultUri = result.getUri();
                Log.e("msg","crop img uri"+resultUri);
                user_pic.setImageURI(resultUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Log.e("msg error","crop img uri"+error);
            }
        }
    }


    public String getImageExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UserRegister()
    {
        Name = sign_up__name.getText().toString().trim();
        Email = sign_up_email.getText().toString().trim();
        Password = sign_up_password.getText().toString().trim();

        if (!validateForm())
        {
            return;
        }


        progressDialog.setMessage("Creating User please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference filePath = mStorageRef.child(resultUri.getLastPathSegment());
        filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot)
            {
                mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {

                            sendEmailVerification();
                            progressDialog.dismiss();
                            user_pic_uri=taskSnapshot.getDownloadUrl().toString();//ignore that error
                            OnAuth(task.getResult().getUser());
                            mAuth.signOut();
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"error on creating user",Toast.LENGTH_SHORT).show();
                        }
                    }


                });

            }
        });





    }




    private void sendEmailVerification()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(RegisterActivity.this, "Sign up success ",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateForm()
    {
        boolean valid = true;

        String username = sign_up__name.getText().toString().trim();
        if (TextUtils.isEmpty(username))
        {
            sign_up__name.setError("Required user name.");
            valid = false;
        }
        else
        {
            sign_up__name.setError(null);
        }

        String email = sign_up_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)|| !isValidEmail(sign_up_email.getText().toString().trim()))
        {
            sign_up_email.setError("Enter please enter a valid email address ");
            valid = false;
        }
        else
        {
            sign_up_email.setError(null);
        }

        String password = sign_up_password.getText().toString().trim();
        if (TextUtils.isEmpty(password) )
        {
            sign_up_password.setError("Required password.");
            valid = false;
        }
        else if( password.length()<6)
        {
            Toast.makeText(RegisterActivity.this,"Passwor must be greater then 6 digit",Toast.LENGTH_SHORT).show();
            sign_up_password.setError(null);
        }
        else
        {
            sign_up_password.setError(null);
        }

        if (resultUri == null)
        {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else
        {


        }

        return valid;
    }


    private boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void OnAuth(FirebaseUser user)
    {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid)
    {
        User user = BuildNewuser();
        db.child(uid).setValue(user);
    }

    private User BuildNewuser()
    {
        return new User
                (getUser_name(),
                        getUser_email(),
                new Date().getTime(),
                        getUser_pic_url()
        );
    }

    public String getUser_name()
    {
        return Name;
    }

    public String getUser_email()
    {
        return Email;
    }


    public String getUser_pic_url()
    {
        return user_pic_uri;
    }
}
