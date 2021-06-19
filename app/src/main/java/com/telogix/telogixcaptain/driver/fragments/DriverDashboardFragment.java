package com.telogix.telogixcaptain.driver.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Log.LogBook;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsManager;
import com.telogix.telogixcaptain.driver.activities.DriverMainActivity;
import com.telogix.telogixcaptain.driver.models.DriverVehicleStatusResponse;
import com.telogix.telogixcaptain.driver.utils.Drivers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.telogix.telogixcaptain.Log.LogBook.getCurrentDate;

public class DriverDashboardFragment extends Fragment implements Serializable, response_interface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView txtRoutename,txtvehicleNo,txtDateTime;
    ImageButton cross;

    DriverVehicleStatusResponse responseObj = null;
    private SignaturePad mSignaturePad;
    private TextView txtDriverName;
    private Gson gson;




    public DriverDashboardFragment() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverDashboardFragment newInstance(String param1, String param2) {

        DriverDashboardFragment fragment = new DriverDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    private void UploadDeviceID() {

        HashMap data = new HashMap();
         // data.put("DeviceID",new Constant(getContext()).getAndroid_id());
        data.put("RouteAssignID",""+ DriverMainActivity.responseObj.data.getRouteAssignID());
        data.put("Longitude",""+"0");
        data.put("Latitude",""+"0");
          String url="api/VehicleRides/StartJourney";
          Log.d("RequestUrl",url);
        httpvolley.stringrequestpost(url, Request.Method.POST, data, this);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_dashboard, container, false);
        ImageView imgStartRide=view.findViewById(R.id.imgStartRide);
         txtRoutename=view.findViewById(R.id.txtRouteName);
         txtvehicleNo=view.findViewById(R.id.txt_vehicleno);
         txtDateTime=view.findViewById(R.id.txtDatetime);
        txtDriverName=view.findViewById(R.id.txtDriverName);
        imgStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Drivers.getDriverCount(getContext())>1)
                {
                    ShowDriverDialog();
                }
                else {
                    UploadDeviceID();
                }
             }
        });

        Button btnStartRide = view.findViewById(R.id.btn_start_ride);
        btnStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
            }
        });


        return view;
    }
    private void ShowDriverDialog() {
        try {
            final ArrayList<HashMap<String, String>> driverslist = Drivers.getDrivers(getContext());
            final CharSequence[] charSequence = new CharSequence[2];
            if (Drivers.getDrivers(getContext()) != null) {


                charSequence[0] = driverslist.get(0).get("DriverName");
                charSequence[1] = driverslist.get(1).get("DriverName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select Driver")
                    //.setMessage("You can buy our products without registration too. Enjoy the shopping")
                    .setSingleChoiceItems(charSequence, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            singleton.getsharedpreference_editor(getContext()).putString("token",driverslist.get(which).get("Driver")).commit();
                            UploadDeviceID();
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false)

            ;
            builder.create().show();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject1=new JSONObject(response);
        Log.d("ResponseDashboard",response);
if(jsonObject1.has("ResponseCode")) {

//    Toast.makeText(getContext(),""+jsonObject1.get("ResponseCode"),Toast.LENGTH_LONG).show();
    if (jsonObject1.get("ResponseCode").equals(2201) || jsonObject1.getInt("ResponseCode")==2201) {



        Fragment fragment = null;
        try {
            fragment = DriverMapFragment.class.newInstance();
            if (fragment != null ) {
                responseObj=new DriverVehicleStatusResponse();
                responseObj = (DriverVehicleStatusResponse) getArguments().getSerializable("data");

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", responseObj);
                fragment.setArguments(bundle);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .commit();
            }

            new LogBook(getContext()).updateLogBook("Ride Started");
            NotificationsManager.sendNotification(getContext(),"Ride Started","\nRide Started at:"+getCurrentDate(),""+ DriverMainActivity.responseObj.data.vehicleID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    else{
        Toast.makeText(getContext(),"Cannot Start Ride",Toast.LENGTH_SHORT).show();
    }
}
else{
    Toast.makeText(getContext(),"No Response",Toast.LENGTH_SHORT).show();
}
    }

    @Override
    public void onError(VolleyError Error) {
        Toast.makeText(getContext(),"Invalid Response",Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().hideProgress();

    }
}
