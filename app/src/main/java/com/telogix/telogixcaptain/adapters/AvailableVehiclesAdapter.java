package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Pojo.Datum;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.activities.Briefing.BriefingFragment;
import com.telogix.telogixcaptain.activities.Fragments.AssignRouteFragment;
import com.telogix.telogixcaptain.activities.Fragments.ViewLoadFragment;
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.activities.PreLoadInspectActivity;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;

import java.util.ArrayList;

public class AvailableVehiclesAdapter extends RecyclerView.Adapter<AvailableVehiclesAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<Datum> availableVehicles;
    ArrayList<Datum> availableVehiclesFiltered;
    private final ItemSelection itemSelection;

    public AvailableVehiclesAdapter(Context context, ArrayList<Datum> list, ItemSelection selection){
        this.context = context;
        this.availableVehicles = list;
        this.availableVehiclesFiltered = list;
        this.itemSelection = selection;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_vehicle_item,viewGroup,false);
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
        final Datum body = availableVehicles.get(position);
        viewHolder.txtVehicleName.setText("Vehicle "+body.getVehicleID());
        viewHolder.txtVehicleType.setText(body.getVehicleType());
        viewHolder.txtHaulier.setText(body.getHaulierName());
        viewHolder.txtVehicleType.setText(body.getVehicleType());
        viewHolder.txtNumber.setText(body.getVehicleNo());
        viewHolder.status.setText(body.getVehicleStatus());
        viewHolder.btn_inspect.setVisibility(View.GONE);
        String json = NetworkConsume.getInstance().getDefaults("userData",context);
        json= singleton.getsharedpreference(context).getString("userData","");
        Gson gson =  new Gson();

        TokenPojo datum = gson.fromJson(json, TokenPojo.class);
        if (body.getStatusID()==1){
            viewHolder.assign.setText("Assign Load");
            viewHolder.imgload.setEnabled(false);
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        } else if (body.getStatusID() == 2){
            viewHolder.imgload.setEnabled(true);
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            if(!UserRole.getRole(datum.getRoleID()).equals(UserRole.SCHEDULER)) {
                viewHolder.assign.setText("Assign Route");
            }
            else {
                viewHolder.assign.setText("Load Assigned");
            }
            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        else if (body.getStatusID() == 3){
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            if(UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR))  //preload inspector
            {
                viewHolder.assign.setText("Inspect");
                viewHolder.btn_inspect.setVisibility(View.GONE);
            }
            else {
                viewHolder.assign.setText("Route Assigned");
                viewHolder.btn_inspect.setVisibility(View.VISIBLE);
            }
            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        else if (body.getStatusID() == 4){
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            if(UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR) || UserRole.getRole(datum.getRoleID()).equals(UserRole.SUPER_ADMIN)) //preload inspector
            {
                viewHolder.assign.setText("Inspected");
            }
            if(UserRole.getRole(datum.getRoleID()) == UserRole.JM)
            {
                viewHolder.assign.setText("Briefing");
            }

            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        else if (body.getStatusID() == 5){
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            if(datum.getRoleID().equals("1006") || datum.getRoleID().equals("1001")) //preload inspector
            {
                viewHolder.assign.setText("Inspected");
            }
            if(UserRole.getRole(datum.getRoleID()) == UserRole.JM)
            {
                viewHolder.assign.setText("Signed Off");
            }

            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        else if (body.getStatusID() == 6){
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            viewHolder.assign.setText("On Route");

            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        else if (body.getStatusID() == 13){
          //  viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            viewHolder.assign.setText("Checklist disapproved");

            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));
        }
        viewHolder.assign.setOnClickListener(v -> {

            hideKeyboardFrom();
            itemSelection.onClickCloseSearchView();
            if (body.getStatusID() ==2) {
                if ( datum.getRoleID().equals("1006")){
                    // new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign routes!");
                }else {
                    // new CustomToast().errorToast(context, viewHolder.linearLayout, "Functionality will be available soon!");
                    if(!body.getLoadID().equals(null) && body.getLoadID()!="null") {
                        ChangeFragment(new AssignRouteFragment(), body, v);
                    }
                    else
                    {
                        Toast.makeText(context,"No load ID asssociated",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if(body.getStatusID()==3)
            {
                if(datum.getRoleID().equals("1006")) //preload inspector
                {
                    Intent i = new Intent(context, PreLoadInspectActivity.class);
                    i.putExtra("routeid", "" + body.getRouteAssignID());
                    i.putExtra("vehicleid", ""+body.getVehicleID());
                    context.startActivity(i);

                }
                else {
                    Intent i = new Intent(context, MapDetailActivity.class);
                    i.putExtra("routeid", "" + body.getRouteAssignID());
                    i.putExtra("routeid", "" + body.getRouteID());

                    context.startActivity(i);
                }
            }
            else if(body.getStatusID()==1) {
                if (UserRole.getRole(datum.getRoleID()) == UserRole.JM) {
                    itemSelection.onAssignLoadClick(body);
                }
                else {
                    //new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign load!");
                }
            }
            else if(body.getStatusID()==4) {
                if (UserRole.getRole(datum.getRoleID()) == UserRole.JM) {
                    Bundle b=new Bundle();

                    if(body.getLoadID()!=null  ) {
                        BriefingFragment briefingFragment=new BriefingFragment();
                        b.putString("routeid",""+body.getRouteID());
                        b.putString("vehicleNo",""+body.getVehicleNo());
                        b.putString("vehicleType",""+body.getVehicleType());
                        b.putString("VehicleID",""+body.getVehicleID());
                        b.putString("LoadID",""+body.getLoadID());
                        b.putString("routeAssignID",""+body.getRouteAssignID());
                        b.putBoolean("signOff",true);

                        briefingFragment.setArguments(b);
                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content, briefingFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                                .commit();
                    }
                    else
                    {
                        Toast.makeText(context, "invalid vehicle id ", Toast.LENGTH_SHORT).show();
                    }


                }
                else {
                    //new CustomToast().errorToast(context, viewHolder.linearLayout, "You can't assign load!");
                }
            }
        });

        viewHolder.btn_inspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(body.getStatusID()==3)
                {

                    Intent i = new Intent(context, PreLoadInspectActivity.class);
                    i.putExtra("routeid", "" + body.getRouteAssignID());
                    i.putExtra("vehicleid", ""+body.getVehicleID());
                    context.startActivity(i);
                }
            }
        });
        viewHolder.imgload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if(!body.equals(null))
                {
                    if(body.getLoadID()!="null")
                    {
                        CustomData.getInstance().vehicleDetails=body;
                        ChangeFragment(new ViewLoadFragment(),body,v);
                    }
                    else
                    {
                        Toast.makeText(context,"No Load Assigned",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
   viewHolder.layoutload.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

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
                    ArrayList<Datum> filteredList = new ArrayList<>();
                    for (Datum row : availableVehicles) {
                        if (row.getVehicleNo().toLowerCase().contains(charString.toLowerCase())) {
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
                availableVehiclesFiltered = (ArrayList<Datum>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVehicleName , txtVehicleType,txtHaulier , txtNumber,status;
        Button assignRoute,btn_inspect;
        ImageView imgload,imgroute,imginspection,imgbriefing;
        LinearLayout linearLayout,layoutload;

        Button assign;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVehicleName=itemView.findViewById(R.id.txtVname);
            txtVehicleType=itemView.findViewById(R.id.txtVtype);
            txtHaulier=itemView.findViewById(R.id.txtHaulierName);
            txtNumber=itemView.findViewById(R.id.txtVnumber);
            status=itemView.findViewById(R.id.vehicleStatus);
            linearLayout=itemView.findViewById(R.id.mainLL);
            assign=itemView.findViewById(R.id.btnAction);
            btn_inspect=itemView.findViewById(R.id.btnInspect);
            layoutload=itemView.findViewById(R.id.layoutload);
            imgload=itemView.findViewById(R.id.imgload);
            imgroute=itemView.findViewById(R.id.imgroute);
            imginspection=itemView.findViewById(R.id.imginspection);
            imgbriefing=itemView.findViewById(R.id.imgbriefing);


        }
    }
    private void ChangeFragment(Fragment fragment, Datum body, View v){
        Bundle b=new Bundle();
        if(body.getLoadID()!=null  ) {
            b.putString("LoadID",""+body.getLoadID());
            b.putString("VehicleID",""+body.getVehicleID());

            fragment.setArguments(b);

        }
        else
        {
            Toast.makeText(context, "invalid vehicle id ", Toast.LENGTH_SHORT).show();
        }

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }
}
