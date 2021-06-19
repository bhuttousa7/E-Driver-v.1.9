package com.telogix.telogixcaptain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.InspectionRemarks;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.PreLoadInspPojo.PreLoadInsPojo;
import com.telogix.telogixcaptain.adapters.PreloadInspAdapter;
import com.telogix.telogixcaptain.driver.activities.ResumeRideActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PreLoadInspectActivity extends BaseActivity implements response_interface, InspectionRemarks {

    private ArrayList<PreLoadInsPojo> inspectionlist;
    private ListView listView;
    private PreloadInspAdapter preload;
    private String vehicleid = "", routeid = "";
    private boolean dailyinspection,preloadinspection,decanting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load_inspect);


        if (getIntent().hasExtra("routeid") && getIntent().hasExtra("vehicleid")) {
            routeid = getIntent().getStringExtra("routeid");
            vehicleid = getIntent().getStringExtra("vehicleid");
            if (!routeid.equals("") && !vehicleid.equals("")) {

            }
        }
        listView = findViewById(R.id.recyclerview_insp);
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
        if(getIntent().getBooleanExtra("preloadinspection",false))
        {
            getInspectionList();
            decanting=false;
            dailyinspection=false;
            preloadinspection=true;
        }
        else if (getIntent().getBooleanExtra("dailyinspection",false))
        {
            getDailyInspectionList();
            decanting=false;
            dailyinspection=true;
            preloadinspection=false;
        }else if (getIntent().getBooleanExtra("decantinglist",false)){
            getDecantingInspectionList();
            decanting=true;
            dailyinspection=true;
            preloadinspection=false;
        }
        else
        {
            decanting=false;
            dailyinspection=false;
            preloadinspection=true;
            getInspectionList();
        }


        btn_submitinsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preloadinspection) {
                    boolean mandatorycheck = false;
                    for (int i = 0; i < inspectionlist.size(); i++) {

                        if (inspectionlist.get(i).isIgnored() && inspectionlist.get(i).getIsRequired())   // condition for mandatory fields
                        {
                            mandatorycheck = true;
                            break;
                        }


                    }
                    if (mandatorycheck) {
                        Toast.makeText(PreLoadInspectActivity.this, "Fill the mandatory fields", Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<PreLoadInsPojo> arrayChecked = new ArrayList<>();
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

                        for (int i = 0; i < arrayChecked.size(); i++) {
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            data.put("VehicleID", "" + vehicleid);
                            data.put("RouteAssignID", "" + routeid);
                            data.put("InspectionDate", "" + date);
                            data.put("InspectionTypeID", "" + 1);
                            data.put("CheckListDetails[" + i + "].Condition", "Good");
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
                                        } else {
                                            data.put("CheckListDetails[" + i + "].Condition", "Good");
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
                        NetworkConsume.getInstance().ShowProgress(PreLoadInspectActivity.this);
                        httpvolley.stringrequestpost("api/CheckLists/SaveCheckList", Request.Method.POST, data, PreLoadInspectActivity.this);

                    }
                }
                else if(decanting)
                {
                    onBackPressed();
                    Intent i =new Intent(PreLoadInspectActivity.this, ResumeRideActivity.class);
                    startActivity(i);
                }
                else if(dailyinspection)
                {


                    // Toast.makeText(PreLoadInspectActivity.this,"Ride Stopped",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    boolean mandatorycheck = false;
                    for (int i = 0; i < inspectionlist.size(); i++) {

                        if (inspectionlist.get(i).isIgnored() && inspectionlist.get(i).getIsRequired())   // condition for mandatory fields
                        {
                            mandatorycheck = true;
                            break;
                        }


                    }
                    if (mandatorycheck) {
                        Toast.makeText(PreLoadInspectActivity.this, "Fill the mandatory fields", Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<PreLoadInsPojo> arrayChecked = new ArrayList<>();
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

                        for (int i = 0; i < arrayChecked.size(); i++) {
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            data.put("VehicleID", "" + vehicleid);
                            data.put("RouteAssignID", "" + routeid);
                            data.put("InspectionDate", "" + date);
                            data.put("CheckListDetails[" + i + "].Condition", "Good");
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
                                        } else {
                                            data.put("CheckListDetails[" + i + "].Condition", "Good");
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
                        NetworkConsume.getInstance().ShowProgress(PreLoadInspectActivity.this);
                        httpvolley.stringrequestpost("api/CheckLists/SaveCheckList", Request.Method.POST, data, PreLoadInspectActivity.this);

                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pre_load_inspect;
    }

    private void getDailyInspectionList() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetDailyInspectionList", Request.Method.GET, hashMap, this);

    }

    private void getDecantingInspectionList() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetDecantingInspectionList", Request.Method.GET, hashMap, this);

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
        try {
                Log.d("--preldinsA",response);

            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                String responsecode = jsonObject.get("ResponseCode").toString();
                if (responsecode.equals("7002")) {
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
                                    if(dataArray.getJSONObject(i).get("PriorityNumber")!=null) {
                                        preLoadInsPojo.setPriorityNumber(dataArray.getJSONObject(i).getInt("PriorityNumber"));
                                    }}
                                inspectionlist.add(preLoadInsPojo);
                                preLoadInsPojo = new PreLoadInsPojo();
                            }
                            preload = new PreloadInspAdapter(inspectionlist, this);
                            listView.setAdapter(preload);

                        }
                    }
                } else if (responsecode.equals("7005")) {
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
                } else if (responsecode.equals("1001")) {
                    Toast.makeText(PreLoadInspectActivity.this, "Inspection Completed", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            }
        }catch(Exception ex)
        {
            Log.d("error",ex.getLocalizedMessage());
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
                inspectionlist.get(PreloadInspAdapter.pos).setInspectionDetails(hashMap);
            }

        }
        else if(resultCode==0 || resultCode==-1)
        {
            inspectionlist.get(PreloadInspAdapter.pos).setIgnored(true);
            inspectionlist.get(PreloadInspAdapter.pos).setNoSelected(false);
            preload = new PreloadInspAdapter(inspectionlist, this);
            listView.setAdapter(preload);
        }

    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }


    @Override
    public void onYesPressed(int position, boolean isChecked, boolean ignored, boolean checkboxNo) {

        inspectionlist.get(position).setYesSelected(isChecked);
        inspectionlist.get(position).setIgnored(ignored);
        inspectionlist.get(position).setNoSelected(checkboxNo);

    }

    @Override
    public void onNoPressed(int position, boolean isChecked, boolean ignored, boolean checkboxYes) {
        inspectionlist.get(position).setNoSelected(isChecked);
        inspectionlist.get(position).setIgnored(ignored);
        inspectionlist.get(position).setYesSelected(checkboxYes);


    }
}
