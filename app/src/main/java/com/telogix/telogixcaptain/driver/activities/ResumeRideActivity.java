package com.telogix.telogixcaptain.driver.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Log.LogBook;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.OfflineData;
import com.telogix.telogixcaptain.activities.BaseActivity;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsManager;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.fragments.DriverMapFragment;
import com.telogix.telogixcaptain.driver.utils.Drivers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.telogix.telogixcaptain.Log.LogBook.getCurrentDate;
import static com.telogix.telogixcaptain.driver.activities.DriverMainActivity.responseObj;

public class ResumeRideActivity extends BaseActivity implements response_interface, LocationListener {
    String VehicleID="",LoadDecantingSiteID="";
    double latitude=0,longitude=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_ride);
        if(getIntent().hasExtra("VehicleID"))
        {
            VehicleID=getIntent().getStringExtra("VehicleID");
        }
        if(getIntent().hasExtra("LoadDecantingSiteID"))
        {
            LoadDecantingSiteID=getIntent().getStringExtra("LoadDecantingSiteID");
            singleton.getsharedpreference_editor(this).putString("LoadDecantingSiteID",LoadDecantingSiteID).commit();
        }

        LinearLayout layout_resume=findViewById(R.id.layout_resume);
        layout_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Drivers.getDriverCount(ResumeRideActivity.this)>1) {
                    ShowDriverDialog();
                }
                else {
                    checkLastStatusDecanting();
                }


            }
        });
    }

    private void checkLastStatusDecanting() {
        HashMap hashMap = new HashMap();

        hashMap.put("DeviceID",VehicleID);
        if(Constants.internetConnected) {
            httpvolley.stringrequestpost("api/Vehicles/GetVehicleStatusByDeviceID?DeviceID=" + new Constant(this).getAndroid_id(), Request.Method.POST, hashMap, this);
        }
        else
        {
            HashMap<String,HashMap> data=new HashMap<>();
            data.put("api/Vehicles/GetVehicleStatusByDeviceID?DeviceID=" + new Constant(this).getAndroid_id(),hashMap);
            OfflineData.offlineData.add(data);
            NotificationsManager.sendNotification(this,"Ride Resumed offline mode","Ride Resumed offline mode at: "+getCurrentDate(),""+responseObj.data.vehicleID);
            DriverMapFragment.reacheddecanting=false;
            new LogBook(this).updateLogBook("Ride Resumed offline mode");
            ResumeRide();
        }
    }
    private void ShowDriverDialog() {
        try {
            final ArrayList<HashMap<String, String>> driverslist = Drivers.getDrivers(this);
            final CharSequence[] charSequence = new CharSequence[2];
            if (Drivers.getDrivers(this) != null) {


                charSequence[0] = driverslist.get(0).get("DriverName");
                charSequence[1] = driverslist.get(1).get("DriverName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Driver")
                    //.setMessage("You can buy our products without registration too. Enjoy the shopping")
                    .setSingleChoiceItems(charSequence, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            singleton.getsharedpreference_editor(getApplicationContext()).putString("token",driverslist.get(which).get("Driver")).commit();

//                            while(singleton.getsharedpreference(getApplicationContext()).getString("token","")!="")
//                            {
//                                Toast.makeText(getApplicationContext(),"helo",Toast.LENGTH_SHORT).show();
//                            }

                            checkLastStatusDecanting();
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false)

            ;
            builder.create().show();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_resume_ride;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void ResumeRide() {
        try{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        NetworkConsume.getInstance().ShowProgress(this);
        if(!VehicleID.equals(null)){
        if(!VehicleID.equals("")) {
            HashMap hashMap = new HashMap();
            hashMap.put("StatusID", "6");
            hashMap.put("Longitude", ""+longitude);
            hashMap.put("Latitude", ""+latitude);
            hashMap.put("VehicleID",""+VehicleID);
            hashMap.put("RouteAssignID",""+ DriverMainActivity.responseObj.data.getRouteAssignID());
            if(Constants.internetConnected) {
                httpvolley.stringrequestpost("api/DriverLogs/ResumeRide", Request.Method.POST, hashMap, this);
            }
            else
            {
                HashMap<String,HashMap> data=new HashMap<>();
                data.put("api/DriverLogs/ResumeRide",hashMap);
                OfflineData.offlineData.add(data);
                finish();
            }
        }}
    } catch (Exception ex)
        {
            if(!VehicleID.equals(null)){
                if(!VehicleID.equals("")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("StatusID", "6");
                    hashMap.put("Longitude", ""+longitude);
                    hashMap.put("Latitude", ""+latitude);
                    hashMap.put("VehicleID",""+VehicleID);
                    hashMap.put("RouteAssignID",""+ DriverMainActivity.responseObj.data.getRouteAssignID());
                    if(Constants.internetConnected) {
                        httpvolley.stringrequestpost("api/DriverLogs/ResumeRide", Request.Method.POST, hashMap, this);
                    }
                    else
                    {
                        HashMap<String,HashMap> data=new HashMap<>();
                        data.put("api/DriverLogs/ResumeRide",hashMap);
                        OfflineData.offlineData.add(data);
                        finish();
                    }

                }}
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(String response) throws JSONException {
        //NetworkConsume.getInstance().hideProgress();
        try{
            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.has("ResponseCode")) {
                if(jsonObject1.get("ResponseCode").equals(2102))
                {
                    singleton.getsharedpreference_editor(this).putString("LoadDecantingSiteID","").commit();
                    NotificationsManager.sendNotification(this,"Ride Resumed","Ride Resumed at: "+getCurrentDate(),""+responseObj.data.vehicleID);
                    DriverMapFragment.reacheddecanting=false;
                    new LogBook(this).updateLogBook("Ride Resumed");
                    finish();

                }
                if(jsonObject1.get("ResponseCode").equals(1111))
                {


                    if(jsonObject1.has("Data"))
                    {
                        if(jsonObject1.getJSONObject("Data")!=null) {
                            String StatusID=""+jsonObject1.getJSONObject("Data").get("StatusID");
                            if (StatusID.equals("10") || StatusID.equals(10))
                            {
                                showDialog();
                            }
                            else
                            {
                                ResumeRide();
                            }
                        }
                    }
                }
                if(jsonObject1.get("ResponseCode").equals(2601))
                {
                    ResumeRide();
            }
            }}catch (Exception ex)
        {
          //  NetworkConsume.getInstance().hideProgress();
        }
    }

    @Override
    public void onError(VolleyError Error) {
        //NetworkConsume.getInstance().hideProgress();
    }


    private void showDialog() {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.activity_rating, null);
            RatingBar ratingBar=layout.findViewById(R.id.ratingBar2);
       Switch switch1=layout.findViewById(R.id.switch1);
        EditText editText2=layout.findViewById(R.id.editText2);
        final boolean[] checked = {false};
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked[0] =isChecked;            }
        });
           alert.setCancelable(false);
            alert.setView(layout);
            alert.setTitle("");
            alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    SaveDriverRating(checked[0],ratingBar.getNumStars(),editText2.getText().toString());
                    //ResumeRide();

                }
            });
            alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alert.show();
//        new AppRatingDialog.Builder()
//                .setPositiveButtonText("Submit")
//                .setNegativeButtonText("Cancel")
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
//                .setDefaultRating(0)
//                .setTitle("Rate Service Champion")
//                .setDescription("Please select some stars and give your feedback")
//                .setCommentInputEnabled(true)
//                .setStarColor(R.color.starColor)
//                .setNoteDescriptionTextColor(R.color.gray)
//                .setTitleTextColor(R.color.black)
//                .setDescriptionTextColor(R.color.white)
//                .setHint("Service champion was same? write here")
//                .setHintTextColor(R.color.gray)
//                .setCommentTextColor(R.color.white)
//                .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
//                .setCancelable(false)
//                .setCanceledOnTouchOutside(false)
//                .create(this)
//                // .setTargetFragment(this, TAG) // only if listener is implemented by fragment
//                .show();
    }

    private void SaveDriverRating(boolean ServiceChampionSame,int ratings,String Reviews) {
        HashMap hashMap=new HashMap();
        hashMap.put("LoadDecantingSiteID",""+ singleton.getsharedpreference(this).getString("LoadDecantingSiteID",""));
        if(ServiceChampionSame)
        {
            hashMap.put("IsSameChampion",""+1);
        }
        else
        {
            hashMap.put("IsSameChampion",""+0);
        }

        hashMap.put("Rating",""+ratings);
        hashMap.put("Reviews",""+Reviews);
        httpvolley.stringrequestpost("api/LoadDecantingSite/SaveDriverRating", Request.Method.POST, hashMap, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();
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
