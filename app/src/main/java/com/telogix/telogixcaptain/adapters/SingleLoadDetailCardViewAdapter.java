package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SingleLoadDetailCardViewAdapter extends RecyclerView.Adapter<SingleLoadDetailCardViewAdapter.MyViewHolder> {

    List<HashMap> singleLoadDetailList = new ArrayList<HashMap>();
    Context context;
    int responseCode;
    // GridView singleload_detail_gridview;


    public SingleLoadDetailCardViewAdapter(Context context, List singleLoadDetailList, int responseCode) {

        this.context = context;
        this.singleLoadDetailList = singleLoadDetailList;
        this.responseCode = responseCode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleload_detail_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap singleLoadHMapList = (HashMap) singleLoadDetailList.get(position);
        Iterator hmIterator = singleLoadHMapList.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry key = (Map.Entry) hmIterator.next();
            if (responseCode == 1) {

                if (key.getKey().toString().equals("Load Time")) {
                    holder.loadHeading.setText("Load # " + key.getValue());
                }

            }
            if (responseCode == 3) {

                if (key.getKey().toString().equals("Commodity Name")) {
                    holder.loadHeading.setText(key.getValue().toString());
                }

            }


        }

        holder.cardViewRecyclerView.setAdapter(new SingleLoadDetailAdapter(context, singleLoadDetailList.get(position)));

    }


    @Override
    public int getItemCount() {
        return singleLoadDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView cardViewRecyclerView;
        TextView loadHeading;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            loadHeading = itemView.findViewById(R.id.loadHeading);
            cardViewRecyclerView = itemView.findViewById(R.id.cardViewRecyclerView);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            cardViewRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
