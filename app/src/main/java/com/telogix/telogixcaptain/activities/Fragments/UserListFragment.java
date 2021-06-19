package com.telogix.telogixcaptain.activities.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.UserData;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.adapters.UserExcelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListFragment extends Fragment implements response_interface {
    ArrayList<UserData>  userDataArrayList = new ArrayList<UserData>();
    RecyclerView userListRecyclerView;
    public static CheckBox checkAll;
    Button btnSubmitUsrList;
    private ArrayList<UserData> userData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list,container,false);
           userListRecyclerView = view.findViewById(R.id.userListRecyclerView);
           RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
           userListRecyclerView.setLayoutManager(layoutManager);
           checkAll = view.findViewById(R.id.checkall);
           btnSubmitUsrList = view.findViewById(R.id.btnSubmitUsrList);

           btnSubmitUsrList.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(UserExcelAdapter.userDataList.size()>0){
                       userData = UserExcelAdapter.userDataList;
                       ArrayList<UserData> selectedUsers = new ArrayList<UserData>();
                    if(checkAll.isChecked()){
                        selectedUsers.addAll(userData);
                    }
                    else{
                        for(int i = 0; i<userData.size();i++){
                            if(userData.get(i).isChecked()){
                                selectedUsers.add(userData.get(i));
                            }

                        }

                    }
                       HashMap hashMap = new HashMap();
                    for(int i=0;i<selectedUsers.size();i++){
                        if(i == 0){
                            hashMap.put("ExcelID",String.valueOf(selectedUsers.get(i).getExcelID()));
                            hashMap.put("Details[" + i + "].ID",String.valueOf(selectedUsers.get(i).getID()));
                        }

                        else{
                            hashMap.put("Details[" + i + "].ID",String.valueOf(selectedUsers.get(i).getID()));
                        }

                    }
                       NetworkConsume.getInstance().ShowProgress(getContext());
                       Log.i("--userHashDetails",hashMap.toString());
                       httpvolley.stringrequestpost("api/Account/RegisterBulkUser", Request.Method.POST,hashMap,UserListFragment.this);
                   }
               }
           });
        if(getArguments().getString("userExcelData","") != ""){

            try {
                JSONObject jsonObject = new JSONObject(getArguments().
                        getString("userExcelData",""));
                if(jsonObject.has("Data")){

                    JSONArray data = jsonObject.getJSONArray("Data");

                    for(int i=0;i<data.length();i++){
                        UserData userData = new UserData();
                        userData.setExcelID(data.getJSONObject(i).getInt("ExcelID"));
                        userData.setEmail(data.getJSONObject(i).getString("Email"));
                        userData.setUserName(data.getJSONObject(i).getString("UserName"));
                        userData.setPassword(data.getJSONObject(i).getString("Password"));
                        userData.setUserRoles(data.getJSONObject(i).getString("UserRoles"));
                        if(data.getJSONObject(i).getString("UserRoles").toUpperCase().equals("RETAILER")){
                            userData.setDecantingSiteName(data.getJSONObject(i).getString("DecantingSiteName"));

                        }
                        else if(data.getJSONObject(i).getString("UserRoles").toUpperCase().equals("JOURNEYMANAGER")){
                            userData.setHaulierName(data.getJSONObject(i).getString("HaulierName"));
                        }


                        userData.setChecked(data.getJSONObject(i).getBoolean("isChecked"));


                        userDataArrayList.add(userData);

                    }
                    userListRecyclerView.setAdapter(new UserExcelAdapter(userDataArrayList,getContext()));

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return view;
    }

    @Override
    public void onResponse(String response) throws JSONException {
            NetworkConsume.getInstance().hideProgress();
            JSONObject jsonObject = new JSONObject(response);
        Toast.makeText(getContext(), jsonObject.getString("Message"), Toast.LENGTH_LONG).show();
        if(jsonObject.getBoolean("Status") == true){
            getActivity().onBackPressed();
        }


    }

    @Override
    public void onError(VolleyError Error) {

    }
}
