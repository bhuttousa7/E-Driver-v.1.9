package com.telogix.telogixcaptain.activities.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.WorkaroundMapFragment;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.adapters.StopsAdapter;
import com.telogix.telogixcaptain.adapters.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignRouteDetail extends Fragment implements OnMapReadyCallback, response_interface {
    EditText edt_startloc, edt_endloc, editTextRouteName;
    Spinner spn_startCity, spn_endCity;
    double  orgLat, orgLong, destLat, destLng;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    String address,vehicleID,RouteID;
    Geocoder geocoder;
    private GoogleMap mMap;
    public static GoogleMap staticMap;
    Button assignRoute;
    ImageButton cross, cross_end;
    private EditText edt_Routename;
    private SupportMapFragment mapFragment;
    private ArrayList waypoints=new ArrayList();
    private ArrayList<LatLng> stops=new ArrayList();
    private ArrayList<Hazards> hazardslist;
    private PolylineOptions opts=null;
    private boolean trafficshowing=false;
    private CardView mapmenu;
    private RecyclerView recyclerViewWaypoint;
    private ArrayList<RouteDetail> Allpoints=new ArrayList<>();
    private CardView trafficlayout;
    private ImageView trafficImg;
    ScrollView scrollview;

    public AssignRouteDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_assign_route_detail, container, false);

        edt_startloc = v.findViewById(R.id.edt_location);
        edt_endloc = v.findViewById(R.id.edt_locationend);
        edt_Routename = v.findViewById(R.id.edt_routename);
        editTextRouteName=v.findViewById(R.id.edt_routename);
        scrollview=v.findViewById(R.id.scrollview);
        assignRoute=v.findViewById(R.id.btn_assignRoute);
        recyclerViewWaypoint=v.findViewById(R.id.recyclerViewstops);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewWaypoint.setLayoutManager(layoutManager);
        cross=v.findViewById(R.id.cross);
        cross_end=v.findViewById(R.id.cross_end);
        edt_endloc.setFocusable(false);
        edt_startloc.setFocusable(false);

        renderTrafficLayout(v);
        assignRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to assign route?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                HashMap data=new HashMap();
                                data.put("LoadAssignID",vehicleID);
                                data.put("RouteID", RouteID);
                               // Toast.makeText(getContext(),"Vehicleid:"+vehicleID+" routeid:"+RouteID,Toast.LENGTH_SHORT).show();
                                NetworkConsume.getInstance().ShowProgress(getContext());
                                httpvolley.stringrequestpost("api/RouteAssigns/PostRouteAssign", Request.Method.POST,data, AssignRouteDetail.this);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        return v;
    }
    private void getAllHazards() {

      //  NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET,hashMap , this);

    }

    private void renderTrafficLayout(View v) {
         trafficlayout=v.findViewById(R.id.traffic);
        mapmenu=v.findViewById(R.id.mapmenu);
         trafficImg=v.findViewById(R.id.imgtraffic);

        trafficlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trafficshowing)
                {
                    trafficshowing=false;
                    mMap.setTrafficEnabled(false);
                    trafficlayout.setBackgroundColor(Color.WHITE);
                    //trafficlayout.setBackground(getResources().getDrawable(R.drawable.roundview));
                    trafficImg.setImageResource(R.drawable.traffic);
                }
                else
                {
                    trafficshowing=true;
                    mMap.setTrafficEnabled(true);
                    trafficlayout.setBackgroundColor(Color.BLUE);
                    trafficImg.setImageResource(R.drawable.traffic_white);
                }
            }
        });
    }
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map" , "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
//        int checkItem = mMap.getMapType();
        int checkItem = mMap.getMapType() - 1;


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
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            }case 2: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            }
                            default:
                            {   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);}
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        getAllHazards();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orgLat = bundle.getDouble("orgLat", 0.0);
            orgLong = bundle.getDouble("orgLng", 0.0);
            destLat = bundle.getDouble("destLat", 0.0);
            destLng = bundle.getDouble("destLng", 0.0);
            vehicleID = bundle.getString("vehicleID", "");
            RouteID = bundle.getString("RouteID", "");
            String RouteName=bundle.getString("RouteName", "N/A");
            edt_Routename.setText(RouteName);
            edt_Routename.setEnabled(false);
            edt_startloc.setText(getAddress(orgLat,orgLong));
            edt_startloc.setEnabled(false);
            edt_endloc.setText(getAddress(destLat,destLng));
            edt_endloc.setEnabled(false);
            if(mapFragment!=null) {
                mapFragment.getMapAsync(this);
            }
        }

    }
    private String getAddress(double lat, double lng) {
        geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getAddressLine(0);

            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        trafficshowing=true;
        mMap.setTrafficEnabled(true);
        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch()
                    {
                        scrollview.requestDisallowInterceptTouchEvent(true);
                    }
                });
        trafficlayout.setBackgroundColor(Color.BLUE);
        trafficImg.setImageResource(R.drawable.traffic_white);
        mapmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });
        waypoints=new ArrayList();
        stops=new ArrayList();
        if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
            if(SwipeListAdapter.currentRouteClicked!=null) {
                if(SwipeListAdapter.currentRouteClicked.getRouteDetails().size()>0)
                {
                    Allpoints=new ArrayList<>();
                    for(int k = 0; k< SwipeListAdapter.currentRouteClicked.getRouteDetails().size(); k++)
                    {

                        RouteDetail routeDetail= SwipeListAdapter.currentRouteClicked.getRouteDetails().get(k);
                        Allpoints.add(routeDetail);
                        if(routeDetail.getRouteTypeID().equals("1")) {
                            waypoints.add("" + routeDetail.getLatitude() + "," + "" + routeDetail.getLongitude());
                        }
                        else
                        {
                            stops.add(new LatLng(routeDetail.getLatitude(),routeDetail.getLongitude()));
                        }
                    }


                }

                createRoute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Route Detail");
    }
    public String waypoints() {
        String points = "";
        for (int i = 0; i < Allpoints.size(); i++) {
//            if(i==0)
//            {
//                points=points+""+stops.get(i);
//            }
//            else {
//                points=points+","+stops.get(i);
//            }
            points = points + "" +  Allpoints.get(i).getLatitude()+","+Allpoints.get(i).getLongitude();
            points = points + "|";

        }
        try {
            if (Allpoints.size() > 0) {
                recyclerViewWaypoint.setAdapter(new StopsAdapter(getContext(),false, Allpoints,false));
            }
        }
        catch (Exception ex){}
        return points;
    }
    private void createRoute() {
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


        mMap.clear();

        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);
        }
        if(Allpoints!=null) {
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

//        for(int k=0;k<stops.size();k++)
//        {
//            try {
//
//
//                double stoplat=stops.get(k).latitude;
//                double stoplng=stops.get(k).longitude;
//                mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat,stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));
//
//            }
//            catch (Exception ex)
//            {
//
//            }
//        }
        LatLng fromPosition = new LatLng(orgLat, orgLong);
        LatLng toPosition = new LatLng(destLat, destLng);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(getContext(), MapDetailActivity.class);
                i.putExtra("routeid", SwipeListAdapter.currentRouteClicked.getRouteID());
                startActivity(i);
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    public void onResponse(String response) throws JSONException {
        Log.d("responserouteassign",""+response);
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("Message"))
            {
                if(jsonObject.get("Message").equals("Route Assigned"))
                {
                    Toast.makeText(getContext(),"Route Assigned",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else if(jsonObject.get("Message").equals("Hazards List"))
                {

                    JSONArray array = jsonObject.getJSONArray("Data");
                    Hazards hazards=new Hazards();

                    HazardDetail hazardDetail=new HazardDetail();
                    ArrayList<HazardDetail> hazardDetail1list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        hazards = new Hazards();

                        if(array.getJSONObject(i).has("HazardID"))
                        {
                            if(!array.getJSONObject(i).get("HazardID").equals(null))
                            {
                                hazards.setHazardID((Integer) array.getJSONObject(i).get("HazardID"));
                            }
                        }
                        if(array.getJSONObject(i).has("HazardName"))
                        {
                            if(!array.getJSONObject(i).get("HazardName").equals(null))
                            {
                                hazards.setHazardName((String) array.getJSONObject(i).get("HazardName"));
                            }
                        }
                        if(array.getJSONObject(i).has("HazardType"))
                        {
                            if(!array.getJSONObject(i).get("HazardType").equals(null))
                            {
                                hazards.setHazardType(""+ array.getJSONObject(i).get("HazardType"));
                            }
                        }
                        if(array.getJSONObject(i).has("Location"))
                        {
                            if(!array.getJSONObject(i).get("Location").equals(null))
                            {
                                hazards.setLocation(array.getJSONObject(i).get("Location"));
                            }
                        }

                        if(array.getJSONObject(i).has("Latitude"))
                        {
                            if(!array.getJSONObject(i).get("Latitude").equals(null))
                            {
                                hazards.setLatitude(array.getJSONObject(i).get("Latitude"));
                            }
                        }
                        if(array.getJSONObject(i).has("Longitude"))
                        {
                            if(!array.getJSONObject(i).get("Longitude").equals(null))
                            {
                                hazards.setLongitude(array.getJSONObject(i).get("Longitude"));
                            }
                        }
                        if(array.getJSONObject(i).has("Detail"))
                        {
                            if(!array.getJSONObject(i).get("Detail").equals(null))
                            {
                                hazards.setDetail(""+ array.getJSONObject(i).get("Detail"));
                            }
                        }


                        JSONArray hazardDetailArray = array.getJSONObject(i).getJSONArray("HazardDetails");


                        for (int j = 0; j < hazardDetailArray.length(); j++) {

                            hazardDetail=new HazardDetail();
                            hazardDetail1list=new ArrayList<>();
                            if(hazardDetailArray.getJSONObject(j).has("HazardDetailID"))
                            {
                                if(!hazardDetailArray.getJSONObject(j).get("HazardDetailID").equals(null))
                                {
                                    hazardDetail.setHazardDetailID((Integer) hazardDetailArray.getJSONObject(j).get("HazardDetailID"));
                                }
                            }
                            if(hazardDetailArray.getJSONObject(j).has("HazardID"))
                            {
                                if(!hazardDetailArray.getJSONObject(j).get("HazardID").equals(null))
                                {
                                    hazardDetail.setHazardID((Integer) hazardDetailArray.getJSONObject(j).get("HazardID"));
                                }
                            }
                            if(hazardDetailArray.getJSONObject(j).has("ImageUrl"))
                            {
                                if(!hazardDetailArray.getJSONObject(j).get("ImageUrl").equals(null))
                                {
                                    hazardDetail.setImageUrl("" + hazardDetailArray.getJSONObject(j).get("ImageUrl"));

                                }
                            }
                            if(hazardDetailArray.getJSONObject(j).has("VoiceNoteUrl"))
                            {
                                if(!hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl").equals(null))
                                {
                                    hazardDetail.setVoiceNoteUrl("" + hazardDetailArray.getJSONObject(j).get("VoiceNoteUrl"));

                                }
                            }
                            hazardDetail1list.add(hazardDetail);



                        }
                        hazards.setHazardDetails(hazardDetail1list);




                        double hazardlat,hazardlng;
                        hazardlat= Double.parseDouble(hazards.getLatitude().toString());
                        hazardlng=Double.parseDouble(hazards.getLongitude().toString());

                        if(mMap!=null)
                        {
                            if(opts!=null) {
                                if (PolyUtil.isLocationOnPath(new LatLng(hazardlat, hazardlng), opts.getPoints(), opts.isGeodesic(), Constants.driverHazardsRadius)) {
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                                }
                            }
                            //    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                        }


                    }

                }
            }


        }
        catch (Exception ex)
        {
            Log.d("responseroute_error",""+response);
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
Log.d("routeassign error:",""+Error);
    }




}
