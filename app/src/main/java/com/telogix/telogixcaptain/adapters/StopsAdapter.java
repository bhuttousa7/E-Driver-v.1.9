package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.RouteDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.MyViewHolder> {

    private final ArrayList<RouteDetail> allpoints;
    Context context;
    boolean editable=false;
    boolean fromBriefing=false;
    // public static Hazards currentHazard;
    public StopsAdapter(Context context, boolean editable, ArrayList<RouteDetail> allpoints, boolean fromBriefing) {
        this.allpoints = allpoints;
        this.context=context;
        this.editable=editable;
        this.fromBriefing=fromBriefing;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(fromBriefing)
        {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multistopdesign, viewGroup, false));

        }
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.waypointlist_design, viewGroup, false));
    }


    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getAddressLine(0);

            }
        } catch (Exception e) {
           return "Address unknown";
        }
        return "";
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        double lat,lng;
        lat=allpoints.get(i).getLatitude();
        lng=allpoints.get(i).getLongitude();

//        String hazardLocation = ""+Waypoints.get(i).getLocation();
//        String hazardType = Waypoints.get(i).getHazardType();
//
        myViewHolder.txt_hazardname.setText( getAddress(lat,lng));
            try {

                if(allpoints.get(i).getRouteTypeID().equals("4"))
                {
                    myViewHolder.txt_type.setText(" Fuel Station");
                    myViewHolder.image_circle.setImageResource(R.drawable.stopmap);
                }else if(allpoints.get(i).getRouteTypeID().equals("5"))
                {
                    myViewHolder.txt_type.setText(" Restaurant");
                    myViewHolder.image_circle.setImageResource(R.drawable.food);
                }
                else if(allpoints.get(i).getRouteTypeID().equals("6"))
                {
                    myViewHolder.txt_type.setText(" Rest Area");
                    myViewHolder.image_circle.setImageResource(R.drawable.restarea);
                }else if(allpoints.get(i).getRouteTypeID().equals("1"))
                {
                    myViewHolder.txt_type.setText(" Waypoint");
                    myViewHolder.image_circle.setImageResource(R.drawable.circle);
                }
                else
                {
                    myViewHolder.txt_type.setText(" Stop");
                    myViewHolder.image_circle.setImageResource(R.drawable.stop_bus);
                }
            } catch (Exception ex) {

            }


        if(!editable)
        {
            myViewHolder.cross.setVisibility(View.GONE);
            myViewHolder.txt_hazardname.setTextColor(Color.GRAY);
        }
        else {
            myViewHolder.cross.setVisibility(View.VISIBLE);
        }
//        myViewHolder.txt_hazardloc.setText(hazardLocation);
//        myViewHolder.txt_hazardtype.setText(hazardType);
//        myViewHolder.btnHazardDetail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // currentHazard=Waypoints.get(i);
//               // ChangeFragment(new HazardDetailBriefing(),v);
//            }
//        });

    }
    private void ChangeFragment(Fragment fragment, View v) {


        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }


    @Override
    public int getItemCount() {
        return allpoints.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //
        TextView txt_hazardname,txt_type;
        ImageButton cross;
        ImageView image_circle;
//        CardView cardViewHazard;

        public MyViewHolder(View itemView) {
            super(itemView);

            try {
                image_circle = itemView.findViewById(R.id.image_circle);
                txt_type = itemView.findViewById(R.id.txttype);
                txt_hazardname = itemView.findViewById(R.id.txthazadname);
                cross = itemView.findViewById(R.id.cross);
            }
            catch (Exception ex)
            {

            }
        }
    }

}