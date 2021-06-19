package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.AssignRouteDetail;
import com.telogix.telogixcaptain.activities.Fragments.RouteDetailFragment;

import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.MyViewHolder> {

    private final ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes;
    Context context;
    String vehicleID;
    public static com.telogix.telogixcaptain.Pojo.Routes currentRouteClicked;
    com.telogix.telogixcaptain.Interfaces.onClickInterface onClickInterface;

    public RoutesAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes, Context context) {
        this.Routes = Routes;
        this.context = context;

        // this.onClickInterface = onClickInterface;
    }

    public RoutesAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes, Context context, String vehicleID) {
        this.Routes = Routes;
        this.context = context;
        this.vehicleID = vehicleID;
        // this.onClickInterface = onClickInterface;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.routes_listdesign, viewGroup, false);

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        String RouteName = Routes.get(i).getRouteName();
        String RouteStops = "" + Routes.get(i).getRouteDetails().size() + " stop";
        myViewHolder.routeName.setText(RouteName);
        myViewHolder.txt_stops.setText(RouteStops);
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicleID != "" && vehicleID!=null) {
                    currentRouteClicked=Routes.get(i);
                    ChangeFragment(new AssignRouteDetail(), v, i);
                }
                else {
                    currentRouteClicked=Routes.get(i);
                    ChangeFragment(new RouteDetailFragment(), v, i);
                }
            }
        });
    }

    private void ChangeFragment(Fragment fragment, View v, int position) {
        Bundle b = new Bundle();
        if (Routes.get(position).getOriginLat() != null && Routes.get(position).getOriginLat() != null && Routes.get(position).getDestinationLat() != null && Routes.get(position).getDestinationLong() != null) {
            b.putDouble("orgLat", Routes.get(position).getOriginLat());
            b.putDouble("orgLng", Routes.get(position).getOriginLong());
            b.putDouble("destLat", Routes.get(position).getDestinationLat());
            b.putDouble("destLng", Routes.get(position).getDestinationLong());
            b.putString("RouteName", Routes.get(position).getRouteName());
            if(vehicleID!="" && vehicleID!=null)
            {
                b.putString("RouteID", ""+Routes.get(position).getRouteID());
                b.putString("vehicleID",vehicleID);
            }


            fragment.setArguments(b);
        } else {
            Toast.makeText(context, "invalid route position: " + position, Toast.LENGTH_SHORT).show();
        }

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

    @Override
    public int getItemCount() {
        return Routes.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView routeName, txt_stops, txt_startPoint, txt_endPoint;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            routeName = itemView.findViewById(R.id.txt_routename);
            txt_stops = itemView.findViewById(R.id.txt_stops);
            txt_startPoint = itemView.findViewById(R.id.txt_startpoint);
            txt_endPoint = itemView.findViewById(R.id.txt_endpoint);

        }
    }

}