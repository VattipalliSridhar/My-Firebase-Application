package com.indiaapps.myfirebaseapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{



    EditText mEmail,mPassword;
    TextView mforgotpassword;
    Button mLogin,mRegister,MGooglelogin;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;
    String Email,Password;
    public static final String TAG = "Login";
    ProgressDialog mDialog;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText)findViewById(R.id.login_emailId);
        mPassword =(EditText)findViewById(R.id.login_password);
        mforgotpassword = (TextView)findViewById(R.id.forgotPassword);
        mLogin = (Button)findViewById(R.id.login_here);
        mRegister = (Button)findViewById(R.id.register_here);
        MGooglelogin = (Button)findViewById(R.id.google_login);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListner = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                if (mUser!=null)
                {
                    Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(TAG,"AuthStateChange:LogOut");
                }
            }
        };
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        MGooglelogin.setOnClickListener(this);
        mforgotpassword.setOnClickListener(this);








    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.login_here:

                userSign();

                break;

            case R.id.register_here:

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

                break;

            case R.id.forgotPassword:

                Userforgotpassword();

                break;

        }

    }

    private void Userforgotpassword()
    {
        Email = mEmail.getText().toString().trim();
        if (TextUtils.isEmpty(Email))
        {
            Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Login please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())
                {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Password rest email sent ",
                            Toast.LENGTH_SHORT).show();

                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void userSign()
    {
        Email = mEmail.getText().toString().trim();
        Password = mPassword.getText().toString().trim();

        if (!validateForm())
        {
            return;
        }


        mDialog.setMessage("Login please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (!task.isSuccessful())
                {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"You must first sign up",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /*Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                    mDialog.dismiss();
                    checkEmailVerified();
                    //finish();
                }
            }
        });
    }



    private void checkEmailVerified()
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified=firebaseUser.isEmailVerified();

        if(!emailVerified)
        {
            Toast.makeText(LoginActivity.this,"Verify the email address",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
        else
        {
            Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


    private boolean validateForm()
    {
        boolean valid = true;


        if (TextUtils.isEmpty(Email)|| !isValidEmail(Email))
        {
            mEmail.setError("Enter please enter a valid email address ");
            valid = false;
        }
        else
        {
            mEmail.setError(null);
        }


        if (TextUtils.isEmpty(Password))
        {
            mPassword.setError("Required password.");
            valid = false;
        }
        else if( Password.length()<6)
        {
            Toast.makeText(LoginActivity.this,"Passwor must be greater then 6 digit",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else
        {
            mPassword.setError(null);
        }

        return valid;
    }

    private boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListner!=null){
            mAuth.removeAuthStateListener(mAuthListner);
        }
    }

    @Override
    public void onBackPressed() {
        LoginActivity.super.finish();
    }



}
