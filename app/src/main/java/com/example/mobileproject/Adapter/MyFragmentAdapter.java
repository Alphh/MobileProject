package com.example.mobileproject.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mobileproject.Fragment.AllFragment;
import com.example.mobileproject.Fragment.CategoryFragment;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    public MyFragmentAdapter(FragmentManager fm) {

        super(fm);

    }

    //In this class the appropriate fragment is allowed to be chosen using count as an indicator of where the user is pressing

    @Override
    public Fragment getItem(int i) {


        if (i == 0)
            return CategoryFragment.getInstance();

        else if (i == 1)

            return AllFragment.getInstance();

        else

            return null;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Category";

            case 1:
                return "All";

        }
        return "";
    }
}
