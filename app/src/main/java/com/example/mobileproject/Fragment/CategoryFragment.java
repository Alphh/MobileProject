package com.example.mobileproject.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.CategoryModel.CategoryItem;
import com.example.mobileproject.Common.Common;
import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.R;
import com.example.mobileproject.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class CategoryFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;

    RecyclerView recyclerView;

    private static CategoryFragment INSTANCE=null;

    public CategoryFragment() {
       database = FirebaseDatabase.getInstance();
       categoryBackground = database.getReference(Common.STR_CATEGORY_BACKGROUND);

       options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
               .setQuery(categoryBackground,CategoryItem.class)
               .build();

       adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final CategoryItem model)
           {

               Picasso.get()
                       .load(model.getImageLink())
                       .networkPolicy(NetworkPolicy.OFFLINE)
                       .into(holder.background_image, new Callback() {
                           @Override
                           public void onSuccess() {

                           }

                           @Override
                           public void onError(Exception e) {

                               //Try Again online.
                               Picasso.get()
                                       .load(model.getImageLink())
                                       .error(R.drawable.ic_explore_black_24dp)
                                       .into(holder.background_image, new Callback() {
                                           @Override
                                           public void onSuccess() {

                                           }

                                           @Override
                                           public void onError(Exception e) {

                                               Log.e("ERROR,","Could not fetch image");

                                           }
                                       });

                           }
                       });

               holder.category_name.setText(model.getName());

               holder.setItemClickListener(new itemClickListener());
               {

               }


           }

           @NonNull
           @Override
           public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View itemView = LayoutInflater.from(viewGroup.getContext())
                       .inflate(R.layout.layout_category,viewGroup,false);


               return new CategoryViewHolder(itemView);
           }
       };
    }

public static CategoryFragment getInstance()
{
if(INSTANCE == null)
    INSTANCE = new CategoryFragment();
return INSTANCE;
}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        setCategory();

        return view;
    }

    private void setCategory()
    {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();

    }

    @Override
    public void onStop()
    {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(adapter!=null)
            adapter.startListening();

    }
}
