package com.telogix.telogixcaptain.activities.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveTrackingDetailFragment extends Fragment implements OnMapReadyCallback, response_interface {


    private GoogleMap mMap;
    String currentuid = "";
    double currentlatitude=0.0, currentlongitude=0.0;
    private Marker marker = null;
    private final ArrayList waypoints = new ArrayList();
    private final ArrayList<LatLng> stops = new ArrayList();
    private PolylineOptions opts;
    private ArrayList<Hazards> completeHazard;
    LatLng fromPosition,toPosition;
    private static boolean zoomed;
    private String vehicleName;


    public LiveTrackingDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_live_tracking_detail, container, false);
   //     getAllHazards();
        if (getArguments().containsKey("uid")) {
            currentuid = getArguments().getString("uid");
        if(getArguments().containsKey("vehicleName")){
            vehicleName = getArguments().getString("vehicleName");
            getActivity().setTitle("LiveTracking:"+vehicleName);
        }

//            getvehicleByVehicleID();
        }

        return v;
    }

    //private void getvehicleByVehicleID() {
    //    NetworkConsume.getInstance().ShowProgress(getContext());
    //    HashMap hashMap = new HashMap();

    //    httpvolley.stringrequestpost("api/VehicleDevices/GetDeviceRideByDeviceID?DeviceID=" + currentuid, Request.Method.GET, hashMap, this);
    //}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        FirebaseListenerSetup();
        mapFragment.getMapAsync(this);
        //Toast.makeText(getContext(),"ok1",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (marker != null) {
            marker.remove();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;





        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            if (!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if(currentlatitude!=0.0 && currentlongitude!=0.0)
            {
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(currentlatitude, currentlongitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));

                animateMarkerNew(currentlatitude,currentlongitude,marker);
            }
           else if (myLocation != null) {
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.latitude, userLocation.longitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));

                if(toPosition!=null) {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boundsBuilder.include(new LatLng(userLocation.latitude,userLocation.longitude));
                    boundsBuilder.include(toPosition);
                    NetworkConsume.getInstance().hideProgress();
                    final LatLngBounds bounds = boundsBuilder.build();
                    //    viewRoute(mMap);
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                        }
                    });

                }
                NetworkConsume.getInstance().hideProgress();

            } else {
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0.0, 0.0)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));

            }

        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()

                .tilt(60f)
                .target(marker.getPosition())
                .zoom(17f)
                .build()));

    }

    private void FirebaseListenerSetup() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("liveVehicles");
      //  Toast.makeText(getContext(),""+ref,Toast.LENGTH_LONG).show();
        usersRef.child(currentuid).child("latlng").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //    Toast.makeText(getContext(),""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                try {


                    //  Toast.makeText(getContext(), "" + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                    currentlatitude = Double.parseDouble(dataSnapshot.getValue().toString().split(",")[0]);
                    currentlongitude = Double.parseDouble(dataSnapshot.getValue().toString().split(",")[1]);
                    Log.i("--lat:",String.valueOf(currentlatitude));
                    Log.i("--lng:",String.valueOf(currentlongitude));

                    if (marker != null) {
                     //   marker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentlatitude, currentlongitude)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
                        animateMarkerNew(currentlatitude, currentlongitude, marker);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("firebaseerror", databaseError.getDetails());

            }
        });


        resizeMarker(100, 100, R.drawable.tanker);


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

    private Bitmap resizeMarker(int width, int height, int drawable) {

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


    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LiveTrackingDetailFragment.LatLngInterpolatorNew {
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


    //Method for finding bearing between two points
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


        mMap.clear();

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
//        mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
     //   mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(orgLat, orgLong)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));

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

    private void getAllHazards() {

//        NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/Hazards/GetHazards", Request.Method.GET, hashMap, this);

    }

    @Override
    public void onResponse(String response) throws JSONException {

        ArrayList<RouteDetail> routeDetails = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("ResponseCode")) {
            if (jsonObject.get("ResponseCode").equals(6002))   //hazard response
            {
                completeHazard=new ArrayList<>();
                try {
                    if (completeHazard.size() > 0) {
                        completeHazard.clear();
                    }
                    ArrayList<HazardDetail> hazardDetails = new ArrayList<>();
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

                        double hazardlat,hazardlng;
                        hazardlat= Double.parseDouble(hazards.getLatitude().toString());
                        hazardlng=Double.parseDouble(hazards.getLongitude().toString());

                        if(mMap!=null)
                        {
                            if(opts!=null) {
                                if (PolyUtil.isLocationOnPath(new LatLng(hazardlat, hazardlng), opts.getPoints(), opts.isGeodesic(), Constants.stopMarkerRadius)) {
//                                    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                                }
                            }
                            //    mMap.addMarker(new MarkerOptions().position(new LatLng(hazardlat,hazardlng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("hazard"));

                        }
                    }


                } catch (Exception ex) {
                    Log.d("error", "" + ex);
                }
            } else {
                JSONObject array = jsonObject.getJSONObject("Data");
                Routes route = new Routes();
                if (array.has("RouteID")) {
                    if (!array.get("RouteID").equals(null)) {
                        route.setRouteID(array.get("RouteID").toString());
                    }
                }
                if (array.has("RouteName")) {
                    if (!array.get("RouteName").equals(null)) {
                        route.setRouteName("" + array.get("RouteName"));
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
                if (array.has("RouteAddress")) {
                    if (!array.get("RouteAddress").equals(null)) {
                        route.setRouteAddress("" + array.get("RouteAddress"));
                    }
                }

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
                    if (RouteDetailArray.getJSONObject(j).has("RouteTypeID")) {
                        if (!RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals(null)) {
                            rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));

                        } else {
                            rd.setRouteTypeID("");
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
                    routeDetails.add(rd);

                }



                route.setRouteDetails(routeDetails);

                for (int k = 0; k < route.getRouteDetails().size(); k++) {
                    RouteDetail routeDetail = route.getRouteDetails().get(k);
                    if (routeDetail.getRouteTypeID().equals("1")) {
                        waypoints.add("" + routeDetail.getLatitude() + "," + "" + routeDetail.getLongitude());
                    } else {
                        stops.add(new LatLng(routeDetail.getLatitude(), routeDetail.getLongitude()));
                    }
                }

//                createRoute(route.getOriginLat(), route.getOriginLong(), route.getDestinationLat(), route.getDestinationLong());
            }

        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
       // getActivity().onBackPressed();
    }

}
