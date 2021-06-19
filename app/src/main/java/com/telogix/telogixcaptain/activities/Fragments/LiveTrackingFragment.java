package com.telogix.telogixcaptain.activities.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.Datum;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveTrackingFragment extends Fragment implements response_interface, ItemSelection {

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList arraySelectedHaulierList = new ArrayList<>();
    ArrayAdapter availableVehiclesAdapter;
    ListView lstVehicles,lstHaulierNames;
    private ValueEventListener latlngchangelistener;
    private DatabaseReference child;
    private ArrayList list,list2,LiveFirebaseVehicles,deviceID;
    private TokenPojo datum;
    private String uid;

    public LiveTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Toast.makeText(getContext(),"Vehicles",Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_live_tracking, container, false);
        getActivity().setTitle("Live Tracking");
        lstVehicles = v.findViewById(R.id.lstVehicles);
         datum=null;
        String response= singleton.getsharedpreference(getContext()).getString("userData", "");
        if(response!="") {
            Gson gson = new Gson();
            datum = gson.fromJson(response, TokenPojo.class);

        }
        if(datum!=null)
        {
            if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.JM){
                HashMap<String, String> data = new HashMap<>();
                String haulierID = null;
                if(UserRole.getRole(datum.getRoleID()) == UserRole.JM){
                    haulierID   = datum.getHaulierID();
                }
                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER){
                    Bundle bundle = getArguments();
                    haulierID= bundle.getString("haulierID");
                }

                httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles?haulierID="+haulierID, Request.Method.GET, data, this);
            }

            if(UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER)
            {
                getRetailerVehicles();
            }
            else
            {
                //Toast.makeText(getContext(),"Loading Vehicles",Toast.LENGTH_SHORT).show();
                FirebaseListenerSetup();

            }
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        lstVehicles.setLayoutManager(mLayoutManager);
//        lstVehicles.setItemAnimator(new DefaultItemAnimator());


//        lstVehicles.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//        lstVehicles.setAdapter(availableVehiclesAdapter);

        //  FirebaseApp firebaseApp=FirebaseApp.initializeApp(getActivity());
        // Log.d("namefirebase", firebaseApp.getName());

//        usersRef.getRef().child("vID").child("lat").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(getContext(),""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(),""+databaseError.getDetails(),Toast.LENGTH_SHORT).show();
//
//            }
//        });

        return v;
    }

    private void getRetailerVehicles() {
        HashMap<String, String> data = new HashMap<>();

        httpvolley.stringrequestpost("api/LoadDecantingSite/GetRetailerVehicles", Request.Method.GET, data, this);
        NetworkConsume.getInstance().ShowProgress(getContext());
    }
    private void setSelectedHaulierVehicleList(){
        JSONArray jsonArray = new JSONArray(LiveFirebaseVehicles);
        for (int i=0; i<arraySelectedHaulierList.size(); i++){

            for(int j=0;j<LiveFirebaseVehicles.size();j++){

                try {
                    String vehicleName = jsonArray.getJSONObject(j).getString("vehicleName");
                    String devID =  jsonArray.getJSONObject(j).getString("deviceID");
                    if(vehicleName.equals(arraySelectedHaulierList.get(i))){
                        if(!list2.contains(vehicleName))
                        {  list2.add(vehicleName);
                        deviceID.add(devID);}


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }


    }

    private void FirebaseListenerSetup() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        NetworkConsume.getInstance().ShowProgress(getContext());
        DatabaseReference usersRef = ref.child("liveVehicles");

        //Toast.makeText(getContext(),""+usersRef,Toast.LENGTH_SHORT).show();

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        LiveFirebaseVehicles = new ArrayList<>();
        deviceID = new ArrayList();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("--LiveFirebaseVehicles",LiveFirebaseVehicles.toString());
                for(DataSnapshot dsn : dataSnapshot.getChildren()){
                    HashMap data = new HashMap();
                    data.put("deviceID",dsn.getKey());
                    data.put("bearing",dsn.child("bearing").getValue());
                    data.put("latlng",dsn.child("latlng").getValue());
                    data.put("vehicleName",dsn.child("vehicleName").getValue());

                    LiveFirebaseVehicles.add(data);
                }
                    Log.i("--LiveFirebaseVehicles",LiveFirebaseVehicles.toString());
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    uid = null;
                    uid = ds.getKey();

                    usersRef.child(uid).child("vehicleName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //   Toast.makeText(getContext(),"V:"+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();

                            try {


                                if (UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER) {

                                    String vehicleName = "" + dataSnapshot.getValue();
                                    for (int i = 0; i < arrayList.size(); i++) {
                                        if (vehicleName.equals(arrayList.get(i))) {
                                            list.add(dataSnapshot.getValue());
                                            deviceID.add(uid);
                                        }
                                    }

                                }
                                else if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.JM){
                                    setSelectedHaulierVehicleList();

                                }
                                else {
                                    list.add(dataSnapshot.getValue());
                                    deviceID.add(uid);
                                }
                            } catch (
                                    Exception ex) {

                            }

                            availableVehiclesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //  Toast.makeText(getContext(),""+databaseError.getDetails(),Toast.LENGTH_SHORT).show();

                        }

                    });


                    //  latlngchangelistener=new ValueEventListener() {

                }
                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.JM){
                availableVehiclesAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, list2);}
                else{availableVehiclesAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, list);}
                lstVehicles.setAdapter(availableVehiclesAdapter);
                lstVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            String json= singleton.getsharedpreference(getContext()).getString("userData","");
                            Gson gson =  new Gson();

                            TokenPojo datum = gson.fromJson(json, TokenPojo.class);
                            //Toast.makeText(getContext(),""+datum.getRoleID(),Toast.LENGTH_SHORT).show();


                           if (UserRole.getRole(datum.getRoleID()) == UserRole.SUPER_ADMIN || UserRole.getRole(datum.getRoleID()) == UserRole.JM || UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER || UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER) {
                               Bundle b = new Bundle();
                               b.putString("uid", deviceID.get(position).toString());
                               if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.JM){
                                   b.putString("vehicleName",list2.get(position).toString());
                               }
                               else{
                                   b.putString("vehicleName",list.get(position).toString());
                               }


                               ChangeFragment(new LiveTrackingDetailFragment(), b);
                           }else {
                               Toast.makeText(getContext(),"Login with Admin to View",Toast.LENGTH_SHORT).show();
                           }
                           } catch (Exception ex)
                        {
                            NetworkConsume.getInstance().hideProgress();

                            ex.printStackTrace();
                        }
                    }
                });
                //  child.addListenerForSingleValueEvent(latlngchangelistener);

                NetworkConsume.getInstance().hideProgress();

                //Do what you need to do with your list

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                NetworkConsume.getInstance().hideProgress();

                Toast.makeText(getContext(), "" + databaseError.getDetails(), Toast.LENGTH_SHORT).show();

            }
        };

        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        String token = "" + singleton.getsharedpreference(getContext()).getString("token", "");
        //getVehicles(token.trim());
    }

//    private void getVehicles(String token) {
//        Toast.makeText(getContext(), "getVehiclesList", Toast.LENGTH_SHORT).show();
//
//
//        HashMap<String, String> data = new HashMap<>();
//        httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles", Request.Method.GET, data, this);
//        NetworkConsume.getInstance().ShowProgress(getContext());
////        NetworkConsume.getInstance().setAccessKey(token);
////        NetworkConsume.getInstance().getAuthAPI().getVehicles().enqueue(new Callback<AvailableVehicles>() {
////            @Override
////            public void onResponse(Call<AvailableVehicles> call, Response<AvailableVehicles> response) {
////                NetworkConsume.getInstance().hideProgress();
////                arrayList.clear();
////                if (response.isSuccessful()){
////                    arrayList.addAll(response.body().getData());
////                    availableVehiclesAdapter.notifyDataSetChanged();
////
////                }else {
////                    new CustomToast().errorToast(context,linearLayout,"Authorization has been denied for this request");
////                    NetworkConsume.getInstance().Logout(context);
////                }
////            }
////
////            @Override
////            public void onFailure(Call<AvailableVehicles> call, Throwable t) {
////                NetworkConsume.getInstance().hideProgress();
////            }
////        });
//    }

    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onResponse(String response) throws JSONException {
        try {
            NetworkConsume.getInstance().hideProgress();

            if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER || UserRole.getRole(datum.getRoleID()) == UserRole.JM){

                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("Data");
                arraySelectedHaulierList.clear();
                for(int i=0; i< dataArray.length(); i++){
                    arraySelectedHaulierList.add(dataArray.getJSONObject(i).getString("VehicleNo"));

                }
            }
            else {


                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("Data");
                arrayList.clear();
                for (int i = 0; i < dataArray.length(); i++) {
                    arrayList.add("" + dataArray.get(i));

//                Datum datum = new Datum();
//                datum.setVehicleID(dataArray.getJSONObject(i).getInt("VehicleID"));
//                datum.setVehicleType(dataArray.getJSONObject(i).getString("VehicleType"));
//                datum.setVehicleNo(dataArray.getJSONObject(i).getString("VehicleNo"));
//                datum.setHaulierID(dataArray.getJSONObject(i).getInt("HaulierID"));
//                datum.setHaulierName(dataArray.getJSONObject(i).getString("HaulierName"));
//                datum.setCapacity(dataArray.getJSONObject(i).getString("Capacity"));
//                datum.setCompartment(dataArray.getJSONObject(i).getString("Compartment"));
//                datum.setStatusID(dataArray.getJSONObject(i).getInt("StatusID"));
//                datum.setLoadID(dataArray.getJSONObject(i).getString("LoadID"));
//                datum.setRouteAssignID(dataArray.getJSONObject(i).getString("RouteAssignID"));
//                if (datum.getStatusID().equals("5")) {
//                    arrayList.add(datum);
//                }
                }
            }

            FirebaseListenerSetup();
            // availableVehiclesAdapter.notifyDataSetChanged();
            //     availableVehiclesAdapter = new AvailableVehiclesAdapter(getContext(),arrayList,this);
            //  lstVehicles.setAdapter(availableVehiclesAdapter);
        } catch (Exception ex) {

            //  new CustomToast().errorToast(getContext(),linearLayout,"Authorization has been denied for this request");
//            NetworkConsume.getInstance().Logout(getContext());
//
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }

    @Override
    public void onAssignLoadClick(Datum datum) {

    }

    @Override
    public void onClickCloseSearchView() {

    }
}
