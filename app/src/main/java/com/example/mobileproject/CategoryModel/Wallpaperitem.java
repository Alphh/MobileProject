package com.example.mobileproject.CategoryModel;

//here we are *getting* and *setting* the imageURL which is then used to set the categories as we see them on the screen

public class Wallpaperitem {
    private String imageUrl;


    public Wallpaperitem() {

    }

    public Wallpaperitem(String imageUrl) {
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

}
