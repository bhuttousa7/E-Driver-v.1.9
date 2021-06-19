package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.ReportPojo;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.adapters.ReportTypeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportFragment extends Fragment implements response_interface {

    RecyclerView reportTypeListView;
    List<ReportPojo> reportTypeList = new ArrayList<ReportPojo>();
    SearchView searchView;
    ReportTypeListAdapter reportTypeListAdapter;
    Bundle b;
    TokenPojo datum = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Report Type List");
        View v = inflater.inflate(R.layout.fragment_report,container,false);
        bindViews(v);
        b = getArguments();
        return v;
    }

    public void bindViews(View v){
        reportTypeListView = v.findViewById(R.id.reportTypeListView);
        searchView = v.findViewById(R.id.searchView);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(getActivity());
        reportTypeListView.setLayoutManager(layoutManager);
        getReportTypeList();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<ReportPojo> filteredList = new ArrayList<ReportPojo>();

                for(ReportPojo row: reportTypeList){

                    if(row.getName().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(row);
                    }

                }
                reportTypeListAdapter = new ReportTypeListAdapter(getContext(),filteredList,b);
                reportTypeListAdapter.notifyDataSetChanged();
                reportTypeListView.setAdapter(reportTypeListAdapter);
                return true;
            }
        });

    }

    private void getReportTypeList(){
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/Report/GetReportTypeList", Request.Method.GET,hashMap,this);
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject  = new JSONObject(response);

        if(jsonObject.has("Data")){
            JSONArray data = jsonObject.getJSONArray("Data");
            reportTypeList.clear();
            for(int i=0;i<data.length(); i++){
                ReportPojo rp = new ReportPojo();
                if(data.getJSONObject(i).getInt("ID") != 0){
                    rp.setID(data.getJSONObject(i).getInt("ID"));
                }
                if(data.getJSONObject(i).getString("Name") != null){

                    rp.setName(data.getJSONObject(i).getString("Name"));
                }

                reportTypeList.add(rp);
            }
            String userData= singleton.getsharedpreference(getContext()).getString("userData", "");
            if(userData!="") {
                Gson gson = new Gson();
                datum = gson.fromJson(userData, TokenPojo.class);
                b.putParcelable("datum",datum);
            }

            reportTypeListAdapter = new ReportTypeListAdapter(getContext(),reportTypeList,b);
            reportTypeListAdapter.notifyDataSetChanged();
            reportTypeListView.setAdapter(reportTypeListAdapter);
        }

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
