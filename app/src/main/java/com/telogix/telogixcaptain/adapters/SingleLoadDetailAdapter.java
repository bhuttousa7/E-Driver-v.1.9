package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;

import java.util.HashMap;

public class SingleLoadDetailAdapter extends RecyclerView.Adapter<SingleLoadDetailAdapter.MyViewHolder> {
    Context context;
    HashMap singleLoadDetailList = new HashMap();
    LayoutInflater layoutInflater;


    public SingleLoadDetailAdapter(Context context, HashMap singleLoadDetailList){
        this.context = context;
        this.singleLoadDetailList = singleLoadDetailList;
        layoutInflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.from(parent.getContext()).inflate(R.layout.singleload_view_detail_item,parent,false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       // holder.setIsRecyclable(false);
        holder.singleload_detail_gridview.setAdapter(new SingleValueLoadDetailAdapter(context,singleLoadDetailList));

    }

    @Override
    public int getItemCount() {
        return singleLoadDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        GridView singleload_detail_gridview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            singleload_detail_gridview = itemView.findViewById(R.id.singleload_detail_gridview);

        }
    }
}
