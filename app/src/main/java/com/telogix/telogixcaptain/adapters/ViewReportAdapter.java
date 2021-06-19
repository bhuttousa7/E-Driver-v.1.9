package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewReportAdapter extends RecyclerView.Adapter<ViewReportAdapter.MyViewHolder> {

    Bundle b;
    int count = 0;
    GridView TotalSimpleGrid;
    Context context;
    List<HashMap> totaloadDetailsList = new ArrayList<HashMap>();
    List<HashMap> singleloadDetailsList = new ArrayList<HashMap>();
    RecyclerView cardViewLoadList;


    public ViewReportAdapter(Context context, Bundle b,List totaloadDetailsList,List singleloadDetailsList ){
     this.context = context;
     this.b = b;
     this.singleloadDetailsList = singleloadDetailsList;
     this.totaloadDetailsList = totaloadDetailsList;
    }
    @NonNull
    @Override
    public ViewReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_report_details,parent,false);
        TotalSimpleGrid =  v.findViewById(R.id.TotalSimpleGrid);
        TotalSimpleGrid.setAdapter(new TotalLoadDetailAdapter(context,totaloadDetailsList));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((MainActivity)context);
        cardViewLoadList = v.findViewById(R.id.cardViewLoadList);
        cardViewLoadList.setLayoutManager(layoutManager);

        cardViewLoadList.setAdapter(new SingleLoadDetailCardViewAdapter(context,singleloadDetailsList,-1));
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.vehicleNo.setText(b.getString("VehicleNo"));
    }


    @Override
    public int getItemCount() {
        return 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView vehicleNo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleNo = itemView.findViewById(R.id.vehicleNo);
        }
    }
}
