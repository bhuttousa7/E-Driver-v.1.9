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
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HaulierListFragment extends Fragment implements response_interface {
    ListView lstHaulierNames;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter haulierAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_haulier_list, container, false);
        lstHaulierNames = v.findViewById(R.id.lstHaulierNames);
        getActivity().setTitle("Hauliers");
        getHauliersList();
        return v;
    }

    private void getHauliersList() {

        HashMap<String, String> data = new HashMap<>();
        httpvolley.stringrequestpost("api/Hauliers/GetHauliersForDDL", Request.Method.GET, data, this);
        NetworkConsume.getInstance().ShowProgress(getContext());

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
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
       // Log.d("--dataarray", dataArray.toString());
        arrayList.clear();
        for (int i = 0; i < dataArray.length(); i++) {
            arrayList.add(dataArray.getJSONObject(i).getString("Name"));
        }
        haulierAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        haulierAdapter.notifyDataSetChanged();
        lstHaulierNames.setAdapter(haulierAdapter);
        lstHaulierNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Name = (String) adapterView.getItemAtPosition(i);


                try {

                    for (int j = 0; j < dataArray.length(); j++) {
                        String N = dataArray.getJSONObject(j).getString("Name");
                        if (Name == N) {

                            int haulierID = dataArray.getJSONObject(j).getInt("ID");
                            Bundle bundle = getArguments();
                            bundle.putString("haulierID", String.valueOf(haulierID));

                            if (bundle != null) {
                                String flag = bundle.getString("flag");
                                if (flag == "replay") {
                                    bundle.putString("flag",flag);
                                    ChangeFragment(new HaulierWiseVehicleListFragment(), bundle);
                                    break;
                                }
                                if(flag  == "report"){
                                  bundle.putString("flag",flag);
                                  ChangeFragment(new HaulierWiseVehicleListFragment(),bundle);
                                  break;
                                }


                            } else {
                                ChangeFragment(new LiveTrackingFragment(), bundle);
                                break;
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
