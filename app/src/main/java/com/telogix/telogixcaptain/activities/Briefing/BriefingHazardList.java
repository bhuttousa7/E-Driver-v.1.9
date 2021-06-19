package com.telogix.telogixcaptain.activities.Briefing;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BriefingHazardList extends Fragment {


    private RecyclerView recyclerView;

    public BriefingHazardList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_briefing_hazard_list, container, false);
        recyclerView = v.findViewById(R.id.recyclerview_hazards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new HazardAdapter(BriefingFragment.completeHazard));

        return v;
    }

}
