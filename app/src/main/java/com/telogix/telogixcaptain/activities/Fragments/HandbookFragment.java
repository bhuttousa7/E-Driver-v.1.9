package com.telogix.telogixcaptain.activities.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.adapters.TutorialsAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HandbookFragment extends Fragment  {







        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
             getActivity().setTitle("Handbook");


            View rootView = inflater.inflate(R.layout.fragment_tutorials, container, false);
            RecyclerView recycler_view=rootView.findViewById(R.id.recycler_view);
            recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            HashMap<String,String> tutorialhashmap=new HashMap<>();
//            tutorialhashmap.put("id",""+1);
//            tutorialhashmap.put("type","Video");
//            tutorialhashmap.put("url", connection.completeurl("Content/tutorials/tutorial1.mp4"));
//            tutorialhashmap.put("title","Tutorial");
            ArrayList<HashMap<String,String>> tutoriallist=new ArrayList<>();
//            tutoriallist.add(tutorialhashmap);
//            tutoriallist.add(tutorialhashmap);
//            tutoriallist.add(tutorialhashmap);
//            tutoriallist.add(tutorialhashmap);
//            tutoriallist.add(tutorialhashmap);
            tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","pdf");
            tutorialhashmap.put("url", connection.Baseurl+"/Content/tutorials/FatigueAwareness.pdf");
            tutorialhashmap.put("title","Fatigue Awareness");
            tutoriallist.add(tutorialhashmap);
            recycler_view.setAdapter(new TutorialsAdapter(getContext(),tutoriallist));
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
      }

        @Override
        public void onStop() {
            super.onStop();
            // we have to stop any playback in onStop
        }
    }

