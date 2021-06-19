package com.telogix.telogixcaptain.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
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
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.HazardDetail;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.Fragments.AddRoutes;
import com.telogix.telogixcaptain.activities.Fragments.AssignNewRouteFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapDetailActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, response_interface {

    private GoogleMap mMap;
    private LocationManager locationManager;
    double latidude, longitude;
    private TextView txtView;
    private double orgLat,orgLong,destLat,destLng;
    private ArrayList waypoints=new ArrayList();
    private ArrayList<LatLng> stops=new ArrayList();
    private PolylineOptions opts=null;
    private CardView mapMenu;
    boolean trafficshowing=false;
    public static ArrayList<HashMap<String, String>> AllPoints = new ArrayList<>();
    private double Hazardlatitude =0.0,Hazardlongitude=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            renderTrafficLayout();
            try {
                if (getIntent().hasExtra("routeid")) {
                    String routeid = getIntent().getStringExtra("routeid");
                    if (!routeid.equals("")) {
                        getRoutebyID(Integer.parseInt(routeid));
                    }
                    Hazardlatitude=0.0;
                    Hazardlongitude=0.0;
                }
                else if(getIntent().hasExtra("hazarddetail"))
                {
                     Hazardlatitude=Double.parseDouble(getIntent().getStringExtra("latitude"));
                     Hazardlongitude=Double.parseDouble(getIntent().getStringExtra("longitude"));
                }
                else
                {
                    Hazardlatitude=0.0;
                    Hazardlongitude=0.0;

                }            }
            catch (Exception ex){

            }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
         mapMenu=findViewById(R.id.mapmenu);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }

    private void renderTrafficLayout() {
        CardView trafficlayout=findViewById(R.id.traffic);
        ImageView trafficImg=findViewById(R.id.imgtraffic);

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

    private void getRoutebyID(int id) {

        NetworkConsume.getInstance().ShowProgress(this);
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Routes/GetRouteByRouteID?RouteID="+id, Request.Method.GET,hashMap , this);

    }


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
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain","Traffic"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        Toast.makeText(MapDetailActivity.this,""+item,Toast.LENGTH_SHORT).show();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
if(Hazardlatitude!=0.0 && Hazardlongitude!=0.0)
{
    mMap.addMarker(new MarkerOptions().position(new LatLng(Hazardlatitude,Hazardlongitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("Hazard"));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Hazardlatitude,Hazardlongitude), 14), 1500, null);
}
       mapMenu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showMapTypeSelectorDialog();
           }
       });
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latidude=mMap.getCameraPosition().target.latitude;
                longitude=mMap.getCameraPosition().target.longitude;
            }
        });
        if(!getIntent().hasExtra("routeid") && !getIntent().hasExtra("newAssignRoute")) {
            createRoutefromDirectionResult(AddRoutes.directionsResult);
        }
        else if(getIntent().hasExtra("newAssignRoute"))
        {
            createRoutefromDirectionResult(AssignNewRouteFragment.directionsResult);
        }

        }
    private void createRoutefromDirectionResult(DirectionsResult result) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);
        try {
            DirectionsResult res =result;

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
                    if(getIntent().hasExtra("newAssignRoute"))
                    {
                        mMap.addMarker(new MarkerOptions().position(AssignNewRouteFragment.fromPosition).title("Start"));
                        mMap.addMarker(new MarkerOptions().position(AssignNewRouteFragment.toPosition).title("End"));
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boundsBuilder.include(AssignNewRouteFragment.fromPosition);
                        boundsBuilder.include(AssignNewRouteFragment.toPosition);

                        final LatLngBounds bounds = boundsBuilder.build();
                        //    viewRoute(mMap);
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                            }
                        });
                    }
                    else if(AddRoutes.fromPosition.latitude!=0.0 && AddRoutes.fromPosition.longitude!=0.0)
                    {

                        mMap.addMarker(new MarkerOptions().position(AddRoutes.fromPosition).title("Start"));
                        mMap.addMarker(new MarkerOptions().position(AddRoutes.toPosition).title("End"));
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boundsBuilder.include(AddRoutes.fromPosition);
                        boundsBuilder.include(AddRoutes.toPosition);

                        final LatLngBounds bounds = boundsBuilder.build();
                        //    viewRoute(mMap);
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                            }
                        });
                    }


                    for(int k = 0; k< AddRoutes.stopslist.size(); k++)
                    {
                        try {


                            double stoplat=Double.parseDouble(AddRoutes.stopslist.get(k).get("stoplat"));
                            double stoplng=Double.parseDouble(AddRoutes.stopslist.get(k).get("stoplng"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat,stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));
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



        //    viewRoute(mMap);



        mMap.getUiSettings().setZoomControlsEnabled(true);
        //  mMap.getUiSettings().setScrollGesturesEnabled(false);
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
//        for (int i = 0; i < waypoints.size(); i++) {
//            points = points + "" + waypoints.get(i);
//            points = points + "|";
//
//        }
//        return points;
    }

    private void getAllHazards() {


        String token = NetworkConsume.getInstance().get_accessToken(this);
        HashMap hashMap=new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET,hashMap , this);

    }
    private void createRoute(Routes currentroute) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().ShowProgress(this);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);

        try {

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
            Toast.makeText(this, "" + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }


        mMap.clear();

        //Draw the polyline
        if (path.size() > 0) {
            opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllHazards();
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        waypoints=new ArrayList();
        stops=new ArrayList();
         boolean waypoint=false;
        try {


            ArrayList<RouteDetail> routeDetails = new ArrayList<>();
            Routes route=new Routes();
            JSONObject jsonObject = new JSONObject(response);
            route.setOriginLat(0.0);
            route.setOriginLong(0.0);
            route.setDestinationLat(0.0);
            route.setDestinationLong(0.0);
            if (jsonObject.has("ResponseCode"))
            {
                if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ALLHAZARDS)))
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
                else if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.ROUTEBYID)))
                {
                    AllPoints = new ArrayList<>();
                    if(jsonObject.has("Data"))
                    {
                        if(!jsonObject.isNull("Data")) {
                            JSONObject array = jsonObject.getJSONObject("Data");
                            int i=0;
                            route = new Routes();
                            if (array.has("RouteID")) {
                                if (!array.get("RouteID").equals(null)) {
                                    route.setRouteID(""+ array.get("RouteID"));
                                }
                            }
                            if (array.has("RouteName")) {
                                if (!array.get("RouteName").equals(null)) {
                                    route.setRouteName((String) array.get("RouteName"));
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


                            JSONArray RouteDetailArray = array.getJSONArray("RouteDetails");
                            routeDetails = new ArrayList<>();
                            RouteDetail rd = new RouteDetail();
                            for (int j = 0; j < RouteDetailArray.length(); j++) {
                                rd = new RouteDetail();
                                if (RouteDetailArray.getJSONObject(j).has("RouteDetailID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteDetailID").equals(null)) {
                                        rd.setRouteDetailID(""+ RouteDetailArray.getJSONObject(j).get("RouteDetailID"));
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("RouteTypeID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals(null)) {
                                        if (RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals("1")) {

                                            waypoint = true;
                                        } else {
                                            waypoint = false;
                                            rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));
                                        }
                                    }
                                }
                                if (RouteDetailArray.getJSONObject(j).has("RouteID")) {
                                    if (!RouteDetailArray.getJSONObject(j).get("RouteID").equals(null)) {
                                        rd.setRouteID(""+ RouteDetailArray.getJSONObject(j).get("RouteID"));
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
                                stopshash.put("typeid", ""+RouteDetailArray.getJSONObject(j).get("RouteTypeID"));
                                stopshash.put("stoplat", ""+ RouteDetailArray.getJSONObject(j).get("Latitude"));
                                stopshash.put("stoplng", ""+ RouteDetailArray.getJSONObject(j).get("Longitude"));
                                AllPoints.add(stopshash);
                                routeDetails.add(rd);
                            }


                            route.setRouteDetails(routeDetails);

                            createRoute(route);

                        }
                    }
                }
            }



        }


        catch (Exception ex)
    {
        Log.d("error",""+ex);
    }

    }

    @Override
    public void onError(VolleyError Error) {

        NetworkConsume.getInstance().hideProgress();
    }
}
