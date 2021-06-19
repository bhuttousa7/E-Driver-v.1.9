package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Pojo.ReplayTripPojo;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.ReplayFragmentDetails_Map;
import com.telogix.telogixcaptain.activities.Fragments.ViewCheckListFragment;
import com.telogix.telogixcaptain.activities.Fragments.ViewLoadFragment;
import com.telogix.telogixcaptain.activities.Fragments.ViewRatingFragment;
import com.telogix.telogixcaptain.activities.Fragments.ViewStopFragment;

import java.util.ArrayList;

public class ReplayVehiclesAdapter extends RecyclerView.Adapter<ReplayVehiclesAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<ReplayTripPojo> availableVehicles;
    ArrayList<ReplayTripPojo> availableVehiclesFiltered;
    private final ItemSelection itemSelection;
    Button btn_inspect;
    Bundle bundle = new Bundle();




    public ReplayVehiclesAdapter(Context context, ArrayList<ReplayTripPojo> list, ItemSelection selection, Bundle b){
        this.context = context;
        this.availableVehicles = list;
        this.availableVehiclesFiltered = list;
        this.itemSelection = selection;
        this.bundle = b;
        NetworkConsume.getInstance().hideProgress();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_replaytrip_item,viewGroup,false);
        return new ViewHolder(view);
    }
    public void hideKeyboardFrom() {

        View view =((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // searchView.setIconified(true);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


         ReplayTripPojo body = availableVehicles.get(position);
        viewHolder.txtVRouteName.setText(body.getRouteName());
        viewHolder.txtVLoadTime.setText(body.getLoadTime());
        viewHolder.txtPickupLocationName.setText("FROM: "+body.getPickupLocationName());
        viewHolder.txtLastDecantingSiteName.setText("TO: " +body.getLastDecantingSiteName());
        Boolean isloadDecanted = body.isLoadDecanted();
        if(isloadDecanted){
            viewHolder.txtIsLoadDecanted.setText("Load Decanted: Yes");
        }
        else{ viewHolder.txtIsLoadDecanted.setText("Load Decanted: No");
        }
        //viewHolder.status.setText(body.getVehicleStatus());
        viewHolder.btn_inspect.setVisibility(View.GONE);
        String json = NetworkConsume.getInstance().getDefaults("userData",context);
        json= singleton.getsharedpreference(context).getString("userData","");
        Gson gson =  new Gson();

        TokenPojo ReplayTripPojo = gson.fromJson(json, TokenPojo.class);

        viewHolder.viewReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeFragment(new ReplayFragmentDetails_Map(),body,view);


            }
        });

//        if (body.getStatusID()==1){
//            viewHolder.assign.setText("Assign Load");
//            viewHolder.imgload.setEnabled(false);
//            viewHolder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
//
//        } else if (body.getStatusID() == 2){
//            viewHolder.imgload.setEnabled(true);
//            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            if(!UserRole.getRole(ReplayTripPojo.getRoleID()).equals(UserRole.SCHEDULER)) {
//                viewHolder.assign.setText("Assign Route");
//            }
//            else {
//                viewHolder.assign.setText("Load Assigned");
//            }
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        else if (body.getStatusID() == 3){
//            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            if(UserRole.getRole(ReplayTripPojo.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR))  //preload inspector
//            {
//                viewHolder.assign.setText("Inspect");
//                viewHolder.btn_inspect.setVisibility(View.GONE);
//            }
//            else {
//                viewHolder.assign.setText("Route Assigned");
//                viewHolder.btn_inspect.setVisibility(View.VISIBLE);
//            }
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        else if (body.getStatusID() == 4){
//            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            if(UserRole.getRole(ReplayTripPojo.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR) || UserRole.getRole(ReplayTripPojo.getRoleID()).equals(UserRole.SUPER_ADMIN)) //preload inspector
//            {
//                viewHolder.assign.setText("Inspected");
//            }
//            if(UserRole.getRole(ReplayTripPojo.getRoleID()) == UserRole.JM)
//            {
//                viewHolder.assign.setText("Briefing");
//            }
//
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        else if (body.getStatusID() == 5){
//            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            if(ReplayTripPojo.getRoleID().equals("1006") || ReplayTripPojo.getRoleID().equals("1001")) //preload inspector
//            {
//                viewHolder.assign.setText("Inspected");
//            }
//            if(UserRole.getRole(ReplayTripPojo.getRoleID()) == UserRole.JM)
//            {
//                viewHolder.assign.setText("Signed Off");
//            }
//
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        else if (body.getStatusID() == 6){
//            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            viewHolder.assign.setText("On Route");
//
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        else if (body.getStatusID() == 13){
//            //  viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
//            viewHolder.assign.setText("Checklist disapproved");
//
//            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
//        }
//        viewHolder.assign.setOnClickListener(v -> {
//
//            hideKeyboardFrom();
//            itemSelection.onClickCloseSearchView();
//            if (body.getStatusID() ==2) {
//                if ( ReplayTripPojo.getRoleID().equals("1006")){
//                    // new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign routes!");
//                }else {
//                    // new CustomToast().errorToast(context, viewHolder.linearLayout, "Functionality will be available soon!");
//                    if(!body.getLoadID().equals(null) && body.getLoadID()!="null") {
//                        ChangeFragment(new AssignRouteFragment(), body, v);
//                    }
//                    else
//                    {
//                        Toast.makeText(context,"No load ID asssociated",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            else if(body.getStatusID()==3)
//            {
//                if(ReplayTripPojo.getRoleID().equals("1006")) //preload inspector
//                {
//                    Intent i = new Intent(context, PreLoadInspectActivity.class);
//                    i.putExtra("routeid", "" + body.getRouteAssignID());
//                    i.putExtra("vehicleid", ""+body.getVehicleID());
//                    context.startActivity(i);
//
//                }
//                else {
//                    Intent i = new Intent(context, MapDetailActivity.class);
//                    i.putExtra("routeid", "" + body.getRouteAssignID());
//                    i.putExtra("routeid", "" + body.getRouteID());
//
//                    context.startActivity(i);
//                }
//            }
//            else if(body.getStatusID()==1) {
//                if (UserRole.getRole(ReplayTripPojo.getRoleID()) == UserRole.JM) {
//                    itemSelection.onAssignLoadClick(body);
//                }
//                else {
//                    //new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign load!");
//                }
//            }
//            else if(body.getStatusID()==4) {
//                if (UserRole.getRole(ReplayTripPojo.getRoleID()) == UserRole.JM) {
//                    Bundle b=new Bundle();
//
//                    if(body.getLoadID()!=null  ) {
//                        BriefingFragment briefingFragment=new BriefingFragment();
//                        b.putString("routeid",""+body.getRouteID());
//                        b.putString("vehicleNo",""+body.getVehicleNo());
//                        b.putString("vehicleType",""+body.getVehicleType());
//                        b.putString("vehicleID",""+body.getVehicleID());
//                        b.putString("loadID",""+body.getLoadID());
//                        b.putString("routeAssignID",""+body.getRouteAssignID());
//                        b.putBoolean("signOff",true);
//
//                        briefingFragment.setArguments(b);
//                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.main_content, briefingFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
//                                .commit();
//                    }
//                    else
//                    {
//                        Toast.makeText(context, "invalid vehicle id ", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//                else {
//                    //new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign load!");
//                }
//            }
//        });
//
//        viewHolder.btn_inspect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboardFrom();
//                if(body.getStatusID()==3)
//                {
//
//                    Intent i = new Intent(context, PreLoadInspectActivity.class);
//                    i.putExtra("routeid", "" + body.getRouteAssignID());
//                    i.putExtra("vehicleid", ""+body.getVehicleID());
//                    context.startActivity(i);
//                }
//            }
//        });
        viewHolder.imgload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(!body.equals(null))
                {
                    if(body.getLoadID()!=0)
                    {
                        ChangeFragment(new ViewLoadFragment(),body,v);
                    }
                    else
                    {
                        Toast.makeText(context,"No Load Assigned",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        viewHolder.imgstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(!body.equals(null)){
                   ChangeFragment(new ViewStopFragment(),body,v);
                }
            }
        });

        viewHolder.imginspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(!body.equals(null)){
                    ChangeFragment(new ViewCheckListFragment(),body,v);

                }
            }
        });

        viewHolder.imgrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(!body.equals(null))
                {
                    if(body.getLoadID()!=0)
                    {
                        ChangeFragment(new ViewRatingFragment(),body,v);
                    }
                    else
                    {
                        Toast.makeText(context,"No LoadID Found",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return availableVehiclesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    availableVehiclesFiltered = availableVehicles;
                } else {
                    ArrayList<ReplayTripPojo> filteredList = new ArrayList<>();
                    for (ReplayTripPojo row : availableVehicles) {
                        if (row.getLastDecantingSiteName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    availableVehiclesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = availableVehiclesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                availableVehiclesFiltered = (ArrayList<ReplayTripPojo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVRouteName,txtVLoadTime,txtPickupLocationName,txtLastDecantingSiteName,txtIsLoadDecanted,status;
        Button assignRoute,btn_inspect,viewReplay,assign;
        ImageView imgload,imgstop,imginspection,imgrating;
        LinearLayout linearLayout,layoutload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewReplay = itemView.findViewById(R.id.viewReplay);
            txtVRouteName=itemView.findViewById(R.id.txtVRouteName);
            txtVLoadTime=itemView.findViewById(R.id.txtVLoadTime);
            txtPickupLocationName=itemView.findViewById(R.id.txtPickupLocationName);
            txtLastDecantingSiteName=itemView.findViewById(R.id.txtLastDecantingSiteName);
            txtIsLoadDecanted = itemView.findViewById(R.id.txtIsLoadDecanted);

            status=itemView.findViewById(R.id.vehicleStatus);
            linearLayout=itemView.findViewById(R.id.mainLL);
            assign=itemView.findViewById(R.id.btnAction);
            btn_inspect=itemView.findViewById(R.id.btnInspect);
            layoutload=itemView.findViewById(R.id.layoutload);
            imgload=itemView.findViewById(R.id.imgload);
            imgstop=itemView.findViewById(R.id.imgstop);
            imginspection=itemView.findViewById(R.id.imginspection);
            imgrating=itemView.findViewById(R.id.imgbriefing);


        }
    }
    private void ChangeFragment(Fragment fragment, ReplayTripPojo body, View v){
        Bundle b=new Bundle();
        if(body.getRouteAssignID()!= 0 ) {
            b.putBoolean("HaulierWiseVehicleListFragment",true);
            b.putString("RouteAssignID",""+body.getRouteAssignID());
            b.putString("VehicleID",""+body.getVehicleID());
            b.putString("LoadID",""+body.getLoadID());
            b.putString("PickupLocationName",body.getPickupLocationName());
            b.putString("LastDecantingSiteName",body.getLastDecantingSiteName());
            b.putString("VehicleNo",bundle.getString("VehicleNo"));
            fragment.setArguments(b);

        }
        else
        {
            Toast.makeText(context, "invalid vehicle id ", Toast.LENGTH_SHORT).show();
        }

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment,"ReplayFragmentDetails_Map").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }
}
