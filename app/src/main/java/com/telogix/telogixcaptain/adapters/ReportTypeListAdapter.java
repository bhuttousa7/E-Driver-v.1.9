package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Pojo.ReportPojo;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.HaulierListFragment;
import com.telogix.telogixcaptain.activities.Fragments.HaulierWiseVehicleListFragment;
import com.telogix.telogixcaptain.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportTypeListAdapter extends RecyclerView.Adapter<ReportTypeListAdapter.MyViewHolder> {

    private Context context;
    public List<ReportPojo> ReportTypeList = new ArrayList<ReportPojo>();
    private Bundle b;
    private TokenPojo datum;

    public ReportTypeListAdapter(Context context, List<ReportPojo> ReportTypeList,Bundle b){

        this.context = context;
        this.ReportTypeList = ReportTypeList;
        this.b = b;


    }



    @NonNull
    @Override
    public ReportTypeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reporttypelist,parent,false);
        return new ReportTypeListAdapter.MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.listName.setText(ReportTypeList.get(position).getName());
        holder.listName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.putInt("ReportID",ReportTypeList.get(position).getID());
                b.putString("ReportName",ReportTypeList.get(position).getName());
                datum = b.getParcelable("datum");
                if(UserRole.getRole(datum.getRoleID()) == UserRole.MANAGER){

                    ChangeFragment(new HaulierListFragment(),b);
                }
                else{
                    b.putString("haulierID",datum.getHaulierID());
                    ChangeFragment(new HaulierWiseVehicleListFragment(),b);


                }
                //ChangeFragment(new ReportDetailsFragment(),b);
               // ReportTypeList.clear();
            }
        });

    }

    @Override
    public int getItemCount() {
        return ReportTypeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout listNameLayout;
        TextView listName;
        MyViewHolder(View itemView){
            super(itemView);

            listName = itemView.findViewById(R.id.listName);
            listNameLayout = itemView.findViewById(R.id.listNameLayout);




        }

    }
    private void ChangeFragment(Fragment fragment, Bundle b) {

        FragmentTransaction ft = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }


}
