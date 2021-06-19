package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.ViewRating.RatingPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.adapters.ViewRatingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewRatingFragment extends Fragment implements response_interface {
    ListView listviewRating;
    List<RatingPojo> ratingPojoList =new ArrayList<>();
    Bundle b;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_rating,container,false);
        bindViews(v);
        b = getArguments();
        if(b.getString("VehicleNo") != null){
            getActivity().setTitle("View Ratings: "+b.getString("VehicleNo"));
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(b.getString("LoadID",null) != null){

            String LoadID = b.getString("LoadID");
            getRatings(LoadID);
        }

    }
    private void getRatings(String LoadID) {

        httpvolley.stringrequestpost("api/LoadDecantingSite/GetRatingsByLoadID?LoadID="+LoadID, Request.Method.GET,new HashMap<>(),this);
        NetworkConsume.getInstance().ShowProgress(getContext());
    }

    public void bindViews(View v){

        listviewRating = v.findViewById(R.id.listviewRating);

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
       JSONObject jsonObject = new JSONObject(response);
        JSONArray data  = jsonObject.getJSONArray("Data");

        for(int i=0;i<data.length();i++){

            RatingPojo ratingPojo = new RatingPojo();

            if(data.getJSONObject(i).getString("Driver") != null){

                ratingPojo.setDriver(data.getJSONObject(i).getString("Driver"));
            }
            if(data.getJSONObject(i).getString("VehicleNo") != null){

                ratingPojo.setVehicleNo(data.getJSONObject(i).getString("VehicleNo"));
            }
            if(data.getJSONObject(i).getString("DecantingSiteName") != null){

                ratingPojo.setDecantingSiteName(data.getJSONObject(i).getString("DecantingSiteName"));
            }
            if(data.getJSONObject(i).getString("DriverRating") != null){

                ratingPojo.setDriverRating(data.getJSONObject(i).getString("DriverRating"));
            }
            if(data.getJSONObject(i).getString("RetailerRating") != null){

                ratingPojo.setRetailerRating(data.getJSONObject(i).getString("RetailerRating"));
            }
            if(data.getJSONObject(i).getString("DecantedTime") != null){
                ratingPojo.setDecantingTime(data.getJSONObject(i).getString("DecantedTime"));

            }

            ratingPojoList.add(ratingPojo);

        }

        listviewRating.setAdapter(new ViewRatingAdapter(getContext(),ratingPojoList));
    }

    @Override
    public void onError(VolleyError Error) {

    }
}
