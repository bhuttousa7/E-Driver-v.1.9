package com.telogix.telogixcaptain.driver.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.Utils;
import com.telogix.telogixcaptain.activities.BaseActivity;
import com.telogix.telogixcaptain.activities.Fragments.HandbookFragment;
import com.telogix.telogixcaptain.activities.Fragments.RTChecklistFragment;
import com.telogix.telogixcaptain.activities.Fragments.TutorialsFragment;
import com.telogix.telogixcaptain.activities.Fragments.add_HazardFragment;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsActivity;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.fragments.AddStopDriver;
import com.telogix.telogixcaptain.driver.fragments.DriverDashboardFragment;
import com.telogix.telogixcaptain.driver.fragments.DriverMapFragment;
import com.telogix.telogixcaptain.driver.fragments.LogFragment;
import com.telogix.telogixcaptain.driver.fragments.LogoutFragment;
import com.telogix.telogixcaptain.driver.models.Data;
import com.telogix.telogixcaptain.driver.models.DriverVehicleStatusResponse;
import com.telogix.telogixcaptain.driver.utils.Drivers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class DriverMainActivity extends BaseActivity implements Serializable, NavigationView.OnNavigationItemSelectedListener, response_interface {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private static final int GPS_ON = 12;
    Fragment fragment;
    Toolbar toolbar;
    Context context;
    private boolean mLocationPermissionGranted;
    public static DriverVehicleStatusResponse responseObj = null;
    private Gson gson;
    private NavigationView navigationView;
    private static int drivercount=0;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    //public static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        Log.d("AndroidID",new Constant(this).getAndroid_id());
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


         powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
         wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"sdf" );
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       // startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(getPackageName() + BuildConfig.APPLICATION_ID)));
       // turnGPSOn();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerStateChanged(int newState) {
                httpvolley.checkAuth();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setTitle(getString(R.string.vehicles));
        TextView textUsername=  navigationView.getHeaderView(0).findViewById(R.id.username);
        String response= singleton.getsharedpreference(this).getString("userData", "");
        if(response!="") {
            Gson gson = new Gson();
            TokenPojo datum = gson.fromJson(response, TokenPojo.class);
            textUsername.setText(datum.getUserName());
        }
//        HashMap data=new HashMap();
//        httpvolley.stringrequestpost("api/VehicleDevices/GetDeviceRideByDeviceID?DeviceID=868041038085952", Request.Method.GET, data,this);
        LinearLayout layoutuser1=navigationView.getHeaderView(0).findViewById(R.id.layoutuser1);

        LinearLayout layoutuser2=navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
        layoutuser1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b=new Bundle();
                LogoutFragment logoutFragment=new LogoutFragment();
                Paper.init(getApplicationContext());
                Paper.book().read("drivers");

                ArrayList<HashMap<String,String>> drivers=new ArrayList();
                drivers= Paper.book().read("drivers");
                if(drivers!=null)
                {
                    if(drivers.size()>0)
                    {

                        for ( String key : drivers.get(0).keySet() ) {
                            b.putString("Driver","1");
                            b.putString("DriverName",""+ Drivers.getFirstDriverName(DriverMainActivity.this));
                            b.putString("DriverToken",drivers.get(0).get("Driver"));
                        }
                    }

                }
                logoutFragment.setArguments(b);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                ChangeFragment(logoutFragment);
            }
        });
   layoutuser2.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Bundle b=new Bundle();
           LogoutFragment logoutFragment=new LogoutFragment();
           Paper.init(getApplicationContext());
           Paper.book().read("drivers");

           ArrayList<HashMap<String,String>> drivers=new ArrayList();
           drivers= Paper.book().read("drivers");
           if(drivers!=null)
           {
               if(drivers.size()>1)
               {

                   for ( String key : drivers.get(1).keySet() ) {
                       b.putString("Driver","2");
                       b.putString("DriverName",""+ Drivers.getSecondDriverName(DriverMainActivity.this));
                       b.putString("DriverToken",drivers.get(1).get("Driver"));
                   }
               }

           }
           logoutFragment.setArguments(b);
           DrawerLayout drawer = findViewById(R.id.drawer_layout);
           drawer.closeDrawer(GravityCompat.START);
           ChangeFragment(logoutFragment);
       }
   });
        checkLocationPermission();
    }

    private  void requestLocationPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to access GPS from your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DriverMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(DriverMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            checkRideAlreadyStarted();
         //   Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            if (ActivityCompat.shouldShowRequestPermissionRationale(DriverMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
            //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {

            requestLocationPermission();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for enabling the permission", Toast.LENGTH_SHORT).show();

                //do something permission is allowed here....
                mLocationPermissionGranted = true;
                checkRideAlreadyStarted();

            } else {

                Toast.makeText(this, "Please allow the Permission", Toast.LENGTH_SHORT).show();


            }
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        if(!mLocationPermissionGranted){checkLocationPermission();}

        wakeLock.acquire();
        if(Drivers.getDriverCount(this)==2)
        {
            String token= singleton.getsharedpreference(this).getString("token","");
            CircleImageView user1=navigationView.getHeaderView(0).findViewById(R.id.userImage);
            CircleImageView user2=navigationView.getHeaderView(0).findViewById(R.id.userImage2);
            if(token.equals(Drivers.getFirstDriverToken(this)))
            {
                user1.setImageResource(R.drawable.onlineuser);
                user2.setImageResource(R.drawable.ic_person_black_24dp);
            }
            else if(token.equals(Drivers.getSecondDriverToken(this)))
            {
                user2.setImageResource(R.drawable.onlineuser);
                user1.setImageResource(R.drawable.ic_person_black_24dp);


            }
        }
        dualDriverCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        wakeLock.release();

    }

    private void turnGPSOn(){
        try{
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                Utils.displayPromptForEnablingGPS(this);
            }}
        catch (Exception ex){

        }
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_driver_main;
    }

    @Override
    public void onBackPressed() {
        int count=getSupportFragmentManager().getBackStackEntryCount();
        dualDriverCheck();


        if(count==1)
        {
            finish();
        }
        else {
           super.onBackPressed();
        }
    }

    private void UploadDeviceID() {

        HashMap data = new HashMap();
      //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/VehicleRideDrivers/StartJourney?DeviceID="+new Constant(this).getAndroid_id(), Request.Method.GET, data, this);

    }


    private void logOutALL() {

        HashMap data = new HashMap();
        httpvolley.stringrequestpost("api/DriverAttendance/SaveTimeOutForAllDrivers?DeviceID=" + new Constant(DriverMainActivity.this).getAndroid_id(), Request.Method.POST, data, DriverMainActivity.this);

    }

    private void expireToken() {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/UserToken/ExpireToken", Request.Method.PUT, data, this);

    }
    private void dualDriverCheck() {

        String token="";
        if(Drivers.getDriverCount(this)==0){
            token= singleton.getsharedpreference(this).getString("token","");
        }
        else if(Drivers.getDriverCount(this)==1)
        {
            token= Drivers.getFirstDriverToken(this);
        }
        else if(Drivers.getDriverCount(this)==2)
        {
            token= Drivers.getSecondDriverToken(this);
        }

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());

        httpvolley.stringrequestpostDriver("api/VehicleDevices/GetDeviceDriversDeviceID?DeviceID="+new Constant(this).getAndroid_id(), Request.Method.GET, data,token ,this);

    }
private  void checkRideAlreadyStarted(){
            HashMap data=new HashMap();
String url="api/VehicleDevices/GetDeviceRideByDeviceID?DeviceID="+new Constant(this).getAndroid_id();
Log.d("RequestURl",url);
        httpvolley.stringrequestpost(url, Request.Method.GET, data,this);
}

    private void launchFragment(Fragment fragment) {

//        try {
//            fragment = DriverDashboardFragment.class.newInstance();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();
        FragmentTransaction ft = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", responseObj);
        fragment.setArguments(bundle);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {

            getSupportFragmentManager().popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
         //   ChangeFragment(new DriverMapFragment());

            HashMap data = new HashMap();
        //    Log.d("token", singleton.getsharedpreference(this).getString("token", ""));
            dualDriverCheck();
            if(mLocationPermissionGranted == true)
                {
                    httpvolley.stringrequestpost("api/VehicleDevices/GetDeviceRideByDeviceID?DeviceID=" + new Constant(this).getAndroid_id(), Request.Method.GET, data, this);
                }

        }
        else if (id == R.id.triplog) {
// For Test

//            RatingView ratingView=new RatingView(this);
//            ratingView.setCancelable(false);
//            ratingView.show();
            //
            if (toolbar != null) {
                toolbar.setTitle("Log");
            }
            ChangeFragment(new LogFragment());

        }
        else if (id == R.id.nav_hazards) {

            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.hazard));
            }
            ChangeFragment(new add_HazardFragment());

        } else if (id == R.id.nav_addmanualstop) {
            ChangeFragment(new AddStopDriver());

        }
        else if(id==R.id.checklist)
        {
            if(responseObj!=null) {
                if (responseObj.data.IsCheckListApproved) {
                    Toast.makeText(this, "CheckList already approved", Toast.LENGTH_SHORT).show();
                } else {
                    ChangeFragment(new RTChecklistFragment());
                }
            }
            else
            {
                Toast.makeText(this,"No Checklist available",Toast.LENGTH_SHORT).show();
            }

        }
        else if (id == R.id.notifications) {
            hideKeyboardFrom();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.notifications));
            }
            Intent i=new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(i);

        }
        else if (id == R.id.nav_tutorials) {
            ChangeFragment(new TutorialsFragment());
        }
        else if (id == R.id.nav_handbook) {
            ChangeFragment(new HandbookFragment());
        }
        else if (id == R.id.nav_seconddriver) {
            Paper.init(getApplicationContext());
            Paper.book().read("drivers");

            ArrayList<HashMap<String,String>> drivers=new ArrayList();
            drivers= Paper.book().read("drivers");
            if(drivers!=null)
            {
                if(drivers.size()<2)
                {
                    Intent i =new Intent(this, SecondDriverLoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(this, "Both Drivers are already logged in", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Intent i =new Intent(this, SecondDriverLoginActivity.class);
                startActivity(i);
                finish();
            }

               }

        else if (id == R.id.nav_logout) {
            Paper.init(getApplicationContext());

            expireToken();
            ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
            driverslist=Paper.book().read("drivers");
            if(driverslist!=null) {
                drivercount = driverslist.size();
            }

            logOutALL();
           // NetworkConsume.getInstance().Logout(context);

           // singleton.getsharedpreference_editor(getApplicationContext()).remove("userData").commit();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public  void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void ChangeFragment(Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResponse(String response) throws JSONException {

       NetworkConsume.getInstance().hideProgress();
        Log.d("response",""+response);
        Log.d("response:", response);
        JSONObject responseJSON=new JSONObject(response);
        if(responseJSON.has("ResponseCode"))
        {
            if(responseJSON.getInt("ResponseCode")==1801)
            {
                try {
                    DriverVehicleStatusResponse respObj =new DriverVehicleStatusResponse();
                    if(responseJSON.has("Data"))
                    {
                        if(!responseJSON.get("Data").equals(null))
                        {


                    JSONObject jsonObject=responseJSON.getJSONObject("Data");
                    Data data=new Data();
                    data.vehicleID=jsonObject.getInt("VehicleID");
                    data.vehicleNo=jsonObject.get("VehicleNo")+"";
                    data.setCheckListApproved(jsonObject.getBoolean("IsCheckListApproved"));
                    data.statusID=jsonObject.getInt("StatusID");
                    data.vehicleStatus=jsonObject.get("VehicleStatus")+"";
                    data.routeID=jsonObject.get("RouteID")+"";
                    data.routeName=jsonObject.get("RouteName")+"";
                    data.routeAddress=jsonObject.get("RouteAddress")+"";
                    data.description=jsonObject.get("Description")+"";
                    data.isActive=jsonObject.get("IsActive")+"";
                    data.originLong=jsonObject.getDouble("OriginLong");
                    data.originLat=jsonObject.getDouble("OriginLat");
                    data.destinationLong=jsonObject.getDouble("DestinationLong");
                    data.destinationLat=jsonObject.getDouble("DestinationLat");
                    data.setLoadID(jsonObject.getInt("LoadID"));
                    if(jsonObject.has("RouteAssignID")) {
                        data.setRouteAssignID(jsonObject.getInt("RouteAssignID"));
                    }
                    JSONArray RouteDetailArray=jsonObject.getJSONArray("RouteDetails");
                    ArrayList routeDetailslist=new ArrayList<>();
                    RouteDetail rd = new RouteDetail();
                    for (int j = 0; j < RouteDetailArray.length(); j++) {
                        rd = new RouteDetail();
                        if(RouteDetailArray.getJSONObject(j).has("RouteDetailID"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("RouteDetailID").equals(null))
                            {
                                rd.setRouteDetailID(""+ RouteDetailArray.getJSONObject(j).get("RouteDetailID"));
                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("RouteID"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("RouteID").equals(null))
                            {
                                rd.setRouteID(""+RouteDetailArray.getJSONObject(j).get("RouteID"));
                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("RouteType"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("RouteType").equals(null))
                            {
                                rd.setRouteType("" + RouteDetailArray.getJSONObject(j).get("RouteType"));

                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("RouteTypeID"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals(null))
                            {
                                rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));

                            }
                            else
                            {
                                rd.setRouteTypeID("");
                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("Name"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("Name").equals(null))
                            {
                                rd.setName("" + RouteDetailArray.getJSONObject(j).get("Name"));

                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("Longitude"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("Longitude").equals(null))
                            {
                                rd.setLongitude((Double) RouteDetailArray.getJSONObject(j).get("Longitude"));

                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("Latitude"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("Latitude").equals(null))
                            {
                                rd.setLatitude((Double) RouteDetailArray.getJSONObject(j).get("Latitude"));

                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("OrderNumber"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("OrderNumber").equals(null))
                            {
                                rd.setOrderNumber((Integer) RouteDetailArray.getJSONObject(j).get("OrderNumber"));

                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("GoogleAddress"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("GoogleAddress").equals(null))
                            {
                                rd.setGoogleAddress("" + RouteDetailArray.getJSONObject(j).get("GoogleAddress"));
                            }
                        }
                        if(RouteDetailArray.getJSONObject(j).has("Detail"))
                        {
                            if(!RouteDetailArray.getJSONObject(j).get("Detail").equals(null))
                            {
                                rd.setDetail("" + RouteDetailArray.getJSONObject(j).get("Detail"));

                            }
                        }

                        routeDetailslist.add(rd);
                    }


                    respObj.setData(data);

                    data.routeDetails=routeDetailslist;

                    responseObj=respObj;

                        }
                        else
                        {
                            Toast.makeText(this,"No Route Assigned",Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(this,"No Route Assigned",Toast.LENGTH_SHORT).show();
                    }
           //         LocationManager manager= (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                   // if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
     //               {
                    Log.d("--mLocation",String.valueOf(mLocationPermissionGranted));
                        if (respObj.data.statusID < 5) {

                            if(mLocationPermissionGranted == true){
                                Intent i = new Intent(this, RideUnavailable.class);
//                                i.putExtra("VehicleID",""+respObj.data.vehicleID);
//                                i.putExtra("mLocationPermissionGranted",mLocationPermissionGranted);
                                startActivity(i);
                                finish();
                            }
                            else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(5)) {

                            if(mLocationPermissionGranted == true){
                                launchFragment(DriverDashboardFragment.class.newInstance());}
                            else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(6)) {

                            if(mLocationPermissionGranted == true){
                                launchMapFragment();
                            }else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(7)) {
                            // Rest AREA

                            if(mLocationPermissionGranted == true){
                                Intent i = new Intent(this, ResumeRideActivity.class);
                                i.putExtra("VehicleID", "" + respObj.data.vehicleID);
                                startActivity(i);
                            }else{
                                checkLocationPermission();

                            }


                        } else if (respObj.data.statusID.equals(8)) {
//RESTAURANT

                            if(mLocationPermissionGranted == true){
                                Intent i = new Intent(this, ResumeRideActivity.class);
                                i.putExtra("VehicleID", "" + respObj.data.vehicleID);
                                startActivity(i);
                            }else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(9)) {
//MANUAL STOP

                            if(mLocationPermissionGranted == true){
                                launchMapFragment();
                                Intent i = new Intent(this, ResumeRideActivity.class);
                                i.putExtra("VehicleID", "" + respObj.data.vehicleID);
                                startActivity(i);
                            }else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(10)) {
//DECANTING STOP

                            if(mLocationPermissionGranted == true){
                                Intent i = new Intent(this, ResumeRideActivity.class);
                                i.putExtra("VehicleID", "" + respObj.data.vehicleID);
                                startActivity(i);
                                launchMapFragment();
                            }else{
                                checkLocationPermission();
                            }

                        } else if (respObj.data.statusID.equals(11)) {
//RIDE END
                            Toast.makeText(this, "You have completed your ride", Toast.LENGTH_SHORT).show();
                        } else if (respObj.data.statusID.equals(12)) {
//ON MAINTENANCE
                            Toast.makeText(this, "Your vehicle is on maintenance", Toast.LENGTH_SHORT).show();
                        }

  //                  }
//                    else
//                    {

//                       // Utils.displayPromptForEnablingGPS(this);
//                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            else if(responseJSON.getInt("ResponseCode")==0)
            {
                if(mLocationPermissionGranted == true) {
                    Intent i = new Intent(this, RideUnavailable.class);
                    //i.putExtra("VehicleID",""+respObj.data.vehicleID);
             //   i.putExtra("mLocationPermissionGranted",mLocationPermissionGranted);
                startActivity(i);
                    finish();
                }
                else{
                    checkLocationPermission();
                }
            }
            else if(responseJSON.getInt("ResponseCode")==2405)
            {
                try {

                    {if(responseJSON.has("Data"))
                    {
                        if(!responseJSON.getJSONArray("Data").equals(null)) {

                            JSONArray users = responseJSON.getJSONArray("Data");
                            LinearLayout layoutuser1=navigationView.getHeaderView(0).findViewById(R.id.layoutuser1);

                            LinearLayout layoutuser2=navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
                            layoutuser2.setVisibility(View.GONE);
                            if(users.length()>0)
                            {

                                TextView firstDriver=  navigationView.getHeaderView(0).findViewById(R.id.username);
                                 firstDriver.setText(""+users.getJSONObject(0).get("UserName"));

                                 ArrayList<HashMap<String,String>> driverslist=Paper.book().read("drivers");
                                 HashMap driverHash=driverslist.get(0);
                                 driverHash.put("DriverName",""+users.getJSONObject(0).get("UserName"));
                                 Drivers.setFirstDriver(this,driverHash);
                                layoutuser2.setVisibility(View.GONE);

                            }
                            if(users.length()>1)
                            {

                                TextView secondDriver=  navigationView.getHeaderView(0).findViewById(R.id.usernametwo);
                                secondDriver.setText(""+users.getJSONObject(1).get("UserName"));
                                ArrayList<HashMap<String,String>> driverslist=Paper.book().read("drivers");

                               if(driverslist.size()>1) {
                                   HashMap driverHash = driverslist.get(1);
                                   driverHash.put("DriverName", "" + users.getJSONObject(1).get("UserName"));

                                   Drivers.setSecondDriverName(this,driverHash);
                               }
                                layoutuser2.setVisibility(View.VISIBLE);
                            }


                        }
                        else
                        {
                            NetworkConsume.getInstance().Logout(this);
                            singleton.getsharedpreference_editor(this).clear().commit();
                            Paper.book().delete(Constants.KEY_ROLE_ID);
                            Paper.book().delete("drivers");

                            LinearLayout layoutuser2=navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
                            layoutuser2.setVisibility(View.GONE);
                        }
                    }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(responseJSON.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.TIMEOUTMARKED)))
            {

                    NetworkConsume.getInstance().Logout(this);
                    singleton.getsharedpreference_editor(this).clear().commit();
                    Paper.book().delete(Constants.KEY_ROLE_ID);
                    Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();

            }
        }



    }


    private void launchMapFragment() {
        Fragment fragment = null;
        try {
            fragment = DriverMapFragment.class.newInstance();
          //  new LogBook(this).updateLogBook("Ride Started");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (fragment != null && responseObj != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", responseObj);
            fragment.setArguments(bundle);

          getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        }
    }


    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Log.d("response error:", "" + Error);

    }


}
