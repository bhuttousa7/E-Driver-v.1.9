package com.telogix.telogixcaptain.driver.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.offline_response_interface;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.OfflineData;
import com.telogix.telogixcaptain.activities.Fragments.HandbookFragment;
import com.telogix.telogixcaptain.activities.Fragments.RTChecklistFragment;
import com.telogix.telogixcaptain.activities.Fragments.TutorialsFragment;
import com.telogix.telogixcaptain.activities.Fragments.add_HazardFragment;
import com.telogix.telogixcaptain.activities.LoginActivity;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsActivity;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.fragments.AddStopDriver;
import com.telogix.telogixcaptain.driver.fragments.LogFragment;
import com.telogix.telogixcaptain.driver.fragments.LogoutFragment;
import com.telogix.telogixcaptain.driver.fragments.RideUnavailableFragment;
import com.telogix.telogixcaptain.driver.utils.Drivers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class RideUnavailable extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, response_interface, offline_response_interface {
    Toolbar toolbar;
    private NavigationView navigationView;
    private static final int drivercount = 0;
    boolean offline = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_unavailable2);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ChangeFragment(new RideUnavailableFragment());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        navigationView.getMenu().findItem(R.id.nav_seconddriver
//        ).setVisible(false);
        navigationView.getMenu().findItem(R.id.triplog).setVisible(false);
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
        LinearLayout layoutuser2 = navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
        layoutuser2.setVisibility(View.GONE);
        LinearLayout layoutuser1 = navigationView.getHeaderView(0).findViewById(R.id.layoutuser1);


        layoutuser1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                LogoutFragment logoutFragment = new LogoutFragment();
                Paper.init(getApplicationContext());
                Paper.book().read("drivers");

                ArrayList<HashMap<String, String>> drivers = new ArrayList();
                drivers = Paper.book().read("drivers");
                if (drivers != null) {
                    if (drivers.size() > 0) {

                        for (String key : drivers.get(0).keySet()) {
                            b.putString("Driver", "1");
                            b.putString("DriverName", "" + Drivers.getFirstDriverName(RideUnavailable.this));
                            b.putString("DriverToken", drivers.get(0).get("Driver"));
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
                Bundle b = new Bundle();
                LogoutFragment logoutFragment = new LogoutFragment();
                Paper.init(getApplicationContext());
                Paper.book().read("drivers");

                ArrayList<HashMap<String, String>> drivers = new ArrayList();
                drivers = Paper.book().read("drivers");
                if (drivers != null) {
                    if (drivers.size() > 1) {

                        for (String key : drivers.get(1).keySet()) {
                            b.putString("Driver", "2");
                            b.putString("DriverName", "" + Drivers.getSecondDriverName(RideUnavailable.this));
                            b.putString("DriverToken", drivers.get(1).get("Driver"));
                        }
                    }

                }
                logoutFragment.setArguments(b);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                ChangeFragment(logoutFragment);
            }
        });
        TextView textUsername = navigationView.getHeaderView(0).findViewById(R.id.username);
        String response = singleton.getsharedpreference(this).getString("userData", "");
        if (response != "") {
            Gson gson = new Gson();
            TokenPojo datum = gson.fromJson(response, TokenPojo.class);
            textUsername.setText(datum.getUserName());
        }

     // checkLocationPermission();
    }


    private  void requestLocationPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to access GPS from your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RideUnavailable.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

    public void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
//                mLocationPermissionGranted = true;
//                checkRideAlreadyStarted();

            } else {

                Toast.makeText(this, "Please allow the Permission", Toast.LENGTH_SHORT).show();


            }
        }

    }



    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 1) {
//                Intent a = new Intent(Intent.ACTION_MAIN);
//                a.addCategory(Intent.CATEGORY_HOME);
//                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(a);
//                finish()
                this.finishAffinity();

            } else {
                super.onBackPressed();
                if (Drivers.getDriverCount(this) == 2) {
                    String token = singleton.getsharedpreference(this).getString("token", "");
                    CircleImageView user1 = navigationView.getHeaderView(0).findViewById(R.id.userImage);
                    CircleImageView user2 = navigationView.getHeaderView(0).findViewById(R.id.userImage2);
                    if (token.equals(Drivers.getFirstDriverToken(this))) {
                        user1.setImageResource(R.drawable.onlineuser);
                        user2.setImageResource(R.drawable.ic_person_black_24dp);
                    } else if (token.equals(Drivers.getSecondDriverToken(this))) {
                        user2.setImageResource(R.drawable.onlineuser);
                        user1.setImageResource(R.drawable.ic_person_black_24dp);


                    }
                }
                dualDriverCheck();
            }
//            NetworkConsume.getInstance().Logout(this);
//            singleton.getsharedpreference_editor(this).clear().commit();
//            Paper.book().delete(Constants.KEY_ROLE_ID);
//            super.onBackPressed();
        }
    }


    private void expireToken() {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/UserToken/ExpireToken", Request.Method.PUT, data, this);

    }

    public void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        hideKeyboardFrom();
        if (id == R.id.home) {
// For Test

//            RatingView ratingView=new RatingView(this);
//            ratingView.setCancelable(false);
//            ratingView.show();
            //
            if (toolbar != null) {
                toolbar.setTitle("Ride Unavailable");
            }
            ChangeFragment(new RideUnavailableFragment());

        } else if (id == R.id.triplog) {
// For Test

//            RatingView ratingView=new RatingView(this);
//            ratingView.setCancelable(false);
//            ratingView.show();
            //
            if (toolbar != null) {
                toolbar.setTitle("Log");
            }
            ChangeFragment(new LogFragment());

        } else if (id == R.id.notifications) {

            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.notifications));
            }
            Intent i = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_hazards) {

            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.hazard));
            }
            ChangeFragment(new add_HazardFragment());

        } else if (id == R.id.nav_addmanualstop) {
            ChangeFragment(new AddStopDriver());

        } else if (id == R.id.checklist) {
            if (DriverMainActivity.responseObj != null) {
                ChangeFragment(new RTChecklistFragment());
            } else {
                Toast.makeText(this, "No Checklist available", Toast.LENGTH_SHORT).show();
            }
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

            ArrayList<HashMap<String, String>> drivers = new ArrayList();
            drivers = Paper.book().read("drivers");
            if (drivers != null) {
                if (drivers.size() < 2) {
                    Intent i = new Intent(this, SecondDriverLoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Both Drivers are already logged in", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent i = new Intent(this, SecondDriverLoginActivity.class);
                startActivity(i);
                finish();
            }


        } else if (id == R.id.nav_logout) {
//            Paper.init(getApplicationContext());
//
            logOutALL();

//            ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
//            driverslist=Paper.book().read("drivers");
//            if(driverslist!=null) {
//                drivercount = driverslist.size();
//            }
//

//            logOut();
//            NetworkConsume.getInstance().Logout(this);
//            singleton.getsharedpreference_editor(this).clear().commit();
//            // singleton.getsharedpreference_editor(getApplicationContext()).remove("userData").commit();
//            Paper.book().delete(Constants.KEY_ROLE_ID);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutALL() {

        HashMap data = new HashMap();
        httpvolley.stringrequestpost("api/DriverAttendance/SaveTimeOutForAllDrivers?DeviceID=" + new Constant(RideUnavailable.this).getAndroid_id(), Request.Method.POST, data, this);

    }

    private void dualDriverCheck() {

        String token = "";
        if (Drivers.getDriverCount(this) == 0) {
            token = singleton.getsharedpreference(this).getString("token", "");
        } else if (Drivers.getDriverCount(this) == 1) {
            token = Drivers.getFirstDriverToken(this);
        } else if (Drivers.getDriverCount(this) == 2) {
            token = Drivers.getSecondDriverToken(this);
        }

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());

        httpvolley.stringrequestpostDriver("api/VehicleDevices/GetDeviceDriversDeviceID?DeviceID=" + new Constant(this).getAndroid_id(), Request.Method.GET, data, token, this);

    }

    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {


                if (intent.getStringExtra("Title").equalsIgnoreCase("New Ride")) {
                    Intent i = new Intent(RideUnavailable.this, LoginActivity.class);
                    finishAffinity();
                    startActivity(i);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private final BroadcastReceiver mServiceInternetConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getBooleanExtra("internet", true)) {
                    if (offline && Constants.internetConnected) {
                        if (OfflineData.offlineData.size() > 0) {
                            sync_OfflineData();
                        }
                    }
                    Constants.internetConnected = true;
                    offline = false;
                } else {
                    //no connection to internet

                    Constants.internetConnected = false;
                    offline = true;


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    };

    private void sync_OfflineData() {
        try {
            if (OfflineData.offlineData.size() > 0) {
                NetworkConsume.getInstance().ShowProgress(this);
                HashMap<String, HashMap> offline = OfflineData.offlineData.get(0);
                for (String key : offline.keySet()) {
                    httpvolley.stringrequestpost_offline("" + key, Request.Method.POST, offline.get(key), this);
                }
                OfflineData.offlineData.remove(0);
                sync_OfflineData();

            } else {
                NetworkConsume.getInstance().hideProgress();
                Toast.makeText(this, "Sync Completed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            NetworkConsume.getInstance().hideProgress();
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Drivers.getDriverCount(this) == 2) {
            String token = singleton.getsharedpreference(this).getString("token", "");
            CircleImageView user1 = navigationView.getHeaderView(0).findViewById(R.id.userImage);
            CircleImageView user2 = navigationView.getHeaderView(0).findViewById(R.id.userImage2);
            if (token.equals(Drivers.getFirstDriverToken(this))) {
                user1.setImageResource(R.drawable.onlineuser);
                user2.setImageResource(R.drawable.ic_person_black_24dp);
            } else if (token.equals(Drivers.getSecondDriverToken(this))) {
                user2.setImageResource(R.drawable.onlineuser);
                user1.setImageResource(R.drawable.ic_person_black_24dp);


            }
        }
        dualDriverCheck();
        //register receiver for internet connection check
        IntentFilter internetfilter = new IntentFilter();
        internetfilter.addAction(Constants.MY_INTERNET_ACTION_KEY);
        registerReceiver(mServiceInternetConnection, internetfilter);
        // Broadcast for notifications
        internetfilter = new IntentFilter();
        internetfilter.addAction(Constants.NOTIFICATION_BROADCAST_KEY);
        registerReceiver(mNotificationReceiver, internetfilter);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mServiceInternetConnection != null) {
                mServiceInternetConnection.abortBroadcast();
            }
            if (mNotificationReceiver != null) {
                mNotificationReceiver.abortBroadcast();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();

    }

    @Override
    public void onResponse(String response) throws JSONException {
        JSONObject responseJSON = new JSONObject(response);
        if (responseJSON.has("ResponseCode")) {
            if (responseJSON.getInt("ResponseCode") == 2405) {
                try {

                    {
                        if (responseJSON.has("Data")) {
                            if (!responseJSON.getJSONArray("Data").equals(null)) {

                                JSONArray users = responseJSON.getJSONArray("Data");
                                LinearLayout layoutuser1 = navigationView.getHeaderView(0).findViewById(R.id.layoutuser1);

                                LinearLayout layoutuser2 = navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
                                layoutuser2.setVisibility(View.GONE);
                                if (users.length() > 0) {

                                    TextView firstDriver = navigationView.getHeaderView(0).findViewById(R.id.username);
                                    firstDriver.setText("" + users.getJSONObject(0).get("UserName"));

                                    ArrayList<HashMap<String, String>> driverslist = Paper.book().read("drivers");
                                    HashMap driverHash = driverslist.get(0);
                                    driverHash.put("DriverName", "" + users.getJSONObject(0).get("UserName"));
                                    Drivers.setFirstDriver(this, driverHash);
                                    layoutuser2.setVisibility(View.GONE);

                                }
                                if (users.length() > 1) {

                                    TextView secondDriver = navigationView.getHeaderView(0).findViewById(R.id.usernametwo);
                                    secondDriver.setText("" + users.getJSONObject(1).get("UserName"));
                                    ArrayList<HashMap<String, String>> driverslist = Paper.book().read("drivers");

                                    if (driverslist.size() > 1) {
                                        HashMap driverHash = driverslist.get(1);
                                        driverHash.put("DriverName", "" + users.getJSONObject(1).get("UserName"));

                                        Drivers.setSecondDriverName(this, driverHash);
                                    }

                                    layoutuser2.setVisibility(View.VISIBLE);
                                }


                            } else {
                                NetworkConsume.getInstance().Logout(this);
                                singleton.getsharedpreference_editor(this).clear().commit();
                                Paper.book().delete(Constants.KEY_ROLE_ID);
                                Paper.book().delete("drivers");

                                LinearLayout layoutuser2 = navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
                                layoutuser2.setVisibility(View.GONE);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (responseJSON.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.TIMEOUTMARKED))) {
                expireToken();
                NetworkConsume.getInstance().Logout(this);
                singleton.getsharedpreference_editor(this).clear().commit();
                Paper.book().delete(Constants.KEY_ROLE_ID);
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onError(VolleyError Error) {
        Log.d("responseerror", Error + "");
    }

    @Override
    public void onOfflineResponse(String response) throws JSONException {

    }

    @Override
    public void onOfflineError(VolleyError Error) {

    }
}
