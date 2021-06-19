package com.telogix.telogixcaptain.activities.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Retailer.VehiclesFragmentRetailer;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    TokenPojo datum = null;
    LinearLayout livetrack, vehicles, layoutexcel, layouttutorial, layouthandbook, replay, layoutuploadusers, layoutreport, layouthazards;
    private final String notitoken = "";

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Dashboard");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        layouthazards = v.findViewById(R.id.layouthazards);
        layoutexcel = v.findViewById(R.id.layoutexcel);
        livetrack = v.findViewById(R.id.layoutlivetrack);
        replay = v.findViewById(R.id.layoutreplay);
        // routes=v.findViewById(R.id.layoutroutes);
        layouttutorial = v.findViewById(R.id.layouttutorial);
        layouthandbook = v.findViewById(R.id.layouthandbook);
        vehicles = v.findViewById(R.id.layoutvehichles);
        layoutuploadusers = v.findViewById(R.id.layoutuploadusers);
        layoutreport = v.findViewById(R.id.layoutreport);


        getActivity().setTitle("Dashboard");
        layouthandbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Handbook");
                ChangeFragment(new HandbookFragment());
            }
        });
        layouttutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Tutorials");
                ChangeFragment(new TutorialsFragment());
            }
        });
        layoutexcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Upload Excel");
                ChangeFragment(new UploadExcelFragment());
            }
        });
        layouthazards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Hazards");
                ChangeFragment(new HazardFragment());
            }
        });
        livetrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Live Tracking");
                TokenPojo datum = null;
                String response = singleton.getsharedpreference(getContext()).getString("userData", "");
                if (response != "") {
                    Gson gson = new Gson();
                    datum = gson.fromJson(response, TokenPojo.class);

                }
                if (datum != null) {

                    if (UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER) {
                        ChangeFragment(new HaulierListFragment());
                    } else {
                        ChangeFragment(new LiveTrackingFragment());
                    }
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Vehicle List");
                Bundle b = new Bundle();
                b.putString("flag", "replay");
                if (UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER) {
                    ChangeFragment(new HaulierListFragment(), b);
                } else {
                    ChangeFragment(new HaulierWiseVehicleListFragment(), b);
                }

            }
        });
        vehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenPojo datum = null;
                String response = singleton.getsharedpreference(getContext()).getString("userData", "");
                if (response != "") {
                    Gson gson = new Gson();
                    datum = gson.fromJson(response, TokenPojo.class);

                }
                if (datum != null) {

                    if (UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER) {
                        ChangeFragment(new VehiclesFragmentRetailer());
                    } else {
                        ChangeFragment(new VehiclesFragment());
                    }
                }

            }
        });

        layoutuploadusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.SUPER_ADMIN) {
                    ChangeFragment(new UploadUserFragment());
                } else {
                    Toast.makeText(getContext(), "You are not Authorized for this feature", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("flag", "report");
                ChangeFragment(new ReportFragment(), b);
//                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER){
//
//                    ChangeFragment(new HaulierListFragment(),b);
//                }
//                else{
//                        b.putString("haulierID",datum.getHaulierID());
//                        ChangeFragment(new HaulierWiseVehicleListFragment(),b);
//
//
//                }

            }
        });


        String response = singleton.getsharedpreference(getContext()).getString("userData", "");
        if (response != "") {
            Gson gson = new Gson();
            datum = gson.fromJson(response, TokenPojo.class);

        }
        if (datum != null) {

            if (UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER) {
                hideItem();
            }
        }
        return v;
    }

    private void hideItem() {
        layouthazards.setVisibility(View.GONE);
        layoutexcel.setVisibility(View.GONE);
        // routes.setVisibility(View.INVISIBLE);
//        vehicles.setVisibility(View.INVISIBLE);
        layouttutorial.setVisibility(View.GONE);
        layouthandbook.setVisibility(View.GONE);

    }

    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
//    public void checkAuth(){
//
//        HashMap data=new HashMap();
//        httpvolley.stringrequestpost("api/Routes/CheckAuth?token="+notitoken, Request.Method.POST,data, (response_interface) this);
//    }
}
