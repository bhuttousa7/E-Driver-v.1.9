package com.telogix.telogixcaptain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Notifications.NotificationPojo;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    ArrayList<NotificationPojo> notificationList;
    public NotificationsAdapter( ArrayList<NotificationPojo> notificationList)
    {
        this.notificationList=notificationList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_listdesign,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.Title.setText(notificationList.get(position).getTitle());
            holder.Description.setText(notificationList.get(position).getDescription());
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Title , CreatedDate,Description;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title=itemView.findViewById(R.id.Title);
            CreatedDate=itemView.findViewById(R.id.CreatedDate);
            Description=itemView.findViewById(R.id.Description);



        }
    }
}
