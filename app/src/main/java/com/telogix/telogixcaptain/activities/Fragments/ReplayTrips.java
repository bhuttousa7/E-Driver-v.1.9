package com.telogix.telogixcaptain.activities.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.Datum;
import com.telogix.telogixcaptain.Pojo.ReplayTripPojo;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.CustomToast;
import com.telogix.telogixcaptain.activities.AssignLoadActivity;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;
import com.telogix.telogixcaptain.adapters.ReplayVehiclesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ReplayTrips extends Fragment implements ItemSelection, response_interface {
    RecyclerView lstVehicles;
    Context context;
    ArrayList<ReplayTripPojo> arrayList = new ArrayList<ReplayTripPojo>();
    com.telogix.telogixcaptain.adapters.ReplayVehiclesAdapter ReplayVehiclesAdapter;
    private android.widget.SearchView searchView;
    LinearLayout linearLayout;
    public static int selectedposition = 0;
    SearchManager searchManager;
    private SearchView.OnQueryTextListener queryTextListener;
    MenuItem searchItem;
    private TokenPojo datum;
    private final ArrayList<ReplayTripPojo> unloadedVehicles=new ArrayList<ReplayTripPojo>();
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replay_trips, container, false);


        init(view);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ReplayTripPojo> filteredList = new ArrayList<>();
                if (selectedposition == 0) {
                    for (ReplayTripPojo row : arrayList) {
                        if (row.getLastDecantingSiteName().toLowerCase().contains(newText.toLowerCase()) || row.getPickupLocationName().toLowerCase().contains(newText.toLowerCase()) || row.getRouteName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }
                else
                {
                    for (ReplayTripPojo row : unloadedVehicles) {
                        if (row.getLastDecantingSiteName().toLowerCase().contains(newText.toLowerCase()) || row.getPickupLocationName().toLowerCase().contains(newText.toLowerCase()) || row.getRouteName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }

                ReplayVehiclesAdapter = new ReplayVehiclesAdapter(context, filteredList, ReplayTrips.this,bundle);
                lstVehicles.setAdapter(ReplayVehiclesAdapter);
//                ReplayVehiclesAdapter.notifyDataSetChanged();
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        lstVehicles.setLayoutManager(mLayoutManager);
        lstVehicles.setItemAnimator(new DefaultItemAnimator());
        ReplayVehiclesAdapter = new ReplayVehiclesAdapter(context, arrayList, this,bundle);
        searchView=view.findViewById(R.id.searchView);
        // lstVehicles.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        lstVehicles.setAdapter(ReplayVehiclesAdapter);
        String json = NetworkConsume.getInstance().getDefaults("userData", context);
        json = singleton.getsharedpreference(context).getString("userData", "");
        Gson gson = new Gson();
        datum = gson.fromJson(json, TokenPojo.class);



    }

    @Override
    public void onResume() {
        super.onResume();

         bundle = getArguments();
        if(bundle.getString("VehicleNo") != null){
        String VehicleNo = bundle.getString("VehicleNo");
        getActivity().setTitle("Replay: "+VehicleNo);}
        String token = "" + singleton.getsharedpreference(getContext()).getString("token", "");


        getVehicles(token.trim());

        selectedposition = 0;
//        getVehicles(NetworkConsume.getInstance().get_accessToken(context));
    }

    private void getVehicles(String token) {
        try  {
            //Toast.makeText(getContext(),"Vapi/ehicles/GetAllVehicles",Toast.LENGTH_SHORT).show();
            HashMap<String, String> data = new HashMap<>();
            Bundle bundle = getArguments();
            String VehicleID =  bundle.getString("VehicleID");
            httpvolley.stringrequestpost("api/TrackingData/GetVehiclesTrips?VehicleID="+VehicleID, Request.Method.GET, data, this);




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
        try {

            NetworkConsume.getInstance().hideProgress();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("Data");
            arrayList.clear();
            for (int i = 0; i < dataArray.length(); i++) {

                ReplayTripPojo datum = new ReplayTripPojo();
                datum.setRouteAssignID(dataArray.getJSONObject(i).getInt("RouteAssignID"));
                datum.setVehicleID(dataArray.getJSONObject(i).getInt("VehicleID"));
                datum.setLoadID(dataArray.getJSONObject(i).getInt("LoadID"));
                datum.setLoadTime(dataArray.getJSONObject(i).getString("LoadTime"));
                datum.setRouteName(dataArray.getJSONObject(i).getString("RouteName"));
                datum.setPickupLocationName(dataArray.getJSONObject(i).getString("PickupLocationName"));
                datum.setLastDecantingSiteName(dataArray.getJSONObject(i).getString("LastDecantingSiteName"));
                datum.setLoadDecanted(dataArray.getJSONObject(i).getBoolean("IsLoadDecanted"));
                arrayList.add(datum);
                //Toast.makeText(getContext(),""+arrayList,Toast.LENGTH_SHORT).show();
            }
            // ReplayVehiclesAdapter.notifyDataSetChanged();

                lstVehicles.setAdapter(new ReplayVehiclesAdapter(context, arrayList, ReplayTrips.this,bundle));


        } catch (Exception ex) {
            Log.d("--repex",ex.toString());
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
