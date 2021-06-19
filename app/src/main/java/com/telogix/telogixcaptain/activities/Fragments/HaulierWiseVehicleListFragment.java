package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HaulierWiseVehicleListFragment extends Fragment implements response_interface {
    ListView lstVehicles;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter replayVehiclesAdapter;
    private TokenPojo datum;
    private android.widget.SearchView searchView;
    public static int selectedposition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_haulier_wise_vehicle_list, container, false);

        lstVehicles = v.findViewById(R.id.lstVehicles);
        searchView = v.findViewById(R.id.searchView);

        String response = singleton.getsharedpreference(getContext()).getString("userData", "");
        if (response != "") {
            Gson gson = new Gson();
            datum = gson.fromJson(response, TokenPojo.class);
        }

        getActivity().setTitle("Vehicle List");
        getVehiclesList();

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> filteredList = new ArrayList<>();
                if (selectedposition == 0) {
                    for (String row : arrayList) {
                        if (row.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                } else {
                    for (String row : arrayList) {
                        if (row.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }

                replayVehiclesAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, filteredList);
                replayVehiclesAdapter.notifyDataSetChanged();
                lstVehicles.setAdapter(replayVehiclesAdapter);
//                ReplayVehiclesAdapter.notifyDataSetChanged();
                return true;
            }
        });


        return v;
    }


//    @Override
//    public void onPause() {
//        super.onPause();
//        Bundle outState = new Bundle();
//        outState.putParcelable("LIST_INSTANCE_STATE",lstVehicles.onSaveInstanceState());
//        lstVehicles.onRestoreInstanceState(mListInstanceState);
//    }


    private void getVehiclesList() {

        if (datum != null) {
            if (UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER) {
                HashMap<String, String> data = new HashMap<>();
                Bundle bundle = getArguments();
                String haulierID = bundle.getString("haulierID");
                httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles?haulierID=" + haulierID, Request.Method.GET, data, this);
                NetworkConsume.getInstance().ShowProgress(getContext());
            } else {
                String haulierID = datum.getHaulierID();
                HashMap<String, String> data = new HashMap<>();
                httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles?haulierID=" + haulierID, Request.Method.GET, data, this);
                NetworkConsume.getInstance().ShowProgress(getContext());
            }
        }

    }

    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedposition = 0;
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        //Log.d("--dataarray",dataArray.toString());
        arrayList.clear();
        for (int i = 0; i < dataArray.length(); i++) {
            arrayList.add(dataArray.getJSONObject(i).getString("VehicleNo"));
        }


        replayVehiclesAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        replayVehiclesAdapter.notifyDataSetChanged();
        lstVehicles.setAdapter(replayVehiclesAdapter);
        lstVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String VehicleNo = (String) adapterView.getItemAtPosition(i);

                try {
                    for (int j = 0; j < dataArray.length(); j++) {
                        String N = dataArray.getJSONObject(j).getString("VehicleNo");
                        if (VehicleNo == N) {

                            int VehicleID = dataArray.getJSONObject(j).getInt("VehicleID");
                            Bundle bundle = getArguments();
                            bundle.putString("VehicleID", String.valueOf(VehicleID));
                            bundle.putString("VehicleNo", VehicleNo);

                            if (bundle.getString("flag", null) == "replay") {
                                ChangeFragment(new ReplayTrips(), bundle);
                            } else if (bundle.getString("flag", null) == "report") {
                                ChangeFragment(new ReportDetailsFragment(), bundle);
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }


    @Override
    public void onError(VolleyError Error) {

    }
}

