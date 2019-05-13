package com.example.mobileproject.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment {


    private static AllFragment INSTANCE=null;


    public AllFragment() {
        // Required empty public constructor
    }

    public static AllFragment getInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new AllFragment();
        return INSTANCE;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false);
    }

}
