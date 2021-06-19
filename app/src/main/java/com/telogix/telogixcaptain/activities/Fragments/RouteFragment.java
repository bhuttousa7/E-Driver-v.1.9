package com.telogix.telogixcaptain.activities.Fragments;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.RouteDetail;
import com.telogix.telogixcaptain.Pojo.Routes;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.adapters.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.gson.JsonArray;
//import com.schibstedspain.leku.LocationPickerActivity;
//
//import static android.app.Activity.*;
//import static android.provider.MediaStore.Video.VideoColumns.LATITUDE;
//import static android.provider.MediaStore.Video.VideoColumns.LONGITUDE;
//import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_DISPLAY_NAME;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_ID;
//import static com.schibstedspain.leku.LocationPickerActivityKt.TRANSITION_BUNDLE;
//import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteFragment extends Fragment implements response_interface, View.OnClickListener {

    ArrayList<Routes> completeRoutes = new ArrayList<>();
    private ListView recyclerView;
    ImageButton imgbtn_addroute;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
     double latitude,longitude;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    public RouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

      //  Toast.makeText(getContext(),"Load Routes",Toast.LENGTH_SHORT).show();
        View v = inflater.inflate(R.layout.fragment_route, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

         recyclerView = v.findViewById(R.id.recyclerview_route);

      //  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      //  recyclerView.setLayoutManager(layoutManager);
        imgbtn_addroute=v.findViewById(R.id.imgbtn_addroute);
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
        imgbtn_addroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });
//        if(completeRoutes.size()>0)
//        {
//            recyclerView.setAdapter(new RoutesAdapter(completeRoutes,getContext()));
//        }
//        else
//        {
//            getAllRoutes();
//        }

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
                ChangeFragment(new AddRoutes());
               // animateFAB();
                break;
            case R.id.fab1:
               // ChangeFragment(new AddRoutes());

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
    private void getAllRoutes() {

        NetworkConsume.getInstance().ShowProgress(getContext());
        String token = NetworkConsume.getInstance().get_accessToken(getContext());
        httpvolley.stringrequestpost("api/Routes/GetRoutes", Request.Method.GET, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("All Routes");
        getAllRoutes();
    }

    @Override
    public void onResponse(String response) throws JSONException {

       // Toast.makeText(getContext(),""+response,Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().hideProgress();
        try {
if(completeRoutes.size()>0)
{
    completeRoutes.clear();
}
            ArrayList<RouteDetail> routeDetails = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("Data");
            for (int i = 0; i < array.length(); i++) {
                Routes route = new Routes();
                if(array.getJSONObject(i).has("RouteID"))
                {
                    if(!array.getJSONObject(i).get("RouteID").equals(null))
                    {
                        route.setRouteID(array.getJSONObject(i).get("RouteID").toString());
                    }
                }
                if(array.getJSONObject(i).has("RouteName"))
                {
                    if(!array.getJSONObject(i).get("RouteName").equals(null))
                    {
                        route.setRouteName(""+array.getJSONObject(i).get("RouteName"));
                    }
                }
                if(array.getJSONObject(i).has("RouteAddress"))
                {
                    if(!array.getJSONObject(i).get("RouteAddress").equals(null))
                    {
                        route.setRouteAddress(""+ array.getJSONObject(i).get("RouteAddress"));
                    }
                }
                if(array.getJSONObject(i).has("Description"))
                {
                    if(!array.getJSONObject(i).get("Description").equals(null))
                    {
                        route.setDescription(array.getJSONObject(i).get("Description"));
                    }
                }

                if(array.getJSONObject(i).has("IsActive"))
                {
                    if(!array.getJSONObject(i).get("IsActive").equals(null))
                    {
                        route.setIsActive(array.getJSONObject(i).get("IsActive"));
                    }
                }
                if(array.getJSONObject(i).has("OriginLong"))
                {
                    if(!array.getJSONObject(i).get("OriginLong").equals(null))
                    {
                        route.setOriginLong((double) array.getJSONObject(i).get("OriginLong"));
                    }
                }
                if(array.getJSONObject(i).has("OriginLat"))
                {
                    if(!array.getJSONObject(i).get("OriginLat").equals(null))
                    {
                        route.setOriginLat((double) array.getJSONObject(i).get("OriginLat"));
                    }
                }
                if(array.getJSONObject(i).has("DestinationLong"))
                {
                    if(!array.getJSONObject(i).get("DestinationLong").equals(null))
                    {
                        route.setDestinationLong((double) array.getJSONObject(i).get("DestinationLong"));
                    }
                }
                if(array.getJSONObject(i).has("DestinationLat"))
                {
                    if(!array.getJSONObject(i).get("DestinationLat").equals(null))
                    {
                        route.setDestinationLat((double) array.getJSONObject(i).get("DestinationLat"));
                    }
                }
                if(array.getJSONObject(i).has("RouteAddress"))
                {
                    if(!array.getJSONObject(i).get("RouteAddress").equals(null))
                    {
                        route.setRouteAddress(""+ array.getJSONObject(i).get("RouteAddress"));
                    }
                }
                if(array.getJSONObject(i).has("pickupLocationName"))
                {
                    if(!array.getJSONObject(i).get("pickupLocationName").equals(null))
                    {
                        route.setPickupLocationName(String.valueOf(array.getJSONObject(i).get("pickupLocationName")));
                    }
                }
                if(array.getJSONObject(i).has("lastDecantingSiteName"))
                {
                    if(!array.getJSONObject(i).get("lastDecantingSiteName").equals(null))
                    {
                        route.setLastDecatingSiteName(String.valueOf(array.getJSONObject(i).get("lastDecantingSiteName")));

                    }
                }

                JSONArray RouteDetailArray = array.getJSONObject(i).getJSONArray("RouteDetails");
                 routeDetails=new ArrayList<>();
                RouteDetail rd = new RouteDetail();
                for (int j = 0; j < RouteDetailArray.length(); j++) {
                    rd = new RouteDetail();
                    if(RouteDetailArray.getJSONObject(j).has("RouteDetailID"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("RouteDetailID").equals(null))
                        {
                            rd.setRouteDetailID(""+ RouteDetailArray.getJSONObject(j).get("RouteDetailID"));
                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("RouteID"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("RouteID").equals(null))
                        {
                            rd.setRouteID(""+RouteDetailArray.getJSONObject(j).get("RouteID"));
                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("RouteType"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("RouteType").equals(null))
                        {
                            rd.setRouteType("" + RouteDetailArray.getJSONObject(j).get("RouteType"));

                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("RouteTypeID"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("RouteTypeID").equals(null))
                        {
                            rd.setRouteTypeID("" + RouteDetailArray.getJSONObject(j).get("RouteTypeID"));

                        }
                        else
                        {
                            rd.setRouteTypeID("");
                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("Name"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("Name").equals(null))
                        {
                            rd.setName("" + RouteDetailArray.getJSONObject(j).get("Name"));

                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("Longitude"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("Longitude").equals(null))
                        {
                            rd.setLongitude((Double) RouteDetailArray.getJSONObject(j).get("Longitude"));

                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("Latitude"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("Latitude").equals(null))
                        {
                            rd.setLatitude((Double) RouteDetailArray.getJSONObject(j).get("Latitude"));

                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("OrderNumber"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("OrderNumber").equals(null))
                        {
                            rd.setOrderNumber((Integer) RouteDetailArray.getJSONObject(j).get("OrderNumber"));

                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("GoogleAddress"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("GoogleAddress").equals(null))
                        {
                            rd.setGoogleAddress("" + RouteDetailArray.getJSONObject(j).get("GoogleAddress"));
                        }
                    }
                    if(RouteDetailArray.getJSONObject(j).has("Detail"))
                    {
                        if(!RouteDetailArray.getJSONObject(j).get("Detail").equals(null))
                        {
                            rd.setDetail("" + RouteDetailArray.getJSONObject(j).get("Detail"));

                        }
                    }
                    routeDetails.add(rd);

                }



                route.setRouteDetails(routeDetails);
                completeRoutes.add(route);




            }

            recyclerView.setAdapter(new SwipeListAdapter(completeRoutes,getContext(),"",false,true));

        }
        catch (Exception ex)
        {
            Log.d("error",""+ex);
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Log.d("response eroor", "" + singleton.getsharedpreference(getContext()).getString("token", ""));
    }

}
