package com.telogix.telogixcaptain.driver.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.telogix.telogixcaptain.Utils.Constants;

public class MyService_copy extends Service {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private static final long LOCATION_INTERVAL = Constants.locationUpdateInterval;
    private static final float LOCATION_DISTANCE = 10f;
    //    LocationListener[] mLocationListeners = new LocationListener[]{
//            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
//    };
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private boolean firstTimeFlag;
    private LocationCallback mLocationCallback;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        // initializeLocationManager();
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

    }

    @SuppressLint("MissingPermission")
    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setFastestInterval(15000);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() == null)
                    return;
                currentLocation = locationResult.getLastLocation();
                Intent i = new Intent(Constants.MY_BROADCAST_ACTION_KEY);
                i.putExtra("lat", currentLocation.getLatitude());
                i.putExtra("lon", currentLocation.getLongitude());
                i.putExtra("bearing", currentLocation.getBearing());
                sendBroadcast(i);
                // Toast.makeText(this,""+currentLocation.getLatitude(),Toast.LENGTH_SHORT).show();

            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onDestroy() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        return super.onUnbind(intent);

    }
    //    private void initializeLocationManager() {
//        Log.e(TAG, "initializeLocationManager");
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        }
//    }

}