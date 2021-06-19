package com.telogix.telogixcaptain.activities.Notifications;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.OfflineData;

import java.util.ArrayList;
import java.util.HashMap;

import static com.telogix.telogixcaptain.driver.fragments.DriverMapFragment.CurrentFuelStop;
import static com.telogix.telogixcaptain.driver.fragments.DriverMapFragment.DecantingList;

public class NotificationsManager  {
    public static ArrayList DecantingSiteIDList = new ArrayList<Integer>();

    public static void sendNotification(Context context, String title, String Description, String VehicleID)
    {
        HashMap data = new HashMap();
        data.put("Title",title);
        data.put("Description",Description);
        data.put("VehicleID",VehicleID);
        if(CurrentFuelStop != null){
            DecantingSiteIDList.add(CurrentFuelStop.getDecantingSiteID());
            String currentDecantingSiteID = DecantingSiteIDList.toString();
            currentDecantingSiteID = currentDecantingSiteID.replace("[", "").replace("]", "").replace(" ", "");
            data.put("DecantingSiteIDList",currentDecantingSiteID);
        }
       else{
           if(!DecantingList.isEmpty()){
            DecantingSiteIDList = new ArrayList();
            for(int i=0; i<DecantingList.size(); i++){

                Datum dl;
                dl = DecantingList.get(i);
                DecantingSiteIDList.add(dl.getDecantingSiteID());

                }
               String deccommaseparatedlist = DecantingSiteIDList.toString();
               deccommaseparatedlist = deccommaseparatedlist.replace("[", "").replace("]", "").replace(" ", "");
               data.put("DecantingSiteIDList",deccommaseparatedlist);
               Log.i("--dl", String.valueOf(DecantingSiteIDList));
               Log.i("--datanot",String.valueOf(data));


            }
        }
        if(Constants.internetConnected) {
            httpvolley.stringrequestpost("api/PushNotification/Create", Request.Method.POST, data, (response_interface) context);
        }
        else
        {
            HashMap<String,HashMap> hashMap=new HashMap<>();
            hashMap.put("api/PushNotification/Create",data);
            OfflineData.offlineData.add(hashMap);
        }
    }


}
