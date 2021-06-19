package com.telogix.telogixcaptain.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.activities.Fragments.DashboardFragment;
import com.telogix.telogixcaptain.activities.Fragments.HandbookFragment;
import com.telogix.telogixcaptain.activities.Fragments.HaulierWiseVehicleListFragment;
import com.telogix.telogixcaptain.activities.Fragments.HazardFragment;
import com.telogix.telogixcaptain.activities.Fragments.LiveTrackingFragment;
import com.telogix.telogixcaptain.activities.Fragments.HaulierListFragment;
import com.telogix.telogixcaptain.activities.Fragments.MapBottomSheetDialog;
import com.telogix.telogixcaptain.activities.Fragments.PendingUserFragment;
import com.telogix.telogixcaptain.activities.Fragments.ReplayFragmentDetails_Map;
import com.telogix.telogixcaptain.activities.Fragments.RouteFragment;
import com.telogix.telogixcaptain.activities.Fragments.TutorialsFragment;
import com.telogix.telogixcaptain.activities.Fragments.UploadExcelFragment;
import com.telogix.telogixcaptain.activities.Fragments.VehiclesFragment;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsActivity;
import com.telogix.telogixcaptain.activities.Retailer.VehiclesFragmentRetailer;

import org.json.JSONException;

import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, response_interface, MapBottomSheetDialog.ItemClickListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    Fragment fragment;
    Toolbar toolbar;
    Context context;
    private boolean mLocationPermissionGranted;
    private TokenPojo datum;
    private String notitoken ="";

    public  void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         launchFragment();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu0 = navigationView.getMenu();
        nav_Menu0.findItem(R.id.nav_routes).setVisible(false);
        TextView textUsername=  navigationView.getHeaderView(0).findViewById(R.id.username);
        String response= singleton.getsharedpreference(this).getString("userData", "");
        if(response!="") {
            Gson gson = new Gson();
             datum = gson.fromJson(response, TokenPojo.class);
            textUsername.setText(datum.getUserName());
        }
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
        LinearLayout layoutuser2=navigationView.getHeaderView(0).findViewById(R.id.layoutuser2);
        layoutuser2.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.dashboard));
        String userRole="";
        getNotiToken();
        if(datum!=null)
        {

            if(UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER)
            {
                hideItem();
            }
            if(UserRole.getRole(datum.getRoleID()) != UserRole.SUPER_ADMIN){

                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.pendingusers).setVisible(false);
            }
//            if(UserRole.getRole(datum.getRoleID()) == UserRole.JM)
//            {
//
//
//                Menu nav_Menu = navigationView.getMenu();
////        nav_Menu.findItem(R.id.nav_vehicles).setVisible(false);
//                nav_Menu.findItem(R.id.pendingusers).setVisible(false);
//            }

        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(getSupportFragmentManager().getBackStackEntryCount()==1)
        {
            super.onBackPressed();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
//        MenuItem item =menu.findItem(R.id.action_search);
//        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void hideItem()
    {
       NavigationView navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
//        nav_Menu.findItem(R.id.nav_vehicles).setVisible(false);
        nav_Menu.findItem(R.id.nav_hazards).setVisible(false);
        nav_Menu.findItem(R.id.nav_tutorials).setVisible(false);
        nav_Menu.findItem(R.id.uploadexcel).setVisible(false);
        nav_Menu.findItem(R.id.livetracking).setVisible(false);
        nav_Menu.findItem(R.id.nav_routes).setVisible(false);

        nav_Menu.findItem(R.id.nav_tutorials).setVisible(false);
        nav_Menu.findItem(R.id.nav_handbook).setVisible(false);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard)
        {
           // checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.dashboard));
            }
            getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ChangeFragment(new DashboardFragment());

        }
        if (id == R.id.pendingusers)
        {
         //   checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.pendingusers));
            }
            getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ChangeFragment(new PendingUserFragment());

        }
        else if (id == R.id.nav_vehicles)
        {
          //  checkAuth();

            if(UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER)
            {
                ChangeFragment(new VehiclesFragmentRetailer());
            }
            else {
                replaceFragment(VehiclesFragment.class);
            }
           if (toolbar != null) {
               toolbar.setTitle(getString(R.string.vehicles));
           }
        }
        else if (id == R.id.nav_hazards) {
          //  checkAuth();

            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.hazard));
            }
            ChangeFragment(new HazardFragment());
        } else if (id == R.id.nav_tutorials) {
         //   checkAuth();
            ChangeFragment(new TutorialsFragment());
        }
        else if (id == R.id.nav_handbook) {
         //   checkAuth();
            ChangeFragment(new HandbookFragment());
        }
        else if (id == R.id.nav_logout) {

            logout();

        }
        else if (id == R.id.uploadexcel) {

           // checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.hazard));
            }
            ChangeFragment(new UploadExcelFragment());

        }
        else if (id == R.id.notifications) {
          //  checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.notifications));
            }
            Intent i=new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(i);

        }
        else if (id == R.id.livetracking) {
           // checkAuth();

            if (toolbar != null) {
                toolbar.setTitle("Live Tracking");
            }
            if(datum != null){
                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER){
                    ChangeFragment(new HaulierListFragment());
                }
                else{
                    ChangeFragment(new LiveTrackingFragment());
                }

            }


        }
        else if (id == R.id.replay) {
           // checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.replay));
            }
            if(datum!=null)
            {

                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER)
                {
                    Bundle b = new Bundle();
                    b.putString("flag","replay");
                    ChangeFragment(new HaulierListFragment(),b);
                }
                else
                {
                    ChangeFragment(new HaulierWiseVehicleListFragment());
                }
            }
        }
        else if (id == R.id.nav_routes) {
         //   checkAuth();
            if (toolbar != null) {
                toolbar.setTitle(getString(R.string.routes));
            }
            ChangeFragment(new RouteFragment());

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment (Class<VehiclesFragment> fragmentClass){
        try {
            fragment = fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        String backStateName = fragmentClass.getClass().getName();
        androidx.fragment.app.FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_content, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
public void checkAuth(){

    hideKeyboardFrom();
    HashMap data=new HashMap();
    Log.d("token", singleton.getsharedpreference(this).getString("token",""));
    httpvolley.stringrequestpost("api/Routes/CheckAuth?token="+notitoken, Request.Method.POST,data,this);
}

private void logout(){
    hideKeyboardFrom();
    HashMap data = new HashMap();
    httpvolley.stringrequestpost("api/UserToken/ExpireToken", Request.Method.PUT, data, MainActivity.this);
    OneSignal.deleteTag("userID");
    NetworkConsume.getInstance().Logout(context);
    singleton.getsharedpreference_editor(this).clear().commit();
    Paper.book().delete(Constants.KEY_ROLE_ID);
}
    private void launchFragment(){
        Fragment  fragment = null;

            fragment = new DashboardFragment();

        androidx.fragment.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.dashboard));
        }
    }
    //
    private void ChangeFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();

    }

    public boolean checkPermission()
    {

         mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        return mLocationPermissionGranted;
    }
    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        Log.i("response",response);
        if(response.equals("false")){
            hideKeyboardFrom();
            OneSignal.deleteTag("userID");
            NetworkConsume.getInstance().Logout(context);
            singleton.getsharedpreference_editor(this).clear().commit();
            Paper.book().delete(Constants.KEY_ROLE_ID);
            Toast.makeText(getApplicationContext(), "Another device is using this account", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Log.d("response error:",""+Error);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NetworkConsume.getInstance().hideProgress();
        if (requestCode ==PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }



    @Override
    public void onSummarySwitchClick(boolean item) {
           ReplayFragmentDetails_Map.onSummarySwitchClick(item);


    }

    @Override
    public void onStopSwitchClick(boolean item) {
        ReplayFragmentDetails_Map.onStopSwitchClick(item);
    }

    @Override
    public void onSatelliteMapTypeClick(boolean item) {
        if (item){
            ReplayFragmentDetails_Map.onSatelliteMapTypeClick(true);

        }
    }

    @Override
    public void onDefaultMapTypeClick(boolean item) {
        if (item){
            ReplayFragmentDetails_Map.onDefaultMapTypeClick(true);
        }

    }

    @Override
    public void onTerrainMapTypeClick(boolean item) {
        if (item) {
            ReplayFragmentDetails_Map.onTerrainMapTypeClick(true);
        }
    }

    @Override
    public void onSpeedAdapterItemClick(String i) {
       ReplayFragmentDetails_Map.onSpeedAdapterItemClick( i);
    }
    private String getNotiToken()
    {
        final String[] tok = {""};
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notitoken", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        tok[0] =token;
                        notitoken = token;




                    }
                });
        return tok[0];
    }

}
