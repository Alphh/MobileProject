package com.example.mobileproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mobileproject.Adapter.MyFragmentAdapter;
import com.example.mobileproject.Common.Common;
import com.google.firebase.FirebaseApp;

public class HomeActivity extends AppCompatActivity
{
    //this is used to check the permission code
    private final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize firebase
        FirebaseApp.initializeApp(this);
        //initialize layout
        setContentView(R.layout.activity_home);
        //creating toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WallpaperApp");
        setSupportActionBar(toolbar);

    //Creating the bottom navigation view (where the upload button is)
        BottomNavigationView menu_bottom = findViewById(R.id.navigation);
        menu_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_upload)
                    //making sure the user has internet before proceeding to the upload activity
                    if (Common.hasInternetConnectivity(getApplication())) {
                        startActivity(new Intent(HomeActivity.this, UploadWallpaper.class));
                    } else {
                        Toast.makeText(getApplication(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                return false;
            }
        });


        requestStoragePermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aboutus) {
            startActivity(new Intent(HomeActivity.this, AboutUs.class));
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestStoragePermission() {
        //Check whether to ask for permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is needed for the app to work properly")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            HomeActivity.this.finish();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    //Save image if storage permission is granted
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ViewPager viewPager = findViewById(R.id.viewPager);
                MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);

                TabLayout tabLayout = findViewById(R.id.tabLayout);
                tabLayout.setupWithViewPager(viewPager);
            } else {
                HomeActivity.this.finish();
            }
        }
    }
}
