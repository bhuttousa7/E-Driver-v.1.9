package com.telogix.telogixcaptain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.HazardDetailFragment;

import java.util.ArrayList;

public class HazardAdapter extends RecyclerView.Adapter<HazardAdapter.MyViewHolder> {

    private final ArrayList<com.telogix.telogixcaptain.Pojo.Hazards> Hazards;
    public static com.telogix.telogixcaptain.Pojo.Hazards currentHazard;
    public HazardAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Hazards> Routes) {
        this.Hazards = Routes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hazardlist_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        String hazardName = Hazards.get(i).getHazardName();
        String hazardLocation = ""+Hazards.get(i).getLocation();
        String hazardType = Hazards.get(i).getHazardType();

        myViewHolder.txt_hazardname.setText(hazardName);
        myViewHolder.txt_hazardloc.setText(hazardLocation);
        myViewHolder.txt_hazardtype.setText(hazardType);
        myViewHolder.cardViewHazard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHazard=Hazards.get(i);
                ChangeFragment(new HazardDetailFragment(),v);
            }
        });

    }
    private void ChangeFragment(Fragment fragment, View v) {


        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }


    @Override
    public int getItemCount() {
        return Hazards.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_hazardname,txt_hazardtype,txt_hazardloc;
        CardView cardViewHazard;

        public MyViewHolder(View itemView) {
            super(itemView);

         txt_hazardname = itemView.findViewById(R.id.txt_hazardname);
            txt_hazardtype = itemView.findViewById(R.id.txt_hazardtype);
            txt_hazardloc = itemView.findViewById(R.id.txt_hazardlocation);
            cardViewHazard=itemView.findViewById(R.id.cardviewHazard);

        }
    }

}