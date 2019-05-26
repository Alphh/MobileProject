package com.example.mobileproject.Common;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mobileproject.CategoryModel.WallpaperItem;

public class Common {

    public static final String STR_CATEGORY_BACKGROUND = "CategoryBackground";
    public static final String STR_WALLPAPER = "Wallpapers";
    public static final int PICK_IMAGE_REQUEST = 1002;
    public static String CATEGORY_SELECTED;
    public static String CATEGORY_ID_SELECTED;
    public static WallpaperItem select_background = new WallpaperItem();
    public static ContextWrapper APPLICATION_CONTEXT;

    public static boolean hasInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }
}
