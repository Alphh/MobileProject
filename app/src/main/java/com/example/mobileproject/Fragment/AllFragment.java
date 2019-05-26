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

import com.example.mobileproject.CategoryModel.WallpaperItem;
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

public class AllFragment extends Fragment {
    private static AllFragment INSTANCE = null;
    private final FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;
    private RecyclerView recyclerView;

    public AllFragment() {
        Query query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER);

        FirebaseRecyclerOptions<WallpaperItem> options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, int position, @NonNull final WallpaperItem model) {

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

                holder.setItemClickListener(new itemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), ViewWallpaper.class);
                        Common.select_background = model;
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_wallpaper_item, viewGroup, false);


                return new ListWallpaperViewHolder(itemView);
            }
        };

    }

    public static AllFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AllFragment();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_list_wallpaper, container, false);
        recyclerView = view.findViewById(R.id.recycler_list_wallpaper);
        recyclerView.setHasFixedSize(true);
        Toolbar temp = view.findViewById(R.id.toolbar);
        temp.setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        setWallpaper();

        return view;
    }

    private void setWallpaper() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    public void onStop() {
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