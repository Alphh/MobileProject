package com.example.mobileproject.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.CategoryModel.Wallpaperitem;
import com.example.mobileproject.Common.Common;
import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.R;
import com.example.mobileproject.ViewHolder.ListWallpaperViewHolder;
import com.example.mobileproject.ViewWallpaper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


//All Fragment class is used to represent anything under the 'ALL' tab in the app

public class AllFragment extends Fragment {

    //here we start to initiate variables that we will need, including the db variables
    private static AllFragment INSTANCE = null;
    //The FirebaseRecyclerAdapter binds a Query to a RecyclerView
    private final FirebaseRecyclerAdapter<Wallpaperitem, ListWallpaperViewHolder> adapter;
    private RecyclerView recyclerView;

    public AllFragment()
    {
        // here we initiate the database and call for wallpapers
        Query query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER);

        //here we configure the adapter by building recycleroptions
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
                                //if there is an error in setting the image the drawable will appear
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

                //if the holder is clicked on by the user, the viewwallpaper class is started, and the background image is passed to the viewwallpaper class
                holder.setItemClickListener(new itemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), ViewWallpaper.class);
                        Common.select_background = model;
                        startActivity(intent);

                    }
                });
            }
//Here we are setting where the item falls withing the recyclerview
            @NonNull
            @Override
            public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_wallpaper_item, viewGroup, false);

                return new ListWallpaperViewHolder(itemView);
            }
        };

    }

//this is default code for all fragments
    public static AllFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AllFragment();

        return INSTANCE;
    }

    //this is where everything starts
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater gets the layout.xml file and creates view objects
        View view = inflater.inflate(R.layout.activity_list_wallpaper, container, false);
        //this is where the recyclerview starts and goes to find the recycler_list_wallpaper
        recyclerView = view.findViewById(R.id.recycler_list_wallpaper);
        recyclerView.setHasFixedSize(true);
        //toolbar is set here
        Toolbar temp = view.findViewById(R.id.toolbar);
        temp.setVisibility(View.GONE);

        //the grid layout is set and allows for 2 images at a time within the grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //starting activity
        setWallpaper();

        return view;
    }

    //here the activity life cycle starts
    private void setWallpaper() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        //start getting the data
        super.onStart();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    public void onStop() {
        //if user changes activity for example
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null)
            adapter.startListening();
    }
}