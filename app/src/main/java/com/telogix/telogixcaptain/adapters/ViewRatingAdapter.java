package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telogix.telogixcaptain.Pojo.ViewRating.RatingPojo;
import com.telogix.telogixcaptain.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewRatingAdapter extends BaseAdapter {
    List<RatingPojo> ratingPojoList = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    TextView driverName,vehicleNo,decantingSite,driverRating,decantedTime,retailerRating;
    public ViewRatingAdapter(Context context,List<RatingPojo> list){

        this.ratingPojoList = list;
        this.context  = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return ratingPojoList.size();
    }

    @Override
    public Object getItem(int i) {
        return ratingPojoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       View v = layoutInflater.inflate(R.layout.layout_viewrating,null,false);
        LinearLayout ll_multipleratings = v.findViewById(R.id.ll_multipleratings);
        View multipleratingsView = layoutInflater.inflate(R.layout.multipleratings,null,false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0,5,0,0);
        multipleratingsView.setLayoutParams(layoutParams);
        driverName = multipleratingsView.findViewById(R.id.driverName);
        vehicleNo  = multipleratingsView.findViewById(R.id.vehicleNo);
        decantingSite = multipleratingsView.findViewById(R.id.decantingSite);
        driverRating = multipleratingsView.findViewById(R.id.driverRating);
        decantedTime = multipleratingsView.findViewById(R.id.decantedTime);
        retailerRating = multipleratingsView.findViewById(R.id.retailerRating);

        driverName.setText("Driver: "+ratingPojoList.get(i).getDriver());
        vehicleNo.setText("VehicleNo: "+ratingPojoList.get(i).getVehicleNo());
        decantingSite.setText("Decanting Site Name: "+ratingPojoList.get(i).getDecantingSiteName());
        driverRating.setText("Driver Rating: "+ratingPojoList.get(i).getDriverRating());
        if(ratingPojoList.get(i).getRetailerRating() != "null"){
            retailerRating.setText("Retailer Rating: "+ratingPojoList.get(i).getRetailerRating());
        }
        else{
            retailerRating.setText("Retailer Rating: -");
        }

        String formattedDate = parseDateToddMMyyyy(ratingPojoList.get(i).getDecantingTime().split("T")[0]);
        SimpleDateFormat inputFormat = new SimpleDateFormat("");

        decantedTime.setText("Decanting Time: "+formattedDate+" "+parseTimeTohhmmaa(ratingPojoList.get(i).getDecantingTime().split("T")[1]));

        ll_multipleratings.addView(multipleratingsView);
        return v;
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
    public String parseTimeTohhmmaa(String time) {
        String inputPattern = "hh:mm:ss";
        String outputPattern = "hh:mm aa";
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
