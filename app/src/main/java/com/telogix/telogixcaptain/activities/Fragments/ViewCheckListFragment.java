package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.ViewCheckList.CheckListPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.adapters.ViewCheckListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewCheckListFragment extends Fragment implements response_interface {
List<CheckListPojo> checkListPojoList,checkListPojoList2;
ListView listviewchecklists,listviewchecklists2;
TextView checkListType,checkListType2,jmName;
String RouteAssignID = null,date=null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_view_checklist,container,false);
       bindViews(v);
       return v;
    }

    private void bindViews(View v) {
        listviewchecklists = v.findViewById(R.id.listViewChecklists);
        listviewchecklists2 = v.findViewById(R.id.listViewChecklists2);
        checkListType = v.findViewById(R.id.checkListType);
        checkListType2 = v.findViewById(R.id.checkListType2);
        jmName = v.findViewById(R.id.jmName);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(  bundle.getString("RouteAssignID") != null ){
            RouteAssignID = bundle.getString("RouteAssignID");
            getAllCheckLists();
        }
    }

    private void getAllCheckLists() {

        HashMap hashMap = new HashMap();

        httpvolley.stringrequestpost("/api/CheckLists/GetAllCheckListsByRouteAssignID?RouteAssignID="+RouteAssignID, Request.Method.GET,hashMap,this);
    }

    @Override
    public void onResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray data = jsonObject.getJSONArray("Data");
        checkListPojoList = new ArrayList<>();
        checkListPojoList2 = new ArrayList<>();
        for(int i=0;i<data.length();i++){
            CheckListPojo checkListPojo = new CheckListPojo();
            if(data.getJSONObject(i).getString("DecantingSiteName") != null){
                checkListPojo.setDecantingSiteName(data.getJSONObject(i).getString("DecantingSiteName"));
            }

            checkListPojo.setInspectionDate(data.getJSONObject(i).getString("InspectionDate"));
            checkListPojo.setInspectionTitle(data.getJSONObject(i).getString("InspectionTitle"));
            checkListPojo.setInspectionTitleUr(data.getJSONObject(i).getString("InspectionTitleUr"));
            checkListPojo.setInspectionTypeID(data.getJSONObject(i).getInt("InspectionTypeID"));
            checkListPojo.setChecked(data.getJSONObject(i).getBoolean("IsChecked"));
            checkListPojo.setCondition(data.getJSONObject(i).getString("Condition"));
            if(data.getJSONObject(i).getInt("InspectionTypeID") == 1){
                checkListPojo.setJourneyManagerName(data.getJSONObject(i).getString("UserName"));
                checkListPojoList.add(checkListPojo);
            }
            else{
                checkListPojoList2.add(checkListPojo);
            }

        }
         if(checkListPojoList2.size()> 0){
             date = checkListPojoList2.get(0).getInspectionDate().split("T")[0];
         }
         else if (checkListPojoList.size() > 0){
             date = checkListPojoList.get(0).getInspectionDate().split("T")[0];
         }

            String formattedDate = parseDateToddMMyyyy(date);
         jmName.setText("Journey Manager: "+checkListPojoList.get(0).getJourneyManagerName());
         checkListType.setText("PreLoad Check List: "+formattedDate);
         checkListType2.setText("Decanting Inspection CheckList: "+formattedDate);
         if (checkListPojoList.size() > 0){
             listviewchecklists.setAdapter(new ViewCheckListAdapter(getContext(),checkListPojoList));
         }
        if(checkListPojoList2.size() > 0) {
            listviewchecklists2.setAdapter(new ViewCheckListAdapter(getContext(), checkListPojoList2));
        }


    }


    @Override
    public void onError(VolleyError Error) {

    }
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
