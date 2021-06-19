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
public class TutorialsFragment extends Fragment  {







        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
             getActivity().setTitle("Tutorials");


            View rootView = inflater.inflate(R.layout.fragment_tutorials, container, false);
            RecyclerView recycler_view=rootView.findViewById(R.id.recycler_view);
            recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            HashMap<String,String> tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","Video");
            tutorialhashmap.put("url", connection.Baseurl+"/Content/tutorials/DM.MP4");
            tutorialhashmap.put("title","Tutorial");
            ArrayList<HashMap<String,String>> tutoriallist=new ArrayList<>();
            tutoriallist.add(tutorialhashmap);
            tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","Video");
            tutorialhashmap.put("url", connection.Baseurl+"/Content/tutorials/JM_1.MP4");
            tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","Video");
            tutoriallist.add(tutorialhashmap);
            tutorialhashmap.put("url", connection.Baseurl+"/Content/tutorials/JM_2.MP4");
            tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","Video");
            tutoriallist.add(tutorialhashmap);
            tutorialhashmap.put("url", connection.Baseurl+"/Content/tutorials/LoadingandDecantationTraining.MP4");
            tutorialhashmap=new HashMap<>();
            tutorialhashmap.put("id",""+1);
            tutorialhashmap.put("type","Video");
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

