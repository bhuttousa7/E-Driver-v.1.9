package com.telogix.telogixcaptain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Interfaces.onClickInterface;
import com.telogix.telogixcaptain.Pojo.UserPojo;

import java.util.ArrayList;

public class PendingUsersAdapter extends RecyclerView.Adapter<PendingUsersAdapter.ViewHolder>  {
    private final ArrayList<UserPojo>  userList;
    onClickInterface selection;
    public PendingUsersAdapter(ArrayList<UserPojo> userList, onClickInterface selection){
        this.userList=userList;
        this.selection=selection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_userslist,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.UserName.setText("Name: "+userList.get(position).getUserName());
        viewHolder.UserRole.setText(userList.get(position).getRole());
        viewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selection.ItemClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView UserName,UserRole,Isapproved;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName=itemView.findViewById(R.id.UserName);
            UserRole=itemView.findViewById(R.id.UserRole);
            btnAction=itemView.findViewById(R.id.btnAction);
            btnAction.setText("Approve");



        }
    }
}
