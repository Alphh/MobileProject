package com.example.mobileproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DisplayImage extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private File FILE;
    private Button save;
    private ImageView image;
    private boolean saved = false;

    //Any URL with an image extension e.g. jpg/png/jpeg etc..
    private String IMAGE_URL = "https://cdn.pixabay.com/photo/2017/11/06/18/39/apple-2924531_960_720.jpg";

    //Image name .png
    private String IMAGE_NAME = "image1.png";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagesview);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File DIRECTORY = cw.getDir("images", Context.MODE_PRIVATE);
        FILE = new File(DIRECTORY, IMAGE_NAME);

        image = findViewById(R.id.image_view);
        save = findViewById(R.id.BT_save_image);

        //Loads image from url if file doesn't exist internally
        if (!FILE.exists()) {
            final ProgressDialog progressDialog = new ProgressDialog(DisplayImage.this);
            progressDialog.setMessage("Loading Image...");
            progressDialog.show();

            Picasso.get().load(IMAGE_URL).into(image, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    save.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Image loaded from URL!", Toast.LENGTH_LONG).show();
                }

            });

            //Otherwise loads it from local storage
        } else {
            loadImage();
            save.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Image loaded from internal storage!", Toast.LENGTH_LONG).show();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DisplayImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (!FILE.exists()) {
                        saveImage((BitmapDrawable) image.getDrawable());
                    } else {
                        Toast.makeText(getApplicationContext(), "Image already stored internally!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private void saveImage(BitmapDrawable drawable) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Saving Image...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        final Bitmap picture = drawable.getBitmap();

        Thread thread = new Thread() {
            @Override
            public void run() {
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(FILE);
                    picture.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    saved = true;
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();

                } finally {
                    try {
                        fos.close();

                    } catch (IOException e) {
                        pd.dismiss();
                        saved = false;
                        e.printStackTrace();

                    }
                }
                pd.dismiss();

                if (saved) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DisplayImage.this, "Image saved internally in: " + FILE.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DisplayImage.this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };
        thread.start();
    }

    private void loadImage() {
        try {
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(FILE));
            image.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is needed to save internally")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DisplayImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage((BitmapDrawable) image.getDrawable());
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}