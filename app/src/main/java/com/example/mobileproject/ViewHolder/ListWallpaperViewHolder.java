package com.example.mobileproject.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.R;

//this is a container where the wallpaper is begin held dynamically and a click listener is implemented so that if the wallpaper is clicked on, it will proceed to activity
public class ListWallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ImageView wallpaper;
    private itemClickListener itemClickListener;

//the ListWallpaper viewholder describes an item view and metadata about its place within the RecyclerView.
    public ListWallpaperViewHolder(@NonNull View itemView) {
        super(itemView);
        wallpaper = itemView.findViewById(R.id.categoryimage);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(getAdapterPosition());

    }

    public void setItemClickListener(com.example.mobileproject.Interface.itemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
