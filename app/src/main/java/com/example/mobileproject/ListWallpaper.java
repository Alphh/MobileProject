package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.CategoryModel.Wallpaperitem;
import com.example.mobileproject.Common.Common;
import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.ViewHolder.ListWallpaperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


//here we are listing wallpapers in order of category ID chosen
public class ListWallpaper extends AppCompatActivity {

    //initializing firebase and recycleviewer
    private FirebaseRecyclerAdapter<Wallpaperitem, ListWallpaperViewHolder> adapter;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wallpaper);
//creating toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.CATEGORY_SELECTED);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//creating recycler view
        recyclerView = findViewById(R.id.recycler_list_wallpaper);
        recyclerView.setHasFixedSize(true);
        //creating the grid with 2 images horizontally
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadBackgroundList();

    }

    private void loadBackgroundList()
    {
        // First we query the DB fetching the images according to CATEGORY_ID_SELECTED
        Query query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
                //here we are ordering by category ID
                .orderByChild("categoryID").equalTo(Common.CATEGORY_ID_SELECTED);
        //here we start to get the wallpapers
        FirebaseRecyclerOptions<Wallpaperitem> options = new FirebaseRecyclerOptions.Builder<Wallpaperitem>()
                .setQuery(query, Wallpaperitem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Wallpaperitem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, int position, @NonNull final Wallpaperitem model) {
                    //API we use to get image-links and actually turn them into images
                Picasso.get()
                        .load(model.getImageUrl())
                        .into(holder.wallpaper, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(model.getImageUrl())
                                        .error(R.drawable.ic_cloud_upload_black_24dp)
                                        .into(holder.wallpaper, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e("ERROR", "Could not fetch image");
                                            }
                                        });
                            }
                        });


                //if the holder is clicked , the viewwallpaper class is started, and the background image is passed to the viewwallpaper class
                holder.setItemClickListener(new itemClickListener() {
                    @Override
                    public void onClick(int position) {

                        Intent intent = new Intent(ListWallpaper.this, ViewWallpaper.class);
                        Common.select_background = model;
                        startActivity(intent);

                    }
                });


            }

            //viewholder for the wallpapers
            @NonNull
            @Override
            public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_wallpaper_item, viewGroup, false);

                int height = viewGroup.getMeasuredHeight() / 2;
                itemView.setMinimumHeight(height);
                return new ListWallpaperViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }
//activity lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    //Close activity when back button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
