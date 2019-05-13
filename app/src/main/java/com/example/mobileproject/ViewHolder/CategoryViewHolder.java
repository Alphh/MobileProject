package com.example.mobileproject.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView category_name;
    public ImageView background_image;

    itemClickListener itemClickListener;


    public void setItemClickListener(com.example.mobileproject.Interface.itemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        background_image= (ImageView) itemView.findViewById(R.id.categoryimage);
        category_name = (TextView)itemView.findViewById(R.id.categoryname);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        itemClickListener.onClick(v,getAdapterPosition());

    }
}
