package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.telogix.telogixcaptain.R;

public class VehiclesAdapter extends BaseAdapter
{
    Context context;
    String[] vehicles;
    String[] vehicleType;
    String[] haulier;
    String[] serialNo;
    LayoutInflater layoutInflater;

    public VehiclesAdapter(Context context, String[] vehicles, String[] vehicleType, String[] haulier, String[] serialNo) {
        this.context = context;
        this.vehicles = vehicles;
        this.vehicleType = vehicleType;
        this.haulier = haulier;
        this.serialNo = serialNo;
        layoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return vehicles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = layoutInflater.inflate(R.layout.listview_vehicle_item, null);
        TextView txtVehicleName = convertView.findViewById(R.id.txtVname);
        TextView txtVehicleType = convertView.findViewById(R.id.txtVtype);
        TextView txtHaulier = convertView.findViewById(R.id.txtHaulierName);
        TextView txtNumber = convertView.findViewById(R.id.txtVnumber);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#e0e0e0"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#b9f6ca"));
        }

        txtVehicleName.setText(vehicles[position]);
        txtVehicleType.setText(vehicleType[position]);
        txtHaulier.setText(haulier[position]);
        txtNumber.setText(serialNo[position]);

        return convertView;
    }
}
