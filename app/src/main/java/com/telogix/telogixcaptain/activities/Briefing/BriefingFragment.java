package com.telogix.telogixcaptain.activities.Briefing;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.WorkaroundMapFragment;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.Fragments.AssignCopyRoute;
import com.telogix.telogixcaptain.activities.Fragments.StopDetails;
import com.telogix.telogixcaptain.activities.Fragments.ViewLoadFragment;
import com.telogix.telogixcaptain.driver.fragments.ElectronicSignOff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BriefingFragment extends Fragment implements Serializable, OnMapReadyCallback, response_interface {


    private boolean trafficshowing;
    GoogleMap mMap;
    private CardView mapMenu;
    private ArrayList waypoints = new ArrayList();
    private PolylineOptions opts;
    private ArrayList<LatLng> stops = new ArrayList();
    TextView txtRouteName, txtVehicleNo, txtVehicleType, txtStopsCount, txtHazardsCount, txtDistance;
    static int hazardCount = 0;
    private static long distanceBetween = 0;
    ScrollView scrollView;
    private Button btnStoplist, btnHazardlist;
    public static ArrayList<Hazards> completeHazard = new ArrayList<>();
    private Button btnsignOff;
    private String VehicleID,routeAssignID,LoadID;
    public static Routes route;
    public static ArrayList<HashMap<String, String>> AllPoints = new ArrayList<>();
    private Button btnloaddetails;
    private Button btnrouteEdit;

    public BriefingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_briefing, container, false);
        getActivity().setTitle("Route Details");
        bindViews(v);
        renderTrafficLayout(v);
        if (getArguments().getString("routeid", "-1") != "-1") {
            String routeid = getArguments().getString("routeid");
            if (!routeid.equals("") && !routeid.equals("null")) {
                getRoutebyID(Integer.parseInt(routeid));
            }
        }
        if (getArguments().getString("vehicleNo", "---") != "---") {
            String vehicleNo = getArguments().getString("vehicleNo");
            if (!vehicleNo.equals("")) {
                txtVehicleNo.setText(": " + vehicleNo);
            }
        }
        if (getArguments().getString("LoadID", "---") != "---") {
             LoadID = getArguments().getString("LoadID");

        }

        if (getArguments().getString("vehicleType", "---") != "---") {
            String vehicleType = getArguments().getString("vehicleType");
            if (!vehicleType.equals("")) {
                txtVehicleType.setText(": " + vehicleType);
            }
        }if (getArguments().getBoolean("signOff", false)) {
            btnsignOff.setVisibility(View.VISIBLE);
            getActivity().setTitle("Route Briefing");
            CardView traffic=v.findViewById(R.id.traffic);
            traffic.setVisibility(View.VISIBLE);
        }
        if (getArguments().getString("VehicleID", "---") != "---") {
             VehicleID = getArguments().getString("VehicleID");

        }if (getArguments().getString("routeAssignID", "---") != "---") {
             routeAssignID = getArguments().getString("routeAssignID");

        }

    btnrouteEdit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(route!=null)
            {


                Bundle b=new Bundle();
                if (route.getOriginLat() != null && route.getOriginLat() != null && route.getDestinationLat() != null && route.getDestinationLong() != null) {
                    b=new Bundle();
                    b.putSerializable("Route",route);

                    b.putBoolean("EditRoute",true);
                    if(VehicleID!="" && VehicleID!=null)
                    {
                        b.putString("RouteID", ""+route.getRouteID());
                        b.putString("VehicleID",VehicleID);
                    }

                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                ChangeFragment(new AssignCopyRoute(), v,b);
            }
        }
    });
        btnloaddetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b=new Bundle();
                b.putString("LoadID",""+LoadID);
                b.putString("VehicleID",""+VehicleID);
                ViewLoadFragment fragment=new ViewLoadFragment();

                fragment.setArguments(b);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                        .commit();

            }
        });

        btnsignOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElectronicSignOff electronicSignOff=new ElectronicSignOff();
                Bundle b=new Bundle();
                if (VehicleID!="" && routeAssignID!=""){
                    b.putString("routeAssignID",""+routeAssignID);
                    b.putString("VehicleID",""+VehicleID);
                    electronicSignOff.setArguments(b);
                    getActivity().getSupportFragmentManager().popBackStack();
                    ChangeFragment(electronicSignOff);
                }

            }
        });
        return v;
    }

    private void bindViews(View v) {
        txtRouteName = v.findViewById(R.id.txt_routename);
        txtVehicleNo = v.findViewById(R.id.txt_vehicleno);
        txtVehicleType = v.findViewById(R.id.txt_vehicletype);
        txtStopsCount = v.findViewById(R.id.txt_stopsnum);
        txtHazardsCount = v.findViewById(R.id.hazardscount);
        txtDistance = v.findViewById(R.id.txt_distance);
        scrollView = v.findViewById(R.id.scrollview);
        btnStoplist = v.findViewById(R.id.btnStoplist);
        btnHazardlist = v.findViewById(R.id.btnHazardlist);
        btnsignOff=v.findViewById(R.id.btnsignOff);
        btnsignOff.setVisibility(View.GONE);
        btnrouteEdit = v.findViewById(R.id.btnrouteEdit);

        btnloaddetails=v.findViewById(R.id.btnloaddetails);

    }

    @Override
    public void onResume() {
        super.onResume();
        getAllHazards();
    }

    private void getAllHazards() {
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET, hashMap, this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

    }

    private void renderTrafficLayout(View v) {
        CardView trafficlayout = v.findViewById(R.id.traffic);
        mapMenu = v.findViewById(R.id.mapmenu);
        ImageView trafficImg = v.findViewById(R.id.imgtraffic);

        trafficlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trafficshowing) {
                    trafficshowing = false;
                    mMap.setTrafficEnabled(false);
                    trafficlayout.setBackgroundColor(Color.WHITE);
                    //trafficlayout.setBackground(getResources().getDrawable(R.drawable.roundview));
                    trafficImg.setImageResource(R.drawable.traffic);
                } else {
                    trafficshowing = true;
                    mMap.setTrafficEnabled(true);
                    trafficlayout.setBackgroundColor(Color.BLUE);
                    trafficImg.setImageResource(R.drawable.traffic_white);
                }
            }
        });
    }

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType();

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.

                        switch (item) {
                            case 1: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;
                            }
                            case 2: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            }
                            case 3: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            }
                            case 4: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                mMap.setTrafficEnabled(true);
                            }
                            break;
                            default: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            }
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    private void getRoutebyID(int id) {
        Log.d("--id", "getRoutebyID: "+id);
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/Routes/GetRouteByRouteID?RouteID=" + id, Request.Method.GET, hashMap, this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
        mapMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });
        mapMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });
        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if (myLocation != null) {
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
            }
        }
    }

    public String waypoints() {
        String points = "";
//        for (int i = 0; i < waypoints.size(); i++) {
//            points = points + "" + waypoints.get(i);
//            points = points + "|";
//
//        }
//        return points;
        for (int i = 0; i < AllPoints.size(); i++) {
//            if(i==0)
//            {
//                points=points+""+stops.get(i);
//            }
//            else {
//                points=points+","+stops.get(i);
//            }
            points = points + "" + AllPoints.get(i).get("stoplat")+","+AllPoints.get(i).get("stoplng");

            points = points + "|";

        }
        return points;
    }

    private void createRoute(Routes currentroute) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().ShowProgress(getContext());
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);

        try {
            distanceBetween = 0;
//            req.waypoints(waypoints());
//            req.optimizeWaypoints(true);
//            DirectionsResult res = req.await();
            DirectionsApiRequest req = DirectionsApi.newRequest(context).origin(new com.google.maps.model.LatLng(currentroute.getOriginLat(), currentroute.getOriginLong())).destination(new com.google.maps.model.LatLng(currentroute.getDestinationLat(), currentroute.getDestinationLong()));

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
                                distanceBetween += step.distance.inMeters;
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
            NetworkConsume.getInstance().hideProgress();
            Log.e("exception", ex.getLocalizedMessage());

        }

        txtDistance.setText(": " + (distanceBetween / 1000) + " KM");
        mMap.clear();

        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
        //   NetworkConsume.getInstance().hideProgress();
        NetworkConsume.getInstance().hideProgress();
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            }
        });


        mMap.getUiSettings().setZoomControlsEnabled(true);

        for (int k = 0; k < AllPoints.size(); k++) {
            try {
                HashMap stopsHash=new HashMap();
                stopsHash=AllPoints.get(k);
                if(stopsHash.get("typeid").equals("4"))
                {
                    double stoplat = Double.parseDouble(stopsHash.get("stoplat").toString());
                    double stoplng = Double.parseDouble(stopsHash.get("stoplng").toString());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));

                }else if(stopsHash.get("typeid").equals("5"))
                {
                    double stoplat = Double.parseDouble(stopsHash.get("stoplat").toString());
                    double stoplng = Double.parseDouble(stopsHash.get("stoplng").toString());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.food)).title("Stop"));

                }
                else if(stopsHash.get("typeid").equals("6"))
                {
                    double stoplat = Double.parseDouble(stopsHash.get("stoplat").toString());
                    double stoplng = Double.parseDouble(stopsHash.get("stoplng").toString());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.restarea)).title("Stop"));

                }else if(stopsHash.get("typeid").equals("1"))
                {

                }
                else
                {
                    double stoplat = Double.parseDouble(stopsHash.get("stoplat").toString());
                    double stoplng = Double.parseDouble(stopsHash.get("stoplng").toString());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_bus)).title("Stop"));

                }
            } catch (Exception ex) {

            }
        }
        if(stops.size()==0)
        {
//            btnStoplist.setVisibility(View.GONE);
        }
        else
        {
            btnStoplist.setVisibility(View.VISIBLE);

        }

        txtStopsCount.setText(": " + stops.size());
    }

    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void ChangeFragment(Fragment fragment, View v,Bundle b) {
        fragment.setArguments(b);

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        waypoints = new ArrayList();
        stops = new ArrayList();

        hazardCount=0;
        boolean waypoint = false;
        try {



            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("ResponseCode")) {
                if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ALLHAZARDS))) {
                    completeHazard.clear();
                    JSONArray array = jsonObject.getJSONArray("Data");
                    Hazards hazards = new Hazards();

                    HazardDetail hazardDetail = new HazardDetail();
                    ArrayList<HazardDetail> hazardDetail1list = new ArrayList<>();
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
                            }
                        }
                        if (array.getJSONObject(i).has("HazardType")) {
                            if (!array.getJSONObject(i).get("HazardType").equals(null)) {
                                hazards.setHazardType("" + array.getJSONObject(i).get("HazardType"));
                            }
                        }
                        if (array.getJSONObject(i).has("Location")) {
                            if (!array.getJSONObject(i).get("Location").equals(null)) {
                                hazards.setLocation(array.getJSONObject(i).get("Location"));
                            }
                        }

                        if (array.getJSONObject(i).has("Latitude")) {
                            if (!array.getJSONObject(i).get("Latitude").equals(null)) {
                                hazards.setLatitude(array.getJSONObject(i).get("Latitude"));
                            }
                        }
                        if (array.getJSONObject(i).has("Longitude")) {
                            if (!array.getJSONObject(i).get("Longitude").equals(null)) {
                                hazards.setLongitude(array.getJSONObject(i).get("Longitude"));
                            }
                        }
                        if (array.getJSONObject(i).has("Detail")) {
                            if (!array.getJSONObject(i).get("Detail").equals(null)) {
                                hazards.setDetail(""+ array.getJSONObject(i).get("Detail"));
                            }
                        }


                        JSONArray hazardDetailArray = array.getJSONObject(i).getJSONArray("HazardDetails");

                        hazardDetail1list = new ArrayList<>();
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

                                }
                            }
                            if (hazardDetailArray.getJSONObject(j).has("VoiceNoteUrl")) {
                                if (!hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl").equals(null)) {
                                    hazardDetail.setVoiceNoteUrl("" + hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl"));

                                }
                            }
                            hazardDetail1list.add(hazardDetail);


                        }
                        hazards.setHazardDetails(hazardDetail1list);




                        double hazardlat, hazardlng;
                        hazardlat = Double.parseDouble(hazards.getLatitude().toString());
                        hazardlng = Double.parseDouble(hazards.getLongitude().toString());

                        if (mMap != null) {
                            if (opts != null) {
                                if (PolyUtil.isLocationOnPath(new LatLng(hazardlat, hazardlng), opts.getPoints(), opts.isGeodesic(), Constants.driverHazardsRadius)) {
                                    hazardCount++;
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat, hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));
                                    completeHazard.add(hazards);
                                }
                            }
                            //    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                        }


                    }
                    if (hazardCount > 0) {
                        btnHazardlist.setVisibility(View.VISIBLE);
                        btnHazardlist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChangeFragment(new BriefingHazardList());
                            }
                        });
                    } else {
                        btnHazardlist.setVisibility(View.GONE);
                    }

                    txtHazardsCount.setText(": " + hazardCount);
                } else if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ROUTEBYID))) {
                    AllPoints=new ArrayList<>();
                    ArrayList<RouteDetail> routeDetails = new ArrayList<>();
                    route = new Routes();
                    route.setOriginLat(0.0);
                    route.setOriginLong(0.0);
                    route.setDestinationLat(0.0);
                    route.setDestinationLong(0.0);
                    if (jsonObject.has("Data")) {
                        if (!jsonObject.isNull("Data")) {
                            JSONObject array = jsonObject.getJSONObject("Data");
                            int i = 0;
                            route = new Routes();
                            if (array.has("RouteID")) {
                                if (!array.get("RouteID").equals(null)) {
                                    route.setRouteID("" + array.get("RouteID"));
                                }
                            }
                            if (array.has("RouteName")) {
                                if (!array.get("RouteName").equals(null)) {
                                    route.setRouteName((String) array.get("RouteName"));
                                    txtRouteName.setText(": "+route.getRouteName());
                                }
                            }
                            if (array.has("RouteAddress")) {
                                if (!array.get("RouteAddress").equals(null)) {
                                    route.setRouteAddress("" + array.get("RouteAddress"));
                                }
                            }
                            if (array.has("Description")) {
                                if (!array.get("Description").equals(null)) {
                                    route.setDescription(array.get("Description"));
                                }
                            }

                            if (array.has("IsActive")) {
                                if (!array.get("IsActive").equals(null)) {
                                    route.setIsActive(array.get("IsActive"));
                                }
                            }
                            if (array.has("OriginLong")) {
                                if (!array.get("OriginLong").equals(null)) {
                                    route.setOriginLong((double) array.get("OriginLong"));
                                }
                            }
                            if (array.has("OriginLat")) {
                                if (!array.get("OriginLat").equals(null)) {
                                    route.setOriginLat((double) array.get("OriginLat"));
                                }
                            }
                            if (array.has("DestinationLong")) {
                                if (!array.get("DestinationLong").equals(null)) {
                                    route.setDestinationLong((double) array.get("DestinationLong"));
                                }
                            }
                            if (array.has("DestinationLat")) {
                                if (!array.get("DestinationLat").equals(null)) {
                                    route.setDestinationLat((double) array.get("DestinationLat"));
                                }
                            }

                                int stopsCount=0;
                            JSONArray RouteDetailArray = array.getJSONArray("RouteDetails");
                            routeDetails = new ArrayList<>();
                            RouteDetail rd = new RouteDetail();
                            for (int j = 0; j < RouteDetailArray.length(); j++) {

                                rd = new RouteDetail();
                                if (RouteDetailArray.getJSONObject(j).has("RouteDetailID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteDetailID").equals(null)) {
                                        rd.setRouteDetailID("" + RouteDetailArray.getJSONObject(j).get("RouteDetailID"));
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("RouteTypeID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals(null)) {
                                        if (RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals("1")) {

                                            waypoint = true;
                                            rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));

                                        } else {
                                            waypoint = false;

                                            rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));
                                        }
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("RouteID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteID").equals(null)) {
                                        rd.setRouteID("" + RouteDetailArray.getJSONObject(j).get("RouteID"));
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("RouteType")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteType").equals(null)) {
                                        rd.setRouteType("" + RouteDetailArray.getJSONObject(j).get("RouteType"));

                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("Name")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("Name").equals(null)) {
                                        rd.setName("" + RouteDetailArray.getJSONObject(j).get("Name"));

                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("Longitude")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("Longitude").equals(null)) {
                                        rd.setLongitude((Double) RouteDetailArray.getJSONObject(j).get("Longitude"));

                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("Latitude")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("Latitude").equals(null)) {
                                        rd.setLatitude((Double) RouteDetailArray.getJSONObject(j).get("Latitude"));

                                        if (waypoint == true) {
                                            waypoints.add("" + rd.getLatitude() + "," + "" + rd.getLongitude());
                                        } else {
                                            stops.add(new LatLng(rd.getLatitude(), rd.getLongitude()));
                                        }
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("OrderNumber")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("OrderNumber").equals(null)) {
                                        rd.setOrderNumber((Integer) RouteDetailArray.getJSONObject(j).get("OrderNumber"));

                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("GoogleAddress")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("GoogleAddress").equals(null)) {
                                        rd.setGoogleAddress("" + RouteDetailArray.getJSONObject(j).get("GoogleAddress"));
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("Detail")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("Detail").equals(null)) {
                                        rd.setDetail("" + RouteDetailArray.getJSONObject(j).get("Detail"));

                                    }
                                }

                                HashMap stopshash = new HashMap();
                                stopshash.put("typeid",rd.getRouteTypeID());
                                stopshash.put("stoplat", ""+rd.getLatitude());
                                stopshash.put("stoplng", ""+rd.getLongitude());

                                AllPoints.add(stopshash);
                                routeDetails.add(rd);
                            }
                            txtStopsCount.setText(""+stopsCount);

                            route.setRouteDetails(routeDetails);

                            btnStoplist.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle b=new Bundle();
                                    if (route.getOriginLat() != null && route.getOriginLat() != null && route.getDestinationLat() != null && route.getDestinationLong() != null) {
                                        b.putDouble("orgLat", route.getOriginLat());
                                        b.putDouble("orgLng", route.getOriginLong());
                                        b.putDouble("destLat", route.getDestinationLat());
                                        b.putDouble("destLng", route.getDestinationLong());
                                        b.putString("RouteName", route.getRouteName());
                                        b.putBoolean("AssignRoute",false);
                                        b.putBoolean("EditRoute",false);
                                        b.putBoolean("Briefing",true);


                                    } else {
                                        Toast.makeText(getContext(), "invalid route position: " + i, Toast.LENGTH_SHORT).show();
                                    }
                                    ChangeFragment(new StopDetails(), v,b);
                                }
                            });
                            createRoute(route);

                        }
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
    }

}
