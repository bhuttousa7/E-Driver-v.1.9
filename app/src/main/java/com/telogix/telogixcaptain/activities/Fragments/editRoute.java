package com.telogix.telogixcaptain.activities.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.adapters.RoutesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class editRoute extends Fragment implements OnMapReadyCallback, response_interface, View.OnClickListener  {
    EditText edt_startloc, edt_endloc, editTextRouteName;
    Spinner spn_startCity, spn_endCity;
    double  orgLat, orgLong, destLat, destLng;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    String address;
    Geocoder geocoder;
    private GoogleMap mMap;
    public static GoogleMap staticMap;
    Button uploadRoute;
    ImageButton cross, cross_end;
    private EditText edt_Routename;
    private SupportMapFragment mapFragment;
    private ArrayList waypoints=new ArrayList();
    private ArrayList<LatLng> stops=new ArrayList();
    private ArrayList<Hazards> hazardslist=new ArrayList<>();
    private PolylineOptions opts;
    private CardView mapmenu;
    private boolean trafficshowing=false;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    public editRoute() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_route_detail, container, false);

        edt_startloc = v.findViewById(R.id.edt_location);
        edt_endloc = v.findViewById(R.id.edt_locationend);
        edt_Routename = v.findViewById(R.id.edt_routename);
        editTextRouteName=v.findViewById(R.id.edt_routename);
        cross=v.findViewById(R.id.cross);
        cross_end=v.findViewById(R.id.cross_end);
        edt_endloc.setFocusable(false);
        edt_startloc.setFocusable(false);
        renderTrafficLayout(v);
        fab = v.findViewById(R.id.fab);
        fab1 = v.findViewById(R.id.fab1);
        fab2 = v.findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        return v;
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                // ChangeFragment(new AddRoutes());
              //  ChangeFragment(new editRoute());
                break;
            case R.id.fab2:


                break;
        }
    }
    private void ChangeFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void renderTrafficLayout(View v) {
        CardView trafficlayout=v.findViewById(R.id.traffic);
        mapmenu=v.findViewById(R.id.mapmenu);
        ImageView trafficImg=v.findViewById(R.id.imgtraffic);

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
                            }case 2: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            }case 3: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            }case 4: {
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                mMap.setTrafficEnabled(true);
                            }    break;
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
        getAllHazards();
        mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orgLat = bundle.getDouble("orgLat", 0.0);
            orgLong = bundle.getDouble("orgLng", 0.0);
            destLat = bundle.getDouble("destLat", 0.0);
            destLng = bundle.getDouble("destLng", 0.0);
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

    private void getAllHazards() {

        NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET,hashMap , this);

    }
    private String getAddress(double lat, double lng) {
        geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getAddressLine(0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        waypoints=new ArrayList();
        stops=new ArrayList();

        mapmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });
        if (orgLong != 0 && orgLat != 0 && destLng != 0 && destLat != 0) {
            if(RoutesAdapter.currentRouteClicked!=null) {
                if(RoutesAdapter.currentRouteClicked.getRouteDetails().size()>0)
                {
                    for(int k = 0; k< RoutesAdapter.currentRouteClicked.getRouteDetails().size(); k++)
                    {
                        RouteDetail routeDetail= RoutesAdapter.currentRouteClicked.getRouteDetails().get(k);
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
        else
        {
            createRoutefromDirectionResult();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Route Detail");
    }

    private void createRoutefromDirectionResult() {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(this.getResources().getString(R.string.GCP_API_Key))
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);
        try {
            DirectionsResult res = AddRoutes.directionsResult;
            //Loop through legs and steps to get encoded polylines of each step
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

//                    if(AddRoutes.fromPosition.longitude!=0.0 && AddRoutes.fromPosition.latitude!=0.0)
//                    {
//                        LatLng fromPosition = AddRoutes.fromPosition;
//                        LatLng toPosition = new LatLng(destLat, destLng);
//                        mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
//                        mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));
//                    }


                    for(int k = 0; k< AddRoutes.stopslist.size(); k++)
                    {
                        try {


                            double stoplat=Double.parseDouble(AddRoutes.stopslist.get(0).get("stoplat"));
                            double stoplng=Double.parseDouble(AddRoutes.stopslist.get(0).get("stoplng"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat,stoplng)).title("Stop"));
                        }
                        catch (Exception ex)
                        {

                        }
                    }

                }
            }
        } catch (Exception ex) {
            Log.e("exception", ex.getLocalizedMessage());
        }


        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);



            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
        LatLng fromPosition=new LatLng(orgLat,orgLong);
        LatLng toPosition=new LatLng(destLat,destLng);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 8));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 5.0f ) );
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+orgLat+","+orgLong+"&daddr="+destLat+","+destLng+""));
                startActivity(intent);
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //  mMap.getUiSettings().setScrollGesturesEnabled(false);
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
    private void createRoute() {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(this.getResources().getString(R.string.GCP_API_Key))
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
        for(int k=0;k<stops.size();k++)
        {
            try {


                double stoplat=stops.get(k).latitude;
                double stoplng=stops.get(k).longitude;
                mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat,stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));

            }
            catch (Exception ex)
            {

            }
        }

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
                i.putExtra("routeid", RoutesAdapter.currentRouteClicked.getRouteID());
                startActivity(i);
            }
        });

        //mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray array = jsonObject.getJSONArray("Data");
        Hazards hazards=new Hazards();
        hazardslist=new ArrayList<>();
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

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
