package com.telogix.telogixcaptain.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.Fragments.AddRoutes;
import com.telogix.telogixcaptain.activities.Fragments.AssignCopyRoute;
import com.telogix.telogixcaptain.activities.Fragments.AssignNewRouteFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddStopActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, response_interface {

    private GoogleMap mMap;
    private LocationManager locationManager;
    double latidude, longitude;
    Button addStop;
    private TextView txtView;
    ArrayList<HashMap<String,String>> dataArray=new ArrayList();
Spinner spinnerstopType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        spinnerstopType=findViewById(R.id.spinnerstoptype);
        getStopType();

        addStop=findViewById(R.id.addHazard);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placesAutoComplete();
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
        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new AlertDialog.Builder(AddStopActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to set stop at this position")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent();
                                    intent.putExtra("latitude", "" + latidude);
                                    intent.putExtra("longitude", "" + longitude);
                                    if (getIntent().hasExtra("position")) {
                                        intent.putExtra("position", getIntent().getStringExtra("position"));
                                    }
                                    if(spinnerstopType!=null) {
                                        if (!spinnerstopType.getSelectedItem().toString().equals("")) {
                                            if(dataArray.size()>0)
                                            {
                                                int selectedPos=spinnerstopType.getSelectedItemPosition();
                                                HashMap hashMap=dataArray.get(selectedPos);
                                            intent.putExtra("StopTypeID", "" +  hashMap.get("stopTypeID"));
                                            setResult(3, intent);
                                            finish();}
                                        } else {
                                            Toast.makeText(AddStopActivity.this, "Please select stop type", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();


            }
        });
    }

    private void getStopType() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap data=new HashMap();
        httpvolley.stringrequestpost("api/RouteTypes/GetRouteTypesForDDL", Request.Method.GET,data,this);
    }

    private void placesAutoComplete() {
        Places.initialize(getApplicationContext(), this.getResources().getString(R.string.GCP_API_Key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setCountry("PK");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //   txtView.setText(place.getLatLng().latitude+","+place.getLatLng().longitude);

                if(mMap!=null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14);
                    mMap.animateCamera(cameraUpdate);
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("error",""+status);
            }
        });
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
        mMap = googleMap;
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
            }
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latidude=mMap.getCameraPosition().target.latitude;
                longitude=mMap.getCameraPosition().target.longitude;
            }
        });
        if(getIntent().getBooleanExtra("addroute",false)){
            createRoutefromDirectionResult(AddRoutes.directionsResult);
        }else
        if(getIntent().getBooleanExtra("assignroute",false)){
            createRoutefromDirectionResult(AssignNewRouteFragment.directionsResult);
        }
        else if(getIntent().getBooleanExtra("copyroute",false)){
            createRoutefromDirectionResult(AssignCopyRoute.directionsResult);
        }


    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
         dataArray=new ArrayList();
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("Status"))
            {
                if(jsonObject.get("Status").equals(true))
                {
                    JSONArray data=jsonObject.getJSONArray("Data");
                    for(int i=0;i<data.length();i++)
                    {
                        HashMap hashMap=new HashMap();
                        hashMap.put("stopTypeID",data.getJSONObject(i).get("RouteTypeID"));
                        hashMap.put("TypeName",data.getJSONObject(i).get("TypeName"));
                        dataArray.add(hashMap);
                    }
                    ArrayList stopnamelist=new ArrayList();
                    for(int j=0;j<dataArray.size();j++)
                    {
                        stopnamelist.add(dataArray.get(j).get("TypeName"));

                    }
                    spinnerstopType.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,stopnamelist));
                }
            }
        }
        catch (Exception ex)
        {

        }
    }
    private void createRoutefromDirectionResult(DirectionsResult directionsResult) {
        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(connection.GoogleAPIKEY)
                .build();
        List<LatLng> path = new ArrayList();

        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);
        try {
            DirectionsResult res = directionsResult;
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
                    if(getIntent().getBooleanExtra("addroute",false)){
                        if(AddRoutes.fromPosition.latitude!=0.0 && AddRoutes.fromPosition.longitude!=0.0)
                        {

                            mMap.addMarker(new MarkerOptions().position(AddRoutes.fromPosition).title("Start"));
                            mMap.addMarker(new MarkerOptions().position(AddRoutes.toPosition).title("End"));
                        }


                        for(int k = 0; k< AddRoutes.AllPoints.size(); k++)
                        {
                            try {
                                HashMap stopsHash=new HashMap();
                                stopsHash= AddRoutes.AllPoints.get(k);
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

                                }
                                else if(stopsHash.get("typeid").equals("1"))
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
                    }else
                    if(getIntent().getBooleanExtra("assignroute",false)){
                        if(AssignNewRouteFragment.fromPosition.latitude!=0.0 && AssignNewRouteFragment.fromPosition.longitude!=0.0)
                        {

                            mMap.addMarker(new MarkerOptions().position(AssignNewRouteFragment.fromPosition).title("Start"));
                            mMap.addMarker(new MarkerOptions().position(AssignNewRouteFragment.toPosition).title("End"));
                        }


                        for(int k = 0; k< AssignNewRouteFragment.AllPoints.size(); k++)
                        {
                            try {
                                HashMap stopsHash=new HashMap();
                                stopsHash= AssignNewRouteFragment.AllPoints.get(k);
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

                                }
                                else if(stopsHash.get("typeid").equals("1"))
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
                    else
                    if(getIntent().getBooleanExtra("copyroute",false)){
                        if(AssignCopyRoute.fromPosition.latitude!=0.0 && AssignCopyRoute.fromPosition.longitude!=0.0)
                        {

                            mMap.addMarker(new MarkerOptions().position(AssignCopyRoute.fromPosition).title("Start"));
                            mMap.addMarker(new MarkerOptions().position(AssignCopyRoute.toPosition).title("End"));
                        }


                        for(int k = 0; k< AssignCopyRoute.AllPoints.size(); k++)
                        {
                            try {
                                HashMap stopsHash=new HashMap();
                                stopsHash= AssignCopyRoute.AllPoints.get(k);
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

                                }
                                else if(stopsHash.get("typeid").equals("1"))
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



                }
            }
        } catch (Exception ex) {
            Log.e("exception", ex.getLocalizedMessage());
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Constants.polylineColor).width(Constants.polylineWidth);
            mMap.addPolyline(opts);
            //  zoomRoute(mMap,path);

        }



        //    viewRoute(mMap);



        mMap.getUiSettings().setZoomControlsEnabled(true);
        //  mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void onError(VolleyError Error) {
NetworkConsume.getInstance().hideProgress();

    }
}
