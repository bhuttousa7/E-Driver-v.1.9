package com.telogix.telogixcaptain.driver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.InspectionRemarks;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.PreLoadInspPojo.PreLoadInsPojo;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.OfflineData;
import com.telogix.telogixcaptain.adapters.PreloadInspAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DecantingListActivity extends AppCompatActivity implements response_interface, InspectionRemarks {

    private ArrayList<PreLoadInsPojo> inspectionlist;
    private ListView listView;
    private PreloadInspAdapter preload;
    private String vehicleid = "";
    private final String routeid = "";
    private String DecantingSiteID;
    private String RouteAssignID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load_inspect);


        if ( getIntent().hasExtra("VehicleID")) {

            vehicleid = getIntent().getStringExtra("VehicleID");

        }
        if(getIntent().hasExtra("DecantingSiteID"))
        {
            DecantingSiteID=getIntent().getStringExtra("DecantingSiteID");
        }
        if(getIntent().hasExtra("DecantingSiteID"))
        {
            RouteAssignID=getIntent().getStringExtra("RouteAssignID");
        }
        listView = findViewById(R.id.recyclerview_insp);
      //  getDailyInspectionList();
        if(Constants.internetConnected) {
            getDecantingInspectionList();
        }
        else
        {
            preload = new PreloadInspAdapter(OfflineData.inspectionlist, this);
            listView.setAdapter(preload);
        }
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if( inspectionlist.get(position).isIsselected())
//                {
//                    inspectionlist.get(position).setIsselected(false);
//                }
//                else
//                {
//                    inspectionlist.get(position).setIsselected(true);
//                }
//                preload.notifyDataSetChanged();
//            }
//        });

        Button btn_submitinsp = findViewById(R.id.btn_submitinsp);
        btn_submitinsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Constants.internetConnected)
                {

                }
                else
                {
                    inspectionlist= OfflineData.inspectionlist;
                }

                boolean mandatorycheck = false;
                for (int i = 0; i < inspectionlist.size(); i++) {

                    if (inspectionlist.get(i).isIgnored() && inspectionlist.get(i).getIsRequired())   // condition for mandatory fields
                    {
                        mandatorycheck = true;
                        break;
                    }
                }
                if (mandatorycheck) {
                    Toast.makeText(DecantingListActivity.this, "Fill the mandatory fields", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<PreLoadInsPojo> arrayChecked=new ArrayList<>();
                    HashMap data = new HashMap();
                    for (int i = 0; i < inspectionlist.size(); i++) {

                        if (inspectionlist.get(i).isNoSelected() && !inspectionlist.get(i).isYesSelected()) {
                            // data.put("CheckListDetails[" + i + "].IsChecked", "0");
                            arrayChecked.add(inspectionlist.get(i));

                        } else if (inspectionlist.get(i).isYesSelected() && !inspectionlist.get(i).isNoSelected()) {
                            // data.put("CheckListDetails[" + i + "].IsChecked", "1");
                            arrayChecked.add(inspectionlist.get(i));
                        }
                    }
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    data.put("VehicleID", "" + vehicleid);
                    data.put("RouteAssignID", "" + RouteAssignID);
                    data.put("InspectionDate", "" +date);
                    data.put("InspectionTypeID", "" + 3);
                    data.put("LoadDecantingSiteID", "" + DecantingSiteID);
                    for (int i=0;i<arrayChecked.size();i++)
                    {

                        data.put("CheckListDetails[" + i + "].Condition","Good");
                        data.put("CheckListDetails[" + i + "].InspectionID", "" + arrayChecked.get(i).getInspectionListID());
                        if (arrayChecked.get(i).isNoSelected() && !arrayChecked.get(i).isYesSelected()) {
                            data.put("CheckListDetails[" + i + "].IsChecked", "0");

                        } else if (arrayChecked.get(i).isYesSelected() && !arrayChecked.get(i).isNoSelected()) {
                            data.put("CheckListDetails[" + i + "].IsChecked", "1");

                        }

                        if (!arrayChecked.get(i).isIgnored()) {
                            for (int k = 0; k < arrayChecked.get(i).getInspectionDetails().size(); k++) {
                                HashMap hashMap = arrayChecked.get(i).getInspectionDetails();
                                if (hashMap != null) {
                                    if (hashMap.containsKey("description")) {
                                        data.put("CheckListDetails[" + i + "].Condition", hashMap.get("description"));
                                    }
                                    else{
                                        data.put("CheckListDetails[" + i + "].Condition","Good");
                                    }
                                    if (hashMap.containsKey("voiceNoteurl")) {
                                        data.put("CheckListDetails[" + i + "].VoiceNoteUrl", hashMap.get("voiceNoteurl"));
                                    }
                                    if (hashMap.containsKey("imageurl")) {
                                        data.put("CheckListDetails[" + i + "].ImageUrl", hashMap.get("imageurl"));
                                    }
                                }
                            }

                        }

                    }
                  //  NetworkConsume.getInstance().ShowProgress(DecantingListActivity.this);
                    if(Constants.internetConnected) {
                        httpvolley.stringrequestpost("api/CheckLists/SaveCheckList", Request.Method.POST, data, DecantingListActivity.this);
                    }
                    else
                    {

                        HashMap<String,HashMap> hashMap=new HashMap<>();
                        hashMap.put("api/CheckLists/SaveCheckList",data);
                        OfflineData.offlineData.add(hashMap);
                        Intent i =new Intent();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        });


    }

    private void getDecantingInspectionList() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetDecantingInspectionList", Request.Method.GET, hashMap, this);

    }

    private void getDailyInspectionList() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetDailyInspectionList", Request.Method.GET, hashMap, this);

    }

    private void getInspectionList() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetPreLoadInspectionList", Request.Method.GET, hashMap, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int index = PreloadInspAdapter.pos;
        View v = listView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

// ...

// restore index and position
        listView.setSelectionFromTop(index, top);
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("ResponseCode")) {
            String responsecode = jsonObject.get("ResponseCode").toString();
            if (responsecode.equals("7006")) {
                if (jsonObject.has("Data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("Data");
                    if (dataArray != null) {
                        inspectionlist = new ArrayList<>();
                        PreLoadInsPojo preLoadInsPojo = new PreLoadInsPojo();
                        for (int i = 0; i < dataArray.length(); i++) {
                            preLoadInsPojo.setInspectionListID(dataArray.getJSONObject(i).getInt("InspectionListID"));
                            preLoadInsPojo.setInspectionTypeID(dataArray.getJSONObject(i).getInt("InspectionTypeID"));
                            preLoadInsPojo.setInspectionType("" + dataArray.getJSONObject(i).get("InspectionType"));
                            preLoadInsPojo.setInspectionTitle("" + dataArray.getJSONObject(i).get("InspectionTitle"));
                            preLoadInsPojo.setInspectionTitleUr("" + dataArray.getJSONObject(i).get("InspectionTitleUr"));
                            preLoadInsPojo.setIsRequired(dataArray.getJSONObject(i).getBoolean("IsRequired"));
                            preLoadInsPojo.setOrderNumber(dataArray.getJSONObject(i).getInt("OrderNumber"));
                            preLoadInsPojo.setPriorityNumber(dataArray.getJSONObject(i).getInt("PriorityNumber"));

                            inspectionlist.add(preLoadInsPojo);
                            preLoadInsPojo = new PreLoadInsPojo();
                        }
                        preload = new PreloadInspAdapter(inspectionlist, this);
                        listView.setAdapter(preload);

                    }
                }
            }else if (responsecode.equals("7005")) {
                if (jsonObject.has("Data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("Data");
                    if (dataArray != null) {
                        inspectionlist = new ArrayList<>();
                        PreLoadInsPojo preLoadInsPojo = new PreLoadInsPojo();
                        for (int i = 0; i < dataArray.length(); i++) {
                            preLoadInsPojo.setInspectionListID(dataArray.getJSONObject(i).getInt("InspectionListID"));
                            preLoadInsPojo.setInspectionTypeID(dataArray.getJSONObject(i).getInt("InspectionTypeID"));
                            preLoadInsPojo.setInspectionType("" + dataArray.getJSONObject(i).get("InspectionType"));
                            preLoadInsPojo.setInspectionTitle("" + dataArray.getJSONObject(i).get("InspectionTitle"));
                            preLoadInsPojo.setInspectionTitleUr("" + dataArray.getJSONObject(i).get("InspectionTitleUr"));
                            preLoadInsPojo.setIsRequired(dataArray.getJSONObject(i).getBoolean("IsRequired"));
                            preLoadInsPojo.setOrderNumber(dataArray.getJSONObject(i).getInt("OrderNumber"));
                            if(dataArray.getJSONObject(i).has("PriorityNumber")) {
                                if(!dataArray.getJSONObject(i).get("PriorityNumber").equals(null)) {
                                    preLoadInsPojo.setPriorityNumber(dataArray.getJSONObject(i).getInt("PriorityNumber"));
                                }}

                            inspectionlist.add(preLoadInsPojo);
                            preLoadInsPojo = new PreLoadInsPojo();
                        }
                        preload = new PreloadInspAdapter(inspectionlist, this);
                        listView.setAdapter(preload);

                    }
                }
            }  else if (responsecode.equals("1001")) {

                Toast.makeText(DecantingListActivity.this, "Inspection Completed", Toast.LENGTH_SHORT).show();
                Intent i =new Intent();
                setResult(RESULT_OK);
                finish();            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Toast.makeText(this, "I am called" + resultCode, Toast.LENGTH_SHORT).show();
        if (resultCode == 3) {
            HashMap hashMap = new HashMap();
            if (data != null) {
                if (data.hasExtra("description")) {
                    hashMap.put("description", "" + data.getStringExtra("description"));
                }
                if (data.hasExtra("link")) {


                    hashMap.put("imageurl", data.getStringExtra("link"));


                }
                if(data.hasExtra("voicelink")){
                    hashMap.put("voiceNoteurl", data.getStringExtra("voicelink"));

                }
                if(!Constants.internetConnected)
                {
                    OfflineData.inspectionlist.get(PreloadInspAdapter.pos).setInspectionDetails(hashMap);

                }
                else
                {
                inspectionlist.get(PreloadInspAdapter.pos).setInspectionDetails(hashMap);
            }
            }

        }
        else if(resultCode==0 || resultCode==-1)
        {
            if(!Constants.internetConnected) {

                OfflineData.inspectionlist.get(PreloadInspAdapter.pos).setIgnored(true);
                OfflineData.inspectionlist.get(PreloadInspAdapter.pos).setNoSelected(false);
                preload = new PreloadInspAdapter(OfflineData.inspectionlist, this);
                listView.setAdapter(preload);
            }
            else {
                inspectionlist.get(PreloadInspAdapter.pos).setIgnored(true);
                inspectionlist.get(PreloadInspAdapter.pos).setNoSelected(false);
                preload = new PreloadInspAdapter(inspectionlist, this);
                listView.setAdapter(preload);
            }

        }

    }

    @Override
    public void onError(VolleyError Error) {

        NetworkConsume.getInstance().hideProgress();
    }


    @Override
    public void onYesPressed(int position, boolean isChecked, boolean ignored, boolean checkboxNo) {
        if(!Constants.internetConnected) {


            OfflineData.inspectionlist.get(position).setYesSelected(isChecked);
            OfflineData.inspectionlist.get(position).setIgnored(ignored);
            OfflineData.inspectionlist.get(position).setNoSelected(checkboxNo);

        }
        else {
            inspectionlist.get(position).setYesSelected(isChecked);
            inspectionlist.get(position).setIgnored(ignored);
            inspectionlist.get(position).setNoSelected(checkboxNo);
        }

    }

    @Override
    public void onNoPressed(int position, boolean isChecked, boolean ignored, boolean checkboxYes) {
        if(!Constants.internetConnected) {


            OfflineData.inspectionlist.get(position).setNoSelected(isChecked);
            OfflineData.inspectionlist.get(position).setIgnored(ignored);
            OfflineData.inspectionlist.get(position).setYesSelected(checkboxYes);

        }
        else {
            inspectionlist.get(position).setNoSelected(isChecked);
            inspectionlist.get(position).setIgnored(ignored);
            inspectionlist.get(position).setYesSelected(checkboxYes);
        }

    }
}
