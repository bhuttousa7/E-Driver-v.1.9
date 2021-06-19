package com.telogix.telogixcaptain.activities.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
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
import com.telogix.telogixcaptain.Utils.CustomToast;
import com.telogix.telogixcaptain.activities.AssignLoadActivity;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;
import com.telogix.telogixcaptain.adapters.AvailableVehiclesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class VehiclesFragment extends Fragment implements ItemSelection, response_interface {
    RecyclerView lstVehicles;
    Context context;
    ArrayList<Datum> arrayList = new ArrayList<>();
    AvailableVehiclesAdapter availableVehiclesAdapter;
    private android.widget.SearchView searchView;
    LinearLayout linearLayout;
    public static int selectedposition = 0;
    SearchManager searchManager;
    TabLayout tablayout;
    private SearchView.OnQueryTextListener queryTextListener;
    MenuItem searchItem;
    private TokenPojo datum;
    private ArrayList<Datum> unloadedVehicles=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vehicles, container, false);


        init(view);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Datum> filteredList = new ArrayList<>();
                if (selectedposition == 0) {
                    for (Datum row : arrayList) {
                        if (row.getVehicleNo().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }
                else
                {
                    for (Datum row : unloadedVehicles) {
                        if (row.getVehicleNo().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }

                availableVehiclesAdapter = new AvailableVehiclesAdapter(context, filteredList, VehiclesFragment.this);
                lstVehicles.setAdapter(availableVehiclesAdapter);
//                availableVehiclesAdapter.notifyDataSetChanged();
                return true;
            }
        });

       return view;
    }

    public void hideKeyboardFrom() {

        View view =getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            searchView.setIconified(true);
        }
    }
    private void init(View view) {
        context = getActivity();
        lstVehicles = view.findViewById(R.id.lstVehicles);
        linearLayout = view.findViewById(R.id.mainLL);
        tablayout = view.findViewById(R.id.tablayout_vehicles);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        lstVehicles.setLayoutManager(mLayoutManager);
        lstVehicles.setItemAnimator(new DefaultItemAnimator());
        availableVehiclesAdapter = new AvailableVehiclesAdapter(context, arrayList, this);
        searchView=view.findViewById(R.id.searchView);
       // lstVehicles.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        lstVehicles.setAdapter(availableVehiclesAdapter);
        String json = NetworkConsume.getInstance().getDefaults("userData", context);
        json = singleton.getsharedpreference(context).getString("userData", "");
        Gson gson = new Gson();
        datum = gson.fromJson(json, TokenPojo.class);


        if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SCHEDULER))        // scheduler
        {
            Toast.makeText(getContext(),"Scheduler",Toast.LENGTH_SHORT).show();
            tablayout.setTabMode(TabLayout.MODE_FIXED);
            tablayout.addTab(tablayout.newTab().setText("All"));
            tablayout.addTab(tablayout.newTab().setText("Assigned"));
            tablayout.addTab(tablayout.newTab().setText("UnAssigned"));
            tablayout.setScrollPosition(1,1,true);


        }
        else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SUPER_ADMIN))        // scheduler
        {
            Toast.makeText(getContext(),"Super Admin",Toast.LENGTH_SHORT).show();
            tablayout.addTab(tablayout.newTab().setText("All"));


        } else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR)) // preload inspector
        {

            tablayout.addTab(tablayout.newTab().setText("Inspect"));
            tablayout.addTab(tablayout.newTab().setText("Inspected"));
        } else                                       // journey manager
        {
            //Toast.makeText(getContext(),"Loading Tabs",Toast.LENGTH_SHORT).show();
            tablayout.addTab(tablayout.newTab().setText("All"));
            tablayout.addTab(tablayout.newTab().setText("Assign Route"));
            tablayout.addTab(tablayout.newTab().setText("Route Assigned"));
            tablayout.addTab(tablayout.newTab().setText("Unassigned"));
            tablayout.addTab(tablayout.newTab().setText("Assigned"));
            tablayout.addTab(tablayout.newTab().setText("Inspected"));
            tablayout.addTab(tablayout.newTab().setText("Signed Off"));
            tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        }

        tablayout.getTabAt(0).select();
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(getContext(),"Tab Selected"+arrayList.size(),Toast.LENGTH_SHORT).show();
                hideKeyboardFrom();
                if (arrayList.size() > 0) {
                    selectedposition = tab.getPosition();


                    if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SCHEDULER)) {
                        if (selectedposition == 0) {

                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, arrayList, VehiclesFragment.this));
                        } else if (selectedposition == 1) {
                             unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 2) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));
                        } else if (selectedposition == 2) {
                            unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 1) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

                        }
                    }
                    else   if (UserRole.getRole(datum.getRoleID()).equals(UserRole.JM)) {

                        //Toast.makeText(getContext(),"JM",Toast.LENGTH_SHORT).show();
                        if (selectedposition == 0) {
                            //Tab name: "All"
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, arrayList, VehiclesFragment.this));
                        } else if (selectedposition == 1) {
                            //Tab name: "ASSIGN ROUTE"
                            unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 2) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));
                        } else if (selectedposition == 2) {
                            //Tab name: "ROUTE ASSIGNED"
                           unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 3) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

                        }
                        else if (selectedposition==3){
                            // Unassigned
                             unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 1) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

                        }
                        else if (selectedposition==4){
                            //Assigned
                            unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 2) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));


                        }
                        else if (selectedposition==5){
                            //Inspected
                             unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 4) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

                        }
                        else if (selectedposition==6){
                           // Tab name SIGNED OFF
                           unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 5) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

                        }
                    }
                    else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR)) {
                        if (selectedposition == 0) {
                            unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 3) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));
                        } else if (selectedposition == 1) {
                           unloadedVehicles = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getStatusID() == 4) {
                                    unloadedVehicles.add(arrayList.get(i));
                                }
                            }
                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));
                        }
                    } else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SUPER_ADMIN)) {

                        lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, arrayList, VehiclesFragment.this));
                    }
                }


//                    if (tab.getPosition() == 2) {
//                        if(UserRole.getRole(datum.getRoleID()).equals(UserRole.JM))
//                        {
//                        ArrayList<Datum> unloadedVehicles = new ArrayList();
//                        for (int i = 0; i < arrayList.size(); i++) {
//                            if (arrayList.get(i).getStatusID() == 1) {
//                                unloadedVehicles.add(arrayList.get(i));
//                            }
//                        }
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));
//                        }
//                        else
//                        {
//                            ArrayList<Datum> routeAssigned = new ArrayList();
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).getStatusID() == 3) {
//                                    routeAssigned.add(arrayList.get(i));
//                                }
//                            }
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, routeAssigned, VehiclesFragment.this));
//                        }
//
//                    } else if (tab.getPosition() == 1) {
//                        if(UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR))   //preload inspector
//                        {
//                            ArrayList<Datum> routeAssigned = new ArrayList();
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).getStatusID() == 5) {
//                                    routeAssigned.add(arrayList.get(i));
//                                }
//                            }
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, routeAssigned, VehiclesFragment.this));
//                        }
//                        else {
//                            ArrayList<Datum> loadedVehicles = new ArrayList();
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).getStatusID() == 2) {
//                                    loadedVehicles.add(arrayList.get(i));
//                                }
//                            }
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, loadedVehicles, VehiclesFragment.this));
//                        }
//                    } else if (tab.getPosition() == 0) {
//                        if(UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR))  //preload inspector
//                        {
//                            ArrayList<Datum> routeAssigned = new ArrayList();
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).getStatusID() == 3) {
//                                    routeAssigned.add(arrayList.get(i));
//                                }
//                            }
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, routeAssigned, VehiclesFragment.this));
//                        }else {
//
//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, arrayList, VehiclesFragment.this));
//                        }
//                    }
//
//
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("All Vehicles");
        String token = "" + singleton.getsharedpreference(getContext()).getString("token", "");


            getVehicles(token.trim());

        tablayout.setScrollPosition(0, 0, true);
        tablayout.getTabAt(0).select();
        selectedposition = 0;
//        getVehicles(NetworkConsume.getInstance().get_accessToken(context));
    }

    private void getVehicles(String token) {
        try  {
            //Toast.makeText(getContext(),"Vapi/ehicles/GetAllVehicles",Toast.LENGTH_SHORT).show();
            HashMap<String, String> data = new HashMap<>();
            if(datum.getHaulierID().isEmpty() || datum.getHaulierID()==null){
                httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles", Request.Method.GET, data, this);

            }
            else{
                httpvolley.stringrequestpost("api/Vehicles/GetAllVehicles?HaulierId="+datum.getHaulierID(), Request.Method.GET, data, this);
            }

            NetworkConsume.getInstance().ShowProgress(context);
        //Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onAssignLoadClick(Datum datum) {
        CustomData.getInstance().vehicleDetails = datum;
        searchView.setIconified(true);
        startActivity(new Intent(getActivity(), AssignLoadActivity.class));


    }

    @Override
    public void onClickCloseSearchView() {
        try {
            searchView.setIconified(true);
            searchView.clearFocus();
            searchView.setActivated(false);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        //searchItem = menu.findItem(R.id.action_search);
        // searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //  searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);


        // MenuItem item = menu.findItem(R.id.action_search);

//        SearchView searchView = new SearchView(((MainActivity) context).getSupportActionBar().getThemedContext());
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItemCompat.setActionView(item, searchView);

//        performSearch();


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResponse(String response) throws JSONException {
        //Toast.makeText(getContext(),"Response",Toast.LENGTH_SHORT).show();
      //  Log.d("--response",response);
        try {


            NetworkConsume.getInstance().hideProgress();
            NetworkConsume.getInstance().hideProgress();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("Data");
            arrayList.clear();
            for (int i = 0; i < dataArray.length(); i++) {

                Datum datum = new Datum();
                datum.setVehicleID(dataArray.getJSONObject(i).getInt("VehicleID"));
                datum.setVehicleType(dataArray.getJSONObject(i).getString("VehicleType"));
                datum.setVehicleNo(dataArray.getJSONObject(i).getString("VehicleNo"));
                datum.setHaulierID(dataArray.getJSONObject(i).getInt("HaulierID"));
                datum.setHaulierName(dataArray.getJSONObject(i).getString("HaulierName"));
                datum.setCapacity(dataArray.getJSONObject(i).getString("Capacity"));
                datum.setCompartment(dataArray.getJSONObject(i).getString("Compartment"));
                datum.setStatusID(dataArray.getJSONObject(i).getInt("StatusID"));
                datum.setLoadID(dataArray.getJSONObject(i).getString("LoadID"));
                datum.setRouteAssignID(dataArray.getJSONObject(i).getString("RouteAssignID"));
                datum.setRouteID(dataArray.getJSONObject(i).getString("RouteID"));
                arrayList.add(datum);
                //Toast.makeText(getContext(),""+arrayList,Toast.LENGTH_SHORT).show();
            }
            // availableVehiclesAdapter.notifyDataSetChanged();
            if (datum.getRoleID().equals("1006"))   //preload inspector
            {
                ArrayList<Datum> routeAssigned = new ArrayList();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 5) {
                        routeAssigned.add(arrayList.get(i));
                    }
                }
                lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, routeAssigned, VehiclesFragment.this));
            } else {

                lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, arrayList, VehiclesFragment.this));
            }

        } catch (Exception ex) {

            new CustomToast().errorToast(context, linearLayout, "Authorization has been denied for this request");
            NetworkConsume.getInstance().Logout(context);
//
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
