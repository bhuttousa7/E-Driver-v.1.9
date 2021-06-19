package com.telogix.telogixcaptain.driver.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.DriverLog.DriverLogPojo;

import java.util.ArrayList;

public class logAdapter extends RecyclerView.Adapter<logAdapter.MyViewHolder> {

    private final ArrayList<DriverLogPojo> Log;
    private String logTitle;

    public logAdapter(ArrayList<DriverLogPojo> Log) {
        this.Log = Log;
    }

    @Override
    public logAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new logAdapter.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loglistdesign, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(logAdapter.MyViewHolder myViewHolder, int i) {
        try {
            if (Log.get(i).getDescription() != "" && Log.get(i).getDescription()!="null") {
                 logTitle = Log.get(i).getDescription();
            }
            else
            {
                logTitle = "Manual Stop";
            }
            String logTimeDetails = "";
            if (Log.get(i).getStartDate() != "null") {
                logTimeDetails = "" + Log.get(i).getStartDate();
            } else {
                logTimeDetails = "" + Log.get(i).getResumeDate();
            }

            String logDate = logTimeDetails.split("T")[0];
            String logTime = logTimeDetails.split("T")[1];

            myViewHolder.txtlogtitle.setText(logTitle);
            myViewHolder.txtlogtime.setText("DATE: " + logDate);
            myViewHolder.txtlogDate.setText("TIME: " + logTime);

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void ChangeFragment(Fragment fragment, View v) {


        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }


    @Override
    public int getItemCount() {
        return Log.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtlogtitle,txtlogtime,txtlogDate;


        public MyViewHolder(View itemView) {
            super(itemView);

            txtlogtitle = itemView.findViewById(R.id.txtlogtitle);
            txtlogtime = itemView.findViewById(R.id.txtlogtime);
            txtlogDate=itemView.findViewById(R.id.txtlogDate);


        }
    }

}