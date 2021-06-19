package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.Pojo.UserData;
import com.telogix.telogixcaptain.R;

import java.util.ArrayList;

import static com.telogix.telogixcaptain.activities.Fragments.UserListFragment.checkAll;

public class UserExcelAdapter extends RecyclerView.Adapter<UserExcelAdapter.MyViewHolder> {

   public static ArrayList<UserData> userDataList;
   public Context context;
   private int checkCount = 0;
   private int checkAllState;
   private ArrayList<CheckBox> positions = new ArrayList<CheckBox>();

    public UserExcelAdapter(ArrayList<UserData> Data, Context context){

         userDataList = Data;
         this.context = context;

    }



    @NonNull
    @Override
    public UserExcelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from((parent.getContext())).inflate(R.layout.exceluserlist,parent,false);
        return new UserExcelAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.email.setText(userDataList.get(position).getEmail());
        holder.userRole.setText(userDataList.get(position).getUserRoles());
        positions.add(holder.checkBox);
        checkAll.setChecked(true);
        checkAll.setEnabled(false);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    checkAllState = 1;
                    for(int i=0;i<UserExcelAdapter.userDataList.size();i++)
                    {    UserExcelAdapter.userDataList.get(i).setChecked(true);
                        positions.get(i).setChecked(userDataList.get(i).isChecked());
                    }


                }
                else{
                    if(checkAllState == -1){

                    }
                   else if(checkAllState == 1) {
                        for (int i = 0; i < UserExcelAdapter.userDataList.size(); i++)
                        {   UserExcelAdapter.userDataList.get(i).setChecked(false);
                            positions.get(i).setChecked(userDataList.get(i).isChecked());}
                    }
                }
            }
        });
//        holder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkAllState = -1;
//            }
//        });
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                userDataList.get(position).setChecked(b);
//                if(b == true){
//                    checkCount++;
//
//                    if(checkCount == userDataList.size()){
//                        checkAll.setChecked(true);
//                    }
//                }
//                else{
//                    checkCount--;
//                    checkAll.setChecked(false);
//
//
//                }
//            }
//        });

        if(userDataList.get(position).getUserRoles().toUpperCase().equals("RETAILER")){

            holder.typeRetailerLayout.setVisibility(View.GONE);
            holder.typeDsite.setText(userDataList.get(position).getDecantingSiteName());

        }
        else if(userDataList.get(position).getUserRoles().toUpperCase().equals("JOURNEYMANAGER")){

            holder.typeDSiteLayout.setVisibility(View.GONE);
            holder.typeRetailer.setText(userDataList.get(position).getHaulierName());

        }
        else if(userDataList.get(position).getUserRoles().toUpperCase().equals("DRIVER")){

            holder.typeRetailerLayout.setVisibility(View.GONE);
            holder.typeDSiteLayout.setVisibility(View.GONE);

        }

    }



    @Override
    public int getItemCount() {
        return userDataList.size();
    }



    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView email,userRole,typeDsite,typeRetailer;
        LinearLayout typeDSiteLayout,typeRetailerLayout;
       CheckBox checkBox;

        public MyViewHolder(View itemView){
            super(itemView);

            email = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userrole);
            typeDsite = itemView.findViewById(R.id.decantingSite);
            typeRetailer = itemView.findViewById(R.id.retailerName);
            typeDSiteLayout = itemView.findViewById(R.id.dSiteLayout);
            typeRetailerLayout = itemView.findViewById(R.id.retailerLayout);
            checkBox = itemView.findViewById(R.id.userCheckbox);




        }







    }
}
