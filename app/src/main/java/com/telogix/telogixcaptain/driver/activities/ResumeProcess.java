package com.telogix.telogixcaptain.driver.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shuhart.stepview.StepView;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.OfflineData;
import com.telogix.telogixcaptain.activities.BaseActivity;
import com.telogix.telogixcaptain.activities.Notifications.NotificationsManager;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.fragments.DecantingListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.graphics.Color.BLACK;
import static com.telogix.telogixcaptain.Log.LogBook.getCurrentDate;
import static com.telogix.telogixcaptain.driver.activities.DriverMainActivity.responseObj;

public class ResumeProcess extends BaseActivity implements response_interface {

    private StepView stepView;
    private Button actionButton;
    private static final int REQUERTCODE_QR=10;
    private static final int REQUERTCODE_DECANTING=11;
    private static final int REQUERTCODE_ENDRIDE=12;
    String VehicleID="";
    String DecantingSiteID="";
    private String RouteAssignID="";
    private boolean reached;
    private String textendride="";
    private DatabaseReference dbRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_process);
        stepView=findViewById(R.id.step_view);
        actionButton=findViewById(R.id.btn_action);
        try {
            if (getIntent().hasExtra("VehicleID")) {
                VehicleID = getIntent().getStringExtra("VehicleID");
            }
            if (getIntent().hasExtra("DecantingSiteID")) {
                DecantingSiteID = getIntent().getStringExtra("DecantingSiteID");
            }
            if (getIntent().hasExtra("RouteAssignID")) {
                RouteAssignID = getIntent().getStringExtra("RouteAssignID");
            }
            if (getIntent().hasExtra("reached")) {
                reached = getIntent().getBooleanExtra("reached", false);
            }
            if (reached) {
                textendride = "END RIDE";
            } else {
                textendride = "STOP RIDE";
            }
        }
        catch (Exception ex)
        {

        }

        stepView.getState()
                .selectedTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .nextStepCircleColor(ContextCompat.getColor(this, R.color.gray)).nextTextColor(BLACK)
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(ContextCompat.getColor(this, R.color.colorAccent))
                .selectedCircleRadius(getResources().getDimensionPixelSize(R.dimen.dp14))
                .selectedStepNumberColor(ContextCompat.getColor(this, R.color.white))
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("SCAN QR CODE");
                    add("DECANTING LIST");
                    if(reached) {

                        add("Rate Service Champion");
                    }
                    if(reached)
                    {
                        add("END RIDE");}
                    else
                    {
                        add("STOP RIDE");
                    }
                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(4)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime)).doneTextColor(ContextCompat.getColor(this, R.color.gray))
                .stepLineWidth(getResources().getDimensionPixelSize(R.dimen.dp1))
                .nextStepLineColor(ContextCompat.getColor(this, R.color.gray))
                .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
                .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
                // other state methods are equal to the corresponding xml attributes
                .commit();
        if(stepView.getCurrentStep()==0)
        {
            actionButton.setText("SCAN CODE");
        }
        else if(stepView.getCurrentStep()==1)
        {
            actionButton.setText("SUBMIT DECANTING");
        }
        else if(stepView.getCurrentStep()==1)
        {
            if(reached) {
                actionButton.setText("RATE");
            }
        }
        else if(stepView.getCurrentStep()==1)
        {
            if(reached)
            {
                actionButton.setText("END RIDE");
            }
            else{

                actionButton.setText("STOP RIDE");
            }
        }
        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                Toast.makeText(ResumeProcess.this,"RIDE END",Toast.LENGTH_SHORT).show();
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stepView.getCurrentStep()==0)
                {
                    Intent i=new Intent(ResumeProcess.this, ActivityQRcode.class);
                    startActivityForResult(i,REQUERTCODE_QR);

                }
                else if(stepView.getCurrentStep()==1)

                {
                    Intent i=new Intent(ResumeProcess.this, DecantingListActivity.class);
                    i.putExtra("DecantingSiteID",DecantingSiteID);
                    i.putExtra("VehicleID",VehicleID);
                    i.putExtra("RouteAssignID",RouteAssignID);
                    startActivityForResult(i,REQUERTCODE_DECANTING);

                }
                else if(stepView.getCurrentStep()==2)
                {
                    if(reached)
                    {
                        showDialog();
                    }
                    else
                    {
                        StopRide();
                    }


                }
                else if(stepView.getCurrentStep()==3)
                {
                    if(reached)
                    {
                        EndRideRequest();
                    }
                    else
                    {
                        StopRide();
                    }


                }

            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_resume_process;
    }

    private void EndRideRequest() {
        HashMap data = new HashMap();
        String url="api/VehicleRideDrivers/EndJourney?DeviceID="+new Constant(this).getAndroid_id();
        Log.d("RequestUrl",url);
        if(Constants.internetConnected) {
            httpvolley.stringrequestpost(url, Request.Method.POST, data, this);
            NotificationsManager.sendNotification(this,"Ride Completed","\nRide Completed at:"+getCurrentDate(),""+responseObj.data.vehicleID);
            Toast.makeText(this, "Ride Completed", Toast.LENGTH_SHORT).show();
        }
        else
    {
        HashMap<String,HashMap> hashMapoffline=new HashMap<>();
        hashMapoffline.put("api/VehicleRideDrivers/EndJourney?DeviceID="+new Constant(this).getAndroid_id(),data);
        OfflineData.offlineData.add(hashMapoffline);
        NotificationsManager.sendNotification(this,"Ride Completed","\nRide Completed at:"+getCurrentDate(),""+responseObj.data.vehicleID);

        Toast.makeText(this, "Ride Completed", Toast.LENGTH_SHORT).show();
//                    NetworkConsume.getInstance().Logout(this);
//                    singleton.getsharedpreference_editor(this).clear().commit();
//                    // singleton.getsharedpreference_editor(getApplicationContext()).remove("userData").commit();
//                    Paper.book().delete(Constants.KEY_ROLE_ID);
        try {
            dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("liveVehicles").child(new Constant(this).getAndroid_id()).removeValue();
        } catch (Exception e) {
            Log.d("--ResumeProcEx", e.getLocalizedMessage());
        }
        Intent i=new Intent(this, RideUnavailable.class);
        startActivity(i);
        finish();
    }
    }
    private void StopRide() {
        try {



            if (!VehicleID.equals("")) {
                HashMap hashMap = new HashMap();
                hashMap.put("StatusID", "10");
                hashMap.put("Description","");
                hashMap.put("Longitude", "" + 0);
                hashMap.put("Latitude", "" + 0);
                hashMap.put("StopTypeID", "" + 1);
                hashMap.put("RouteAssignID", ""+responseObj.data.getRouteAssignID());
                if(Constants.internetConnected) {
                    NetworkConsume.getInstance().ShowProgress(this);
                    httpvolley.stringrequestpost("api/DriverLogs/SaveDriverLog", Request.Method.POST, hashMap, this);
                }
                else
                {
                    HashMap<String,HashMap> data=new HashMap<>();
                    data.put("api/DriverLogs/SaveDriverLog",hashMap);
                    OfflineData.offlineData.add(data);
                    NotificationsManager.sendNotification(this,"Ride Stopped.Vehicle "+responseObj.data.vehicleID,"\nRide Stopped at:"+getCurrentDate(),""+responseObj.data.vehicleID);
                    finish();
                    Intent i=new Intent(ResumeProcess.this, ResumeRideActivity.class);
                    i.putExtra("VehicleID",VehicleID);
                    i.putExtra("LoadDecantingSiteID",""+DecantingSiteID);
                    startActivity(i);
                }
                //  httpvolley.stringrequestpost("api/VehicleRides/UpdateStatus", Request.Method.PUT, hashMap, this);
            }
        }catch (Exception ex)
        {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUERTCODE_QR) {
            if (resultCode == RESULT_OK) {

                stepView.go(1,true);

                if(stepView.getCurrentStep()+1==1)
                {
                    actionButton.setText("SUBMIT DECANTING");
                }
                else if(stepView.getCurrentStep()+1==2)
                {
                    if(reached)
                    {
                        actionButton.setText("RATE");
                    }
                    else {
                        actionButton.setText("STOP RIDE");
                    }
                }
                if(reached)
                {
                    if(stepView.getCurrentStep()+1==3)
                    {
                        actionButton.setText("END RIDE");
                    }
                }
            } else
            {
                Toast.makeText(getApplicationContext(),"Invalid QR Code",Toast.LENGTH_SHORT).show();
            }
        }
        else  if(requestCode==REQUERTCODE_DECANTING) {
            if (resultCode == RESULT_OK) {
                stepView.go(2,true);

                if(stepView.getCurrentStep()+1==2)
                {
                    if(reached)
                    {
                        actionButton.setText("RATE");
                    }
                    else {
                        actionButton.setText("STOP RIDE");
                    }
                }
                if(stepView.getCurrentStep()+1==3)
                {
                    if(reached)
                    {
                        actionButton.setText("End Ride");
                    }
                }
            } else
            {

            }
        }
    }

    private void SaveDriverRating(boolean ServiceChampionSame,float ratings,String Reviews) {
        HashMap hashMap=new HashMap();
        hashMap.put("LoadDecantingSiteID",""+DecantingSiteID);
        hashMap.put("Rating",""+ratings);
        if(ServiceChampionSame) {
            hashMap.put("IsSameChampion", "1");
        }
        else
        {
            hashMap.put("IsSameChampion", "0");
        }
        hashMap.put("Reviews",""+Reviews);
        if(Constants.internetConnected) {
            httpvolley.stringrequestpost("api/LoadDecantingSite/SaveDriverRating", Request.Method.POST, hashMap, this);
        }
        else
        {
            HashMap<String,HashMap> data=new HashMap<>();
            data.put("api/LoadDecantingSite/SaveDriverRating",hashMap);
            OfflineData.offlineData.add(data);
        }
    }

    private void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.activity_rating, null);
        RatingBar ratingBar=layout.findViewById(R.id.ratingBar2);
        Switch switch1=layout.findViewById(R.id.switch1);
        EditText editText2=layout.findViewById(R.id.editText2);
        final boolean[] checked = {false};
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[0] =isChecked;            }
        });
        alert.setCancelable(false);
        alert.setView(layout);
        alert.setTitle("");
        alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                SaveDriverRating(checked[0],ratingBar.getRating(),editText2.getText().toString());
                //ResumeRide();
                actionButton.setText("End Ride");
                stepView.go(3,true);

            }
        });
        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
//        new AppRatingDialog.Builder()
//                .setPositiveButtonText("Submit")
//                .setNegativeButtonText("Cancel")
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
//                .setDefaultRating(0)
//                .setTitle("Rate Service Champion")
//                .setDescription("Please select some stars and give your feedback")
//                .setCommentInputEnabled(true)
//                .setStarColor(R.color.starColor)
//                .setNoteDescriptionTextColor(R.color.gray)
//                .setTitleTextColor(R.color.black)
//                .setDescriptionTextColor(R.color.white)
//                .setHint("Service champion was same? write here")
//                .setHintTextColor(R.color.gray)
//                .setCommentTextColor(R.color.white)
//                .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
//                .setCancelable(false)
//                .setCanceledOnTouchOutside(false)
//                .create(this)
//                // .setTargetFragment(this, TAG) // only if listener is implemented by fragment
//                .show();
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try{
            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.has("ResponseCode")) {
                if(jsonObject1.get("ResponseCode").equals(2101))
                {
                    NotificationsManager.sendNotification(this,"Ride Stopped.Vehicle "+responseObj.data.vehicleID,"Ride Stopped at: "+getCurrentDate(),""+responseObj.data.vehicleID);
                    finish();
                    Intent i=new Intent(ResumeProcess.this, ResumeRideActivity.class);
                    i.putExtra("VehicleID",VehicleID);
                    i.putExtra("LoadDecantingSiteID",""+DecantingSiteID);
                    startActivity(i);
                }
                else if (jsonObject1.get("ResponseCode").equals(2201)) {
               //     NotificationsManager.sendNotification(this,"Ride Completed"+responseObj.data.vehicleID,"Ride Completed at:"+getCurrentDate(),""+responseObj.data.vehicleID);

                    Toast.makeText(this, "Ride Completed", Toast.LENGTH_SHORT).show();
//                    NetworkConsume.getInstance().Logout(this);
//                    singleton.getsharedpreference_editor(this).clear().commit();
//                    // singleton.getsharedpreference_editor(getApplicationContext()).remove("userData").commit();
//                    Paper.book().delete(Constants.KEY_ROLE_ID);
                    try {
                        dbRef = FirebaseDatabase.getInstance().getReference();
                        dbRef.child("liveVehicles").child(new Constant(this).getAndroid_id()).removeValue();
                    } catch (Exception e) {
                        Log.d("UMAIR", e.getLocalizedMessage());
                    }
                    Intent i=new Intent(this, RideUnavailable.class);
                    startActivity(i);
                    finish();
                }
            }}catch (Exception ex)
        {
            NetworkConsume.getInstance().hideProgress();
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
