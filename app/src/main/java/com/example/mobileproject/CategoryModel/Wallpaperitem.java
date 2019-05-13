package com.example.mobileproject.CategoryModel;

public class Wallpaperitem
{
    public String categoryID;
    public String imageUrl;


    public Wallpaperitem()
    {

    }

    public Wallpaperitem(String imageUrl, String categoryID) {
        this.categoryID = categoryID;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
