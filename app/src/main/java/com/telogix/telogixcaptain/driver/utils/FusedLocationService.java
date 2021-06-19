package com.telogix.telogixcaptain.driver.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.telogix.telogixcaptain.Utils.Constants;

import java.util.ArrayList;

public class FusedLocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 500, FASTEST_INTERVAL = 500; // = 10 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private float[] mMagnetic;
    private float[] mGravity;
    private float compass_last_measured_bearing;
    private float azimuthInDegress;
    private final float oldDegree=-1;
    private final float difference=0;
    private double latitude,longitude;
    private Handler h;
    private Runnable runnable;

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        initSensors();
         super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    private void initSensors() {


        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mSensorMagneticField = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /* Initialize the gravity sensor */
        if (mSensorGravity != null) {
            Log.i("Sensor", "Gravity sensor available. (TYPE_GRAVITY)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorGravity, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.i("Sensor", "Gravity sensor unavailable. (TYPE_GRAVITY)");
        }

        /* Initialize the magnetic field sensor */
        if (mSensorMagneticField != null) {
            Log.d("Sensor", "Magnetic field sensor available. (TYPE_MAGNETIC_FIELD)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorMagneticField, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.i("Sensor",
                    "Magnetic field sensor unavailable. (TYPE_MAGNETIC_FIELD)");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(h!=null)
        {
            if(runnable!=null)
            {
                h.removeCallbacks(runnable);
            }
          }
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        return super.onUnbind(intent);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
          //  locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);



        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            Intent i = new Intent(Constants.MY_BROADCAST_ACTION_KEY);
            i.putExtra("lat", location.getLatitude());
            i.putExtra("lon", location.getLongitude());
            i.putExtra("bearing", azimuthInDegress);
            sendBroadcast(i);
            Log.d("locChanged",location.getLatitude()+":"+location.getLongitude());
    }

    }


    @Override
    public void onCreate() {
        super.onCreate();

      CountDownTimer countDownTimer=  new CountDownTimer((7 +1) * 1000, 1000) // Wait 5 secs, tick every 1 sec
        {
            @Override
            public final void onTick(final long millisUntilFinished)
            {
            }
            @Override
            public final void onFinish()
            {
//                Intent i = new Intent(Constants.MY_BROADCAST_ACTION_KEY);
//                i.putExtra("lat", latitude);
//                i.putExtra("lon", longitude);
//                i.putExtra("bearing", azimuthInDegress);
//                sendBroadcast(i);
//                this.start();
            }
        };
//      countDownTimer.start();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);





        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
           Log.d("accuracy",""+accuracy);
        }

        float[] mGravity;
        float[] mGeomagnetic;
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                        mGeomagnetic);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);

                    float azimuthInRadians = orientation[0];
                     azimuthInDegress = (float)Math.toDegrees(azimuthInRadians)+360%360;
                    //  Log.d("compass",azimuthInDegress+"");




                    }

                }
            }

    };
}
