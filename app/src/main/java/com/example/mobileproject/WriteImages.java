package com.example.mobileproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.mobileproject.CategoryModel.CategoryItem;
import com.example.mobileproject.Common.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;

//AsyncTask - everything occurs on another thread
public class WriteImages extends AsyncTask {
    //image_array stores Images while image_details stores the image Category and URL
    private final ArrayList<ImageView> image_array;
    private final ArrayList<String[]> image_details;

    private final File FILE = Common.APPLICATION_CONTEXT.getDir("images", Context.MODE_PRIVATE); //Parent Directory

    public WriteImages(ArrayList<ImageView> image_array, ArrayList<String[]> image_details) {
        this.image_array = image_array;
        this.image_details = image_details;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        int count = 0;
        String image_name;

        for (ImageView image : image_array) {
            image_name = this.image_details.get(count)[1];

            /*File Writing Operation start*/
            File temp = new File(FILE, image_name);
            FileOutputStream fos = null;

            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable(); //Images are stored as bitmaps
            final Bitmap picture = drawable.getBitmap();

            try {
                fos = new FileOutputStream(temp);
                picture.compress(Bitmap.CompressFormat.PNG, 100, fos);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            /* File Writing Operation end*/


            final int index = count;
            final String finalImage_name = image_name;

            /*Writing image info to Realm DB
             * Database transactions slow the app down so run on yet another Thread
             * */
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        realm.beginTransaction();

                        CategoryItem img = realm.createObject(CategoryItem.class);
                        img.setName(WriteImages.this.image_details.get(index)[0]);
                        img.setImageLink(finalImage_name);
                        img.setCategoryId(WriteImages.this.image_details.get(index)[2]);

                        realm.commitTransaction();
                    }
                }
            };
            thread.start();
            Log.i("image " + count, Arrays.toString(image_details.get(count)));

            count++;
        }

        return null;
    }
}