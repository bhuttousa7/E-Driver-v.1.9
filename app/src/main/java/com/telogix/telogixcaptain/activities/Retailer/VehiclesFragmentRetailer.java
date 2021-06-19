package com.telogix.telogixcaptain.activities.Retailer;

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
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.Retailer.Datum;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.CustomToast;
import com.telogix.telogixcaptain.activities.AssignLoadActivity;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;
import com.telogix.telogixcaptain.adapters.DecantedVehiclesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class VehiclesFragmentRetailer extends Fragment implements ItemSelection, response_interface {
    RecyclerView lstVehicles;
    Context context;
    ArrayList<Datum> arrayList = new ArrayList<>();
    DecantedVehiclesAdapter availableVehiclesAdapter;
    private android.widget.SearchView searchView;
    LinearLayout linearLayout;
    public static int selectedposition = 0;
    SearchManager searchManager;
    TabLayout tablayout;
    private SearchView.OnQueryTextListener queryTextListener;
    MenuItem searchItem;
    private TokenPojo datum;
    private final ArrayList<Datum> unloadedVehicles=new ArrayList<>();

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

                availableVehiclesAdapter = new DecantedVehiclesAdapter(getContext(), filteredList, VehiclesFragmentRetailer.this);
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
        context = getContext();
        lstVehicles = view.findViewById(R.id.lstVehicles);
        linearLayout = view.findViewById(R.id.mainLL);
        tablayout = view.findViewById(R.id.tablayout_vehicles);
        tablayout.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        lstVehicles.setLayoutManager(mLayoutManager);
        lstVehicles.setItemAnimator(new DefaultItemAnimator());
        availableVehiclesAdapter = new DecantedVehiclesAdapter(getContext(), arrayList, this);
        searchView=view.findViewById(R.id.searchView);
       // lstVehicles.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        lstVehicles.setAdapter(availableVehiclesAdapter);
        String json = NetworkConsume.getInstance().getDefaults("userData", context);
        json = singleton.getsharedpreference(context).getString("userData", "");
        Gson gson = new Gson();
        datum = gson.fromJson(json, TokenPojo.class);
        if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SCHEDULER))        // scheduler
        {
            tablayout.setTabMode(TabLayout.MODE_FIXED);
            tablayout.addTab(tablayout.newTab().setText("All"));
            tablayout.addTab(tablayout.newTab().setText("Assigned"));
            tablayout.addTab(tablayout.newTab().setText("UnAssigned"));
            tablayout.setScrollPosition(1,1,true);


        }
        else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SUPER_ADMIN))        // scheduler
        {
            tablayout.addTab(tablayout.newTab().setText("All"));


        } else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR)) // preload inspector
        {

            tablayout.addTab(tablayout.newTab().setText("Inspect"));
            tablayout.addTab(tablayout.newTab().setText("Inspected"));
        } else                                       // journey manager
        {
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
                hideKeyboardFrom();
                if (arrayList.size() > 0) {
                    selectedposition = tab.getPosition();

                    lstVehicles.setAdapter(new DecantedVehiclesAdapter(getContext(), arrayList, VehiclesFragmentRetailer.this));

                }


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

        getActivity().setTitle("Decanted Vehicles");
        String token = "" + singleton.getsharedpreference(getContext()).getString("token", "");
        if(UserRole.getRole(datum.getRoleID()) == UserRole.RETAILER)
        {
            getDecantedVehiclesRetailer(token.trim());
        }
        else {

        }
        tablayout.setScrollPosition(0, 0, true);
        tablayout.getTabAt(0).select();
        selectedposition = 0;
//        getVehicles(NetworkConsume.getInstance().get_accessToken(context));
    }
    private void getDecantedVehiclesRetailer(String token) {

        HashMap<String, String> data = new HashMap<>();
        httpvolley.stringrequestpost("api/LoadDecantingSite/GetDecantedLoad", Request.Method.GET, data, this);
        NetworkConsume.getInstance().ShowProgress(context);

    }


    @Override
    public void onAssignLoadClick(com.telogix.telogixcaptain.Pojo.Datum datum) {
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
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onResponse(String response) throws JSONException {
        try {

            NetworkConsume.getInstance().hideProgress();
            NetworkConsume.getInstance().hideProgress();
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.DECANTEDVEHICLES)))
                {
                    JSONArray dataArray = jsonObject.getJSONArray("Data");
                    arrayList.clear();
                    for (int i = 0; i < dataArray.length(); i++) {

                        Datum datum = new Datum();
                        datum.setVehicleID(dataArray.getJSONObject(i).getInt("VehicleID"));
                        datum.setVehicleNo(dataArray.getJSONObject(i).getString("VehicleNo"));
                        datum.setDriver(dataArray.getJSONObject(i).getString("Driver"));
                        datum.setLoadDecantingSiteID(""+dataArray.getJSONObject(i).getInt("LoadDecantingSiteID"));
                        datum.setCommodityName(dataArray.getJSONObject(i).getString("CommodityName"));
                        datum.setCompartmentNo(dataArray.getJSONObject(i).getString("CompartmentNo"));
                        datum.setCommodityLoad(dataArray.getJSONObject(i).getString("CommodityLoad"));
                        datum.setDecantedTime(dataArray.getJSONObject(i).getString("DecantedTime"));
                        datum.setDriverRating(dataArray.getJSONObject(i).getString("DriverRating"));
                        datum.setDriverReviews(dataArray.getJSONObject(i).getString("DriverReviews"));

                        arrayList.add(datum);

                    }

                    lstVehicles.setAdapter(new DecantedVehiclesAdapter(getContext(), arrayList, VehiclesFragmentRetailer.this));

                }
                else if(jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.RETAILERRATINGSAVED)))
                {
                    Toast.makeText(getContext(), "Rating Saved", Toast.LENGTH_SHORT).show();
                    String token = "" + singleton.getsharedpreference(getContext()).getString("token", "");
                    getDecantedVehiclesRetailer(token);
                }
            }


        } catch (Exception ex) {

            new CustomToast().errorToast(context, linearLayout, "Authorization has been denied for this request");
            NetworkConsume.getInstance().Logout(context);

        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
