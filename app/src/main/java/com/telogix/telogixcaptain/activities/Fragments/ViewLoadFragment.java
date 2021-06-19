package com.telogix.telogixcaptain.activities.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.ViewLoad.Data;
import com.telogix.telogixcaptain.Pojo.ViewLoad.LoadCommodity;
import com.telogix.telogixcaptain.Pojo.ViewLoad.LoadDecantingSite;
import com.telogix.telogixcaptain.activities.EditLoadActivity;
import com.telogix.telogixcaptain.adapters.ViewLoadAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import spencerstudios.com.ezdialoglib.EZDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewLoadFragment extends Fragment implements response_interface {


    private TextView txtPickupLocation,txtCompany,txtVehicle,txtLoadTime;
    private ListView listdecantings;
    private String VehicleID,VehicleNO;
    private String LoadID;
    private ImageButton btnEditLoad,btndeleteload;

    public ViewLoadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_view_load, container, false);
         txtPickupLocation=v.findViewById(R.id.txtPickupLocation);
          btnEditLoad=v.findViewById(R.id.btneditload);
          btndeleteload=v.findViewById(R.id.btndeleteload);
         txtCompany=v.findViewById(R.id.txtCompany);
         txtVehicle=v.findViewById(R.id.txtVehicle);
         txtLoadTime=v.findViewById(R.id.txtLoadTime);
         listdecantings=v.findViewById(R.id.listviewDecantingsites);
        getActivity().setTitle("View Load");


        btndeleteload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EZDialog.Builder(getContext())

                        .setTitle("Please Confirm!")
                        .setMessage("Are you sure you want to delete this load? \n"

                        )
                        .setPositiveBtnText("Yes")
                        .setNegativeBtnText("No")
                        .setCancelableOnTouchOutside(false)
                        .OnPositiveClicked(() -> deleteLoad(VehicleID,LoadID))
                        .OnNegativeClicked(() ->{
                        })
                        .build();
            }
        });
        btnEditLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), EditLoadActivity.class);
                i.putExtra("LoadID",getArguments().getString("LoadID","-1"));
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!getArguments().getString("LoadID","-1").equals("-1"))
        {
            if(getArguments().getBoolean("HaulierWiseVehicleListFragment",false) == true){
                btndeleteload.setVisibility(View.GONE);
                btnEditLoad.setVisibility(View.GONE);
            }
            LoadID=getArguments().getString("LoadID","-1");
            VehicleID=getArguments().getString("VehicleID","-1");

            getLoad(LoadID);
        }
    }

    private void deleteLoad(String VehicleID,String LoadID) {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap data = new HashMap();
        httpvolley.stringrequestpost("api/Vehicles/DeleteLoad?vehicleID="+VehicleID+"&loadID="+LoadID, Request.Method.POST, data, this);

    }

    private void getLoad(String LoadID) {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap data = new HashMap();
        httpvolley.stringrequestpost("api/LoadAssigns/GetLoadAssign?id="+LoadID, Request.Method.GET, data, this);

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject1=new JSONObject(response);
        try {


            if (jsonObject1.has("ResponseCode")) {

                if (jsonObject1.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.VIEWLOAD))) {
                    if(jsonObject1.has("Data"))
                    {
                        if(!jsonObject1.getJSONObject("Data").equals(null)){
                        Data ViewLoadData=new Data();
                            JSONObject data = jsonObject1.getJSONObject("Data");
                           ViewLoadData.setLoadID( data.getInt("LoadID"));
                            ViewLoadData.setPickupLocation(data.getString("PickupLocation"));
                            ViewLoadData.setPickupLocationID(data.getInt("PickupLocationID"));
                            ViewLoadData.setCompany(data.getString("Company"));
                            ViewLoadData.setCompanyID(data.getInt("CompanyID"));
                            ViewLoadData.setVehicle( data.getString("Vehicle"));
                            ViewLoadData.setVehicleID(data.getInt("VehicleID"));
                            ViewLoadData.setLoadTime(data.getString("LoadTime"));
                            JSONArray jsonLoadDecantingSites=data.getJSONArray("LoadDecantingSites");
                            ArrayList<LoadDecantingSite> loadDecantingSitesList=new ArrayList<>();
                            for(int i=0;i<jsonLoadDecantingSites.length();i++) {
                                LoadDecantingSite loadDecantingSite = new LoadDecantingSite();
                                loadDecantingSite.setDecantingSite(jsonLoadDecantingSites.getJSONObject(i).getString("DecantingSite"));
                                loadDecantingSite.setDecantingSiteID(jsonLoadDecantingSites.getJSONObject(i).getInt("DecantingSiteID"));
                                loadDecantingSite.setDecantingTime(jsonLoadDecantingSites.getJSONObject(i).getString("DecantingTime"));
                                loadDecantingSite.setLoadDecantingSiteID(jsonLoadDecantingSites.getJSONObject(i).getInt("LoadDecantingSiteID"));
                                loadDecantingSite.setLoadID(jsonLoadDecantingSites.getJSONObject(i).getInt("LoadID"));

                                JSONArray jsonLoadCommodities = jsonLoadDecantingSites.getJSONObject(i).getJSONArray("LoadCommodities");
                                ArrayList<LoadCommodity> loadCommoditiesList=new ArrayList<>();
                                for (int j = 0; j < jsonLoadCommodities.length(); j++)
                                {

                                    LoadCommodity loadCommodity=new LoadCommodity();
                                    loadCommodity.setCommodity(jsonLoadCommodities.getJSONObject(j).getString("Commodity"));
                                    loadCommodity.setCommodityID(jsonLoadCommodities.getJSONObject(j).getInt("CommodityID"));
                                    loadCommodity.setLoadCommodityID(jsonLoadCommodities.getJSONObject(j).getInt("LoadCommodityID"));
                                    loadCommodity.setLoadDecantingSiteID(jsonLoadCommodities.getJSONObject(j).getInt("LoadDecantingSiteID"));
                                    if(!jsonLoadCommodities.getJSONObject(j).get("CompartmentNo").equals(null))
                                    {
                                        loadCommodity.setCompartmentNo(jsonLoadCommodities.getJSONObject(j).getString("CompartmentNo"));

                                    }
                                    if(!jsonLoadCommodities.getJSONObject(j).get("CommodityLoad").equals(null))
                                    {
                                        loadCommodity.setCommodityLoad(jsonLoadCommodities.getJSONObject(j).getString("CommodityLoad"));

                                    }
                                    loadCommoditiesList.add(loadCommodity);
 //   loadDecantingSite.setLoadCommodities(loadCommodity);


                                }
                                loadDecantingSite.setLoadCommodities(loadCommoditiesList);
                                loadDecantingSitesList.add(loadDecantingSite);

                            }
                          ViewLoadData.setLoadDecantingSites(loadDecantingSitesList);

                            txtPickupLocation.setText("Pickup Location : "+ViewLoadData.getPickupLocation());
                            txtCompany.setText("Company : "+ViewLoadData.getCompany());
                            txtLoadTime.setText("LoadTime: "+ViewLoadData.getLoadTime().split("T")[0]+" "+ViewLoadData.getLoadTime().split("T")[1]);
                            txtVehicle.setText("Vehicle Name : "+ViewLoadData.getVehicle());

                            listdecantings.setAdapter(new ViewLoadAdapter(getContext(),loadDecantingSitesList));
                        }
                    }


                }
                else if (jsonObject1.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.DELETELOAD))) {
                    Toast.makeText(getContext(),"Load removed",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                }
        }
        catch (Exception ex)
        {
            NetworkConsume.getInstance().hideProgress();
            Log.d("error",""+ex.getLocalizedMessage());
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
