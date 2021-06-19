package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Interfaces.InspectionRemarks;
import com.telogix.telogixcaptain.Pojo.RTINSPECTION.Data;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class RTChecklistAdapter extends BaseAdapter {

    public static Data data;
    Context context;
    String vehicleID;
    public static int pos = -1;
    public static int REQUESTCODE = 1;
    InspectionRemarks inspectionRemarks;

    com.telogix.telogixcaptain.Interfaces.onClickInterface onClickInterface;

    public RTChecklistAdapter(Data data, Context context) {
        RTChecklistAdapter.data = data;
        this.context = context;


        // this.onClickInterface = onClickInterface;
    }

    @Override
    public int getCount() {
        return data.getCheckListDetails().size();
    }

    @Override
    public Object getItem(int position) {
        return data.getCheckListDetails().get(position).getInspectionID();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.preloadinspectdesign, null, false);
        TextView PickupLocation, TrailerCode, Commodity, DecantingTime, DecantingSite, CommodityLoad,txt_currentstatus;
        txt_currentstatus=itemView.findViewById(R.id.txt_currentstatus);
        txt_currentstatus.setVisibility(View.VISIBLE);
        if(data.getCheckListDetails().get(position).getIsChecked())
        {
            txt_currentstatus.setText("Approved");
            txt_currentstatus.setTextColor(context.getResources().getColor(R.color.dark_green));
        }
        else
        {
            txt_currentstatus.setText("Not Approved");
            txt_currentstatus.setTextColor(Color.RED);
        }
        TriStateToggleButton triStateButton = itemView.findViewById(R.id.tstb_1);
        triStateButton.setVisibility(View.GONE);


        Button btn_adddetail;
        PickupLocation = itemView.findViewById(R.id.txtPickupLocation);

        btn_adddetail = itemView.findViewById(R.id.btn_adddetail);
        btn_adddetail.setVisibility(View.GONE)
        ;
        TextView textmandatory = itemView.findViewById(R.id.txtmandatory);

        PickupLocation.setText(data.getCheckListDetails().get(position).getInspectionTitleUr());


        return itemView;

    }


}
