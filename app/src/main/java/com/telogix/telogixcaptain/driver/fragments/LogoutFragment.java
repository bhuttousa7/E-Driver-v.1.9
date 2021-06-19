package com.telogix.telogixcaptain.driver.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.driver.Constant;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends Fragment implements response_interface {


    public LogoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_logout, container, false);
        TextView txtDriverName=v.findViewById(R.id.txtDriverName);
        Button logout=v.findViewById(R.id.logoutDriver);

        if(!getArguments().getString("DriverName","").equals(""))
        {
            txtDriverName.setText(getArguments().getString("DriverName",""));
            singleton.getsharedpreference_editor(getContext()).putString("token",getArguments().getString("DriverToken","") ).commit();

        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut(getArguments().getString("DriverToken",""));
            }
        });

    return v;
    }
    private void logOut(String token) {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpostDriver("api/DriverAttendance/SaveTimeOut?DeviceID="
                +new Constant(getContext()).getAndroid_id(), Request.Method.POST,
                data,token, this);

    }


    @Override
    public void onResponse(String response) throws JSONException {
        Paper.init(getContext());
        ArrayList<HashMap<String,String>> drivers=new ArrayList();
        drivers= Paper.book().read("drivers");
        if(drivers!=null) {
            if (getArguments().getString("Driver", "").equals("1")) {
               if(drivers.size()==1)
               {
                   NetworkConsume.getInstance().Logout(getContext());
                   singleton.getsharedpreference_editor(getContext()).clear().commit();
                   Paper.book().destroy();
                   Toast.makeText(getContext(),"Logged Out",Toast.LENGTH_SHORT).show();
               }
                else if(drivers.size()>1) {
               //    singleton.getsharedpreference_editor(getContext()).putString("token", drivers.get(1).get("DriverToken")).apply();
                   SharedPreferences.Editor editor=  getContext().getSharedPreferences("loggedin", Context.MODE_PRIVATE).edit();
                   editor.putString("token",drivers.get(1).get("DriverToken"));
                   editor.commit();
                   editor.apply();
                    drivers.remove(0);
                   Paper.book().write("drivers",drivers);
                   getActivity().recreate();
                }


            } else {
                if(drivers.size()==1)
                {
                    NetworkConsume.getInstance().Logout(getContext());
                    singleton.getsharedpreference_editor(getContext()).clear().commit();

                    Paper.book().destroy();
                    Toast.makeText(getContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                }
                if(drivers.size()>1) {
                    SharedPreferences.Editor editor=  getContext().getSharedPreferences("loggedin", Context.MODE_PRIVATE).edit();
                   editor.putString("token",drivers.get(0).get("DriverToken"));
                   editor.commit();
                   editor.apply();
                    //singleton.getsharedpreference_editor(getContext()).putString("token", drivers.get(0).get("DriverToken")).apply();
                    drivers.remove(1);
                    Paper.book().write("drivers",drivers);
//                    getActivity().onBackPressed();
                getActivity().recreate();
                }
            }

        }
       }

    @Override
    public void onError(VolleyError Error) {
        Toast.makeText(getContext(),"Unable to log off, try again",Toast.LENGTH_SHORT).show();
    }
}
