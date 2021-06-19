package com.telogix.telogixcaptain.activities.Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Pojo.pickupPojo.Datum;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.WorkaroundMapFragment;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.AddStopActivity;
import com.telogix.telogixcaptain.activities.MapActivity;
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.activities.viewRouteActivity;
import com.telogix.telogixcaptain.adapters.SimpleArrayListAdapterDecanting;
import com.telogix.telogixcaptain.adapters.SimpleArrayListAdapterPickup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;

import static android.app.Activity.RESULT_CANCELED;

//import com.google.gson.Gson;
//import com.google.maps.DirectionsApi;
//import com.schibstedspain.leku.LocationPickerActivity;
//import com.schibstedspain.leku.locale.SearchZoneRect;

//import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_DISPLAY_NAME;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_ID;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TRANSITION_BUNDLE;
//import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignCopyRoute extends Fragment implements Serializable,OnMapReadyCallback, response_interface {
    private static final int MAP_BUTTON_REQUEST_CODE_ORIGIN = 4;
    private static final int MAP_BUTTON_REQUEST_CODE_DEST = 5;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int STOPADDREQUEST = 99;
    EditText edt_startloc, edt_endloc, editTextRouteName;
    Spinner spn_startCity, spn_endCity;
    double latitude, longitude, orgLat, orgLong, destLat, destLng;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static ArrayList<HashMap<String, String>> AllPoints = new ArrayList<>();
    private Location mLastKnownLocation;
    String address;
    Geocoder geocoder;
    private GoogleMap mMap;
    public static GoogleMap staticMap;
    Button uploadRoute;
    ImageButton cross, cross_end;
    public ArrayList<String> stops = new ArrayList<>();
    DragLinearLayout stops_layout;
    public static DirectionsResult directionsResult;
    private SimpleArrayListAdapterDecanting mSimpleArrayListAdapterDecanting;
    private SimpleArrayListAdapterPickup simpleArrayListAdapterPickup;
    private int  decanting =0,pickup=0;
    public static ArrayList<HashMap<String, String>> stopslist=new ArrayList<>();
    private Button add_stop;
    public static LatLng fromPosition,toPosition;
    SearchableSpinner searchableSpinnerPickup1,searchableSpinnerDecenting1;
    private ArrayList<Datum> pickuploc_list;
    ScrollView scrollView;
    private ArrayList<com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> decantingloc_list;
    private PolylineOptions opts;
    private boolean trafficshowing=false;
    private CardView mapMenu;
    private String vehicleID,RouteID;
    private boolean AssignRoute;
    private boolean EditRoute;
    private Routes routes=null;

    public AssignCopyRoute() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            vehicleID = bundle.getString("vehicleID", "");
            RouteID = bundle.getString("RouteID", "");
            AssignRoute=bundle.getBoolean("AssignRoute");
            EditRoute=bundle.getBoolean("EditRoute");
             routes = (Routes) bundle.getSerializable("Route");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_routes, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        getDeviceLocation();
        getStartLocation();
        getDecantingLocations();
        searchableSpinnerPickup1 = v.findViewById(R.id.SearchableSpinnerPickup);
        searchableSpinnerDecenting1 = v.findViewById(R.id.SearchableSpinnerDecenting);
        searchableSpinnerPickup1.setVisibility(View.INVISIBLE);
        searchableSpinnerDecenting1.setVisibility(View.INVISIBLE);
        edt_startloc = v.findViewById(R.id.edt_location);
        edt_endloc = v.findViewById(R.id.edt_locationend);
        edt_startloc.setVisibility(View.VISIBLE);
        edt_endloc.setVisibility(View.VISIBLE);
        editTextRouteName = v.findViewById(R.id.edt_routename);
        stops_layout = v.findViewById(R.id.linearLayout1);
        scrollView = v.findViewById(R.id.scrollview);
        cross = v.findViewById(R.id.cross);
        cross_end = v.findViewById(R.id.cross_end);
        edt_endloc.setFocusable(false);
        edt_startloc.setFocusable(false);
        uploadRoute = v.findViewById(R.id.btn_uploadRoute);
        if (EditRoute) {
            uploadRoute.setText(R.string.editroute);
        } else
        {
            uploadRoute.setText(R.string.assignroute);
        }

        Button add_route = v.findViewById(R.id.btn_viewRoute);
        add_stop = v.findViewById(R.id.btn_addstop);
        renderTrafficLayout(v);
        add_stop.setEnabled(false);
        add_stop.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.divider), PorterDuff.Mode.MULTIPLY);
        searchableSpinnerPickup1.setOnItemSelectedListener(mOnItemSelectedListener);
        searchableSpinnerDecenting1.setOnItemSelectedListener(mOnItemSelectedListenerDecenting);
        add_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddStopActivity.class);
                i.putExtra("copyroute",true);
                startActivityForResult(i, STOPADDREQUEST);
            }
        });
        add_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               AddWaypointLayout();
                LayoutInflater layoutInflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                EditText editText = stopsview.findViewById(R.id.edt_location);
                ImageView imgtype=stopsview.findViewById(R.id.imgtype);
                imgtype.setImageResource(R.drawable.gps);
                TextView texttype=stopsview.findViewById(R.id.texttype);
                texttype.setText("waypoint");
                ImageButton cross = stopsview.findViewById(R.id.cross);
                editText.setFocusable(false);
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                        removePoint(position);

                        if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                            createRoute();
                        }
                    }
                });
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                        addStopsMapActivty(10, position);
                    }
                });
                stops_layout.addView(stopsview);
                for(int i = 0; i < stops_layout.getChildCount(); i++){
                    View child = stops_layout.getChildAt(i);
                    // the child will act as its own drag handle
                    stops_layout.setViewDraggable(child, child);
                }
                int position = stops_layout.indexOfChild(stopsview);
                stopsview.setTag(position);
                stopsview.findViewById(R.id.edt_location).setTag(position);
                stopsview.findViewById(R.id.cross).setTag(position);
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_startloc.setText("");
                orgLat = 0.0;
                orgLong = 0.0;
                edt_startloc.setVisibility(View.GONE);
                searchableSpinnerPickup1.setVisibility(View.VISIBLE);
            }
        });
        cross_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_endloc.setText("");
                destLat = 0.0;
                destLng = 0.0;
                edt_endloc.setVisibility(View.GONE);
                searchableSpinnerDecenting1.setVisibility(View.VISIBLE);

            }
        });
        uploadRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!editTextRouteName.getText().equals("") && orgLat != 0.0 && orgLong != 0.0 && destLat != 0.0 && destLng != 0.0) {
                        NetworkConsume.getInstance().ShowProgress(getContext());
                        HashMap<String, String> route = new HashMap();
                        route.put("OriginLat", "" + orgLat);
                        route.put("OriginLong", "" + orgLong);
                        route.put("DestinationLat", "" + destLat);
                        route.put("RouteName", "" + editTextRouteName.getText().toString());
                        route.put("RouteAddress", "" + address);
                        route.put("Description", "route description");
                        route.put("DestinationLong", "" + destLng);
                        if (AllPoints != null) {
                            for (int i = 0; i < AllPoints.size(); i++) {

                                route.put("RouteDetails[" + i + "].Latitude", "" + AllPoints.get(i).get("stoplat"));
                                route.put("RouteDetails[" + i + "].Longitude", "" + AllPoints.get(i).get("stoplng"));
                                route.put("RouteDetails[" + i + "].RouteTypeID", "" + AllPoints.get(i).get("typeid"));

                            }
                        }
//                        for (int i = 0; i < stops.size(); i++) {
//                            route.put("RouteDetails[" + i + "].Latitude", "" + stops.get(i).split(",")[0]);
//                            route.put("RouteDetails[" + i + "].Longitude", "" + stops.get(i).split(",")[1]);
//                            route.put("RouteDetails[" + i + "].RouteTypeID", "" + 1);
//                        }
//                        if (stopslist != null) {
//                            for (int i = 0; i < stopslist.size(); i++) {
//                                route.put("RouteDetails[" + i + "].Latitude", "" + stopslist.get(i).get("stoplat"));
//                                route.put("RouteDetails[" + i + "].Longitude", "" + stopslist.get(i).get("stoplng"));
//                                route.put("RouteDetails[" + i + "].RouteTypeID", "" + stopslist.get(i).get("typeid"));
//
//                            }
//                        }
                      //  NetworkConsume.getInstance().ShowProgress(getContext());
                        if(EditRoute)
                        {
                            route.put("RouteID", "" + RouteID);
                            httpvolley.stringrequestpost("api/Routes/EditRoute", Request.Method.PUT, route, AssignCopyRoute.this);

                        }
                        else
                        {
                            httpvolley.stringrequestpost("api/Routes/AddRoute", Request.Method.POST, route, AssignCopyRoute.this);

                        }

//                        new AlertDialog.Builder(getContext())
//                                .setTitle("Confirmation")
//                                .setMessage("Are you sure you want to add this route?")
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        NetworkConsume.getInstance().ShowProgress(getContext());
//                                        HashMap<String, String> route = new HashMap();
//                                        route.put("OriginLat", "" + orgLat);
//                                        route.put("OriginLong", "" + orgLong);
//                                        route.put("DestinationLat", "" + destLat);
//                                        route.put("RouteName", "" + editTextRouteName.getText().toString());
//                                        route.put("RouteAddress", "" + address);
//                                        route.put("Description", "route description");
//                                        route.put("DestinationLong", "" + destLng);
//                                        httpvolley.stringrequestpost("api/Routes/AddRoute", Request.Method.POST, route, AddRoutes.this);
//
//
//                                    }})
//                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        dialog.dismiss();
//                                    }} ).show();
//
                    } else {
                        Toast.makeText(getContext(), "Input all fields", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "Input all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        edt_startloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                openMap(MAP_BUTTON_REQUEST_CODE_ORIGIN);
            }
        });
        edt_endloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap(MAP_BUTTON_REQUEST_CODE_DEST);
            }
        });
        editTextRouteName.setText(routes.getRouteName());
        edt_startloc.setText(getAddress(routes.getOriginLat(),routes.getOriginLong()));
        edt_endloc.setText(getAddress(routes.getDestinationLat(),routes.getDestinationLong()));
        AutoFillWaypoints(routes);
        //draglistener
        stops_layout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition,
                               View secondView, int secondPosition) {
                View view=firstView.findViewById(R.id.edt_location);
                if(view instanceof EditText) {
                    if (((EditText) view).getText().toString() != "") {
                        if (AllPoints.size() > secondPosition && AllPoints.size() > firstPosition) {
                            Collections.swap(AllPoints, firstPosition, secondPosition);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    createRoute();
                                }
                            }, 400);
                        }
                    }
                }



            }

        });


        //

        return v;
    }

    private void AddWaypointLayout() {
        LayoutInflater layoutInflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
        EditText editText = stopsview.findViewById(R.id.edt_location);
        ImageButton cross = stopsview.findViewById(R.id.cross);
        editText.setFocusable(false);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                removeWaypoint(position);
                removePoint(position);
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                addStopsMapActivty(10, position);
            }
        });
        stops_layout.addView(stopsview);
        for(int i = 0; i < stops_layout.getChildCount(); i++){
            View child = stops_layout.getChildAt(i);
            // the child will act as its own drag handle
            stops_layout.setViewDraggable(child, child);
        }
        int position = stops_layout.indexOfChild(stopsview);
        stopsview.setTag(position);
        stopsview.findViewById(R.id.edt_location).setTag(position);
        stopsview.findViewById(R.id.cross).setTag(position);

    }

    public void AddStopLayout(String address){
        LayoutInflater layoutInflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View stopsview = layoutInflater.inflate(R.layout.hazardlistdesignbriefing, null);
        TextView txthazadname = stopsview.findViewById(R.id.txthazadname);
        txthazadname.setText(address);
        txthazadname.setMaxLines(2);
        txthazadname.setEllipsize(TextUtils.TruncateAt.MIDDLE);

        ImageButton cross = stopsview.findViewById(R.id.cross);
        TextView txthazadloc = stopsview.findViewById(R.id.txthazadloc);
        TextView txthazadtype = stopsview.findViewById(R.id.txthazadtype);
        Button btnhazardDetail = stopsview.findViewById(R.id.btnhazardDetail);
        txthazadloc.setVisibility(View.GONE);
        txthazadtype.setVisibility(View.GONE);
        btnhazardDetail.setVisibility(View.GONE);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                removeStop(position);
                removePoint(position);
            }
        });

        stops_layout.addView(stopsview);

        int position = stops_layout.indexOfChild(stopsview);
        stopsview.setTag(position);
        cross.setTag(position);
       // stopsview.findViewById(R.id.edt_location).setTag(position);
        //stopsview.findViewById(R.id.cross).setTag(position);

    }
    public void AutoFillWaypoints(Routes routes){
        orgLat=routes.getOriginLat();
        orgLong=routes.getOriginLong();
        destLat=routes.getDestinationLat();
        destLng=routes.getDestinationLong();

        for (int j=0;j< routes.getRouteDetails().size();j++)
        {
            try {


                if (routes.getRouteDetails().get(j).getRouteTypeID().equals("1")) {
                    HashMap stopshash = new HashMap();
                    stopshash.put("typeid", "1");
                    stopshash.put("stoplat", "" + routes.getRouteDetails().get(j).getLatitude());
                    stopshash.put("stoplng", "" + routes.getRouteDetails().get(j).getLongitude());

                    AllPoints.add(stopshash);
                    stops.add(routes.getRouteDetails().get(j).getLatitude() + "," + routes.getRouteDetails().get(j).getLongitude());
                    LayoutInflater layoutInflater = (LayoutInflater)
                            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                    EditText editText = stopsview.findViewById(R.id.edt_location);
                    ImageButton cross = stopsview.findViewById(R.id.cross);
                    editText.setText(getAddress(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude()));
                    editText.setFocusable(false);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                            removeWaypoint(position);
                            removePoint(position);
                        }
                    });
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                            addStopsMapActivty(10, position);
                        }
                    });
                    stops_layout.addView(stopsview);
                    for (int i = 0; i < stops_layout.getChildCount(); i++) {
                        View child = stops_layout.getChildAt(i);
                        // the child will act as its own drag handle
                        stops_layout.setViewDraggable(child, child);
                    }
                    int position = stops_layout.indexOfChild(stopsview);
                    stopsview.setTag(position);
                    stopsview.findViewById(R.id.edt_location).setTag(position);
                    stopsview.findViewById(R.id.cross).setTag(position);
                }
                else if (routes.getRouteDetails().get(j).getRouteTypeID().equals("4")) {
                    HashMap stopshash = new HashMap();
                    stopshash.put("typeid", "1");
                    stopshash.put("stoplat", "" + routes.getRouteDetails().get(j).getLatitude());
                    stopshash.put("stoplng", "" + routes.getRouteDetails().get(j).getLongitude());
                    AllPoints.add(stopshash);

                    if(mMap!=null) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));
                    }
                    stops.add(routes.getRouteDetails().get(j).getLatitude() + "," + routes.getRouteDetails().get(j).getLongitude());
                    LayoutInflater layoutInflater = (LayoutInflater)
                            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                    EditText editText = stopsview.findViewById(R.id.edt_location);
                    ImageButton cross = stopsview.findViewById(R.id.cross);
                    ImageView imgtype = stopsview.findViewById(R.id.imgtype);
                    imgtype.setImageResource(R.drawable.stopmap);
                    TextView texttype = stopsview.findViewById(R.id.texttype);
                    texttype.setText("Fuel Station");
                    editText.setText(getAddress(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude()));
                    editText.setFocusable(false);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                            removePoint(position);
                        }
                    });
                    stops_layout.addView(stopsview);
                    for (int i = 0; i < stops_layout.getChildCount(); i++) {
                        View child = stops_layout.getChildAt(i);
                        // the child will act as its own drag handle
                        stops_layout.setViewDraggable(child, child);
                    }
                    int position = stops_layout.indexOfChild(stopsview);
                    stopsview.setTag(position);
                    stopsview.findViewById(R.id.edt_location).setTag(position);
                    stopsview.findViewById(R.id.cross).setTag(position);
                }
                else if (routes.getRouteDetails().get(j).getRouteTypeID().equals("5")) {
                    HashMap stopshash = new HashMap();
                    stopshash.put("typeid", "1");
                    stopshash.put("stoplat", "" + routes.getRouteDetails().get(j).getLatitude());
                    stopshash.put("stoplng", "" + routes.getRouteDetails().get(j).getLongitude());
                    AllPoints.add(stopshash);
                    stops.add(routes.getRouteDetails().get(j).getLatitude() + "," + routes.getRouteDetails().get(j).getLongitude());
                    LayoutInflater layoutInflater = (LayoutInflater)
                            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                    EditText editText = stopsview.findViewById(R.id.edt_location);
                    ImageButton cross = stopsview.findViewById(R.id.cross);
                    ImageView imgtype = stopsview.findViewById(R.id.imgtype);
                    imgtype.setImageResource(R.drawable.food);
                    if(mMap!=null) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.food)).title("Stop"));
                    }
                    TextView texttype = stopsview.findViewById(R.id.texttype);
                    texttype.setText("Restaurant");
                    editText.setText(getAddress(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude()));
                    editText.setFocusable(false);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                            removePoint(position);
                        }
                    });
                    stops_layout.addView(stopsview);
                    for (int i = 0; i < stops_layout.getChildCount(); i++) {
                        View child = stops_layout.getChildAt(i);
                        // the child will act as its own drag handle
                        stops_layout.setViewDraggable(child, child);
                    }
                    int position = stops_layout.indexOfChild(stopsview);
                    stopsview.setTag(position);
                    stopsview.findViewById(R.id.edt_location).setTag(position);
                    stopsview.findViewById(R.id.cross).setTag(position);
                }
                else if (routes.getRouteDetails().get(j).getRouteTypeID().equals("6")) {
                    HashMap stopshash = new HashMap();
                    stopshash.put("typeid", "1");
                    stopshash.put("stoplat", "" + routes.getRouteDetails().get(j).getLatitude());
                    stopshash.put("stoplng", "" + routes.getRouteDetails().get(j).getLongitude());
                    AllPoints.add(stopshash);
                    stops.add(routes.getRouteDetails().get(j).getLatitude() + "," + routes.getRouteDetails().get(j).getLongitude());
                    LayoutInflater layoutInflater = (LayoutInflater)
                            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                    EditText editText = stopsview.findViewById(R.id.edt_location);
                    ImageButton cross = stopsview.findViewById(R.id.cross);
                    ImageView imgtype = stopsview.findViewById(R.id.imgtype);
                    imgtype.setImageResource(R.drawable.restarea);
                    TextView texttype = stopsview.findViewById(R.id.texttype);
                    texttype.setText("Rest Area");
                    if(mMap!=null) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.restarea)).title("Rest Area"));
                    }
                    editText.setText(getAddress(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude()));
                    editText.setFocusable(false);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                            removePoint(position);
                        }
                    });
                    stops_layout.addView(stopsview);
                    for (int i = 0; i < stops_layout.getChildCount(); i++) {
                        View child = stops_layout.getChildAt(i);
                        // the child will act as its own drag handle
                        stops_layout.setViewDraggable(child, child);
                    }
                    int position = stops_layout.indexOfChild(stopsview);
                    stopsview.setTag(position);
                    stopsview.findViewById(R.id.edt_location).setTag(position);
                    stopsview.findViewById(R.id.cross).setTag(position);
                } else {
                    HashMap stopshash = new HashMap();
                    stopshash.put("typeid", "1");
                    stopshash.put("stoplat", "" + routes.getRouteDetails().get(j).getLatitude());
                    stopshash.put("stoplng", "" + routes.getRouteDetails().get(j).getLongitude());
                    AllPoints.add(stopshash);
                    if(mMap!=null) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_bus)).title("Stop"));
                    }
                    stops.add(routes.getRouteDetails().get(j).getLatitude() + "," + routes.getRouteDetails().get(j).getLongitude());
                    LayoutInflater layoutInflater = (LayoutInflater)
                            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                    EditText editText = stopsview.findViewById(R.id.edt_location);
                    ImageButton cross = stopsview.findViewById(R.id.cross);
                    ImageView imgtype = stopsview.findViewById(R.id.imgtype);
                    imgtype.setImageResource(R.drawable.stop_bus);
                    TextView texttype = stopsview.findViewById(R.id.texttype);
                    texttype.setText("Stop");
                    editText.setText(getAddress(routes.getRouteDetails().get(j).getLatitude(), routes.getRouteDetails().get(j).getLongitude()));
                    editText.setFocusable(false);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (Integer) v.getTag();
                            //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                            removePoint(position);
                        }
                    });
                    stops_layout.addView(stopsview);
                    for (int i = 0; i < stops_layout.getChildCount(); i++) {
                        View child = stops_layout.getChildAt(i);
                        // the child will act as its own drag handle
                        stops_layout.setViewDraggable(child, child);
                    }
                    int position = stops_layout.indexOfChild(stopsview);
                    stopsview.setTag(position);
                    stopsview.findViewById(R.id.edt_location).setTag(position);
                    stopsview.findViewById(R.id.cross).setTag(position);
                }
            }catch (Exception e){

            }
        }

    }
    private void renderTrafficLayout(View v) {
        CardView trafficlayout=v.findViewById(R.id.traffic);
        ImageView trafficImg=v.findViewById(R.id.imgtraffic);
        mapMenu=v.findViewById(R.id.mapmenu);
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
    private final OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
//            Toast.makeText(context, "Item on position " + position + " : " + simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLocation() + " Selected", Toast.LENGTH_SHORT).show();
            pickup = simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLocationID();

            orgLat= simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLatitude();
            orgLong=simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLongitude();
            if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                NetworkConsume.getInstance().ShowProgress(getContext());
                createRoute();
                getAllHazards();
            }
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(getContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };
    private final OnItemSelectedListener mOnItemSelectedListenerDecenting = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
//            Toast.makeText(context, "Item on position " + position + " : " + mSimpleArrayListAdapterDecanting.getItem(position-1).getDecantingSite() + " Selected", Toast.LENGTH_SHORT).show();
            decanting = mSimpleArrayListAdapterDecanting.getmBackupStrings().get(position-1).getDecantingSiteID();
            destLat = mSimpleArrayListAdapterDecanting.getmBackupStrings().get(position-1).getDecantingLatitude();
            destLng = mSimpleArrayListAdapterDecanting.getmBackupStrings().get(position-1).getDecantingLongitude();

            if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                NetworkConsume.getInstance().ShowProgress(getContext());
                createRoute();
                getAllHazards();
            }
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(getContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };
    private void getAllHazards() {


        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET,hashMap , this);

    }
    private void getDecantingLocations() {
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/DecantingSites/GetDecantingSites",Request.Method.GET,hashMap,this);
    }

    private void getStartLocation() {
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/PickupLocations/GetPickupLocations",Request.Method.GET,hashMap,this);
    }


    private void removeWaypoint(int position) {
        if (stops.size() == 1) {
            stops.clear();
        } else if (stops.size() > position) {
            stops.remove(position);
        }

        stops_layout.removeView(stops_layout.findViewWithTag(position));

        createRoute();
        getAllHazards();
    }
    private void removePoint(int position) {
        if (AllPoints.size() == 1) {
            AllPoints.clear();
        } else if (AllPoints.size() > position) {
            AllPoints.remove(position);
        }

        stops_layout.removeView(stops_layout.findViewWithTag(position));
    }
    private void removeStop(int position) {
        if (stopslist.size() == 1) {
            stopslist.clear();
        } else if (stops.size() > position) {
            stopslist.remove(position);
        }

        stops_layout.removeView(stops_layout.findViewWithTag(position));

        createRoute();
        getAllHazards();
    }
    private void addStopsMapActivty(int requestCode, int position) {
        Intent i = new Intent(getContext(), MapActivity.class);
        i.putExtra("position", "" + position);
        startActivityForResult(i, requestCode);

    }


    private void openMap(int requestcode) {
        Intent locationPickerIntent = new Intent(getContext(), MapActivity.class);
        startActivityForResult(locationPickerIntent, requestcode);

    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == 3 && data != null) {
                if (requestCode == MAP_BUTTON_REQUEST_CODE_ORIGIN) {
                    String orgLt = data.getStringExtra("latitude");
                    String orgLg = data.getStringExtra("longitude");
                    if (!orgLt.equals(null) && !orgLt.equals(null) && !orgLg.equals(null) && !orgLg.equals("")) {
                        orgLat = Double.parseDouble(orgLt);
                        orgLong = Double.parseDouble(orgLg);
                    }

//                    Toast.makeText(getContext(), "destination" + data.getDoubleExtra(LATITUDE, 0.0), Toast.LENGTH_SHORT).show();

                    geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                    try {
                        List<Address> listaddress = geocoder.getFromLocation(orgLat, orgLong, 1);
                        for (int i = 0; i < listaddress.size(); i++) {
                            edt_startloc.setText("" + listaddress.get(i).getAddressLine(0));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == MAP_BUTTON_REQUEST_CODE_DEST) {
                    geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                    try {
                        String destLt = data.getStringExtra("latitude");
                        String destLg = data.getStringExtra("longitude");
                        if (!destLt.equals(null) && !destLt.equals(null) && !destLg.equals(null) && !destLg.equals("")) {
                            destLat = Double.parseDouble(destLt);
                            destLng = Double.parseDouble(destLg);
                            List<Address> listaddress = geocoder.getFromLocation(destLat, destLng, 1);
                            for (int i = 0; i < listaddress.size(); i++) {

                                address = listaddress.get(i).getAddressLine(0);
                                edt_endloc.setText("" + address);

                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == STOPADDREQUEST) {
                    geocoder = new Geocoder(getContext(), Locale.ENGLISH);
             //old
//                    try {
//                        String stoplat = data.getStringExtra("latitude");
//                        String stopLong = data.getStringExtra("longitude");
//                        String stoptypeID = data.getStringExtra("StopTypeID");
//                        if (!stoplat.equals(null) && !stoplat.equals(null) && !stopLong.equals(null) && !stopLong.equals("") && !stoptypeID.equals("")) {
//                            double stopLat = Double.parseDouble(stoplat);
//                            double stopLng = Double.parseDouble(stopLong);
//                            List<Address> listaddress = geocoder.getFromLocation(stopLat, stopLng, 1);
//                            for (int i = 0; i < listaddress.size(); i++) {
//
//                                address = listaddress.get(i).getAddressLine(0);
//
//                              //  TextView txtview_stops = new TextView(getContext());
//                                //txtview_stops.setText(address);
//                                //stops_layout.addView(txtview_stops);
//
//
//                                HashMap stopshash = new HashMap();
//                                stopshash.put("typeid", stoptypeID);
//                                stopshash.put("stoplat", stoplat);
//                                stopshash.put("stoplng", stopLong);
//                                stopslist.add(stopshash);
//                                stops.add(stoplat+","+stopLong);
//                                AllPoints.add(stopshash);
//                                AddStopLayout(address);
////                                int position = stops_layout.indexOfChild(txtview_stops);
////                                txtview_stops.setTag(position);
////                                txtview_stops.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        stops_layout.removeView(stops_layout.findViewWithTag(v.getTag()));
////                                    }
////                                });
//
//                            }
//
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                    try {
                        String stoplat = data.getStringExtra("latitude");
                        String stopLong = data.getStringExtra("longitude");
                        String stoptypeID = data.getStringExtra("StopTypeID");
                        if (!stoplat.equals(null) && !stoplat.equals(null) && !stopLong.equals(null) && !stopLong.equals("") && !stoptypeID.equals("")) {
                            double stopLat = Double.parseDouble(stoplat);
                            double stopLng = Double.parseDouble(stopLong);
                            HashMap stopshash = new HashMap();
                            stopshash.put("typeid", stoptypeID);
                            stopshash.put("stoplat", stoplat);
                            stopshash.put("stoplng", stopLong);
                            stopslist.add(stopshash);
                            AllPoints.add(stopshash);


//                       old code end

//                 new code start

                            LayoutInflater layoutInflater = (LayoutInflater)
                                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View stopsview = layoutInflater.inflate(R.layout.addwaypoint_design, null);
                            EditText editText = stopsview.findViewById(R.id.edt_location);
                            TextView texttype=stopsview.findViewById(R.id.texttype);
                            ImageView imgtype=stopsview.findViewById(R.id.imgtype);
                            if(stoptypeID.equals("4"))
                            {
                                imgtype.setImageResource(R.drawable.stopmap);
                            }else if(stoptypeID.equals("5"))
                            {
                                imgtype.setImageResource(R.drawable.food);
                            }
                            else if(stoptypeID.equals("6"))
                            {
                                imgtype.setImageResource(R.drawable.restarea);
                            }
                            else
                            {
                                imgtype.setImageResource(R.drawable.stop_bus);
                            }
                            texttype.setText("stop");
                            ImageButton cross = stopsview.findViewById(R.id.cross);
                            editText.setFocusable(false);
                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int position = (Integer) v.getTag();
                                    //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                                    removePoint(position);
                                    if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                                        createRoute();
                                    }
                                }
                            });
                            editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int position = (Integer) v.getTag();
                                    //    Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                                    addStopsMapActivty(10, position);
                                }
                            });
                            stops_layout.addView(stopsview);
                            for(int i = 0; i < stops_layout.getChildCount(); i++){
                                View child = stops_layout.getChildAt(i);
                                // the child will act as its own drag handle
                                stops_layout.setViewDraggable(child, child);
                            }
                            int position = stops_layout.indexOfChild(stopsview);
                            stopsview.setTag(position);
                            stopsview.findViewById(R.id.edt_location).setTag(position);
                            stopsview.findViewById(R.id.cross).setTag(position);
                            populateStopsText(getAddress(stopLat, stopLng), position);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                    createRoute();
                }


            }
            if (resultCode == 3) {
                if (requestCode == 10) {
                    try {


                        String stopLat = data.getStringExtra("latitude");

                        String stopLng = data.getStringExtra("longitude");


                        String position = data.getStringExtra("position");
                        Log.d("stop position", position);
                        String stopsLatlng = stopLat + "," + stopLng;
                        HashMap stopshash = new HashMap();
                        stopshash.put("typeid", "1");
                        stopshash.put("stoplat", stopLat);
                        stopshash.put("stoplng", stopLng);
                        AllPoints.add(stopshash);
                        stops.add(stopsLatlng);
                        AllPoints.add(stopshash);
                        //stops.add(""+stopLat+","+stopLng);
                        populateStopsText(getAddress(Double.parseDouble(stopLat), Double.parseDouble(stopLng)), Integer.parseInt(position));
                        if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
                            createRoute();
                        }
                    } catch (Exception ex) {

                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d("RESULT****", "CANCELLED");
            }

        } catch (Exception ex) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AllPoints.clear();
    }

    private String getAddress(double lat, double lng) {
        geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getAddressLine(0);

            }
        } catch (Exception e) {
            Log.d("error",e+"");
        }
        return "";
    }

    private void populateStopsText(String address, int position) {
        ConstraintLayout v = stops_layout.findViewWithTag(position);
        EditText edt = v.findViewById(R.id.edt_location);
        edt.setText("" + address);

    }

    public String waypoints() {
        String points = "";
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
//        String points = "";
//        for (int i = 0; i < stops.size(); i++) {
////            if(i==0)
////            {
////                points=points+""+stops.get(i);
////            }
////            else {
////                points=points+","+stops.get(i);
////            }
//            points = points + "" + stops.get(i);
//            points = points + "|";
//
//        }
//        return points;
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
            directionsResult = res;
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
            Log.e("exception", ""+ex);

        }


        mMap.clear();

        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
        NetworkConsume.getInstance().hideProgress();
        fromPosition = new LatLng(orgLat, orgLong);
        toPosition = new LatLng(destLat, destLng);
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(getContext(), MapDetailActivity.class);
                i.putExtra("routeid",""+RouteID);
                startActivity(i);
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(false);
        add_stop.setEnabled(true);
        add_stop.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.red), PorterDuff.Mode.MULTIPLY);

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

                } else if(stopsHash.get("typeid").equals("1"))
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
    }

    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void viewRoute(GoogleMap mMap) {
        Intent i = new Intent(getContext(), viewRouteActivity.class);
        staticMap = mMap;
        startActivity(i);

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        getLocationPermission();
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation =
                                    mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                latitude = mLastKnownLocation.getLatitude();
                                longitude = mLastKnownLocation.getLongitude();
                                Log.d("latitide", "Latitude: " + mLastKnownLocation.getLatitude());
                                Log.d("longitude", "Longitude: " + mLastKnownLocation.getLongitude());
                            }
                        } else {
                            Toast.makeText(getContext(), "Current location not found", Toast.LENGTH_SHORT).show();

                        }


                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
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

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("ResponseCode"))
            {
                if(jsonObject.get("ResponseCode").equals(9002))
                {
                    NetworkConsume.getInstance().hideProgress();
                    pickuploc_list=new ArrayList<>();
                    Datum pickup=new Datum();
                    if(jsonObject.get("Data")!=null)
                    {
                        JSONArray dataarray=jsonObject.getJSONArray("Data");

                        for(int i=0;i<dataarray.length();i++)
                        {
                            pickup=new Datum();
                            pickup.setPickupLocationID(dataarray.getJSONObject(i).getInt("PickupLocationID"));
                            pickup.setPickupLocation(""+dataarray.getJSONObject(i).get("PickupLocationName"));
                            pickup.setPickupLatitude(dataarray.getJSONObject(i).getDouble("PickupLatitude"));
                            pickup.setPickupLongitude(dataarray.getJSONObject(i).getDouble("PickupLongitude"));
                            if(!dataarray.getJSONObject(i).isNull("Detail"))
                            {
                                pickup.setDetail(""+dataarray.getJSONObject(i).get("Detail"));

                            }

                            pickuploc_list.add(pickup);
                        }
                        simpleArrayListAdapterPickup = new SimpleArrayListAdapterPickup(getContext(), pickuploc_list);
                        searchableSpinnerPickup1.setAdapter(simpleArrayListAdapterPickup);

                    }


                }else  if(jsonObject.get("ResponseCode").equals(3002))
                {
                    NetworkConsume.getInstance().hideProgress();
                    decantingloc_list=new ArrayList<>();
                    com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum decanting=new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum();
                    if(jsonObject.get("Data")!=null)
                    {
                        JSONArray dataarray=jsonObject.getJSONArray("Data");

                        for(int i=0;i<dataarray.length();i++)
                        {
                            decanting=new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum();
                            decanting.setDecantingSiteID(dataarray.getJSONObject(i).getInt("DecantingSiteID"));
                            decanting.setDecantingSite(""+dataarray.getJSONObject(i).get("DecantingSiteName"));
                            decanting.setDecantingLatitude(dataarray.getJSONObject(i).getDouble("DecantingLatitude"));
                            decanting.setDecantingLongitude(dataarray.getJSONObject(i).getDouble("DecantingLongitude"));
                            if(!dataarray.getJSONObject(i).isNull("Detail")) {
                                decanting.setDetail("" + dataarray.getJSONObject(i).get("Detail"));
                            }
                            decantingloc_list.add(decanting);
                        }
                        mSimpleArrayListAdapterDecanting = new SimpleArrayListAdapterDecanting(getContext(), decantingloc_list);
                        searchableSpinnerDecenting1.setAdapter(mSimpleArrayListAdapterDecanting);

                    }

                }
                else if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ADDROUTE)))
                {
                    if (jsonObject.has("Status")) {
                        if (jsonObject.get("Status").equals(true)) {
                            stopslist.clear();
                            directionsResult=null;
                            if(AssignRoute)
                            {
                            if(jsonObject.has("Data"))
                            {
                                String routeID=jsonObject.getJSONObject("Data").getString("RouteID");
                                AssignRoute(vehicleID,routeID);
                            }
                            }
                            else
                            {
                                AllPoints.clear();
                                Toast.makeText(getContext(),"Route Added",Toast.LENGTH_SHORT).show();
                                 getActivity().onBackPressed();

                            }
                        }
                    }
                }
                else if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.EDITROUTE)))
                {
                            if(AssignRoute) {
                                stopslist.clear();
                                directionsResult = null;

AllPoints.clear();
                                Toast.makeText(getContext(), "Route Assigned", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();

                            }else if(EditRoute)
                            {
                                stopslist.clear();
                                directionsResult = null;

                                AllPoints.clear();
                                Toast.makeText(getContext(), "Route Edited", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }


                }
                else if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ASSIGNROUTE)))
                {
                    NetworkConsume.getInstance().hideProgress();

                    stopslist.clear();
                    directionsResult=null;
                    Toast.makeText(getContext(),"Route Assigned",Toast.LENGTH_SHORT).show();
                    AllPoints.clear();
                    getActivity().onBackPressed();
                }
                else if(jsonObject.get("ResponseCode").equals(6002))
                { NetworkConsume.getInstance().hideProgress();
                    JSONArray array = jsonObject.getJSONArray("Data");
                    Hazards hazards=new Hazards();
                    ArrayList<Hazards> hazardslist = new ArrayList<>();
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

                        hazardslist.add(hazards);


                        double hazardlat,hazardlng;
                        hazardlat= Double.parseDouble(hazards.getLatitude().toString());
                        hazardlng=Double.parseDouble(hazards.getLongitude().toString());

                        if(mMap!=null)
                        {
                            if(opts!=null) {
                                if (PolyUtil.isLocationOnPath(new LatLng(hazardlat, hazardlng), opts.getPoints(), opts.isGeodesic(), Constants.stopMarkerRadius)) {
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                                }
                            }
                            //    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                        }


//            hazardDetails.add(hazardDetail);
//            hazards.setHazardDetails(hazardDetails);
//            completeHazard.add(hazards);
//



                    }

                }
            }

        } catch (Exception ex) {

        }
    }

    public void AssignRoute(String LOADID,String RouteID){
        HashMap data=new HashMap();
        data.put("LoadAssignID",LOADID);
        data.put("RouteID", RouteID);
        // Toast.makeText(getContext(),"Vehicleid:"+vehicleID+" routeid:"+RouteID,Toast.LENGTH_SHORT).show();
      //  NetworkConsume.getInstance().ShowProgress(getContext());
        httpvolley.stringrequestpost("api/RouteAssigns/PostRouteAssign", Request.Method.POST,data, AssignCopyRoute.this);

    }
    @Override
    public void onDetach() {
        if(searchableSpinnerPickup1!=null)
        {
            searchableSpinnerPickup1.hideEdit();
        }
        if(searchableSpinnerDecenting1!=null)
        {
            searchableSpinnerPickup1.hideEdit();
        }
        super.onDetach();
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Log.d("error add route", "" + Error);
    }

    private double distance(double originLat, double originLong, double destLat, double destLng) {
        double theta = originLong - destLng;
        double dist = Math.sin(deg2rad(originLat)) * Math.sin(deg2rad(destLat)) + Math.cos(deg2rad(originLat)) * Math.cos(deg2rad(destLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch()
                    {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
        mapMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });
        if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
            NetworkConsume.getInstance().ShowProgress(getContext());
            createRoute();
            getAllHazards();
        }


    }


    //.....Mapbox


}




