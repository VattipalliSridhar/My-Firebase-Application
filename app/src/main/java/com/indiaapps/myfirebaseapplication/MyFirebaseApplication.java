package com.indiaapps.myfirebaseapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sridhar on 8/1/2017.
 */

public class MyFirebaseApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
