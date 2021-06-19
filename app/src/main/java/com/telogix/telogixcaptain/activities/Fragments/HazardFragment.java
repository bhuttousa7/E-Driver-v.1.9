package com.telogix.telogixcaptain.activities.Fragments;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.adapters.SwipeableHazardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//import com.schibstedspain.leku.LocationPickerActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HazardFragment extends Fragment implements response_interface {

    ArrayList<Hazards> completeHazard = new ArrayList<>();
    private ListView recyclerView;
    ImageButton imgbtn_addHazard;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    double latitude,longitude;


    public HazardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hazard, container, false);
        getActivity().setTitle("All Hazard");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        recyclerView = v.findViewById(R.id.recyclerview_route);
        imgbtn_addHazard=v.findViewById(R.id.imgbtn_addroute);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
        imgbtn_addHazard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment(new add_HazardFragment());

            }
        });
//        if(completeHazard.size()>0)
//        {
//            recyclerView.setAdapter(new HazardAdapter(completeHazard));
//        }
//        else
//        {
//            getAllHazards();
//        }
        return v;
    }
    private void ChangeFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void getAllHazards() {

        NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET,hashMap , this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("All Hazard");
        getAllHazards();
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {

            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("ResponseCode"))
                {
                if(jsonObject.get("ResponseCode").equals(6002)) {
                    if (completeHazard.size() > 0) {
                        completeHazard.clear();
                    }
                    ArrayList<HazardDetail> hazardDetails = new ArrayList<>();


                    JSONArray array = jsonObject.getJSONArray("Data");
                    Hazards hazards = new Hazards();
                    HazardDetail hazardDetail = new HazardDetail();
                    for (int i = 0; i < array.length(); i++) {
                        hazards = new Hazards();

                        if (array.getJSONObject(i).has("HazardID")) {
                            if (!array.getJSONObject(i).get("HazardID").equals(null)) {
                                hazards.setHazardID((Integer) array.getJSONObject(i).get("HazardID"));
                            }
                        }
                        if (array.getJSONObject(i).has("HazardName")) {
                            if (!array.getJSONObject(i).get("HazardName").equals(null)) {
                                hazards.setHazardName((String) array.getJSONObject(i).get("HazardName"));
                            } else {
                                hazards.setDetail("No Name");
                            }
                        }
                        if (array.getJSONObject(i).has("HazardType")) {
                            if (!array.getJSONObject(i).get("HazardType").equals(null)) {
                                hazards.setHazardType("" + array.getJSONObject(i).get("HazardType"));
                            } else {
                                hazards.setHazardType("No Hazard Type");
                            }
                        }
                        if (array.getJSONObject(i).has("Location")) {
                            if (!array.getJSONObject(i).get("Location").equals(null)) {
                                hazards.setLocation(array.getJSONObject(i).get("Location"));
                            } else {
                                hazards.setLocation("Unknown Location");
                            }
                        }

                        if (array.getJSONObject(i).has("Latitude")) {
                            if (!array.getJSONObject(i).get("Latitude").equals(null)) {
                                hazards.setLatitude(array.getJSONObject(i).get("Latitude"));
                            } else {
                                hazards.setLatitude("0");
                            }
                        }
                        if (array.getJSONObject(i).has("Longitude")) {
                            if (!array.getJSONObject(i).get("Longitude").equals(null)) {
                                hazards.setLongitude(array.getJSONObject(i).get("Longitude"));
                            } else {
                                hazards.setLongitude("0");
                            }
                        }
                        if (array.getJSONObject(i).has("Detail")) {
                            if (!array.getJSONObject(i).get("Detail").equals(null)) {
                                hazards.setDetail(array.getJSONObject(i).get("Detail").toString());
                            } else {
                                hazards.setDetail("No Description");
                            }
                        }


                        JSONArray hazardDetailArray = array.getJSONObject(i).getJSONArray("HazardDetails");


                        for (int j = 0; j < hazardDetailArray.length(); j++) {

                            hazardDetail = new HazardDetail();
                            if (hazardDetailArray.getJSONObject(j).has("HazardDetailID")) {
                                if (!hazardDetailArray.getJSONObject(j).get("HazardDetailID").equals(null)) {
                                    hazardDetail.setHazardDetailID((Integer) hazardDetailArray.getJSONObject(j).get("HazardDetailID"));
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("HazardID")) {
                                if (!hazardDetailArray.getJSONObject(j).get("HazardID").equals(null)) {
                                    hazardDetail.setHazardID((Integer) hazardDetailArray.getJSONObject(j).get("HazardID"));
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("ImageUrl")) {
                                if (!hazardDetailArray.getJSONObject(j).get("ImageUrl").equals(null)) {
                                    hazardDetail.setImageUrl("" + hazardDetailArray.getJSONObject(j).get("ImageUrl"));

                                } else {
                                    hazardDetail.setImageUrl("N/A");
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("VoiceNoteUrl")) {
                                if (!hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl").equals(null)) {
                                    hazardDetail.setVoiceNoteUrl("" + hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl"));

                                }
                            }
                            hazardDetails.add(hazardDetail);

                        }


                        // hazardDetails.add(hazardDetail);
                        hazards.setHazardDetails(hazardDetails);
                        completeHazard.add(hazards);
                        hazardDetails = new ArrayList<>();


                    }

                    recyclerView.setAdapter(new SwipeableHazardAdapter(getContext(), HazardFragment.this, completeHazard));
                }
                else if(jsonObject.get("ResponseCode").equals(6009))
                    {
                        Toast.makeText(getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                        getAllHazards();
                    }
                }
        }
        catch (Exception ex)
        {
            Log.d("error",""+ex);
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Log.d("response eroor", "" + singleton.getsharedpreference(getContext()).getString("token", ""));
    }

}
