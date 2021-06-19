package com.telogix.telogixcaptain.adapters;



import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WaypointAdapter extends RecyclerView.Adapter<WaypointAdapter.MyViewHolder> {

    private final ArrayList Waypoints;
    Context context;
    boolean editable=false;
   // public static Hazards currentHazard;
    public WaypointAdapter(Context context,boolean editable,ArrayList Waypoints) {
        this.Waypoints = Waypoints;
        this.context=context;
        this.editable=editable;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.waypointlist_design, viewGroup, false));
    }


    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress.get(i).getAddressLine(0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String waypointName = Waypoints.get(i).toString();
        double lat,lng;
        lat=Double.parseDouble(waypointName.split(",")[0]);
        lng=Double.parseDouble(waypointName.split(",")[1]);

//        String hazardLocation = ""+Waypoints.get(i).getLocation();
//        String hazardType = Waypoints.get(i).getHazardType();
//
        myViewHolder.txt_hazardname.setText( getAddress(lat,lng));
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
        return Waypoints.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //
        TextView txt_hazardname,txt_type,txt_hazardloc;
        Button btnHazardDetail;
        ImageButton cross;
        ImageView image_circle;
//        CardView cardViewHazard;

        public MyViewHolder(View itemView) {
            super(itemView);
            image_circle=itemView.findViewById(R.id.image_circle);
            txt_type=itemView.findViewById(R.id.txttype);
            txt_hazardname = itemView.findViewById(R.id.txthazadname);
            cross = itemView.findViewById(R.id.cross);

        }
    }

}