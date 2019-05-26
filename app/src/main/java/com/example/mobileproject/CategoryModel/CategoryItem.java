package com.example.mobileproject.CategoryModel;

import io.realm.RealmObject;

public class CategoryItem extends RealmObject {
    private String name;
    private String imageLink;
    private String category_id;

    //here we are *getting* and *setting* the image URL and the category name which is then used to set the categories as we see them on the screen

    public CategoryItem()
    {
    }

    public CategoryItem(String name, String imageLink, String category_id) {
        this.name = name;
        this.imageLink = imageLink;
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return category_id;
    }

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
