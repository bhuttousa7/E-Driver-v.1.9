package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.DriverStops.DatumStops;

import java.util.ArrayList;

public class DriverStopsAdapter extends ArrayAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<DatumStops>  datumStops=new ArrayList<>();

    public DriverStopsAdapter(@NonNull Context context, int resource,  ArrayList<DatumStops>  datumStops) {
        super(context, resource,datumStops);
        this.datumStops=datumStops;
        this.context=context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return datumStops.get(position).getStopTypeTitle();
    }

    @Override
    public long getItemId(int position) {
        return datumStops.get(position).getStopTypeID();
    }

    @Override
    public int getCount() {
        return datumStops.size();
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=layoutInflater.inflate(R.layout.layoutstops,null,false);
        TextView  txtstops=v.findViewById(R.id.txtstops);
        txtstops.setText(""+datumStops.get(i).getStopTypeTitle());

        return v;
    }

    //    public DriverStopsAdapter(Context context, ArrayList<DatumStops> datumStops)
//    {
//        this.datumStops=datumStops;
//        this.context=context;
//        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//    @Override
//    public int getCount() {
//        return datumStops.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        View v=layoutInflater.inflate(R.layout.layoutstops,null,false);
//        TextView  txtstops=v.findViewById(R.id.txtstops);
//        txtstops.setText(datumStops.get(i).getStopTypeTitle());
//
//        return v;
//    }

//    @Override
//    public long getItemId(int i) {
//        return datumStops.get(i).getStopTypeID();
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        return null;
//    }
}
