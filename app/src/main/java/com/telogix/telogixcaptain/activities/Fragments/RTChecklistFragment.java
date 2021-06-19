package com.telogix.telogixcaptain.activities.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.RTINSPECTION.CheckListDetails;
import com.telogix.telogixcaptain.Pojo.RTINSPECTION.Data;
import com.telogix.telogixcaptain.adapters.RTChecklistAdapter;
import com.telogix.telogixcaptain.driver.activities.DriverMainActivity;
import com.telogix.telogixcaptain.driver.activities.RideUnavailable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class RTChecklistFragment extends Fragment implements response_interface {
ListView rt_insplistview;
    private String routeAssignID;
    private ArrayList<Data> rtInspectionlist;
    private RTChecklistAdapter rtChecklistAdapter;
Button btn_acceptrtinsp,btn_rejectrtinsp;
    private Data rtChecklistdata=null;
    private String mTextDetails;

    public RTChecklistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_rtchecklist, container, false);
        getActivity().setTitle("CheckList Approval");
        rt_insplistview=v.findViewById(R.id.rt_insplistview);
        btn_acceptrtinsp=v.findViewById(R.id.btn_acceptrtinsp);
        btn_rejectrtinsp=v.findViewById(R.id.btn_rejectrtinsp);
       // getRTInspectionList();
        try{
        if(DriverMainActivity.responseObj!=null) {
            routeAssignID = ""+ DriverMainActivity.responseObj.data.getRouteAssignID();
            getRTInspectionList();
        }
        else {
            Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }}
        catch (Exception ex){
            Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        btn_acceptrtinsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!rtChecklistdata.equals(null))
                {
                AcceptChecklist();
                }
            }
        });
        btn_rejectrtinsp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Rejection Reason");

// Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                input.setPadding(10,10,10,10);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTextDetails = input.getText().toString();

                        if(!rtChecklistdata.equals(null))
                        {
                            RejectChecklist();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        return v;
    }

    private void AcceptChecklist() {
        try {


        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        hashMap.put("CheckID",""+rtChecklistdata.getCheckID());
        hashMap.put("Status",""+1);
        hashMap.put("Detail","Checklist Accepted");
        httpvolley.stringrequestpost("api/CheckLists/UpdateApproval", Request.Method.POST, hashMap, this);
        }
        catch (Exception ex)
        {ex.printStackTrace();}
    }

    private void RejectChecklist() {
        try {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        hashMap.put("CheckID",""+rtChecklistdata.getCheckID());
        hashMap.put("Status",""+0);
        hashMap.put("Detail",""+mTextDetails);
        httpvolley.stringrequestpost("api/CheckLists/UpdateApproval", Request.Method.POST, hashMap, this);
    }
        catch (Exception ex)
    {ex.printStackTrace();}
    }

    private void getRTInspectionList() {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
      //  routeAssignID=""+70; // for testing
        httpvolley.stringrequestpost("api/CheckLists/GetCheckListByRouteAssignID?RouteAssignID="+routeAssignID, Request.Method.GET, hashMap, this);

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {


            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                String responsecode = jsonObject.get("ResponseCode").toString();
                if (responsecode.equals("1006")) {
                    if (jsonObject.has("Data")) {
                        JSONObject dataObject = jsonObject.getJSONObject("Data");
                        if (dataObject != null) {
                            rtInspectionlist = new ArrayList<>();
                             rtChecklistdata = new Data();
                            rtChecklistdata.setCheckID(dataObject.getInt("CheckID"));
                            rtChecklistdata.setVehicleID(dataObject.getInt("VehicleID"));
                            rtChecklistdata.setVehicleNo("" + dataObject.get("VehicleNo"));
                            rtChecklistdata.setInspectionDate("" + dataObject.get("InspectionDate"));
                            rtChecklistdata.setIsApproved(dataObject.getBoolean("IsApproved"));
                                JSONArray checklistjsonArray=dataObject.getJSONArray("CheckListDetails");

                                ArrayList<CheckListDetails> checkListDetailsArrayList=new ArrayList<>();
                                for (int j=0;j<checklistjsonArray.length();j++) {
                                    CheckListDetails checkListDetails = new CheckListDetails();
                                    checkListDetails.setInspectionID(checklistjsonArray.getJSONObject(j).getInt("InspectionID"));
                                    checkListDetails.setInspectionTitle(checklistjsonArray.getJSONObject(j).getString("InspectionTitle"));
                                    checkListDetails.setInspectionTitleUr(checklistjsonArray.getJSONObject(j).getString("InspectionTitleUr"));
                                    checkListDetails.setCondition(checklistjsonArray.getJSONObject(j).getString("Condition"));
                                    checkListDetails.setIsChecked(checklistjsonArray.getJSONObject(j).getBoolean("IsChecked"));
                                    if (checklistjsonArray.getJSONObject(j).get("Detail")!=null)
                                    {
                                        checkListDetails.setDetail(""+checklistjsonArray.getJSONObject(j).get("Detail"));
                                    }
                                    else
                                    {
                                        checkListDetails.setDetail("");
                                    }
                                    if (checklistjsonArray.getJSONObject(j).get("VoiceNoteUrl")!=null)
                                    {
                                        checkListDetails.setVoiceNoteUrl(""+checklistjsonArray.getJSONObject(j).get("VoiceNoteUrl"));
                                    }
                                    else
                                    {
                                        checkListDetails.setVoiceNoteUrl("");
                                    }
                                    if (checklistjsonArray.getJSONObject(j).get("ImageUrl")!=null)
                                    {
                                        checkListDetails.setImageUrl(""+checklistjsonArray.getJSONObject(j).get("ImageUrl"));
                                    }
                                    else
                                    {
                                        checkListDetails.setImageUrl("");
                                    }
                                    checkListDetailsArrayList.add(checkListDetails);

                                }

                            rtChecklistdata.setCheckListDetails(checkListDetailsArrayList);

                            rtChecklistAdapter = new RTChecklistAdapter(rtChecklistdata, getContext());
                            rt_insplistview.setAdapter(rtChecklistAdapter);

                        }
                    }
                }
                else if(responsecode.equals("1007")){
                    if(jsonObject.has("Status"))
                    {
                        if(!jsonObject.getBoolean("Status"))
                        {
                            Intent i=new Intent(getContext(), RideUnavailable.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                        else
                        {
                            getActivity().onBackPressed();
                        }
                    }
                    Toast.makeText(getContext(),"Checklist Status Updated",Toast.LENGTH_SHORT).show();
                 //   getActivity().onBackPressed();
                }
            }
        }
        catch (Exception ex)
        {ex.printStackTrace();}
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
