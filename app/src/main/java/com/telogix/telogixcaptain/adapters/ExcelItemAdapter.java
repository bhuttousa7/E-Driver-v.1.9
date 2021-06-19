package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;

import java.util.ArrayList;

public class ExcelItemAdapter extends RecyclerView.Adapter<ExcelItemAdapter.MyViewHolder> {

    public static ArrayList<com.telogix.telogixcaptain.Pojo.Data> Data;
    Context context;
    String vehicleID;

    com.telogix.telogixcaptain.Interfaces.onClickInterface onClickInterface;

    public ExcelItemAdapter(ArrayList<com.telogix.telogixcaptain.Pojo.Data> Data, Context context) {
        ExcelItemAdapter.Data = Data;
        this.context = context;

        // this.onClickInterface = onClickInterface;
    }



    @Override
    public ExcelItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.excelitemlist, viewGroup, false);

        return new ExcelItemAdapter.MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.PickupLocation.setText(Data.get(position).getPickupLocation());
        holder.TrailerCode.setText(Data.get(position).getTrailerCode());
        holder.Commodity.setText(Data.get(position).getCommodity());
        holder.DecantingTime.setText(Data.get(position).getDecantingTime());
        holder.DecantingSite.setText(Data.get(position).getDecantingSite());
        holder.CommodityLoad.setText(Data.get(position).getCommodityLoad());
        holder.checkBox.setChecked(Data.get(position).isIsselected());
        if(!Data.get(position).isChecked())
        {
            holder.checkBox.setVisibility(View.GONE);
        }
        else
        {
            holder.checkBox.setVisibility(View.VISIBLE);

        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data.get(position).setIsselected(!holder.checkBox.isSelected());
            }
        });
    }



    @Override
    public int getItemCount() {
        return Data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView PickupLocation, TrailerCode, Commodity, DecantingTime,DecantingSite,CommodityLoad;
        CardView cardView;
        CheckBox checkBox;
        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            PickupLocation = itemView.findViewById(R.id.txtPickupLocation);
            TrailerCode = itemView.findViewById(R.id.TrailerCode);
            Commodity = itemView.findViewById(R.id.Commodity);
            DecantingTime = itemView.findViewById(R.id.DecantingTime);
            DecantingSite = itemView.findViewById(R.id.DecantingSite);
            checkBox=itemView.findViewById(R.id.checkbox);
            CommodityLoad=itemView.findViewById(R.id.CommodityLoad);


        }
    }


}
