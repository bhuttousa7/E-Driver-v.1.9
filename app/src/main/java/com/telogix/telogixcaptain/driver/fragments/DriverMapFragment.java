package com.telogix.telogixcaptain.driver.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.offline_response_interface;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Log.LogBook;
import com.telogix.telogixcaptain.Pojo.Datum;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.DecantStaffPojo;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.PreLoadInspPojo.PreLoadInsPojo;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.InternetService;
import com.telogix.telogixcaptain.Utils.MapWrapperLayout;
import com.telogix.telogixcaptain.Utils.OfflineData;
import com.telogix.telogixcaptain.Utils.RotatingLinearLayout;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.Briefing.BriefingFragment;
import com.telogix.telogixcaptain.activities.Fragments.RouteDetailFragment;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsManager;
import com.telogix.telogixcaptain.adapters.CustomInfoAdapter;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.activities.ResumeProcess;
import com.telogix.telogixcaptain.driver.activities.ResumeRideActivity;
import com.telogix.telogixcaptain.driver.models.DriverVehicleStatusResponse;
import com.telogix.telogixcaptain.driver.utils.BackgroundLocationManager;
import com.telogix.telogixcaptain.driver.utils.BackgroundLocationManagertwo;
import com.telogix.telogixcaptain.driver.utils.BackgroundLocationSpeedBroadcast;
import com.telogix.telogixcaptain.driver.utils.Drivers;
import com.telogix.telogixcaptain.driver.utils.InternetCheckingManager;
import com.telogix.telogixcaptain.driver.utils.OnInfoWindowTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;
import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;
import static com.telogix.telogixcaptain.Log.LogBook.getCurrentDate;

public class DriverMapFragment extends Fragment implements Serializable, OnMapReadyCallback, response_interface, offline_response_interface, InternetService.ConnectionServiceCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    DriverVehicleStatusResponse responseObj = null;

    GoogleMap mMap;
    private PolylineOptions opts = null;
    Marker currentPosMarker;
    DatabaseReference dbRef;
    boolean reached = false;
    final static float ALPHA = (float) 0.33;
    private MapWrapperLayout mapWrapperLayout;
    private OnInfoWindowTouchListener infoButtonListener;
    ArrayList<Hazards> completeHazard = new ArrayList<>();
    static RouteDetail CurrentStop = null;
    private final int DECANTING_DISTANCE=200;
//    Handler handler = new Handler();
//    Runnable runnable = null;
    private static final int saveLiveLocationTime = 10000;
    public static com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum CurrentFuelStop = null;
    Double PreviosLatitude = 0.0, PreviousLongitude = 0.0;
    ArrayList<Marker> hazardsShowinglist = new ArrayList();
    private RotatingLinearLayout rotatingLinearLayout;
    private boolean Playing;
    float speedinKph =0;
    private MediaPlayer mpintro = new MediaPlayer();
    double NortPoleLat = 90.0000, NorthPoleLong = 135.0000;
    public static Routes CurrentRoute = null;
    private ArrayList<Hazards> NearestHazards = new ArrayList<>();
    public static ArrayList<com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> DecantingList = new ArrayList<>();
    public static ArrayList LogArrayList = new ArrayList();
    Button btnaddstop;
    private final BroadcastReceiver SpeedChecker = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           double  latitude = intent.getDoubleExtra("lat", 0.0);
           double longitude = intent.getDoubleExtra("lon", 0.0);
           float speed = intent.getFloatExtra("speed",0);
            speedinKph = (speed * 18)/5;
            try {
                if(PreviosLatitude==0.0 && PreviousLongitude==0.0){
                    PreviosLatitude=latitude;
                    PreviousLongitude=longitude;
                }
                else{
                    float[] movedResult = new float[1];
                    Location.distanceBetween(PreviosLatitude, PreviousLongitude, latitude, longitude, movedResult);
                     MovedDistance=movedResult[0];
                    if (MovedDistance > 30) {
                        blackback.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        blackback.setVisibility(View.GONE);
                    }
                    PreviosLatitude=latitude;
                    PreviousLongitude=longitude;
                }

            } catch (Exception ex) {

            }
            // Black Screen Check if Vehicle is moving


        }
    };
    private final BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lat = intent.getDoubleExtra("lat", 0.0);
            lon = intent.getDoubleExtra("lon", 0.0);
            try {
                CheckStop(lat, lon);
            } catch (Exception ex) {

            }
            // Black Screen Check if Vehicle is moving
            float[] movedResult = new float[1];
            Location.distanceBetween(PreviosLatitude, PreviousLongitude, lat, lon, movedResult);


//            if (MovedDistance < 15) {
//                blackback.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                    blackback.setVisibility(View.GONE);
//            }
// Black screen end

            if (mpintro.isPlaying()) {

            } else {
                if (NearestHazards.size() <= 0) {
                    if (mMap != null) {
                        NearestHazards = new ArrayList();
//                        NearestHazards.addAll(completeHazard);    // For Nearest Hazards


                        for (int i = 0; i < completeHazard.size(); i++) {
                            // for hazards relative to the car
                            float[] results = new float[1];
                            Location.distanceBetween(lat, lon, Double.parseDouble(completeHazard.get(i).getLatitude() + ""), Double.parseDouble(completeHazard.get(i).getLongitude() + ""), results);
                            float distance = results[0];
                            if (distance < Constants.stopMarkerRadius) {
                                NearestHazards.add(completeHazard.get(i)); //

                            }
                            //for route hazards only
//                            if (opts != null) {
//
//
//                                if (PolyUtil.isLocationOnPath(new LatLng(Double.parseDouble(completeHazard.get(i).getLatitude() + ""), Double.parseDouble(completeHazard.get(i).getLongitude() + "")), opts.getPoints(), opts.isGeodesic(), Constants.driverHazardsRadius)) {
////                                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(completeHazard.get(i).getLatitude() + ""), Double.parseDouble(completeHazard.get(i).getLongitude() + ""))).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));
//                                    NearestHazards.add(completeHazard.get(i));
//                                }
//
//                            }
                            //    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));
                        }
                    }
                }

                //  Toast.makeText(getContext(),"Lat:"+lat+" Lng:"+lon,Toast.LENGTH_SHORT).show();
//            Double lat = intent.getDoubleExtra("lat", 0.0);
//            Double lon = intent.getDoubleExtra("lon", 0.0);
                Float bearing = intent.getFloatExtra("bearing", 0.0f);

                if (currentPosMarker != null) {
                    if (lat != 0.0 && lon != 0.0) {
                        //animateMarkerNew(lat, lon, currentPosMarker, bearing);
                    } else {
                        //   animateMarkerNew(currentPosMarker.getPosition().latitude, currentPosMarker.getPosition().longitude, currentPosMarker, bearing);
                    }
                } else {
                    // currentPosMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(50, 50, R.drawable.navigation))).position(new LatLng(lat, lon)));
                }
                //   Toast.makeText(getContext(),""+lat+","+lon,Toast.LENGTH_SHORT).show();
                VehicleLiveLocation vehicleLiveLocation = new VehicleLiveLocation();
                vehicleLiveLocation.vehicleName = responseObj.data.vehicleNo;
                vehicleLiveLocation.latlng = lat + "," + lon;
                Log.i("--DriverLocation:",vehicleLiveLocation.latlng);
                vehicleLiveLocation.haulierID = String.valueOf(new Datum().getHaulierID());
                vehicleLiveLocation.vehicleNo =responseObj.data.vehicleID.toString();
                vehicleLiveLocation.bearing = bearing + "";
                int month  = Calendar.MONTH;
                SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yy HH:mm:ss");
                vehicleLiveLocation.time = df.format(Calendar.getInstance().getTime());
                vehicleLiveLocation.deviceId = new Constant(getContext()).getAndroid_id();
                vehicleLiveLocation.setSpeedinKph(speedinKph);

//                int month  = Calendar.MONTH;
//                SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yy HH:mm:ss");
//                vehicleLiveLocation.time =  String.valueOf(df.format(Calendar.getInstance().getTime()));

                try {

                    dbRef.child("liveVehicles").child(new Constant(getContext()).getAndroid_id()).setValue(vehicleLiveLocation);
                        SaveLiveLocation(vehicleLiveLocation);



//                    runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.i("--run","fired");
//                            SaveLiveLocation(vehicleLiveLocation);
//                            handler.postDelayed(this::run,saveLiveLocationTime);
//                        }
//
//                    };
                } catch (Exception e) {
                    Log.d("error", e.getLocalizedMessage());
                }
                try {
                    if (mMap != null) {
                        if (opts != null) {
                            if (PreviosLatitude == 0.0 && PreviousLongitude == 0.0) {
//                                PreviosLatitude = lat;
//                                PreviousLongitude = lon;
//                            NearestHazards = new ArrayList(); //
                                for (int i = 0; i < completeHazard.size(); i++) {
                                    float[] results = new float[1];
                                    Location.distanceBetween(lat, lon, Double.parseDouble(completeHazard.get(i).getLatitude() + ""), Double.parseDouble(completeHazard.get(i).getLongitude() + ""), results);
                                    float distance = results[0];
//                                if (distance < Constants.stopMarkerRadius) {
//                                    NearestHazards.add(completeHazard.get(i)); //
//
//                                }

                                }
                            } else {

//                                Location.distanceBetween(PreviosLatitude, PreviousLongitude, lat, lon, movedResult);
//                                MovedDistance = movedResult[0];

//                            if (MovedDistance > 15) {
//                                for (int i = 0; i < completeHazard.size(); i++) {
//                                    float[] results = new float[1];
//                                    Location.distanceBetween(lat, lon, Double.parseDouble(completeHazard.get(i).getLatitude() + ""), Double.parseDouble(completeHazard.get(i).getLongitude() + ""), results);
//                                    float distance = results[0];
//                                    if (distance < Constants.stopMarkerRadius) {
//
////                                        NearestHazards.add(completeHazard.get(i)); //
//
//                                    }
//                                }



                                CheckingAngleBasedMarker(lat, lon);

//                            }
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        checkAudioAvailability((Hazards) marker.getTag());
                                    }
                                });

                            }


                        }


                        //   mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };



    private static ArrayList<RouteDetail> ALLSTOPSLIST;
    public static boolean reacheddecanting;
    private ArrayList<RouteDetail> Allpoints = new ArrayList<>();
    private static boolean zoomed;
    private boolean alertShowing;
    private TextView txtSOS;
    private RelativeLayout blackback;
    private TextView txtofflineMode;
    private CountDownTimer countDownTimer;
    private float MovedDistance;

    private void SaveLiveLocation(VehicleLiveLocation vl){
        //Save Live Location to database
        //call web api
        //wazir

        String[] latlngarray = vl.latlng.split(",");
        if(latlngarray.length > 1){
            Log.w(latlngarray[0], latlngarray[1]);

            HashMap data = new HashMap();
            data.put("VehicleID",vl.vehicleNo);
            data.put("Latitude", latlngarray[0]);
            data.put("Longitude", latlngarray[1]);
            data.put("Date",vl.time);
            data.put("DeviceID",vl.deviceId);

            data.put("Speed",String.valueOf(Double.valueOf(vl.getSpeedinKph())));
            Log.i("--tdata",data.toString());
            Log.i("--speed",String.valueOf(Double.valueOf(vl.getSpeedinKph())));
            if(Constants.internetConnected) {
                httpvolley.stringrequestpost("api/TrackingData/SaveData", Request.Method.POST, data, (response_interface) this.getContext());
            }
            else
            {
                HashMap<String,HashMap> hashMap=new HashMap<>();
                hashMap.put("api/TrackingData/SaveData",data);
                OfflineData.offlineData.add(hashMap);
            }
        }




    }


    private void calculateSpeed(double lat, double lon){

    }
    private void CheckStop(double lat, double lon) {
        try {
            if (responseObj != null) {
                float[] destinationdistance = new float[1];
                if (lat != 0.0 && lon != 0.0) {
                    //Other Stop Check
                    for (int i = 0; i < ALLSTOPSLIST.size(); i++) {
                        Location.distanceBetween(lat, lon, ALLSTOPSLIST.get(i).getLatitude(), ALLSTOPSLIST.get(i).getLongitude(), destinationdistance);
                        float distance = destinationdistance[0];
                        String alertTitle = "", alertMessage = "";
                        if (distance < DECANTING_DISTANCE) {
                            if (ALLSTOPSLIST.get(i).getRouteTypeID().equals("4")) {
                                continue;
//                     alertTitle="Reached Fuel Station";
//                      alertMessage="You have reached your decanting station.You may stop ride";
//                    btnEndRide.setText("STOP RIDE");
//                    btnEndRide.setVisibility(View.VISIBLE);

                            } else if (ALLSTOPSLIST.get(i).getRouteTypeID().equals("6")) {
                                alertTitle = "Reached Rest Station";
                                alertMessage = "You have reached your Rest stop.You may stop ride";
                                btnEndRide.setText("STOP RIDE");

                                btnEndRide.setVisibility(View.VISIBLE);

                            } else if (ALLSTOPSLIST.get(i).getRouteTypeID().equals("5")) {
                                alertTitle = "Reached Restaurant ";
                                alertMessage = "You have reached your meal stop.You may stop ride";
                                btnEndRide.setText("STOP RIDE ");

                                btnEndRide.setVisibility(View.VISIBLE);
                            } else {
                                alertTitle = "Reached Stop ";
                                alertMessage = "You have reached your Stop";
                                btnEndRide.setText("STOP RIDE");
                                btnEndRide.setVisibility(View.VISIBLE);
                            }
                            btnEndRide.setVisibility(View.VISIBLE); //
                            if (!ALLSTOPSLIST.get(i).isNotified()) {

                                int finalI = i;
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                                        .setTitle(alertTitle)
                                        .setMessage(alertMessage)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
//                                  if(ALLSTOPSLIST.size()>0)
//                                  {
//                                      CurrentStop=ALLSTOPSLIST.get(finalI);
//
//                                      ALLSTOPSLIST.remove(finalI);
//                                  }
                                                dialog.dismiss();
                                            }
                                        });
                                dialog.setCancelable(false);
                                dialog.show();
                                if (ALLSTOPSLIST.size() > 0) {
                                    CurrentStop = ALLSTOPSLIST.get(finalI);
                                    ALLSTOPSLIST.remove(finalI);
                                }
                                reacheddecanting = true;
                            }
                        } else {

                            reacheddecanting = false;
                            btnEndRide.setVisibility(View.GONE);
                        }
                    }

                    //Decanting List
                    for (int k = 0; k < DecantingList.size(); k++) {
                        Location.distanceBetween(lat, lon, DecantingList.get(0).getDecantingLatitude(), DecantingList.get(0).getDecantingLongitude(), destinationdistance);
                        float distance = destinationdistance[0];
                        String alertTitle = "", alertMessage = "";
                        if (distance < DECANTING_DISTANCE) {
                            alertTitle = "Reached Fuel Station";
                            alertMessage = "You have reached your decanting station.You may stop ride";
                            btnEndRide.setText("STOP RIDE");
                            btnEndRide.setVisibility(View.VISIBLE);
                            reacheddecanting = true;
                            btnEndRide.setVisibility(View.VISIBLE); //
                            if (DecantingList.size() == 1) {
                                CurrentFuelStop = DecantingList.get(0);
                                if (!reached) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                                            .setTitle("Reached Destination")
                                            .setMessage("You have reached your destination,decant and end ride")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    CurrentFuelStop = DecantingList.get(0);
                                                    alertShowing = false;
                                                    dialog.dismiss();
                                                }
                                            });
                                    dialog.setCancelable(false);
                                    if (!alertShowing) {
                                        dialog.show();
                                    }
                                    alertShowing = true;
                                }
                                btnEndRide.setText("END RIDE");
                                btnEndRide.setVisibility(View.VISIBLE);
                                reached = true;
                            } else {
                                if (!DecantingList.get(0).isNotified()) {
                                    int finalI = 0;

                                    int finalK = k;
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                                            .setTitle(alertTitle)
                                            .setMessage(alertMessage)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    if (DecantingList.size() > finalI) {
                                                    CurrentFuelStop = DecantingList.get(finalK);
//                                                        DecantingList.remove(finalI);
//                                                    }

                                                    reacheddecanting = true;
                                                    DecantingList.get(finalI).setNotified(true);
                                                    btnEndRide.setText("STOP RIDE");
                                                    btnEndRide.setVisibility(View.VISIBLE);
                                                    alertShowing = false;
                                                    dialog.dismiss();

                                                }
                                            });
                                    dialog.setCancelable(false);
                                    if (!alertShowing) {
                                        dialog.show();
                                    }

                                    alertShowing = true;

                                }
                            }
                            break;
                        } else {

                            reacheddecanting = false;
                            if (!reacheddecanting) {
                                btnEndRide.setVisibility(View.GONE);
                            }
                        }
                    }

if(CurrentStop!=null)
{
    btnEndRide.setVisibility(View.VISIBLE);
}

//        if(!reacheddecanting) {
//    Location.distanceBetween(lat, lon, responseObj.getData().destinationLat, responseObj.getData().destinationLong, destinationdistance);
//    float distance = destinationdistance[0];
//    if (distance < 400) {
//        btnEndRide.setVisibility(View.VISIBLE); //
//        if (!reached) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
//                    .setTitle("Reached Destination")
//                    .setMessage("You have reached your destination,decant and end ride")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            CurrentFuelStop = DecantingList.get(DecantingList.size()-1);
//                            dialog.dismiss();
//                        }
//                    });
//
//            dialog.show();
//            reached = true;
//
//        }
//    } else {
//        btnEndRide.setVisibility(View.GONE);
//    }
//}
//
                }
            }
        } catch (Exception ex) {

        }
    }

    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                lat = intent.getDoubleExtra("lat", 0.0);
                lon = intent.getDoubleExtra("lon", 0.0);
                Float bearing = intent.getFloatExtra("bearing", 0.0f);

                if (currentPosMarker != null) {
                    if (lat != 0.0 && lon != 0.0) {
                        animateMarkerNew(lat, lon, currentPosMarker, bearing);
                    } else {
                        animateMarkerNew(currentPosMarker.getPosition().latitude, currentPosMarker.getPosition().longitude, currentPosMarker, bearing);
                    }
                } else {
                    currentPosMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(50, 50, R.drawable.navigation))).position(new LatLng(lat, lon)));
                }
            } catch (Exception ex) {
            }
        }
    };

    private final BroadcastReceiver mServiceRecieverAllTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                lat = intent.getDoubleExtra("lat", 0.0);
                lon = intent.getDoubleExtra("lon", 0.0);
                Float bearing = intent.getFloatExtra("bearing", 0.0f);

                if (currentPosMarker != null) {
                    if (lat != 0.0 && lon != 0.0) {
                        animateMarkerNew(lat, lon, currentPosMarker, bearing);
                    } else {
                        animateMarkerNew(currentPosMarker.getPosition().latitude, currentPosMarker.getPosition().longitude, currentPosMarker, bearing);
                    }
                } else {
                    currentPosMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(50, 50, R.drawable.navigation))).position(new LatLng(lat, lon)));
                }
            } catch (Exception ex) {
            }
        }
    };
    // checking internet connection periodically
    private final BroadcastReceiver mServiceInternetConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getBooleanExtra("internet", true)) {
                    txtofflineMode.setVisibility(View.GONE);
                    if (!Constants.internetConnected) {
                        if (OfflineData.offlineData.size() > 0) {
                            sync_OfflineData();
                        }
                    }
                    Constants.internetConnected = true;
                } else {
                    //no connection to internet
                    txtofflineMode.setVisibility(View.VISIBLE);
                    if (Constants.internetConnected) {
                        showInternetDialog(false);

                    }
                    Constants.internetConnected = false;


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private double lon, lat;
    private boolean mapLoaded = false;
    private Button btnEndRide;
    private ArrayList markersShowing = new ArrayList();
    private Hazards CurrentPlayed = null;

    private void CheckingAngleBasedMarker(double OrgLat, double OrgLng) {

        if (mapLoaded) {
            for (int i = 0; i < hazardsShowinglist.size(); i++) {
                Marker m = hazardsShowinglist.get(i);
                if (m != null) {
                    m.setVisible(false);
                    m.remove();
                }
            }
            LatLng NorthWestLatlng = mMap.getProjection().getVisibleRegion().farLeft;
            LatLng NorthEastLatLng = mMap.getProjection().getVisibleRegion().farRight;
            LatLng center = new LatLng(OrgLat, OrgLng);
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(center);
            boundsBuilder.include(NorthWestLatlng);
            boundsBuilder.include(NorthEastLatLng);
            final LatLngBounds latLngBounds = boundsBuilder.build();

            markersShowing = new ArrayList();
            for (int i = 0; i < NearestHazards.size(); i++) {

                double hazardlat = Double.parseDouble(NearestHazards.get(i).getLatitude() + "");
                double hazardlng = Double.parseDouble(NearestHazards.get(i).getLongitude() + "");
                if (latLngBounds.contains(new LatLng(hazardlat, hazardlng))) {
                    loadMarkerInfo(NearestHazards.get(i));
                    markersShowing.add(NearestHazards.get(i));
                }
            }
            sortLocations(markersShowing, lat, lon);
            // currentPosMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(50, 50, R.drawable.navigation))).position(new LatLng(lat, lon)));
        }
    }

    public void sortLocations(ArrayList<Hazards> hazardslist, final double myLatitude, final double myLongitude) {
        try {
            float distance = -1;
            Hazards hazards = null;
            hazards = new Hazards();
            for (int i = 0; i < hazardslist.size(); i++) {


                float[] result = new float[1];
                double hazardlat = Double.parseDouble(hazardslist.get(i).getLatitude() + "");
                double hazardlong = Double.parseDouble(hazardslist.get(i).getLongitude() + "");
                Location.distanceBetween(myLatitude, myLongitude, hazardlat, hazardlong, result);
                if (distance == -1) {
                    distance = result[0];
                    hazards = hazardslist.get(i);
                } else {
                    if (result[0] < distance) {
                        distance = result[0];
                        hazards = hazardslist.get(i);
                    }
                }
            }
            if (hazards != null) {
                float[] movedResult = new float[1];
                Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(hazards.getLatitude() + ""), Double.parseDouble(hazards.getLongitude() + ""), movedResult);
                if (movedResult[0] < 700) {
                    showMarkerWithAudio(hazards);
                }
            }
        } catch (Exception ex) {

        }
    }

    private void checkAudioAvailability(Hazards infoWindowData) {

        String AudioSavePathInDevice = "";
        try {


            if (infoWindowData.getHazardDetails().size() > 0) {
                if (infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl() != "" && infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl() != null) {
                    // play.setVisibility(View.VISIBLE);

                }
                if (infoWindowData.getHazardDetails().size() > 1) {
                    AudioSavePathInDevice = infoWindowData.getHazardDetails().get(1).getImageUrl();
                } else {
                    //  play.setVisibility(View.GONE);
                    //markerShowingInfoWindow.hideInfoWindow();
                    //markerShowingInfoWindow.showInfoWindow();
                }
            }
        } catch (Exception ex) {

        }
        try {
            if (!mpintro.isPlaying()) {
                if (AudioSavePathInDevice != "") {
                    mpintro = new MediaPlayer();
                    mpintro.setDataSource(connection.BaseurlOLD + AudioSavePathInDevice);
                    mpintro.prepare();
                    Playing = true;
                    mpintro.setLooping(false);
                    mpintro.start();
                    // play.setBackground(getContext().getResources().getDrawable(R.drawable.stop));
                    mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            CurrentPlayed = infoWindowData;
                            //   play.setBackground(getContext().getResources().getDrawable(R.drawable.btnplay));
                            Playing = false;
                        }
                    });
                } else {

                    Toast.makeText(getContext(), "No audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mpintro != null) {
                    if (mpintro.isPlaying()) {
                        mpintro.stop();
                        mpintro.release();


                    }
                }
            }
        } catch (Exception ex) {
            Log.d("tag", "" + ex);
            // Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
        }
    }

    private void loadMarkerInfo(Hazards hazards) {

        CustomInfoAdapter customInfoWindow = new CustomInfoAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(hazards.getLatitude() + ""), Double.parseDouble(hazards.getLongitude() + ""))).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));


        m.setTag(hazards);

        hazardsShowinglist.add(m);
        // m.showInfoWindow();

    }

    private void showMarkerWithAudio(Hazards hazards) {

        CustomInfoAdapter customInfoWindow = new CustomInfoAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(hazards.getLatitude() + ""), Double.parseDouble(hazards.getLongitude() + ""))).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));
        m.setTag(hazards);
        hazardsShowinglist.add(m);
        m.showInfoWindow();
        if (CurrentPlayed != null) {
            if (CurrentPlayed.getHazardName() == hazards.getHazardName()) {

            } else {
                checkAudioAvailability((Hazards) m.getTag());

            }
        } else {
            checkAudioAvailability((Hazards) m.getTag());
        }
    }

    private boolean mLocationPermissionGranted;


    public DriverMapFragment() {
        // Required empty public constructor
    }


    //---------------------------------------------
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private boolean firstTimeFlag;
    private LocationCallback mLocationCallback;

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(getContext(), "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDecantingInspectionList();
        getActivity().setTitle("Live Tracking");
        getAllHazards();
        getAllDecantingStations();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        }
        if (reacheddecanting) {
            btnEndRide.setText("Stop Ride");
            btnEndRide.setVisibility(View.VISIBLE);
        }
        if (reached) {
            btnEndRide.setText("End Ride");
            btnEndRide.setVisibility(View.VISIBLE);
        }
    }

    private void getAllDecantingStations() {

        HashMap hashMap = new HashMap();
        hashMap.put("LoadID", "" + responseObj.data.getLoadID());
        httpvolley.stringrequestpost("api/DecantingSites/GetDecantingSiteByLoadID?LoadID=" + responseObj.getData().getLoadID().toString(), Request.Method.GET, hashMap, this);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mServiceReceiver.isOrderedBroadcast()) {
            mServiceReceiver.setResultCode(RESULT_OK);
        }
        //   mServiceReceiver.abortBroadcast();
//        if (fusedLocationProviderClient != null)
//            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }


    //-------------------------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                final androidx.appcompat.app.AlertDialog.Builder builder =
                        new androidx.appcompat.app.AlertDialog.Builder(getContext());

                final String message = "Enable either GPS or any other location"
                        + " service to find current location.  Click OK to go to";

                builder.setCancelable(false);
                builder.setMessage(message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {

                                        d.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        d.cancel();
                                    }
                                });
//                builder.create().show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (mLocationPermissionGranted) {

        }
        try {
            responseObj = (DriverVehicleStatusResponse) getArguments().getSerializable("data");
            ALLSTOPSLIST = new ArrayList();
            for (int i = 0; i < responseObj.data.routeDetails.size(); i++) {
                if (!responseObj.data.routeDetails.get(i).getRouteTypeID().equals("1")) {
                    ALLSTOPSLIST.add(responseObj.data.routeDetails.get(i));
                }
            }
            dbRef = FirebaseDatabase.getInstance().getReference();
        } catch (Exception ex) {

        }


    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getContext(), InternetService.class));
        super.onDestroy();

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements Serializable, DriverMapFragment.LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    private void animateMarkerNew(double currentlatitude, double currentlongitude, final Marker marker, final float bearing) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(currentlatitude, currentlongitude);

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(300); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        if (zoomed) {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .bearing(bearing)
                                    .tilt(60f)
                                    .target(newPosition)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build()));

                        } else {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .bearing(bearing)
                                    .tilt(60f)
                                    .target(newPosition)
                                    .zoom(17f)
                                    .build()));
                            zoomed = true;
                        }

                        marker.isFlat();
                        //   marker.setRotation(getBearing(startPosition, new LatLng(currentlatitude, currentlongitude)));
                        // marker.setRotation(bearing);

                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);


                }
            });
            valueAnimator.start();
        }
    }

    public static float exponentialSmoothing(float input, float output, float alpha) {
        output = output + alpha * (input - output);
        return output;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_map, container, false);
        getActivity().setTitle("LIVE TRACKING");
        blackback = view.findViewById(R.id.blackback);
        blackback.setVisibility(View.GONE);
        btnEndRide = view.findViewById(R.id.btnEndRide);
        btnaddstop = view.findViewById(R.id.btnaddstop);
        txtofflineMode = view.findViewById(R.id.txtofflineMode);
        LinearLayout layouthazards, layoutroutedetails, layoutlogbook;
        layouthazards = view.findViewById(R.id.layouthazards);
        layoutlogbook = view.findViewById(R.id.layoutlogbook);
        layoutroutedetails = view.findViewById(R.id.layoutroutes);
        txtSOS = view.findViewById(R.id.txtSOS);
        txtSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationsManager.sendNotification(getContext(), "SOS ALERT", "SOS ALERT at:"+getCurrentDate(), "" + responseObj.data.vehicleID);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Notification has been sent to JM and Retailer")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
//                                try {
//                                 //   OneSignal.postNotification(new JSONObject("{'contents': {'en':'Test Message'}, 'include_player_ids': ['" + userId + "']}"), null);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        });
        btnaddstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeFragment(new AddStopDriver(), v, new Bundle());
            }
        });
        layouthazards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("routeid", responseObj.data.routeID);
                b.putString("vehicleNo", "" + responseObj.data.vehicleID);
                b.putString("vehicleType", responseObj.data.vehicleStatus);
                b.putString("loadID", "" + responseObj.data.getLoadID());
                ChangeFragment(new BriefingFragment(), null, v, b);
            }
        });
        layoutroutedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                if (responseObj.data.originLat != null && responseObj.data.originLong != null && responseObj.data.destinationLat != null && responseObj.data.destinationLong != null) {
                    b.putDouble("orgLat", responseObj.data.originLat);
                    b.putDouble("orgLng", responseObj.data.originLong);
                    b.putDouble("destLat", responseObj.data.destinationLat);
                    b.putDouble("destLng", responseObj.data.destinationLong);
                    b.putString("RouteName", responseObj.data.routeName);
                    b.putBoolean("AssignRoute", false);
                    b.putBoolean("EditRoute", false);
                    b.putBoolean("Briefing", false);
                    b.putBoolean("fromDriverMap", true);


                } else {

                }
                ChangeFragment(new RouteDetailFragment(), v, b);

//                Bundle b = new Bundle();
//                b.putString("routeid", responseObj.data.routeID);
//                b.putString("vehicleNo", "" + responseObj.data.vehicleID);
//                b.putString("vehicleType", responseObj.data.vehicleStatus);
//                ChangeFragment(new BriefingFragment(), null, v, b);
            }
        });
        layoutlogbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeFragment(new LogFragment(), v, new Bundle());
            }
        });
        btnEndRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Drivers.getDriverCount(getContext()) == 2) {
                        ShowDriverDialog();
                    } else {
                        AddStoporEndRide();
                    }

                } catch (Exception ex) {

                }

            }
        });
        MapsInitializer.initialize(getContext().getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private void AddStoporEndRide() {
        if (CurrentStop != null) {

            if (CurrentStop.getRouteTypeID().equals("4")) {

            } else if (CurrentStop.getRouteTypeID().equals("5")) {
                new LogBook(getContext()).updateLogBook("Stopped at Restaurant");
                StopRide("" + responseObj.data.vehicleID, "8");


            } else if (CurrentStop.getRouteTypeID().equals("6")) {
                new LogBook(getContext()).updateLogBook("Stopped at Rest Area");
                StopRide("" + responseObj.data.vehicleID, "7");
            }
        }

        if (CurrentFuelStop != null) {
            new LogBook(getContext()).updateLogBook("Stopped at Decanting Station");
            try {
                if (mServiceReceiver != null) {
                    //    mServiceReceiver.abortBroadcast();
                }
                if (mServiceRecieverAllTime != null) {

                    // mServiceRecieverAllTime.abortBroadcast();
                }
            } catch (Exception ex) {

            }
            Intent i = new Intent(getContext(), ResumeProcess.class);
            i.putExtra("VehicleID", "" + responseObj.data.vehicleID);
            i.putExtra("DecantingSiteID", "" + CurrentFuelStop.getLoadDecantingSiteID());
            i.putExtra("RouteAssignID", "" + responseObj.getData().getRouteAssignID());
            i.putExtra("reached", reached);
            startActivity(i);

        } else {
//                        EndRideRequest();
        }
    }

    private void ShowDriverDialog() {
        try {
            final ArrayList<HashMap<String, String>> driverslist = Drivers.getDrivers(getContext());
            final CharSequence[] charSequence = new CharSequence[2];
            if (Drivers.getDrivers(getContext()) != null) {


                charSequence[0] = driverslist.get(0).get("DriverName");
                charSequence[1] = driverslist.get(1).get("DriverName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select Driver")
                    //.setMessage("You can buy our products without registration too. Enjoy the shopping")
                    .setSingleChoiceItems(charSequence, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            singleton.getsharedpreference_editor(getContext()).putString("token", driverslist.get(which).get("Driver")).commit();
                            AddStoporEndRide();
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false)

            ;
            builder.create().show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

            }
            if (resultCode == RESULT_CANCELLED) {
                //handle cancel
            }
        }
    }

    private void ChangeFragment(Fragment fragment, View v, Bundle b) {
        fragment.setArguments(b);

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

    private void EndRideRequest() {
        HashMap data = new HashMap();
        String url = "api/VehicleRideDrivers/EndJourney?DeviceID=" + new Constant(getContext()).getAndroid_id();
        Log.d("RequestUrl", url);
        httpvolley.stringrequestpost(url, Request.Method.GET, data, this);
    }

    private void ChangeFragment(Fragment fragment, Datum body, View v, Bundle bundle) {
        Bundle b = new Bundle();
        if (body != null) {
            if (body.getLoadID() != null) {
                b.putString("vehicleID", "" + body.getLoadID());

                fragment.setArguments(b);

            } else {
                Toast.makeText(getContext(), "invalid vehicle id ", Toast.LENGTH_SHORT).show();
            }
        } else {

            fragment.setArguments(bundle);
        }
        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        InternetCheckingManager.getInstance().startService(getContext());
        try {

            // for hazard
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.MY_BROADCAST_ACTION_KEY);
            getActivity().registerReceiver(mServiceReceiver, filter);
            // for internet connection
            IntentFilter internetfilter = new IntentFilter();
            internetfilter.addAction(Constants.MY_INTERNET_ACTION_KEY);
            getActivity().registerReceiver(mServiceInternetConnection, internetfilter);
            //
            IntentFilter filterAlltime = new IntentFilter();
            filterAlltime.addAction(Constants.MY_BROADCAST_ACTION_KEY_All_Time);
            getActivity().registerReceiver(mServiceRecieverAllTime, filterAlltime);
// Broadcast for notifications
            filterAlltime = new IntentFilter();
            filterAlltime.addAction(Constants.NOTIFICATION_BROADCAST_KEY);
            getActivity().registerReceiver(mNotificationReceiver, filterAlltime);

            filterAlltime = new IntentFilter();
            filterAlltime.addAction(Constants.MY_BROADCAST_SPEED_CHECK);
            getActivity().registerReceiver(SpeedChecker, filterAlltime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            if (mServiceReceiver != null) {
                if(mServiceReceiver.isOrderedBroadcast()){
                    mServiceReceiver.setResultCode(RESULT_OK);
                    mServiceReceiver.abortBroadcast();

                }


            }
            if (mServiceRecieverAllTime != null) {
                if(mServiceRecieverAllTime.isOrderedBroadcast()){
                    mServiceRecieverAllTime.setResultCode(RESULT_OK);
                    mServiceRecieverAllTime.abortBroadcast();
                    InternetCheckingManager.getInstance().stopService(getContext());                }

            }
            if (mServiceInternetConnection != null) {

                if(mServiceInternetConnection.isOrderedBroadcast()) {
                    mServiceInternetConnection.setResultCode(RESULT_OK);
                    mServiceInternetConnection.abortBroadcast();
                }
            }
            if(mNotificationReceiver!=null){
                if(mNotificationReceiver.isOrderedBroadcast()) {
                    mNotificationReceiver.setResultCode(RESULT_OK);
                    mNotificationReceiver.abortBroadcast();
                }


            }
        } catch (Exception ex) {

        }
    }

    //    @Override
//    public void onStop() {
//        super.onStop();
//        try {
//            if (mServiceReceiver != null) {
//                getActivity().unregisterReceiver(mServiceReceiver);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void createRoute(Routes currentroute, String waypointsStr, ArrayList<RouteDetail> stops) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        // NetworkConsume.getInstance ().ShowProgress(getActivity());
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        try {

            DirectionsApiRequest req = DirectionsApi.newRequest(context).origin(new com.google.maps.model.LatLng(currentroute.getOriginLat(), currentroute.getOriginLong())).destination(new com.google.maps.model.LatLng(currentroute.getDestinationLat(), currentroute.getDestinationLong()));
            req.mode(TravelMode.DRIVING);
            req.waypoints(waypointsStr);
            DirectionsResult res = req.await();

            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LatLng fromPosition = new LatLng(currentroute.getOriginLat(), currentroute.getOriginLong());
            LatLng toPosition = new LatLng(currentroute.getDestinationLat(), currentroute.getDestinationLong());
            mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
            mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

            for (int k = 0; k < currentroute.getRouteDetails().size(); k++) {
                try {

                    RouteDetail routeDetail = currentroute.getRouteDetails().get(k);
                    if (routeDetail.getRouteTypeID().equals("4")) {

                        mMap.addMarker(new MarkerOptions().position(new LatLng(routeDetail.getLatitude(), routeDetail.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Decanting Station"));

                    } else if (routeDetail.getRouteTypeID().equals("5")) {

                        mMap.addMarker(new MarkerOptions().position(new LatLng(routeDetail.getLatitude(), routeDetail.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.food)).title("Restaurant"));

                    } else if (routeDetail.getRouteTypeID().equals("6")) {

                        mMap.addMarker(new MarkerOptions().position(new LatLng(routeDetail.getLatitude(), routeDetail.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.restarea)).title("Rest Area"));

                    } else if (routeDetail.getRouteTypeID().equals("1")) {


                    } else {


                        mMap.addMarker(new MarkerOptions().position(new LatLng(routeDetail.getLatitude(), routeDetail.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_bus)).title("Stop"));


                    }
                } catch (Exception ex) {

                }
            }

        } catch (Exception ex) {
            NetworkConsume.getInstance().hideProgress();
            Log.e("exception", ex.getLocalizedMessage());
        }


        mMap.clear();

        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
        //  NetworkConsume.getInstance().hideProgress();
        LatLng fromPosition = new LatLng(currentroute.getOriginLat(), currentroute.getOriginLong());
        LatLng toPosition = new LatLng(currentroute.getDestinationLat(), currentroute.getDestinationLong());
        mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
        mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));


        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(fromPosition);
        boundsBuilder.include(toPosition);

        final LatLngBounds bounds = boundsBuilder.build();
        //    viewRoute(mMap);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapLoaded = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
            }
        });


        mMap.getUiSettings().setZoomControlsEnabled(true);

//        for (int k = 0; k < stops.size(); k++) {
//            try {
//                mMap.addMarker(new MarkerOptions().position(new LatLng(stops.get(k).getLatitude(), stops.get(k).getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));
//            } catch (Exception ex) {
//                NetworkConsume.getInstance().hideProgress();
//            }
//        }
        if (Allpoints != null) {
            for (int k = 0; k < Allpoints.size(); k++) {
                try {

                    if (Allpoints.get(k).getRouteTypeID().equals("4")) {
                        double stoplat = Double.parseDouble(Allpoints.get(k).getLatitude().toString());
                        double stoplng = Double.parseDouble(Allpoints.get(k).getLongitude().toString());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));

                    } else if (Allpoints.get(k).getRouteTypeID().equals("5")) {
                        double stoplat = Double.parseDouble(Allpoints.get(k).getLatitude().toString());
                        double stoplng = Double.parseDouble(Allpoints.get(k).getLongitude().toString());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.food)).title("Stop"));

                    } else if (Allpoints.get(k).getRouteTypeID().equals("6")) {
                        double stoplat = Double.parseDouble(Allpoints.get(k).getLatitude().toString());
                        double stoplng = Double.parseDouble(Allpoints.get(k).getLongitude().toString());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.restarea)).title("Stop"));

                    } else if (Allpoints.get(k).getRouteTypeID().equals("1")) {

                    } else {
                        double stoplat = Double.parseDouble(Allpoints.get(k).getLatitude().toString());
                        double stoplng = Double.parseDouble(Allpoints.get(k).getLongitude().toString());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_bus)).title("Stop"));

                    }
                } catch (Exception ex) {

                }
            }
        }


    }

    private void getAllHazards() {

        // NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET, hashMap, this);

    }

    private Bitmap resizeMarker(int width, int height, int drawable) {

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        BackgroundLocationManager.getInstance().startService(getContext());
        BackgroundLocationManagertwo.getInstance().startService(getContext());
        BackgroundLocationSpeedBroadcast.getInstance().startService(getContext());

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
        // mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (responseObj != null) {

            // Route Object
            Routes routes = new Routes();
            routes.setRouteID(responseObj.data.routeID);
            routes.setRouteName(responseObj.data.routeName);
            routes.setRouteAddress(responseObj.data.routeAddress);
            routes.setDescription(responseObj.data.description);
            routes.setIsActive(responseObj.data.isActive);
            routes.setOriginLat(responseObj.data.originLat);
            routes.setOriginLong(responseObj.data.originLong);
            routes.setDestinationLat(responseObj.data.destinationLat);
            routes.setDestinationLong(responseObj.data.destinationLong);
            routes.setRouteDetails((ArrayList<RouteDetail>) responseObj.data.routeDetails);
            String waypoints = "";
            ArrayList<RouteDetail> stops = new ArrayList<>();
            if (responseObj.data.routeDetails.size() > 0) {
                Allpoints = new ArrayList<>();
                for (int i = 0; i < responseObj.data.routeDetails.size(); i++) {
                    Allpoints.add(responseObj.data.routeDetails.get(i));
                    waypoints += responseObj.data.routeDetails.get(i).getLatitude() + "," + responseObj.data.routeDetails.get(i).getLongitude() + "|";
                    if (responseObj.data.routeDetails.get(i).getRouteTypeID().equals("1")) {
//                        waypoints += responseObj.data.routeDetails.get(i).getLatitude() + "," + responseObj.data.routeDetails.get(i).getLongitude() + "|";
                    } else {
                        stops.add(responseObj.data.routeDetails.get(i));
                    }
                }
            }
            CurrentRoute = routes;
            createRoute(routes, waypoints, stops);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        if (mMap != null) {
            currentPosMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(50, 50, R.drawable.navigation))).position(new LatLng(lat, lon)));
        }


    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void StopRide(String VehicleID, String StatusID) {

        if (!VehicleID.equals("")) {
            HashMap hashMap = new HashMap();
            hashMap.put("StatusID",""+ StatusID);
            hashMap.put("VehicleID", ""+VehicleID);
            hashMap.put("Latitude", "" + lat);
            hashMap.put("Longitude", "" + lon);
            hashMap.put("RouteAssignID", ""+responseObj.data.getRouteAssignID());
            hashMap.put("StopTypeID","1");
            httpvolley.stringrequestpost("api/DriverLogs/SaveDriverLog", Request.Method.POST, hashMap, this);

            //    httpvolley.stringrequestpost("api/VehicleRides/UpdateStatus", Request.Method.PUT, hashMap, this);
//            httpvolley.stringrequestpost("api/DriverLogs/ResumeRide", Request.Method.PUT, hashMap, this);

        }
    }

    @Override
    public void hasInternetConnection() {

        //     showDialog(true);
        Log.d("connection", "Internet available");
    }

    private void showInternetDialog(boolean internetstatus) {

        try {


            String desc = "You dont have an internet Connection.Shift to offline mode?";
            String title = "No Internet Connection";
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                    .setTitle(title)

                    .setMessage(desc)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
            Log.d("connection", "no Internet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    @Override
    public void hasNoInternetConnection() {
//        showDialog(false);
    }

    private void getDecantingInspectionList() {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/InspectionLists/GetDecantingInspectionList", Request.Method.GET, hashMap, this);

    }

    private void sync_OfflineData() {
        try {
            if (OfflineData.offlineData.size() > 0) {

                HashMap<String, HashMap> offline = OfflineData.offlineData.get(0);
                for (String key : offline.keySet()) {
                    httpvolley.stringrequestpost_offline("" + key, Request.Method.POST, offline.get(key), this);
                }


            } else {

                Toast.makeText(getContext(), "Sync Completed", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception ex) {
            NetworkConsume.getInstance().hideProgress();
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(String response) throws JSONException {
        Log.d("Response", response);
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.has("ResponseCode")) {
                if (jsonObject1.get("ResponseCode").equals(2201)) {             //Ride Completed
                    Toast.makeText(getContext(), "Ride Completed", Toast.LENGTH_SHORT).show();
                    NetworkConsume.getInstance().Logout(getContext());
                    singleton.getsharedpreference_editor(getContext()).clear().commit();
                    // singleton.getsharedpreference_editor(getApplicationContext()).remove("userData").commit();
                    Paper.book().delete(Constants.KEY_ROLE_ID);

                } else  if(jsonObject1.get("ResponseCode").equals(2601))
                {
                    singleton.getsharedpreference_editor(getContext()).putString("LoadDecantingSiteID", "").commit();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
                }
                else if (jsonObject1.get("ResponseCode").equals(2101)) {            //resume Ride

                    CurrentStop=null;
                    Intent i = new Intent(getContext(), ResumeRideActivity.class);
                    i.putExtra("VehicleID", "" + responseObj.data.vehicleID);
                    startActivity(i);
                } else if (jsonObject1.getInt("ResponseCode") == 3006) {      //get Decanting Sites
                    DecantingList = new ArrayList<>();

                    JSONArray decantingJsonArray = jsonObject1.getJSONArray("Data");
                    for (int i = 0; i < decantingJsonArray.length(); i++) {
                        com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum DecantingPojo = new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum();
                        DecantingPojo.setDecantingSiteID(decantingJsonArray.getJSONObject(i).getInt("DecantingSiteID"));
                        DecantingPojo.setDecantingSite("" + decantingJsonArray.getJSONObject(i).get("DecantingSiteName"));
                        DecantingPojo.setDecantingLatitude(decantingJsonArray.getJSONObject(i).getDouble("DecantingLatitude"));
                        DecantingPojo.setDecantingLongitude(decantingJsonArray.getJSONObject(i).getDouble("DecantingLongitude"));
                        DecantingPojo.setLoadDecantingSiteID(decantingJsonArray.getJSONObject(i).getInt("LoadDecantingSiteID"));

                        ArrayList<DecantStaffPojo> listStaff=new ArrayList<>();
                        JSONArray decantingstaffarray=decantingJsonArray.getJSONObject(i).getJSONArray("DecantingStaffs");
                        for(int j=0;j<decantingstaffarray.length();j++){
                            DecantStaffPojo dsp=new DecantStaffPojo();
                            dsp.setStaffName(decantingstaffarray.getJSONObject(j).getString("StaffName"));
                            dsp.setStaffID(decantingstaffarray.getJSONObject(j).getInt("StaffID"));
                            dsp.setQRCode(decantingstaffarray.getJSONObject(j).getString("QRCode"));
                            dsp.setImageUrl(decantingstaffarray.getJSONObject(j).getString("ImageUrl"));
                            listStaff.add(dsp);
                        }
                      DecantingPojo.setDecantStaffList(listStaff);


                        DecantingList.add(DecantingPojo);
                    }
                } else if (jsonObject1.getInt("ResponseCode") == 7006) {         // get Inspection List
                    if (jsonObject1.has("Data")) {
                        JSONArray dataArray = jsonObject1.getJSONArray("Data");
                        if (dataArray != null) {
                            ArrayList<PreLoadInsPojo> inspectionlist = new ArrayList<>();
                            PreLoadInsPojo preLoadInsPojo = new PreLoadInsPojo();
                            for (int i = 0; i < dataArray.length(); i++) {
                                preLoadInsPojo.setInspectionListID(dataArray.getJSONObject(i).getInt("InspectionListID"));
                                preLoadInsPojo.setInspectionTypeID(dataArray.getJSONObject(i).getInt("InspectionTypeID"));
                                preLoadInsPojo.setInspectionType("" + dataArray.getJSONObject(i).get("InspectionType"));
                                preLoadInsPojo.setInspectionTitle("" + dataArray.getJSONObject(i).get("InspectionTitle"));
                                preLoadInsPojo.setInspectionTitleUr("" + dataArray.getJSONObject(i).get("InspectionTitleUr"));
                                preLoadInsPojo.setIsRequired(dataArray.getJSONObject(i).getBoolean("IsRequired"));
                                preLoadInsPojo.setOrderNumber(dataArray.getJSONObject(i).getInt("OrderNumber"));
                                preLoadInsPojo.setPriorityNumber(dataArray.getJSONObject(i).getInt("PriorityNumber"));

                                inspectionlist.add(preLoadInsPojo);
                                preLoadInsPojo = new PreLoadInsPojo();
                            }
                            OfflineData.inspectionlist = inspectionlist;


                        }
                    }
                } else if (jsonObject1.getInt("ResponseCode") == 6002) {   // get hazards
                    if (completeHazard.size() > 0) {
                        completeHazard.clear();
                    }
                    ArrayList<HazardDetail> hazardDetails = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("Data");
                    Hazards hazards = new Hazards();
                    HazardDetail hazardDetail = new HazardDetail();
                    for (int i = 0; i < array.length(); i++) {
                        hazards = new Hazards();

                        if (array.getJSONObject(i).has("HazardID")) {
                            if (!array.getJSONObject(i).get("HazardID").equals(null)) {
                                hazards.setHazardID((Integer) array.getJSONObject(i).get("HazardID"));
                            }
                        }
                        if (array.getJSONObject(i).has("HazardName")) {
                            if (!array.getJSONObject(i).get("HazardName").equals(null)) {
                                hazards.setHazardName((String) array.getJSONObject(i).get("HazardName"));
                            } else {
                                hazards.setDetail("No Name");
                            }
                        }
                        if (array.getJSONObject(i).has("HazardType")) {
                            if (!array.getJSONObject(i).get("HazardType").equals(null)) {
                                hazards.setHazardType("" + array.getJSONObject(i).get("HazardType"));
                            } else {
                                hazards.setHazardType("No Hazard Type");
                            }
                        }
                        if (array.getJSONObject(i).has("Location")) {
                            if (!array.getJSONObject(i).get("Location").equals(null)) {
                                hazards.setLocation(array.getJSONObject(i).get("Location"));
                            } else {
                                hazards.setLocation("No Location");
                            }
                        }

                        if (array.getJSONObject(i).has("Latitude")) {
                            if (!array.getJSONObject(i).get("Latitude").equals(null)) {
                                hazards.setLatitude(array.getJSONObject(i).get("Latitude"));
                            } else {
                                hazards.setLatitude("0");
                            }
                        }
                        if (array.getJSONObject(i).has("Longitude")) {
                            if (!array.getJSONObject(i).get("Longitude").equals(null)) {
                                hazards.setLongitude(array.getJSONObject(i).get("Longitude"));
                            } else {
                                hazards.setLongitude("0");
                            }
                        }
                        if (array.getJSONObject(i).has("Detail")) {
                            if (!array.getJSONObject(i).get("Detail").equals(null)) {
                                hazards.setDetail(array.getJSONObject(i).get("Detail").toString());
                            } else {
                                hazards.setDetail("No Description");
                            }
                        }


                        JSONArray hazardDetailArray = array.getJSONObject(i).getJSONArray("HazardDetails");

                        hazardDetail = new HazardDetail();
                        for (int j = 0; j < hazardDetailArray.length(); j++) {

                            hazardDetail = new HazardDetail();
                            if (hazardDetailArray.getJSONObject(j).has("HazardDetailID")) {
                                if (!hazardDetailArray.getJSONObject(j).get("HazardDetailID").equals(null)) {
                                    hazardDetail.setHazardDetailID((Integer) hazardDetailArray.getJSONObject(j).get("HazardDetailID"));
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("HazardID")) {
                                if (!hazardDetailArray.getJSONObject(j).get("HazardID").equals(null)) {
                                    hazardDetail.setHazardID((Integer) hazardDetailArray.getJSONObject(j).get("HazardID"));
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("ImageUrl")) {
                                if (!hazardDetailArray.getJSONObject(j).get("ImageUrl").equals(null)) {
                                    hazardDetail.setImageUrl("" + hazardDetailArray.getJSONObject(j).get("ImageUrl"));

                                } else {
                                    hazardDetail.setImageUrl("N/A");
                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("VoiceNoteUrl")) {
                                if (!hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl").equals(null)) {
                                    hazardDetail.setVoiceNoteUrl("" + hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl"));

                                }
                            }
                            hazardDetails.add(hazardDetail);

                        }


                        // hazardDetails.add(hazardDetail);
                        hazards.setHazardDetails(hazardDetails);

                        completeHazard.add(hazards);
                        hazardDetails = new ArrayList<>();


                    }
                }

            }

        } catch (Exception ex) {
            Log.d("error", "" + ex);
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();

        Log.d("response eroor", "" + singleton.getsharedpreference(getContext()).getString("token", ""));
    }

    @Override
    public void onOfflineResponse(String response) throws JSONException {
        try {
            JSONObject jsonObject1 = new JSONObject(response);

            if (!jsonObject1.equals(null)) {
                if (jsonObject1.getInt("ResponseCode") == 1111) // check last decanting or not
                {


                    if (jsonObject1.has("Data")) {
                        if (jsonObject1.getJSONObject("Data") != null) {
                            String StatusID = "" + jsonObject1.getJSONObject("Data").get("StatusID");
                            if (StatusID.equals("10") || StatusID.equals(10)) {
                                showDialog();
                            }

                        }
                    }
                } else if (jsonObject1.getInt("ResponseCode") == 2102) {
//                    singleton.getsharedpreference_editor(getContext()).putString("LoadDecantingSiteID", "").commit();

                    NotificationsManager.sendNotification(getContext(), "Ride Resumed Offline", "Ride Resumed at: "+getCurrentDate(), "" + responseObj.data.vehicleID);
                    reacheddecanting = false;
                }
                OfflineData.offlineData.remove(0);
                sync_OfflineData();
            }
        } catch (Exception ex) {
            OfflineData.offlineData.remove(0);
            sync_OfflineData();
            ex.printStackTrace();
        }
    }

    @Override
    public void onOfflineError(VolleyError Error) {
        OfflineData.offlineData.remove(0);
        sync_OfflineData();
        Error.printStackTrace();
    }

    private void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.activity_rating, null);
        RatingBar ratingBar = layout.findViewById(R.id.ratingBar2);
        Switch switch1 = layout.findViewById(R.id.switch1);
        EditText editText2 = layout.findViewById(R.id.editText2);
        final boolean[] checked = {false};
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[0] = isChecked;
            }
        });
        alert.setCancelable(false);
        alert.setView(layout);
        alert.setTitle("");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                SaveDriverRating(checked[0], ratingBar.getNumStars(), editText2.getText().toString());
                //ResumeRide();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private void SaveDriverRating(boolean ServiceChampionSame, int ratings, String Reviews) { // offline scenario for driver rating
        HashMap hashMap = new HashMap();
        hashMap.put("LoadDecantingSiteID", "" + singleton.getsharedpreference(getContext()).getString("LoadDecantingSiteID", ""));
        if (ServiceChampionSame) {
            hashMap.put("IsSameChampion", "" + 1);
        } else {
            hashMap.put("IsSameChampion", "" + 0);
        }

        hashMap.put("Rating", "" + ratings);
        hashMap.put("Reviews", "" + Reviews);
        httpvolley.stringrequestpost("api/LoadDecantingSite/SaveDriverRating", Request.Method.POST, hashMap, this);


    }
}

