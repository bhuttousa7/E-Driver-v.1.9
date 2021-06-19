package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.ViewLoad.Data;
import com.telogix.telogixcaptain.activities.Fragments.AssignCopyRoute;
import com.telogix.telogixcaptain.activities.Fragments.AssignRouteDetail;
import com.telogix.telogixcaptain.activities.Fragments.RouteDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SwipeListAdapter extends BaseSwipeAdapter  {

    private final ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes;
    Context context;
    String vehicleID;
    public static com.telogix.telogixcaptain.Pojo.Routes currentRouteClicked;

    com.telogix.telogixcaptain.Interfaces.onClickInterface onClickInterface;
boolean AssignRoute,EditRoute;
    private Geocoder geocoder;

    public SwipeListAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes, Context context) {
        this.Routes = Routes;
        this.context = context;

        // this.onClickInterface = onClickInterface;
    }

    public SwipeListAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Routes> Routes, Context context, String vehicleID, boolean AssignRoute, boolean EditRoute) {
        this.Routes = Routes;
        this.context = context;
        this.vehicleID = vehicleID;
        this.AssignRoute=AssignRoute;
        this.EditRoute=EditRoute;
        // this.onClickInterface = onClickInterface;
    }



    private void ChangeFragment(Fragment fragment, View v, int position,Bundle b) {
           fragment.setArguments(b);

        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }



    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipelayout;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.routes_listdesign, parent, false);

        return itemView;
    }

    @Override
    public void fillValues(int i, View itemView) {
        TextView routeName, txt_stops, txt_startPoint, txt_endPoint,txtstartAddress,txtendAddress;
        LinearLayout cardView;
        cardView = itemView.findViewById(R.id.cardView);
        routeName = itemView.findViewById(R.id.txt_routename);
        Button btnCopy=itemView.findViewById(R.id.btnCopy);
        Button btnEdit=itemView.findViewById(R.id.btnEditRoute);
        txt_stops = itemView.findViewById(R.id.txt_stops);
        txt_startPoint = itemView.findViewById(R.id.txt_startpoint);
        txt_endPoint = itemView.findViewById(R.id.txt_endpoint);
        txtendAddress=itemView.findViewById(R.id.txtendAddress);
        txtstartAddress=itemView.findViewById(R.id.txtstartAddress);
        String RouteName = Routes.get(i).getRouteName();
        List<Address> addressesFrom= getAddressLocality(Routes.get(i).getOriginLat(),Routes.get(i).getOriginLong());
        List<Address> addressesTo= getAddressLocality(Routes.get(i).getDestinationLat(),Routes.get(i).getDestinationLong());
        if(addressesFrom!=null && addressesTo!=null) {
            try {
                Data data = new Data();
                String from = addressesFrom.get(0).getSubAdminArea();
                String To = addressesTo.get(0).getSubAdminArea();
                String fromAddress = addressesFrom.get(0).getAddressLine(0);
                String ToAddress = addressesTo.get(0).getAddressLine(0);
                String pickupLocationName = Routes.get(i).getPickupLocationName();
                String lastDecantingSiteName  = Routes.get(i).getLastDecatingSiteName();
                if (from != "") {
                    if(pickupLocationName!=null && !pickupLocationName.isEmpty())
                        txt_startPoint.setText(pickupLocationName);
                    else txt_startPoint.setText(from);
                } else {
                    txt_startPoint.setText("---");
                }
                if (To != "") {
                    if(lastDecantingSiteName!=null && !lastDecantingSiteName.isEmpty())
                        txt_endPoint.setText(lastDecantingSiteName);
                    else txt_endPoint.setText(To);
                } else {
                    txt_endPoint.setText("---");
                }
                if (fromAddress != "") {
                    txtstartAddress.setText(fromAddress);
                } else {
                    txtstartAddress.setText("---");
                }
                if (ToAddress != "") {
                    txtendAddress.setText(ToAddress);
                } else {
                    txtendAddress.setText("---");
                }
            }
            catch (Exception ex)
            {}
        }
        else
        {
            txtendAddress.setText("---");
            txtstartAddress.setText("---");
            txt_startPoint.setText("---");
            txt_endPoint.setText("---");
        }

        if(Routes.get(i).getRouteDetails().size()>0)
        {
            int stopCount=0;
            for(int j=0;j<Routes.get(i).getRouteDetails().size();j++){

                if(!Routes.get(i).getRouteDetails().get(j).getRouteTypeID().equals("1"))
                {
                    stopCount++;
                }
                String RouteStops = "" + stopCount+ " stop";
                txt_stops.setText(RouteStops);
            }
        }
        else
        {
            String RouteStops = "" + Routes.get(i).getRouteDetails().size() + " stop";
            txt_stops.setText(RouteStops);
        }
        routeName.setText(RouteName);

        if(EditRoute)
        {btnEdit.setVisibility(View.VISIBLE);}
        else
        {
            btnEdit.setVisibility(View.GONE);
        }
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRouteClicked=Routes.get(i);
                EditRoute=false;
                Bundle b=new Bundle();

                if (Routes.get(i).getOriginLat() != null && Routes.get(i).getOriginLat() != null && Routes.get(i).getDestinationLat() != null && Routes.get(i).getDestinationLong() != null) {
                    b.putSerializable("Route",Routes.get(i));
                    b.putDouble("orgLat", Routes.get(i).getOriginLat());
                    b.putDouble("orgLng", Routes.get(i).getOriginLong());
                    b.putDouble("destLat", Routes.get(i).getDestinationLat());
                    b.putDouble("destLng", Routes.get(i).getDestinationLong());
                    b.putString("RouteID",""+ Routes.get(i).getRouteID());
                    b.putString("RouteName", Routes.get(i).getRouteName());
                    b.putBoolean("AssignRoute",AssignRoute);
                    b.putBoolean("EditRoute",EditRoute);
                    if(vehicleID!="" && vehicleID!=null)
                    {
                        b.putString("RouteID", ""+Routes.get(i).getRouteID());
                        b.putString("vehicleID",vehicleID);
                    }

                } else {
                    Toast.makeText(context, "invalid route position: " + i, Toast.LENGTH_SHORT).show();
                }
                ChangeFragment(new AssignCopyRoute(), v, i,b);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRouteClicked=Routes.get(i);
                EditRoute=true;
                Bundle b=new Bundle();

                if (Routes.get(i).getOriginLat() != null && Routes.get(i).getOriginLat() != null && Routes.get(i).getDestinationLat() != null && Routes.get(i).getDestinationLong() != null) {
                    b.putSerializable("Route",Routes.get(i));
                    b.putDouble("orgLat", Routes.get(i).getOriginLat());
                    b.putDouble("orgLng", Routes.get(i).getOriginLong());
                    b.putDouble("destLat", Routes.get(i).getDestinationLat());
                    b.putDouble("destLng", Routes.get(i).getDestinationLong());
                    b.putString("RouteName", Routes.get(i).getRouteName());
                    b.putString("RouteID",""+ Routes.get(i).getRouteID());
                    b.putBoolean("AssignRoute",AssignRoute);
                    b.putBoolean("EditRoute",EditRoute);
                    if(vehicleID!="" && vehicleID!=null)
                    {
                        b.putString("RouteID", ""+Routes.get(i).getRouteID());
                        b.putString("vehicleID",vehicleID);
                    }

                } else {
                    Toast.makeText(context, "invalid route position: " + i, Toast.LENGTH_SHORT).show();
                }
                ChangeFragment(new AssignCopyRoute(), v, i,b);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicleID != "" && vehicleID!=null) {
                    currentRouteClicked=Routes.get(i);
                    Bundle b=new Bundle();

                    if (Routes.get(i).getOriginLat() != null && Routes.get(i).getOriginLat() != null && Routes.get(i).getDestinationLat() != null && Routes.get(i).getDestinationLong() != null) {
                        b.putSerializable("Route",Routes.get(i));
                        b.putDouble("orgLat", Routes.get(i).getOriginLat());
                        b.putDouble("orgLng", Routes.get(i).getOriginLong());
                        b.putDouble("destLat", Routes.get(i).getDestinationLat());
                        b.putDouble("destLng", Routes.get(i).getDestinationLong());
                        b.putString("RouteName", Routes.get(i).getRouteName());
                        b.putBoolean("AssignRoute",AssignRoute);
                        b.putString("RouteID",""+ Routes.get(i).getRouteID());
                        b.putBoolean("EditRoute",EditRoute);
                        if(vehicleID!="" && vehicleID!=null)
                        {
                            b.putString("RouteID", ""+Routes.get(i).getRouteID());
                            b.putString("vehicleID",vehicleID);
                        }

                    } else {
                        Toast.makeText(context, "invalid route position: " + i, Toast.LENGTH_SHORT).show();
                    }
                    ((Activity)context).onBackPressed();
                    ChangeFragment(new AssignRouteDetail(), v, i,b);
                }
                else {
                    currentRouteClicked=Routes.get(i);
                    Bundle b=new Bundle();
                    if (Routes.get(i).getOriginLat() != null && Routes.get(i).getOriginLat() != null && Routes.get(i).getDestinationLat() != null && Routes.get(i).getDestinationLong() != null) {
                        b.putSerializable("Route",Routes.get(i));
                        b.putDouble("orgLat", Routes.get(i).getOriginLat());
                        b.putDouble("orgLng", Routes.get(i).getOriginLong());
                        b.putDouble("destLat", Routes.get(i).getDestinationLat());
                        b.putDouble("destLng", Routes.get(i).getDestinationLong());
                        b.putString("RouteName", Routes.get(i).getRouteName());
                        b.putString("RouteID",""+ Routes.get(i).getRouteID());
                        b.putBoolean("AssignRoute",AssignRoute);
                        b.putBoolean("EditRoute",EditRoute);
                        b.putBoolean("Briefing",false);
                        b.putString("RouteID", ""+Routes.get(i).getRouteID());
                        if(vehicleID!="" && vehicleID!=null)
                        {
                            b.putString("RouteID", ""+Routes.get(i).getRouteID());
                            b.putString("vehicleID",vehicleID);
                        }

                    } else {
                        Toast.makeText(context, "invalid route position: " + i, Toast.LENGTH_SHORT).show();
                    }
                    ChangeFragment(new RouteDetailFragment(), v, i,b);
                }
            }
        });
    }
    private List<Address> getAddressLocality(double lat, double lng) {
        geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> listaddress = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < listaddress.size(); i++) {
                return listaddress;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public int getCount() {
        return Routes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




}