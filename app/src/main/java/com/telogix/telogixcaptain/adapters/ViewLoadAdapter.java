package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.ViewLoad.LoadDecantingSite;

import java.util.ArrayList;

public class ViewLoadAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LoadDecantingSite> loadDecantingSiteslist;
    public ViewLoadAdapter(Context context,ArrayList<LoadDecantingSite> loadDecantingSitelist){
        this.context=context;
        this.loadDecantingSiteslist=loadDecantingSitelist;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return loadDecantingSiteslist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return loadDecantingSiteslist.get(0).getLoadID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v=layoutInflater.inflate(R.layout.layout_viewloaddecanting,null,false);
        try {
            TextView txtLoadID, txtDecantingSite, txtDecantingTime, txtCommodity, txtCompartmentNo, txtCommodityLoad;

            txtDecantingSite = v.findViewById(R.id.txtDecantingSite);
            txtDecantingTime = v.findViewById(R.id.txtDecantingTime);
            LinearLayout multicommodities=v.findViewById(R.id.multicommodities);


            txtDecantingSite.setText("" + loadDecantingSiteslist.get(position).getDecantingSite());
            txtDecantingTime.setText("" + loadDecantingSiteslist.get(position).getDecantingTime().split("T")[0]+" " + loadDecantingSiteslist.get(position).getDecantingTime().split("T")[1]);
            if (loadDecantingSiteslist.get(position).getLoadCommodities() != null) {
                for (int i=0;i<loadDecantingSiteslist.get(position).getLoadCommodities().size();i++) {
                    View multiCommoditiesView=layoutInflater.inflate(R.layout.multicommodities,null,false);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 5, 0, 0);
                    multiCommoditiesView.setLayoutParams(params);
                    txtCommodity = multiCommoditiesView.findViewById(R.id.txtCommodity);
                    txtCompartmentNo = multiCommoditiesView.findViewById(R.id.txtCompartmentNo);
                    txtCommodityLoad=multiCommoditiesView.findViewById(R.id.txtCommodityLoad);
                    txtCommodity.setText("" + loadDecantingSiteslist.get(position).getLoadCommodities().get(i).getCommodity());
                    txtCompartmentNo.setText("" + loadDecantingSiteslist.get(position).getLoadCommodities().get(i).getCompartmentNo());
                    txtCommodityLoad.setText("" + loadDecantingSiteslist.get(position).getLoadCommodities().get(i).getCommodityLoad());

                    multicommodities.addView(multiCommoditiesView);
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return v;
    }
}
