package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.telogix.telogixcaptain.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TotalLoadDetailAdapter extends BaseAdapter {
    List<HashMap> loadDetailsList = new ArrayList<HashMap>();
    Context context;
    LayoutInflater inflater;
    String key = null;

    public TotalLoadDetailAdapter(Context context, List loadDetailsList){
        this.context = context;
        this.loadDetailsList = loadDetailsList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return (loadDetailsList.get(0).size() * 2);
    }

    @Override
    public Object getItem(int i) {
        HashMap hashmap = new HashMap();
        String  key = String.valueOf(loadDetailsList.get(0).keySet().toArray()[i]);
        String value = String.valueOf(loadDetailsList.get(0).get(key));
        hashmap.put(key,value);
        return hashmap;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1 = inflater.inflate(R.layout.totalloadview_detail_item,null);
        TextView totalloadtextview = view1.findViewById(R.id.totalloadtextview);


            if(i%2 == 0){


                if(i == 0){
                     key = String.valueOf(loadDetailsList.get(0).keySet().toArray()[i]);
                    totalloadtextview.setText(key+":");
                }
                else{
                     key = String.valueOf(loadDetailsList.get(0).keySet().toArray()[i/2]);
                    totalloadtextview.setText(key+":");
                }


            }
            else{
                String value = String.valueOf(loadDetailsList.get(0).get(key));
                totalloadtextview.setText(value);
            }




        return view1;
    }
}
