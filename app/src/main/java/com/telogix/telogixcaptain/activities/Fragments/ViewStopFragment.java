package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.DriverLog.DriverLogPojo;
import com.telogix.telogixcaptain.Pojo.ViewStop.StopPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.adapters.ViewStopAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ViewStopFragment extends Fragment implements response_interface {
    ArrayList<StopPojo> stoplist;
    String RouteAssignID = null;
    TextView stop_title,stop_description,stop_position,stop_duration;
    ListView listviewStops;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_stop,container,false);
        bindViews(v);
        Bundle bundle = getArguments();
        if(bundle.getString("VehicleNo")!=null)
        getActivity().setTitle("View Stops: "+bundle.getString("VehicleNo"));
        return v;
    }

    private void bindViews(View v){
        stop_title = v.findViewById(R.id.stop_title);
        stop_duration = v.findViewById(R.id.stop_duration);
        stop_description = v.findViewWithTag(R.id.stop_description);
        stop_position = v.findViewById(R.id.stop_position);
        listviewStops = v.findViewById(R.id.listviewStops);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(  bundle.getString("RouteAssignID") != null ){
            RouteAssignID = bundle.getString("RouteAssignID");
            getDriverLog();
        }
    }

    private void getDriverLog() {
        try {
            HashMap hashMap = new HashMap();

            httpvolley.stringrequestpost("api/DriverLogs/GetDriverLog?RouteAssignID=" + RouteAssignID, Request.Method.GET, hashMap, this);
        }
        catch (Exception ex){

        }
    }
    @Override
    public void onResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray  data = jsonObject.getJSONArray("Data");
        if(jsonObject.get("ResponseCode").equals(2103))
        {
            Log.i("--datalength",String.valueOf(data.length()));
            stoplist = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                DriverLogPojo driverLogPojo = new DriverLogPojo();
                if (data.getJSONObject(i).getString("StopTypeTitle") != "Ride Started") {
                    String Stoptitle = data.getJSONObject(i).getString("StopTypeTitle");
                    if (Stoptitle.equals("Resumed")) {
                        Double old_latitude = data.getJSONObject(i-1).getDouble("Latitude");
                        Double old_longitude = data.getJSONObject(i-1).getDouble("Longitude");
                        LatLng coordinates = new LatLng(old_latitude, old_longitude);
                       // stopMarkersList.add(coordinates);
                        String VehicleNo = data.getJSONObject(i).getString("VehicleNo");
                        String Description = data.getJSONObject(i-1).getString("Description");
                        String StopTypeTitle = data.getJSONObject(i-1).getString("StopTypeTitle");
                        String oldtime = data.getJSONObject(i-1).getString("StartDate");
                        String newtime = data.getJSONObject(i).getString("StartDate");
                        Date olddate = StringtoDate(oldtime);
                        Date newdate = StringtoDate(newtime);
                        Long stopduration = getDateDiff(olddate,newdate, TimeUnit.MINUTES);
                        driverLogPojo.setDriverDetails(i-1, StopTypeTitle, VehicleNo, stopduration, Description, coordinates);
                        String currentStopMarkerDetail = "\n"+StopTypeTitle+"\n"+"Duration:"+stopduration+" min"+"\nDescription:"+Description+"\n"+coordinates+"\n";
                        //totalStopDuration+=stopduration;
                        StopPojo stopPojo = new StopPojo();
                        stopPojo.setStopTitle(StopTypeTitle);
                        stopPojo.setStopDescription(Description);
                        stopPojo.setStopDuration(""+stopduration);
                        stopPojo.setStopPosition(coordinates);
                        String stopTime="";
                        if(olddate.getHours()>12){
                                int hours = olddate.getHours()-12;
                                stopTime =  ""+hours+":"+olddate.getMinutes()+" PM";
                        }
                        else{
                                int hours = olddate.getHours();
                                stopTime =  ""+hours+":"+olddate.getMinutes()+" AM";
                        }
                        stopPojo.setStopTime(stopTime);
                        stoplist.add(stopPojo);





                    }


                }

            }
            listviewStops.setAdapter(new ViewStopAdapter(getContext(),stoplist));
        }

    }

    @Override
    public void onError(VolleyError Error) {

    }
    public Date StringtoDate(String date){
        Date date1 = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date1 =   format.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  date1;
    }
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
