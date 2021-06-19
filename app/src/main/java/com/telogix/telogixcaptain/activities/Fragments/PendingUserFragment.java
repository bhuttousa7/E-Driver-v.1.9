package com.telogix.telogixcaptain.activities.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.onClickInterface;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.UserPojo;
import com.telogix.telogixcaptain.adapters.PendingUsersAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingUserFragment extends Fragment implements response_interface, onClickInterface {


    private RecyclerView pendingusers_recycler;
    private ArrayList<UserPojo>  userList=new ArrayList<>();
    private PendingUsersAdapter pendingUserAdapter;

    public PendingUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pending_user, container, false);
         pendingusers_recycler = v.findViewById(R.id.pendingusers_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        pendingusers_recycler.setLayoutManager(layoutManager);
        pendingUserAdapter=new PendingUsersAdapter(userList,this);
        pendingusers_recycler.setAdapter(pendingUserAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPendingUsers();
    }

    private void getPendingUsers() {

        HashMap<String, String> data = new HashMap<>();
        httpvolley.stringrequestpost("api/Account/GetPendingUsers", Request.Method.GET, data, this);
        NetworkConsume.getInstance().ShowProgress(getContext());

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.PENDINGUSERS))) {
                    if (jsonObject.has("Data")) {
                        JSONArray data = jsonObject.getJSONArray("Data");
                        userList=new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {

                            UserPojo userPojo=new UserPojo();
                            userPojo.setUserName(""+data.getJSONObject(i).get("UserName"));
                            userPojo.setRole(""+data.getJSONObject(i).get("Role"));
                            userPojo.setID(data.getJSONObject(i).getString("id"));
                            userPojo.setApproved(data.getJSONObject(i).getBoolean("IsApproved"));
                            userList.add(userPojo);

                        }

                        pendingUserAdapter=new PendingUsersAdapter(userList,this);
                        pendingusers_recycler.setAdapter(pendingUserAdapter);
                       }
                }
                else if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.USERAPPROVED))) {
                    Toast.makeText(getContext(),"User Approved",Toast.LENGTH_SHORT).show();
                    getPendingUsers();
                }

                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }






    @Override
    public void ItemClick(int position) {
        HashMap<String, String> data = new HashMap<>();
        httpvolley.stringrequestpost("/api/Account/UpdateUserStatus?id="+userList.get(position).getID(), Request.Method.POST, data, this);

    }
}
