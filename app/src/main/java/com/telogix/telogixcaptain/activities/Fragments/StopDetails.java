package com.telogix.telogixcaptain.activities.Fragments;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.activities.Briefing.BriefingFragment;
import com.telogix.telogixcaptain.adapters.StopsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class StopDetails extends Fragment {


    private final ArrayList<RouteDetail> Allpoints=new ArrayList<>();
    private Geocoder geocoder;

    public StopDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_stop_details, container, false);
        RecyclerView recyclerViewStopList=v.findViewById(R.id.recyclerstoplist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewStopList.setLayoutManager(layoutManager);

        try {
        TextView txtStart=v.findViewById(R.id.txtstart);
        TextView txtEnd=v.findViewById(R.id.txtend);
        txtStart.setText(getAddress(BriefingFragment.route.getOriginLat(), BriefingFragment.route.getOriginLong()));
        txtEnd.setText(getAddress(BriefingFragment.route.getDestinationLat(), BriefingFragment.route.getDestinationLong()));
        for(int i = 0; i< BriefingFragment.route.getRouteDetails().size(); i++){
            RouteDetail routeDetail= BriefingFragment.route.getRouteDetails().get(i);
                if(!routeDetail.getRouteTypeID().equals("1")) {
                    Allpoints.add(routeDetail);
                }
                        }

            if (Allpoints.size() > 0) {
                recyclerViewStopList.setAdapter(new StopsAdapter(getContext(),false, Allpoints,true));
            }
        }
        catch (Exception ex){}

        return v;
    }
    private String getAddress(double lat, double lng) {
        geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getLocality();

            }
        } catch (Exception e) {
            //e.printStackTrace();
            return "Unknown";
        }
        return "Unknown";
    }

}
