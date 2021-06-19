package com.telogix.telogixcaptain.driver.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.telogix.telogixcaptain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RideUnavailableFragment extends Fragment {


    public RideUnavailableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_unavailable, container, false);
    }

}
