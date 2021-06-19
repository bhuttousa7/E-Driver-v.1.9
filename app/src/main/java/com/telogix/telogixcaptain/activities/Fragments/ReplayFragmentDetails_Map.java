package com.telogix.telogixcaptain.activities.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.DriverLog.DriverLogPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.adapters.StopInfoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ReplayFragmentDetails_Map extends Fragment implements OnMapReadyCallback, response_interface {

    static String speedtype  = null;
    String RouteAssignID = null;
    String VehicleID = null;
    String PickupLocationName = null;
    String LastDecantingSiteName = null;
    private GoogleMap mMap;
    private PolylineOptions opts;
    LatLng fromPosition,toPosition;
    LatLng start,stop;
    private final ArrayList waypoints = new ArrayList();
    private final ArrayList<LatLng> stops = new ArrayList();
    private final List<LatLng>stopMarkersList = new ArrayList();
    private final List<Marker>stopMarkers =  new ArrayList();
    private final List<LatLng> routepath = new ArrayList();
    static ArrayList speedinkph = new ArrayList();
    static ArrayList datetime = new ArrayList();
    ArrayList stopdetails;
    float v = 0;

    private static boolean defaultSet = false, zoomed,setMarker = false,paused = false,driverLog = false, start_end_marker_set = false,firstPlay = true;
    private Marker marker = null,stopMarker = null,startMarker = null,endMarker = null;
    private int lastLocPos = -1;
    private static int speed =1000;
    private final int newspeed=100;
    ImageButton startReplay;
    Button inc_speed_btn,dec_speed_btn;
    Runnable runnable;
    Handler handler;
    private static int clicked = 0,c2=-1,c3=-1;
    ValueAnimator valueAnimator;
    PopupMenu popup;
    View map_type_menu_view;
    LinearLayout replay_btn_layout,map_type_layout,map_ll,summary_ll;
    static Spinner speed_control_spinner;
    com.google.android.material.floatingactionbutton.FloatingActionButton map_layer_btn;
    static ImageButton  satellite_type,default_type,terrain_type;
   static TextView default_map_textview,satellite_map_textview,terrain_map_textview,tv_speedinkph,tv_datetime;
    View view_terrain_map_type,view_default_map_type,view_satellite_map_type;
    TextView tv_totaldistance,tv_totaltime,tv_stoptime,tv_totalmovingtime;
   static Switch summary_checkbox,stops_checkbox;
    float totalStopDuration = 0, totalMovingTime = 0,totaldistance = 0,totalTime = 0;
    BottomSheetBehavior sheetBehavior;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String speedtype_str;

    public ReplayFragmentDetails_Map() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_replay_details__map, container, false);
        bindViews(v);
        return v;

    }
    public void bindViews(View v){

        startReplay= v.findViewById(R.id.startreplay);
        replay_btn_layout = v.findViewById(R.id.replay_btn_layout);
        map_layer_btn = v.findViewById(R.id.fab_layer_toggle);
        satellite_type = v.findViewById(R.id.satellite_type);
        default_type = v.findViewById(R.id.default_type);
        terrain_type = v.findViewById(R.id.terrain_type);
        default_map_textview = v.findViewById(R.id.default_map_textview);
        satellite_map_textview = v.findViewById(R.id.satellite_map_textview);
        terrain_map_textview = v.findViewById(R.id.terrain_map_textview);
        view_default_map_type = v.findViewById(R.id.view_default_map_type);
        view_terrain_map_type = v.findViewById(R.id.view_terrain_map_type);
        view_satellite_map_type = v.findViewById(R.id.view_satellite_map_type);
        summary_ll = v.findViewById(R.id.summary_ll);
        summary_checkbox = v.findViewById(R.id.summary_checkbox);
        tv_speedinkph = v.findViewById(R.id.speedinkph);
        tv_datetime = v.findViewById(R.id.datetime);
        tv_totaldistance  = v.findViewById(R.id.tv_totaldistance);
        tv_totaltime = v.findViewById(R.id.tv_totaltime);
        tv_stoptime = v.findViewById(R.id.tv_stoptime);
        tv_totalmovingtime = v.findViewById(R.id.tv_totalmovingtime);
        stops_checkbox = v.findViewById(R.id.stops_checkbox);
        map_ll = v.findViewById(R.id.map_typell);
        map_ll.setBackgroundColor(Color.TRANSPARENT);
        map_type_layout = v.findViewById(R.id.custom_map_type_layout);
        speed_control_spinner  = v.findViewById(R.id.speed_control_spinner);
        ArrayAdapter<CharSequence> speed_control_adapter  = ArrayAdapter.createFromResource(this.getContext(),R.array.spinnerItems,R.layout.support_simple_spinner_dropdown_item);
        speed_control_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        speed_control_spinner.setAdapter(speed_control_adapter);


    }




    @Override
    public void onPause() {
        super.onPause();
        firstPlay = true;
        clicked = 0;
        paused = false;
        speed = 1000;
        driverLog = false;
        c3 = -1;
        if(marker != null){
        synchronized (marker){
            startReplay.setImageResource(R.drawable.play);
            if(startMarker != null && endMarker != null){
                startMarker.remove();
                endMarker.remove();
                start_end_marker_set = false;
            }

            marker.notifyAll();
            if(handler!=null){
                handler.removeCallbacks(runnable);}
//            Bundle b = getArguments();
//            ChangeFragment(new ReplayTrips(),b);
        }}

        }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // sheetBehavior = BottomSheetBehavior.from(map_type_layout);
        Bundle bundle = getArguments();
        if( bundle.getString("VehicleID")!= null && bundle.getString("RouteAssignID") != null ){
           VehicleID = bundle.getString("VehicleID");
           RouteAssignID = bundle.getString("RouteAssignID");
        }
        if(bundle.getString("LastDecantingSiteName")!= null && bundle.getString("PickupLocationName")!= null){

            LastDecantingSiteName = bundle.getString("LastDecantingSiteName");
            PickupLocationName = bundle.getString("PickupLocationName");
        }

        sharedPreferences = getContext().getSharedPreferences("MapBottomSheetDialog",Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();

            getVehicleLocationCoordinates();




        stops_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b == true) {

                    if (stopMarkersList.size() != 0) {

                    for (int i = 0; i < stopMarkersList.size(); i++) {
//                      stopMarker =  mMap.addMarker(new MarkerOptions().position(stopMarkers.get(i)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.stop_point))));
//                        stopMarker.setSnippet(String.valueOf(stopdetails.get(i)));
                        stopMarker = mMap.addMarker(new MarkerOptions().position(stopMarkersList.get(i)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(70, 70, R.drawable.stop_point))));
                        stopMarker.setTag(stopdetails.get(i).toString().trim());
                        StopInfoAdapter stopInfoAdapter = new StopInfoAdapter(getContext(), stopdetails.get(i).toString().trim());
                        mMap.setInfoWindowAdapter(stopInfoAdapter);
                        stopMarkers.add(stopMarker);


                    }
                }
                    if(startMarker != null) {
                        startMarker.setTag(PickupLocationName + "\n" + "" + datetime.get(0).toString());
                        StopInfoAdapter startMarkerInfoAdapter = new StopInfoAdapter(getContext(), PickupLocationName + "\n" + "" + datetime.get(0).toString());
                        mMap.setInfoWindowAdapter(startMarkerInfoAdapter);
                    }
                    if(endMarker != null){
                        endMarker.setTag(LastDecantingSiteName+"\n"+""+datetime.get(datetime.size() - 1).toString());
                        StopInfoAdapter endMarkerInfoAdapter = new StopInfoAdapter(getContext(),LastDecantingSiteName+"\n"+""+datetime.get(datetime.size() - 1).toString());
                        mMap.setInfoWindowAdapter(endMarkerInfoAdapter);
                    }

                   // sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                   // startReplay.setVisibility(View.VISIBLE);
                }
                else{
                    for(int i=0;i<stopMarkers.size();i++){
                      stopMarkers.get(i).remove();
                    }
                    stopMarkers.clear();


                  //  startReplay.setVisibility(View.VISIBLE);
                  //  sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);

                }

            }
        });

        summary_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    summary_ll.setVisibility(View.VISIBLE);
                    if(totaldistance == 0){
                        tv_totaldistance.setText("Total Distance:\n 0.0Km");
                    }
                    if(totalStopDuration == 0){
                        tv_stoptime.setText("Total Stop Time:\n 00h:00m");
                    }
                    if(totalTime == 0){
                        tv_totaltime.setText("Total Time:\n 00h:00m");
                    }
                    if(totalMovingTime == 0){
                        tv_totalmovingtime.setText("Total Moving Time:\n 00h:00m");
                    }
                    startReplay.setVisibility(View.VISIBLE);
                 //   sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                }
                else{
                    summary_ll.setVisibility(View.GONE);
                 //   startReplay.setVisibility(View.VISIBLE);
                  //  sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        satellite_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_satellite_map_type.setVisibility(View.VISIBLE);
                view_default_map_type.setVisibility(View.GONE);
                view_terrain_map_type.setVisibility(View.GONE);
                satellite_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                terrain_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.BLACK);
                if(mMap != null)
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //    sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                startReplay.setVisibility(View.VISIBLE);
                tv_datetime.setTextColor(Color.WHITE);


            }
        });
        default_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_satellite_map_type.setVisibility(View.GONE);
                view_default_map_type.setVisibility(View.VISIBLE);
                view_terrain_map_type.setVisibility(View.GONE);
                satellite_map_textview.setTextColor(Color.BLACK);
                terrain_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                if(mMap != null)
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
              //  sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                startReplay.setVisibility(View.VISIBLE);
                tv_datetime.setTextColor(Color.BLACK);
            }
        });
        terrain_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_satellite_map_type.setVisibility(View.GONE);
                view_default_map_type.setVisibility(View.GONE);
                view_terrain_map_type.setVisibility(View.VISIBLE);
                terrain_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                satellite_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.BLACK);
                if(mMap != null)
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
              //  sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
                startReplay.setVisibility(View.VISIBLE);
                tv_datetime.setTextColor(Color.BLACK);
            }
        });
        speed_control_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                speedtype = null;
                speedtype = adapterView.getItemAtPosition(i).toString();

                if(speedtype.equals("Normal"))
                    speed=1000;
                if(speedtype.equals("Fast"))
                    speed=800;
                if(speedtype.equals("Very Fast"))
                    speed=500;
                if(speedtype.equals("Slow"))
                    speed=1200;
                if(speedtype.equals("Very Slow"))
                    speed=1500;

                Toast.makeText(getContext(), speedtype+": "+speed,Toast.LENGTH_SHORT).show();
           //     sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
           //     startReplay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            //    sheetBehavior.setState( BottomSheetBehavior.STATE_COLLAPSED);
             //   startReplay.setVisibility(View.VISIBLE);
            }
        });
//        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View view, int i) {
//                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
//
//                     if(summary_checkbox.isChecked()){
//                         summary_ll.setVisibility(View.VISIBLE);
//                     }
//
//                }
//                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
//
//                    if(summary_checkbox.isChecked()){
//                        summary_ll.setVisibility(View.GONE);
//                    }
//
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View view, float v) {
//
//            }
//        });
        map_layer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(routepath.size() == 0){
                    Toast.makeText(getContext(),"No Replay Data found",Toast.LENGTH_SHORT).show();
                }
                 else{
                     synchronized (marker){
                if(clicked == 1 && paused == false ) {

                    paused = true;
                    marker.notifyAll();
                   // valueAnimator.pause();
                    handler.removeCallbacks(runnable);
                    startReplay.setImageResource(R.drawable.play);
                }
                         Fragment bottomsheetFrag = (Fragment) getParentFragmentManager()
                                 .findFragmentByTag("ReplayFragmentDetails_Map");

                         MapBottomSheetDialog mapBottomSheetDialog = MapBottomSheetDialog.newInstance();

                         FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                         mapBottomSheetDialog.show(ft,mapBottomSheetDialog.TAG);



//                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    startReplay.setVisibility(View.GONE);
//                    if (summary_checkbox.isChecked()) {
//                        summary_ll.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    startReplay.setVisibility(View.VISIBLE);
//                    if (summary_checkbox.isChecked()) {
//                        summary_ll.setVisibility(View.VISIBLE);
//                    }
//
//                }

//                if(map_type_layout.getVisibility() == View.GONE){
//                    map_type_layout.setVisibility(View.VISIBLE);
//                }
//                else{
//                    map_type_layout.setVisibility(View.GONE);
//
//                }
            }}
            }
        });

        startReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clicked == 0){
                    if(routepath.size() != 0){
                    startReplay();
                  //  paused  = false;
                    clicked =1;
                    startReplay.setImageResource(R.drawable.pause);
                    }
                    else{Toast.makeText(getContext(),"No Replay Data found",Toast.LENGTH_SHORT).show();}
                }
                else {
                    if (paused == false) {
                        synchronized (marker) {
                            paused = true;
                            marker.notifyAll();
                         //   valueAnimator.resume();
                            handler.removeCallbacks(runnable);
                            startReplay.setImageResource(R.drawable.play);
                        }

                    } else {
                      //  marker = mMap.addMarker(new MarkerOptions().position(routepath.get(lastLocPos)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
                        synchronized (marker) {
                            paused = false;
                            c2 = -1;
                          //  valueAnimator.pause();
                            marker.notifyAll();
                            startReplay.setImageResource(R.drawable.pause);

                            startReplay();
                        }
                    }
                }
            }
        });
//        inc_speed_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(speed - newspeed < 100)
//                {
//
//                }else{
//                    speed = speed - newspeed;
//                }
//
//
//            }
//        });
//        dec_speed_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(speed + newspeed < 100)
//                {
//
//                }else{
//                    speed = speed + newspeed;
//                }
//            }
//        });
//        if (marker != null) {
//            //   marker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentlatitude, currentlongitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
//            animateMarkerNew(currentlatitude, currentlongitude, marker);
//        }

    }
    @SuppressLint("ResourceType")
    public void setMenu(){

        popup = new PopupMenu(getContext(), map_type_menu_view, Gravity.CENTER_HORIZONTAL,R.attr.actionOverflowMenuStyle,0);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.map_options, popup.getMenu());
    }

    public void showMapTypeMenu(View v){
        popup.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.map_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public boolean onMenuItemClick(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                item.setChecked(!item.isChecked());

                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                item.setChecked(!item.isChecked());
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                item.setChecked(!item.isChecked());
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                item.setChecked(!item.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getVehicleLocationCoordinates(){

        HashMap data=new HashMap();
      //int  VehicleID = 750;
     //int   RouteAssignID = 4270;
        httpvolley.stringrequestpost("api/TrackingData/GetLoadReplayData?VehicleID="+VehicleID+"&LoadID="+RouteAssignID, Request.Method.GET,data,this);

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
        JSONArray data = jsonObject.getJSONArray("Data");
        if (jsonObject.get("ResponseCode").equals(2103)) {
            //DriverLog Response
            stopdetails = new ArrayList();
            ArrayList resumedPos = new ArrayList();
            stopMarkersList.clear();
            stopdetails.clear();
            totalStopDuration = 0;
            for (int i = 0; i < data.length(); i++) {
                DriverLogPojo driverLogPojo = new DriverLogPojo();
                if (data.getJSONObject(i).getString("StopTypeTitle") != "Ride Started") {
                    String Stoptitle = data.getJSONObject(i).getString("StopTypeTitle");
                    if (Stoptitle.equals("Resumed")) {
                        Double old_latitude = data.getJSONObject(i-1).getDouble("Latitude");
                        Double old_longitude = data.getJSONObject(i-1).getDouble("Longitude");
                        LatLng coordinates = new LatLng(old_latitude, old_longitude);
                        stopMarkersList.add(coordinates);
                        String VehicleNo = data.getJSONObject(i).getString("VehicleNo");
                        String Description = data.getJSONObject(i-1).getString("Description");
                        String StopTypeTitle = data.getJSONObject(i-1).getString("StopTypeTitle");
                        String oldtime = data.getJSONObject(i-1).getString("StartDate");
                        String newtime = data.getJSONObject(i).getString("StartDate");
                        Date olddate = StringtoDate(oldtime);
                        Date newdate = StringtoDate(newtime);
                        Long stopduration = getDateDiff(olddate,newdate,TimeUnit.MINUTES);
                        driverLogPojo.setDriverDetails(i-1, StopTypeTitle, VehicleNo, stopduration, Description, coordinates);
                        String currentStopMarkerDetail = "\n"+StopTypeTitle+"\n"+"Duration:"+stopduration+" min"+"\nDescription:"+Description+"\n"+coordinates+"\n";
                        totalStopDuration+=stopduration;
                        stopdetails.add(currentStopMarkerDetail);


                    }


                }

            }
            //totalStoptime
            tv_stoptime.setText("Total Stop Time:\n"+MinutestoHoursAndMinutes(totalStopDuration));

            //total_distance
          for(int i=0;i<routepath.size()-1;i++) {
              float[] result = new float[1];
              Location.distanceBetween(routepath.get(i).latitude, routepath.get(i).longitude, routepath.get(i+1).latitude, routepath.get(i+1).longitude, result);
              totaldistance += result[0];
          }
            tv_totaldistance.setText("Total Distance:\n"+niceround((totaldistance/1000))+"km");

           //totalMovingTime
            String starttime = data.getJSONObject(0).getString("StartDate");
            String endtime = data.getJSONObject(data.length()-1).getString("StartDate");
            Date olddate = StringtoDate(starttime);
            Date newdate = StringtoDate(endtime);
            Long totalduration = getDateDiff(olddate,newdate,TimeUnit.MINUTES);
            totalTime = totalduration;
            tv_totaltime.setText("Total Time:\n"+MinutestoHoursAndMinutes(totalTime));
            totalMovingTime = totalduration - totalStopDuration;
            tv_totalmovingtime.setText("Total Moving Time:\n"+MinutestoHoursAndMinutes(totalMovingTime));


            if(view_satellite_map_type.getVisibility() == View.GONE &&
                    view_terrain_map_type.getVisibility() == View.GONE &&
                    view_default_map_type.getVisibility() == View.GONE && defaultSet == false){

                view_default_map_type.setVisibility(View.VISIBLE);
                default_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                editor.putInt("view_default_map_type",view_default_map_type.getVisibility());
                editor.putString("default_map_textview", String.format("#%06X", (0xFFFFFF & default_map_textview.getCurrentTextColor())));
                editor.commit();
                defaultSet = true;
            }

            if(sharedPreferences.getBoolean("summary_checkbox",false)){
                boolean b = sharedPreferences.getBoolean("summary_checkbox",false);
                editor.putBoolean("summary_checkbox",b);
                editor.commit();
                summary_checkbox.setChecked(b);
            }
            if(sharedPreferences.getBoolean("stops_checkbox",false)){
                boolean b = sharedPreferences.getBoolean("stops_checkbox",false);
                editor.putBoolean("stops_checkbox",b);
                editor.commit();
                stops_checkbox.setChecked(b);

            }
            if(sharedPreferences.getString("satellite_map_textview","") != ""){
                String color = sharedPreferences.getString("satellite_map_textview","");
                satellite_map_textview.setTextColor(Color.parseColor(color));
                int visibility_view = sharedPreferences.getInt("view_satellite_map_type",-1);
                view_satellite_map_type.setVisibility(visibility_view);
                if(view_satellite_map_type.getVisibility() == View.VISIBLE){
                    satellite_type.callOnClick();
                }

              //  setOptionsState();
            }
            if(sharedPreferences.getString("terrain_map_textview","") != ""){
                String color = sharedPreferences.getString("terrain_map_textview","");
                terrain_map_textview.setTextColor(Color.parseColor(color));
                int visibility_view = sharedPreferences.getInt("view_terrain_map_type",-1);
                view_terrain_map_type.setVisibility(visibility_view);
                if(view_terrain_map_type.getVisibility() == View.VISIBLE){
                    terrain_type.callOnClick();
                }
              //  setOptionsState();


            }
            if(sharedPreferences.getString("default_map_textview","") != ""){
                String color = sharedPreferences.getString("default_map_textview","");
                default_map_textview.setTextColor(Color.parseColor(color));
                int visibility_view = sharedPreferences.getInt("view_default_map_type",-1);
                view_default_map_type.setVisibility(visibility_view);
                if(view_default_map_type.getVisibility() == View.VISIBLE){
                    default_type.callOnClick();
                }


            }
            setOptionsState();
        }
        else{
           // JSONArray data = jsonObject.getJSONArray("Data");
            routepath.clear();

        speedinkph.clear();
        datetime.clear();
        for (int i = 0; i < data.length(); i++) {
            Double latitude = data.getJSONObject(i).getDouble("Latitude");
            Double longitude = data.getJSONObject(i).getDouble("Longitude");
            LatLng coordinates = new LatLng(latitude, longitude);

            if (!routepath.contains(coordinates)) {
                routepath.add(coordinates);


                if (data.getJSONObject(i).getString("Speed") != "null") {
                    speedinkph.add(data.getJSONObject(i).getString("Speed"));
                }
                if ((data.getJSONObject(i).getString("Date")) != null) {
                    datetime.add(data.getJSONObject(i).getString("Date"));
                }
            }
        }
        Log.i("--RoutePath",routepath.toString());

        tv_speedinkph.setText("" + "0.0" + "");
        if ( data.getJSONObject(0).getString("Date") != null) {
            tv_datetime.setText("" + datetime.get(0));
        }

        start = new LatLng(data.getJSONObject(0).getDouble("Latitude"), data.getJSONObject(0).getDouble("Longitude"));
        stop = new LatLng(data.getJSONObject(data.length() - 1).getDouble("Latitude"), data.getJSONObject(data.length() - 1).getDouble("Longitude"));

    }
        try{
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_replay);
        mapFragment.getMapAsync(this);
        }
        catch (Exception e){

        }


    }

    private void setOptionsState() {



        editor.putString("satellite_map_textview", String.format("#%06X", (0xFFFFFF & satellite_map_textview.getCurrentTextColor())));
        editor.putString("terrain_map_textview", String.format("#%06X", (0xFFFFFF & terrain_map_textview.getCurrentTextColor())));
        editor.putString("default_map_textview", String.format("#%06X", (0xFFFFFF & default_map_textview.getCurrentTextColor())));

        editor.putInt("view_satellite_map_type",view_satellite_map_type.getVisibility());
        editor.putInt("view_default_map_type",view_default_map_type.getVisibility());
        editor.putInt("view_terrain_map_type",view_terrain_map_type.getVisibility());

        editor.commit();
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();

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
    public String MinutestoHoursAndMinutes(float duration){

        int hours = (int) (duration/60);
        int min  = (int )(duration%60);
        String strmin = null;
        if(min<10) {strmin = "0"+min;}
        else{ strmin = ""+min; }
        String totaltime = ""+hours+"h:"+strmin+"m";
        return totaltime;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setCompassEnabled(false);

        //Check Location Permissions
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //Location is enabled
        else
        {
            if (mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .addAll(routepath).color(Constants.polylineColor).width(Constants.polylineWidth));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 4));

                if(start_end_marker_set == false) {
                startMarker = mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(80,80, R.drawable.start_marker))));
                endMarker = mMap.addMarker(new MarkerOptions().position(stop).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(80, 80, R.drawable.end_marker))));

                startMarker.setTag(PickupLocationName+"\n"+""+datetime.get(0).toString());
                StopInfoAdapter startMarkerInfoAdapter = new StopInfoAdapter(getContext(),PickupLocationName+"\n"+""+datetime.get(0).toString());
                mMap.setInfoWindowAdapter(startMarkerInfoAdapter);

                endMarker.setTag(LastDecantingSiteName+"\n"+""+datetime.get(datetime.size() - 1).toString());
                StopInfoAdapter endMarkerInfoAdapter = new StopInfoAdapter(getContext(),LastDecantingSiteName+"\n"+""+datetime.get(datetime.size() - 1).toString());
                mMap.setInfoWindowAdapter(endMarkerInfoAdapter);

                marker = mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
                start_end_marker_set = true;

                }
                setMarker = true;


                if(driverLog == false){
                    driverLog = true;
                    getDriverLog();
                }
                if(setMarker) {
                     zoomRoute(mMap,routepath);
                     replay_btn_layout.setVisibility(View.VISIBLE);
                  }

        }


    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

private synchronized void startReplay(){
//    final long strt = SystemClock.uptimeMillis();
//    final long duration = speed;
    final int[] speedinkmph = {0};
    final Interpolator interpolator = new LinearInterpolator();
//    final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
   handler = new Handler();
    final int[] k = {0};
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                if (!isAdded())
                { return;}

                if(lastLocPos != -1){
                    k[0] = lastLocPos;

                }
                if(routepath.get(k[0])!= null && k[0]<routepath.size()-1) {

                    if (c3 == -1) {
                        if (marker != null) {
                        marker.remove();
                    }
                     marker = mMap.addMarker(new MarkerOptions().position(routepath.get(k[0])).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
                     c3 = 0;
                    }

                    try {

                                marker.setTag(routepath.get(k[0]).toString());
                                StopInfoAdapter currentMarkerInfoAdapter = new StopInfoAdapter(getContext(),routepath.get(k[0]).toString());
                                mMap.setInfoWindowAdapter(currentMarkerInfoAdapter);
                                boolean contains = mMap.getProjection().getVisibleRegion().latLngBounds.contains(routepath.get(k[0]+1));
                                if(!contains){
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()

                                            .target(marker.getPosition())
                                            .zoom(16f)
                                            .build()));
                                }
                               animateMarker(mMap,marker, routepath.get(k[0]+1),marker.isVisible(),k[0]);


                                Date date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse("" + datetime.get(k[0]+1));
                                Date date2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse("" + datetime.get(k[0]));
                                long duration = date1.getTime() - date2.getTime();
                                int d = Integer.parseInt(String.valueOf(duration));


                                if(firstPlay){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 20));
                                    firstPlay = false;
                                }

                                if(speedinkph.size() != 0 && datetime.size() !=0){

                                    speedinkmph[0] = Math.toIntExact(Math.round(Double.valueOf(String.valueOf(speedinkph.get(k[0])))));
                                    tv_speedinkph.setText(""+ speedinkmph[0] +"");
                                    tv_datetime.setText("" + datetime.get(k[0]));
                                }

                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()

                                            .target(marker.getPosition())
                                            .zoom(16f)
                                            .build()));

                                    double lat = Math.abs(routepath.get(k[0]).latitude - routepath.get(k[0]+1).latitude);
                                    double lng = Math.abs(routepath.get(k[0]).longitude - routepath.get(k[0]+1).longitude);
                                    if(speedinkph.size() != 0 && Double.valueOf(String.valueOf(speedinkph.get(k[0]))) >0) {
                                        if (lat >= 0.000002 || lng >= 0.000002) {
                                            marker.setRotation(getBearing(routepath.get(k[0]), routepath.get(k[0] + 1)));
                                        }
                                    }


                            } catch (Exception ex) {
                                Log.i("--exAnimUpdate",ex.toString());
                            }

                handler.postDelayed(this, speed);

                    k[0] = k[0] +1;
                    synchronized (marker){
                        while(!paused){
                            try {
                                lastLocPos = k[0];
                                break;
                            }catch (Exception e){
                                Log.i("--Exception:",e.toString());

                            }
                        }

                    }
            }
            else{
                    synchronized (marker){
                        marker.notifyAll();
                        if(handler!=null){
                            handler.removeCallbacks(runnable);
                        }
                        startReplay.setImageResource(R.drawable.play);
                        clicked = 0;
                    }
                }
            }
        };
        handler.post(runnable);
    }
   public static double niceround(double x) {
        return Math.ceil(x * Math.pow(10, -Math.floor(Math.log10(x)))) / Math.pow(10, -Math.floor(Math.log10(x)));
    }
    public static void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition,
                                     final boolean hideMarker,int k) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 995;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));
                marker.setAnchor(0.5f,0.5f);
                if(speedinkph.size() != 0){

                    int speedinkmph = Math.toIntExact(Math.round(Double.valueOf(String.valueOf(speedinkph.get(k)))));
                    tv_speedinkph.setText(""+speedinkmph+"");
                    tv_datetime.setText("" + datetime.get(k));
                }
                if (t < 1.0) {
                    handler.postDelayed(this, 0);
                } else {
                }
            }
        });
    }

    public static void onSpeedAdapterItemClick( String i) {

       speedtype_str = i;

        if(speedtype_str.equals("Normal"))
            speed=1000;
        if(speedtype_str.equals("Fast"))
            speed=600;
        if(speedtype_str.equals("Very Fast"))
            speed=500;
        if(speedtype_str.equals("Slow"))
            speed=1500;
        if(speedtype_str.equals("Very Slow"))
            speed=3000;

    }



    public static void onSummarySwitchClick(boolean item) {
        summary_checkbox.setChecked(item);

    }
    public static void onStopSwitchClick(boolean item) {
        stops_checkbox.setChecked(item);
    }

    public static void onSatelliteMapTypeClick(boolean item) {
        if (item){
            satellite_type.callOnClick();
        }

    }


    public static void onDefaultMapTypeClick(boolean item) {
        if (item){
            default_type.callOnClick();
        }
    }


    public static void onTerrainMapTypeClick(boolean item) {
        if (item){
                terrain_type.callOnClick();
        }
    }

    Bitmap resizeMarker(int width, int height, int drawable) {

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public String waypoints() {
        String points = "";
        for (int i = 0; i < waypoints.size(); i++) {
//            if(i==0)
//            {
//                points=points+""+stops.get(i);
//            }
//            else {
//                points=points+","+stops.get(i);
//            }
            points = points + "" + waypoints.get(i);
            points = points + "|";

        }
        return points;
    }

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


    private void animateMarkerNew(double currentlatitude, double currentlongitude, final Marker marker) {

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

                        if(zoomed)
                        {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()

                                    .target(newPosition)
                                    .zoom(16f)
                                    .build()));
                            marker.isFlat();
                            marker.setRotation(getBearing(startPosition, new LatLng(currentlatitude, currentlongitude)));

                        }
                        else
                        {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()


                                    .target(newPosition)
                                    .zoom(16f)
                                    .build()));
                            marker.setRotation(getBearing(startPosition, new LatLng(currentlatitude, currentlongitude)));
                            zoomed=true;
                        }
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }




    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements ReplayFragmentDetails_Map.LatLngInterpolatorNew {
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

    private void createRoute(double orgLat, double orgLong, double destLat, double destLng) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);

        try {

//            req.waypoints(waypoints());
//            req.optimizeWaypoints(true);
//            DirectionsResult res = req.await();
            DirectionsApiRequest req = DirectionsApi.newRequest(context).origin(new com.google.maps.model.LatLng(orgLat, orgLong)).destination(new com.google.maps.model.LatLng(destLat, destLng));
            req.waypoints(waypoints());
            //Loop through legs and steps to get encoded polylines of each step
            // req.waypoints(waypoints());
            DirectionsResult res = req.await();
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                // Toast.makeText(getContext(),""+route,Toast.LENGTH_LONG).show();


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
        } catch (Exception ex) {
            Log.e("exception", ex.getLocalizedMessage());
            Toast.makeText(getContext(), "" + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

//        if(mMap != null){
//            mMap.clear();
//        }


        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
        for (int k = 0; k < stops.size(); k++) {
            try {
                double stoplat = stops.get(k).latitude;
                double stoplng = stops.get(k).longitude;
                // mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));

            } catch (Exception ex) {

            }
        }

        fromPosition = new LatLng(orgLat, orgLong);
        toPosition = new LatLng(destLat, destLng);
        mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
        mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

//        for(int k=0;k<routepath.size();k++){
//            marker = mMap.addMarker(new MarkerOptions().position(routepath.get(k)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
//        }

        if(toPosition!=null) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(fromPosition);
            boundsBuilder.include(toPosition);
            NetworkConsume.getInstance().hideProgress();
            final LatLngBounds bounds = boundsBuilder.build();
            //    viewRoute(mMap);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));

                }
            });
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                Intent i = new Intent(getContext(), MapDetailActivity.class);
//                i.putExtra("routeid", RoutesAdapter.currentRouteClicked.getRouteID());
//                startActivity(i);
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }






}