package com.example.mobileproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileproject.CategoryModel.CategoryItem;
import com.example.mobileproject.CategoryModel.Wallpaperitem;
import com.example.mobileproject.Common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadWallpaper extends AppCompatActivity {

    ImageView image_preview;
    Button btn_upload,btn_browser;
    MaterialSpinner spinner;
    //Material Spinner Data
    Map<String,String> spinnerData = new HashMap<>();

    private Uri filePath;

    String categoryIdSelect ="";

    //FirebaseStorage
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() !=null)
        {
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                image_preview.setImageBitmap(bitmap);
                btn_upload.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_wallpaper);

        //Firebase Storage procedure
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //View
        image_preview = (ImageView)findViewById(R.id.image_preview);
        btn_browser = (Button)findViewById(R.id.btn_browser);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        spinner = (MaterialSpinner)findViewById(R.id.spinner);

        //Load Spinner data
        loadCategoryToSpinner();

        //Button Event
        btn_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseImage();
            }



            private void chooseImage()
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedIndex() == 0) //hint not chosen
                    Toast.makeText(UploadWallpaper.this,"Please choose a category",Toast.LENGTH_SHORT).show();
                else
                upload();
            }
        });


    }

    private void upload()
    {
        if(filePath !=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            StorageReference ref = storageReference.child(new StringBuilder("images/").append(UUID.randomUUID().toString())
                    .toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            saveUrlCategory(categoryIdSelect,taskSnapshot.getStorage().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadWallpaper.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded : "+ (int) progress+"%");
                        }
                    });
        }
    }

    private void saveUrlCategory(String categoryIdSelect, String imageLink)
    {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .push()
                .setValue(new Wallpaperitem(imageLink,categoryIdSelect))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaper.this,"Succcess!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void loadCategoryToSpinner()
    {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_CATEGORY_BACKGROUND)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            CategoryItem item = postSnapShot.getValue(CategoryItem.class);
                            String key = postSnapShot.getKey();
                            spinnerData.put(key,item.getName());
                        }

                        //Since material spinner does not recieve hint, we will need to manually do it
                        Object[] valueArray = spinnerData.values().toArray();
                        List<Object> valueList = new ArrayList<>();
                        valueList.add(0,"Category");
                        valueList.addAll(Arrays.asList(valueArray));
                        spinner.setItems(valueList);
                        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item)
                            {//When the user chooses category, we need to get categoryID
                                Object[] keyArray = spinnerData.keySet().toArray();
                                List<Object> keyList = new ArrayList<>();
                                keyList.add(0,"Category_Key");
                                keyList.addAll(Arrays.asList(keyArray));
                                // Assign key when user chooses category
                                categoryIdSelect = keyList.get(position).toString();

                            }
                        });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}