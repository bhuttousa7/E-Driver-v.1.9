package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.telogix.telogixcaptain.R;

public class StopInfoAdapter implements GoogleMap.InfoWindowAdapter{
    Context context;
    TextView tv_stopDetails;
    String stopdetails ;
    String sd;
    public StopInfoAdapter(Context context, String stopdetails){

        this.context  = context;
        this.sd = stopdetails;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.stoppopup_design,null);
        stopdetails = (String)marker.getTag();
        if(stopdetails != null){
            tv_stopDetails = v.findViewById(R.id.tv_stopdetail);
            tv_stopDetails.setText(stopdetails);
        }


        if (marker.isInfoWindowShown() && marker != null)
        {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
        return  v;

    }
}
