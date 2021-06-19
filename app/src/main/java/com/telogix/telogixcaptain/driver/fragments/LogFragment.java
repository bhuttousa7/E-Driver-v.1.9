package com.telogix.telogixcaptain.driver.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.DriverLog.DriverLogPojo;
import com.telogix.telogixcaptain.driver.activities.DriverMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment implements response_interface {


    private RecyclerView recyclerView;

    public LogFragment() {
        // Required empty public constructor
    }

    ArrayList<HashMap<String,String>> historylist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_log, container, false);
        getActivity().setTitle("LOG BOOK");
        recyclerView = v.findViewById(R.id.recyclerviewLog);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        getActivity().setTitle("Driver Log");
        historylist=new ArrayList<>();
        getDriverLog();
//        String json = singleton.getsharedpreference(getContext()).getString("logbook", "");
//
//       Gson gson = new Gson();
//        historylist = gson.fromJson(json, new TypeToken<List<HashMap<String,String>>>() {
//        }.getType());


        return v;
    }

    private void getDriverLog() {
        try {
            HashMap hashMap = new HashMap();

            httpvolley.stringrequestpost("api/DriverLogs/GetDriverLog?RouteAssignID=" + DriverMainActivity.responseObj.data.getRouteAssignID(), Request.Method.GET, hashMap, this);
        }
        catch (Exception ex){

        }
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.has("ResponseCode")) {
                if (jsonObject1.get("ResponseCode").equals(2103)) {

                    JSONArray data=jsonObject1.getJSONArray("Data");
                    ArrayList<DriverLogPojo> datumStopsArrayList=new ArrayList<>();
                    for(int i=0;i<data.length();i++)
                    {
                        DriverLogPojo driverLogPojo =new DriverLogPojo();

                        driverLogPojo.setUserName(data.getJSONObject(i).getString("UserName"));
                        driverLogPojo.setStopTypeTitle(data.getJSONObject(i).getString("StopTypeTitle"));
                        if(data.getJSONObject(i).get("StartDate")!=null) {
                            driverLogPojo.setStartDate(data.getJSONObject(i).getString("StartDate"));
                        }
                        else
                        {
                            driverLogPojo.setStartDate("");
                        }
                        if(data.getJSONObject(i).get("ResumeDate")!=null) {
                            driverLogPojo.setResumeDate(data.getJSONObject(i).getString("ResumeDate"));
                        }
                        else
                        {
                            driverLogPojo.setResumeDate("");
                        }
                        driverLogPojo.setDescription(data.getJSONObject(i).getString("Description"));

                        datumStopsArrayList.add(driverLogPojo);
                    }
                    if(datumStopsArrayList!=null) {
                        if (datumStopsArrayList.size()>0)
                        {
                        recyclerView.setAdapter(new logAdapter(datumStopsArrayList));
                    }
                        else {
                            Toast.makeText(getContext(),"LOG NOT FOUND",Toast.LENGTH_SHORT).show();
                        }
                    }
                        else
                    {
                        Toast.makeText(getContext(),"LOG NOT FOUND",Toast.LENGTH_SHORT).show();
//            getActivity().onBackPressed();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError Error) {
        Log.d("error",""+Error);
    }
}
