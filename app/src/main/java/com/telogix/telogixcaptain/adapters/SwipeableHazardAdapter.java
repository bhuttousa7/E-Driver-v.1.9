package com.telogix.telogixcaptain.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.activities.Fragments.EditHazardFragment;
import com.telogix.telogixcaptain.activities.Fragments.HazardDetailFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class SwipeableHazardAdapter extends BaseSwipeAdapter {


    private final ArrayList<com.telogix.telogixcaptain.Pojo.Hazards> Hazards;
    public static com.telogix.telogixcaptain.Pojo.Hazards currentHazard;
    public Context context;
    com.telogix.telogixcaptain.Interfaces.response_interface response_interface;
    public SwipeableHazardAdapter(Context context, com.telogix.telogixcaptain.Interfaces.response_interface response_interface, ArrayList<com.telogix.telogixcaptain.Pojo.Hazards> Routes) {
        this.Hazards = Routes;
        this.context=context;
        this.response_interface=response_interface;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipelayout;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.hazardlist_design, parent, false);

        return itemView;
    }

    @Override
    public void fillValues(int i, View itemView) {
        TextView txt_hazardname,txt_hazardtype,txt_hazardloc;
        SwipeLayout cardViewHazard;
        txt_hazardname = itemView.findViewById(R.id.txt_hazardname);
        txt_hazardtype = itemView.findViewById(R.id.txt_hazardtype);
        txt_hazardloc = itemView.findViewById(R.id.txt_hazardlocation);
        cardViewHazard=itemView.findViewById(R.id.swipelayout);
        Button btnDelete=itemView.findViewById(R.id.btnDeleteHazard);
        Button btnEdit=itemView.findViewById(R.id.btnEditHazard);
        String hazardName = Hazards.get(i).getHazardName();
        String hazardLocation = ""+Hazards.get(i).getLocation();
        String hazardType = Hazards.get(i).getHazardType();

        btnEdit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHazard=Hazards.get(i);
                ChangeFragment(new EditHazardFragment(),v);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new AlertDialog.Builder(context).setTitle("Are you sure you want to delete this Hazard?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int j) {
                       deleteHazard(Hazards.get(i));
                       dialogInterface.dismiss();
                   }
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               }).show();

            }
        });
        txt_hazardname.setText(hazardName);
        txt_hazardloc.setText(hazardLocation);
        txt_hazardtype.setText(hazardType);
        cardViewHazard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardViewHazard.getOpenStatus()== SwipeLayout.Status.Open)
                {
                    cardViewHazard.close();
                }
                else{
                currentHazard=Hazards.get(i);
                ChangeFragment(new HazardDetailFragment(),v);
                }
            }
        });
        cardViewHazard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cardViewHazard.open();
                return true;
            }
        });
    }

    private void deleteHazard(com.telogix.telogixcaptain.Pojo.Hazards hazards) {
    httpvolley.stringrequestpost("api/Hazards/DeleteHazard?HazardID="+hazards.getHazardID(), Request.Method.DELETE,new HashMap<>(), response_interface);
// response 6009
    }

    @Override
    public int getCount() {
        return Hazards.size();
    }

    @Override
    public Object getItem(int i) {
        return Hazards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Hazards.get(i).getHazardID();
    }
    private void ChangeFragment(Fragment fragment, View v) {


        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

}
