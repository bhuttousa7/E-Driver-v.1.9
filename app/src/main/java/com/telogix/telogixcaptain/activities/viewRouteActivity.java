package com.telogix.telogixcaptain.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.AddRoutes;

import org.json.JSONException;

public class viewRouteActivity extends BaseActivity implements OnMapReadyCallback, LocationListener{





        private GoogleMap mMap;
        private LocationManager locationManager;
        double latidude, longitude;
        Button addHazard;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_route);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            addHazard=findViewById(R.id.addHazard);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
//            if(getIntent().hasExtra("orgLat") && getIntent().hasExtra("orgLng") && getIntent().hasExtra("destLat") && getIntent().hasExtra("destLng"))
//            {
//
//            }

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            addHazard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(viewRouteActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to set hazard at this position")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent=new Intent();
                                    intent.putExtra("latitude",""+latidude);
                                    intent.putExtra("longitude",""+longitude);
                                    setResult(3,intent);
                                    finish();
                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }} ).show();
                }
            });
        }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_route;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onLocationChanged(Location location) {
            if (mMap != null) {
                latidude=location.getLatitude();
                longitude=location.getLongitude();
                //      LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
//            mMap.animateCamera(cameraUpdate);
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    Activity#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for Activity#requestPermissions for more details.
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//        locationManager.removeUpdates(this);
            }
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device.
         * This method will only be triggered once the user has installed
         * Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = AddRoutes.staticMap;
mMap.notify();

//            // Add a marker in Sydney and move the camera
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            } else {
//                if (!mMap.isMyLocationEnabled())
//                    mMap.setMyLocationEnabled(true);
//
//                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//                if (myLocation == null) {
//                    Criteria criteria = new Criteria();
//                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                    String provider = lm.getBestProvider(criteria, true);
//                    myLocation = lm.getLastKnownLocation(provider);
//                }
//
//                if (myLocation != null) {
//                    LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
//                }
//            }
//            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//                @Override
//                public void onCameraIdle() {
//                    latidude=mMap.getCameraPosition().target.latitude;
//                    longitude=mMap.getCameraPosition().target.longitude;
//                }
//            });
        }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
