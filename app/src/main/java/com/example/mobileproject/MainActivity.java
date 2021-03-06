package com.example.mobileproject;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mobileproject.Common.Common;
import com.google.firebase.FirebaseApp;

//in this class basically we are sent to the home activity

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        //App context to get directory
        Common.APPLICATION_CONTEXT = (ContextWrapper) getApplicationContext();


        Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
