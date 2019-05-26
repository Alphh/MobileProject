package com.example.mobileproject.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.R;


//this is basically a container holding the category name and images dynamically with a click listener
public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView category_name;
    public final ImageView background_image;

    private itemClickListener itemClickListener;


    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        background_image = itemView.findViewById(R.id.categoryimage);
        category_name = itemView.findViewById(R.id.categoryname);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(com.example.mobileproject.Interface.itemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(getAdapterPosition());
    }
}
