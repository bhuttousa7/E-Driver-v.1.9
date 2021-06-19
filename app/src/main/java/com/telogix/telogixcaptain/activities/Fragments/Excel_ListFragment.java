package com.telogix.telogixcaptain.activities.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.Data;
import com.telogix.telogixcaptain.Pojo.ExcelPojo;
import com.telogix.telogixcaptain.adapters.ExcelItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Excel_ListFragment extends Fragment implements response_interface {


    public Excel_ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_excel__list, container, false);
         RecyclerView recyclerView = v.findViewById(R.id.recyclerview_excel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Button btn_excelres=v.findViewById(R.id.btn_submitexcelres);
        btn_excelres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ExcelItemAdapter.Data!=null)
                {
                    if(ExcelItemAdapter.Data.size()>0) {
                        ArrayList<Data> Data = ExcelItemAdapter.Data;
                        ArrayList<com.telogix.telogixcaptain.Pojo.Data> selectedItems = new ArrayList<>();
                        for (int i = 0; i < Data.size(); i++) {
                            if (Data.get(i).isIsselected()) {
                                selectedItems.add(Data.get(i));

                            }
                        }
                        HashMap hashMap = new HashMap();

                        for (int k = 0; k < selectedItems.size(); k++) {
                            if (k == 0) {
                                hashMap.put("ExcelID",selectedItems.get(k).getExcelID());
                                hashMap.put("Details[" + k + "].ID",selectedItems.get(k).getID());
                            } else {
                                hashMap.put("Details[" + k + "].ID",selectedItems.get(k).getID());

                            }
                        }
                        NetworkConsume.getInstance().ShowProgress(getContext());
                         Log.i("--ExcelDetails",hashMap.toString());
                        httpvolley.stringrequestpost("api/LoadAssigns/AssignLoadByExcel", Request.Method.POST, hashMap, Excel_ListFragment.this);
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Select load first",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(getArguments().getString("jsonData","")!="")
        {
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(getArguments().getString("jsonData",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonObject.has("Data")) {
                try {
                    jsonObject = new JSONObject(getArguments().getString("jsonData",""));
                    JSONArray array = jsonObject.getJSONArray("Data");
                    ExcelPojo ep=new ExcelPojo();
                    Data data=new Data();
                    ArrayList<Data> dataArrayList=new ArrayList();
                    for(int i=0;i<array.length();i++)
                    {
                        data.setID(""+array.getJSONObject(i).get("ID"));
                        data.setExcelID(""+array.getJSONObject(i).get("ExcelID"));
                        data.setLoadTime(""+array.getJSONObject(i).get("LoadTime"));
                        data.setPickupLocationID(""+array.getJSONObject(i).get("PickupLocationID"));
                        data.setPickupLocation(""+array.getJSONObject(i).get("PickupLocation"));
                        data.setVehicleID(""+array.getJSONObject(i).get("VehicleID"));
                        data.setTrailerCode(""+array.getJSONObject(i).get("TrailerCode"));
                        data.setCommodityID(""+array.getJSONObject(i).get("CommodityID"));
                        data.setCommodity(""+array.getJSONObject(i).get("Commodity"));
                        data.setDecantingTime(""+array.getJSONObject(i).get("DecantingTime"));
                        data.setDecantingSiteID(""+array.getJSONObject(i).get("DecantingSiteID"));
                        data.setDecantingSite(""+array.getJSONObject(i).get("DecantingSite"));
                        data.setCompartmentNo(""+array.getJSONObject(i).get("CompartmentNo"));
                        data.setCommodityLoad(""+array.getJSONObject(i).get("CommodityLoad"));
                        data.setChecked(array.getJSONObject(i).getBoolean("IsChecked"));
                        dataArrayList.add(data);
                        data=new Data();
                    }
                    recyclerView.setAdapter(new ExcelItemAdapter(dataArrayList,getContext()));



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return v;
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("Status")) {
                if (jsonObject.get("Status").equals(true)) {
                    Toast.makeText(getContext(),"Operation Successful",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();

                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Log.d("responseExcel",response);

    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        if(Error.networkResponse.statusCode == 400){
            String responseBody = new String(Error.networkResponse.data, StandardCharsets.UTF_8);
            JSONObject data = null;
            try {
                data = new JSONObject(responseBody);
                if(responseBody.contains("Message")){
                    String response = data.optString("Message");
                    Snackbar snackbar = Snackbar.make(getView(),response,Snackbar.LENGTH_SHORT);
                    snackbar.show();
                  //  Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //String response = data.optString("ModelState");

        }



        Log.d("responseExcelerror",""+Error);
    }
}
