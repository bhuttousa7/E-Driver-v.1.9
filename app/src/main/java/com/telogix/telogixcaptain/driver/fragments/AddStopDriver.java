package com.telogix.telogixcaptain.driver.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.DriverStops.DatumStops;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsManager;
import com.telogix.telogixcaptain.adapters.DriverStopsAdapter;
import com.telogix.telogixcaptain.driver.activities.DriverMainActivity;
import com.telogix.telogixcaptain.driver.activities.ResumeRideActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.telogix.telogixcaptain.Log.LogBook.getCurrentDate;
import static com.telogix.telogixcaptain.driver.activities.DriverMainActivity.responseObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStopDriver extends Fragment implements response_interface, LocationListener {


    private Spinner stopspinner;
    private LocationManager locationManager;
    private EditText edt_stopreason;

    public AddStopDriver() {
        // Required empty public constructor
    }

    ArrayList<String> stopsReason = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_stop_driver, container, false);
        getActivity().setTitle("ADD MANUAL STOP");
        getStopsType();

        stopspinner = v.findViewById(R.id.spinnerReason);

        Button btn_manualstop = v.findViewById(R.id.btn_manualstop);
         edt_stopreason = v.findViewById(R.id.edt_stopreason);
        btn_manualstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stopspinner.getSelectedItem().toString().equals("")) {

                    if(DriverMainActivity.responseObj!=null) {
                        StopRide("", "9", DriverMainActivity.responseObj.data.getRouteAssignID()
                        );
                    }
                    else {
                        Toast.makeText(getContext(), "No Route found, Cannot add stop", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Enter Reason To Stop", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void getStopsType() {

        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/StopTypes/GetStopTypesForDDL", Request.Method.GET, hashMap, this);

    }

    private void StopRide(String VehicleID, String StatusID, int RouteAssignID) {

        try {


            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            locationManager.requestLocationUpdates(provider, 400, 1, this);

            Location location = locationManager.getLastKnownLocation(provider);

            double lat = location.getLatitude();
            double lng = location.getLongitude();

                HashMap hashMap = new HashMap();
                hashMap.put("StatusID", ""+StatusID);
                hashMap.put("RouteAssignID", "" + RouteAssignID);
                hashMap.put("Longitude", "" + lng);
                hashMap.put("Latitude", "" + lat);
                hashMap.put("Description", "" + edt_stopreason.getText());
                hashMap.put("StopTypeID", "" + stopspinner.getSelectedItemId());

                httpvolley.stringrequestpost("api/DriverLogs/SaveDriverLog", Request.Method.POST, hashMap, this);

        }catch (Exception ex)
        {
            HashMap hashMap = new HashMap();
            hashMap.put("StatusID", ""+StatusID);
            hashMap.put("RouteAssignID", "" + RouteAssignID);
            hashMap.put("Longitude", "" + 0.0);
            hashMap.put("Latitude", "" + 0.0);
            hashMap.put("Description", "" + edt_stopreason.getText());
            hashMap.put("StopTypeID", "" + stopspinner.getSelectedItemId());

            httpvolley.stringrequestpost("api/DriverLogs/SaveDriverLog", Request.Method.POST, hashMap, this);
            ex.printStackTrace();
        }
    }
    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.has("ResponseCode")) {
                if (jsonObject1.get("ResponseCode").equals(2101)) {
                    NotificationsManager.sendNotification(getContext(),"Manual stop by driver at:"+getCurrentDate()+" .Vehicle "+ DriverMainActivity.responseObj.data.vehicleID,"Ride Completed",""+responseObj.data.vehicleID);

                    getActivity().getSupportFragmentManager().popBackStack();
                    Intent i = new Intent(getContext(), ResumeRideActivity.class);
                    i.putExtra("VehicleID", "" + DriverMainActivity.responseObj.data.vehicleID);
                    startActivity(i);
                }
                else if(jsonObject1.get("ResponseCode").equals(2002))
                {
                    JSONArray data=jsonObject1.getJSONArray("Data");
                    ArrayList<DatumStops> datumStopsArrayList=new ArrayList<>();
                    for(int i=0;i<data.length();i++)
                    {
                        DatumStops datumStops=new DatumStops();
                        datumStops.setStopTypeID(data.getJSONObject(i).getInt("StopTypeID"));
                        datumStops.setStopTypeTitle(data.getJSONObject(i).getString("StopTypeTitle"));
                        datumStopsArrayList.add(datumStops);
                    }
                    stopspinner.setAdapter(new DriverStopsAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,datumStopsArrayList));
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
