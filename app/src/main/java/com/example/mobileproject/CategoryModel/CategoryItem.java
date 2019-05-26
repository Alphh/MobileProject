package com.example.mobileproject.CategoryModel;

import io.realm.RealmObject;

public class CategoryItem extends RealmObject {
    private String name;
    private String imageLink;

    public CategoryItem() {
    }

    public CategoryItem(String name, String imageLink) {
        this.name = name;
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
