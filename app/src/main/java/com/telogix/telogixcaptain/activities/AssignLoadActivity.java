package com.telogix.telogixcaptain.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.CommoditiesPojo.CommoditiesPojo;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.DecantingSitesPojo;
import com.telogix.telogixcaptain.Pojo.pickupPojo.Datum;
import com.telogix.telogixcaptain.Pojo.pickupPojo.PickupLocationPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.MultiSelectionSpinner;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;
import com.telogix.telogixcaptain.adapters.SimpleArrayListAdapterCommodities;
import com.telogix.telogixcaptain.adapters.SimpleArrayListAdapterDecanting;
import com.telogix.telogixcaptain.adapters.SimpleArrayListAdapterPickup;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignLoadActivity extends BaseActivity implements response_interface {
    boolean checkfields = true;
    EditText vehicleName, vehicleType, CompartmentNo1, LoadWeight1;
    TextView etLoadTime, etDecantingTime;
    Button btnSubmit, btnAddDecating;
    int mYear;
    int mMonth;
    int mDay;
    String date_time = "";
    int mHour;
    int mMinute;
    int count =0;
    boolean sd = false,sc = false;
    int multicount = 0,spinnerdecant =0,spinnercommodity = 0;
    SearchableSpinner searchableSpinnerPickup1, searchableSpinnerDecenting1, searchableSpinnerCommodity1;
    Context context;
    String dateStr, decentingTime, loadtime;
    private SimpleArrayListAdapterPickup simpleArrayListAdapterPickup;
    private SimpleArrayListAdapterDecanting mSimpleArrayListAdapterDecanting;
    private SimpleArrayListAdapterCommodities mSimpleArrayListAdapterCommodities;
    private int commodity = 0, decanting = 0, pickup = 0;
    String dateTimeFormat = "MM/dd/yyyy mm:hh";
    SimpleDateFormat spf = new SimpleDateFormat(dateTimeFormat);
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    private boolean isDecenting = false, multidecant , multicommodity, compno, loadw;
    LinearLayout linearLayout, layout_multidecanting,layout_multidecanting_2;
    MultiSelectionSpinner mySpinner;
    ArrayList<Datum> pickupArrayList = new ArrayList<>();
    ArrayList<com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> decantingArrayList = new ArrayList<>();
    ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> commodityArrayList = new ArrayList<>();
    HashMap<String, com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> MulitDecantings = new HashMap<>();
    HashMap<String, com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> MulitDecanting_arranged = new HashMap<>();
    private LocalDateTime localDate, loadtimeDate;
    HashMap<String,String> olddecantingSites = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_load);
        context = this;
        init();
        //   mySpinner.
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Are you sure? ").setMessage("Are you sure you want to go back? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AssignLoadActivity.super.onBackPressed();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_assign_load;
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Assign Load");
        setSupportActionBar(toolbar);
        // Views binding
        vehicleName = findViewById(R.id.etVname);
        vehicleType = findViewById(R.id.etVtype);
        searchableSpinnerPickup1 = findViewById(R.id.SearchableSpinnerPickup);

        searchableSpinnerDecenting1 = findViewById(R.id.SearchableSpinnerDecenting);
        searchableSpinnerCommodity1 = findViewById(R.id.SearchableSpinnerCommodity);
        if (CustomData.getInstance() != null) {
            vehicleName.setText("Vehicle No: " + CustomData.getInstance().vehicleDetails.getVehicleNo());
            vehicleType.setText("Vehicle Type: " + CustomData.getInstance().vehicleDetails.getVehicleType());
        }
        etLoadTime = findViewById(R.id.etLoadTime);
        etDecantingTime = findViewById(R.id.etDecantingTime);
        layout_multidecanting = findViewById(R.id.layout_multidecanting);
        layout_multidecanting_2 = findViewById(R.id.layout_multidecanting);
        CompartmentNo1 = findViewById(R.id.edtCompartno1);
        LoadWeight1 = findViewById(R.id.edtloadWeight1);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAddDecating = findViewById(R.id.btnAddDecating);

        btnAddDecating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add_decanting(v);
            }
        });
        getPickupLocations();
        linearLayout = findViewById(R.id.mainLL);
        simpleArrayListAdapterPickup = new SimpleArrayListAdapterPickup(this, pickupArrayList);
        searchableSpinnerPickup1.setAdapter(simpleArrayListAdapterPickup);

        mSimpleArrayListAdapterDecanting = new SimpleArrayListAdapterDecanting(this, decantingArrayList);
        searchableSpinnerDecenting1.setAdapter(mSimpleArrayListAdapterDecanting);
        mSimpleArrayListAdapterCommodities = new SimpleArrayListAdapterCommodities(this, commodityArrayList);
        searchableSpinnerCommodity1.setAdapter(mSimpleArrayListAdapterCommodities);

        /// multi selection


        //   mySpinner = findViewById(R.id.spn_items);


        ///

        // onClick events
        toolbar.setNavigationOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> {
            //    Object selectedCommodities = searchableSpinnerCommodity1.getSelectedItem();

            // code by talha


            //
            if (pickup == 0 || decanting == 0 || commodity == 0  || CompartmentNo1.getText().toString().equals("") || CompartmentNo1.getText().toString().equals(null)
                    || LoadWeight1.getText().toString().equals(null) || LoadWeight1.getText().toString().equals("")) {
                Toast.makeText(context, "Please fill out the form!", Toast.LENGTH_SHORT).show();
            } else if (etLoadTime.getText().toString().equals("") || etDecantingTime.getText().toString().equals("")) {
                Toast.makeText(context, "please select the decentingTime!", Toast.LENGTH_SHORT).show();
            }
            else {
                if(multicount >0){
                    if( multidecant == false|| multicommodity == false || checkMultiLoads() == false){
                        Toast.makeText(context, "Please fill out the form!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        alertDialog();
                    }
                }
                else{
                    alertDialog();
                }

            }
        });

        searchableSpinnerPickup1.setOnItemSelectedListener(mOnItemSelectedListener);
        searchableSpinnerDecenting1.setOnItemSelectedListener(mOnItemSelectedListenerDecenting);
        searchableSpinnerCommodity1.setOnItemSelectedListener(mOnItemSelectedListenerCommodity);
        etLoadTime.setOnClickListener(v -> {
            isDecenting = false;
            datePicker(etDecantingTime, etLoadTime);
        });
        etDecantingTime.setOnClickListener(v -> {
            isDecenting = true;
            datePicker(etDecantingTime, etLoadTime);
        });
    }

    private void add_decanting(View v) {

        View view = getLayoutInflater().inflate(R.layout.assign_multiload, null);

        //
        SearchableSpinner searchablespinnerDecanting = view.findViewById(R.id.SearchableSpinnerDecenting);
        SearchableSpinner searchableSpinnerCommodities = view.findViewById(R.id.SearchableSpinnerCommodity);
        TextView eDecantingTime = view.findViewById(R.id.eDecantingTime);
        //  MultiSelectionSpinner SearchableSpinnerCommodity = view.findViewById(R.id.spn_items);
        ImageButton cross = view.findViewById(R.id.cross);
        SimpleArrayListAdapterDecanting mSimpleArrayListAdapterMultiDecanting = new SimpleArrayListAdapterDecanting(AssignLoadActivity.this, decantingArrayList);
        searchablespinnerDecanting.setAdapter(mSimpleArrayListAdapterMultiDecanting);
        searchableSpinnerCommodities.setAdapter(new SimpleArrayListAdapterCommodities(AssignLoadActivity.this, commodityArrayList));

//         searchablespinnerDecanting.setSelectedItem(decanting);
//         searchableSpinnerCommodities.setSelectedItem(commodity);



        eDecantingTime.setText(etDecantingTime.getText().toString());

        searchablespinnerDecanting.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d = mSimpleArrayListAdapterMultiDecanting.getselectedItem();
                int pos = (Integer) searchablespinnerDecanting.getTag();
                MulitDecantings.put("" + pos, d);
                multidecant = true;
                if(sd == false){
                spinnerdecant++;
                sd = true;
                }
                reArrangedDecanting();
                // Toast.makeText(AssignLoadActivity.this,"pos: "+pos,Toast.LENGTH_LONG).show();
                //  decanting=d.getDecantingSiteID();
            }

            @Override
            public void onNothingSelected() {
               // multidecant = false;
            }
        });
//        if (searchablespinnerDecanting == null || searchablespinnerDecanting.getSelectedItem() == null) {
//            multidecant = false;
//        } else {
//            multidecant = true;
//        }
        eDecantingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecenting = true;
                datePicker(eDecantingTime, etLoadTime);
            }
        });
        searchableSpinnerCommodities.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                commodityArrayList.get(position - 1).getCommodityID();
                multicommodity = true;
                if(sc == false){
                    spinnercommodity++;
                    sc = true;
                }

            }

            @Override
            public void onNothingSelected() {
               // multicommodity = false;
            }
        });
        // SearchableSpinnerCommodity.setItems(commodityArrayList);
//        if (searchableSpinnerCommodities == null || searchableSpinnerCommodities.getSelectedItem() == null) {
//            multicommodity = false;
//        } else {
//            multicommodity = true;
//        }
        eDecantingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecenting = true;
                datePicker(eDecantingTime, etLoadTime);
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                    Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();

                removeView(position);
            }
        });


        layout_multidecanting.addView(view);
        int position = layout_multidecanting.indexOfChild(view);
        Log.d("--positionlayo",String.valueOf(position));
        view.setTag(position);
        com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d = new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum();

        MulitDecantings.put("" + position, d);
        reArrangedDecanting();

        view.findViewById(R.id.SearchableSpinnerCommodity).setTag(position);
        view.findViewById(R.id.SearchableSpinnerDecenting).setTag(position);
        //  view.findViewById(R.id.spn_items).setTag(position);
        view.findViewById(R.id.eDecantingTime).setTag(position);
        view.findViewById(R.id.cross).setTag(position);
        multicount++;
        if(multicount>0){
            multicommodity = true;
            multidecant = true;
        }
        else{
            multicommodity = false;
            multidecant = false;
        }
//        int count =0;
//        MulitDecanting_arranged.clear();
//        Log.d("--Multidearranged",MulitDecanting_arranged.toString());
//        for(int i=0 ; i<=MulitDecantings.size();i++){
//
//            if(MulitDecantings.get(""+i)!= null){
//                MulitDecanting_arranged.put(""+count,MulitDecantings.get(""+i));
//                count++;
//            }
//        }

    }

    private void removeView(int position) {
        Log.d("--pos",String.valueOf(position));

        Log.d("--layoutmultiremove",String.valueOf(layout_multidecanting.getChildCount()));
//        SearchableSpinner multidecanting = layout_multidecanting.getChildAt(position).findViewById(R.id.SearchableSpinnerDecenting);
//        SearchableSpinner SearchableSpinnerCommodity = layout_multidecanting.getChildAt(position).findViewById(R.id.SearchableSpinnerCommodity);
//        multidecanting.hideEdit();
//        SearchableSpinnerCommodity.hideEdit();
        MulitDecantings.remove("" + position);
        layout_multidecanting.removeView(layout_multidecanting.findViewWithTag(position));
        Log.d("--positionlayo",String.valueOf(layout_multidecanting.findViewWithTag(position)));
        if(sd == true){
        spinnerdecant--;
        sd = false;
        }
        if(sc == true){
            spinnercommodity--;
            sc = false;
        }

        multicount--;
        if(multicount>0){
            multicommodity = true;
            multidecant = true;
        }
        else{
            multicommodity = false;
            multidecant = false;
        }

        reArrangedDecanting();

    }

    private void reArrangedDecanting(){

       count=0;
        MulitDecanting_arranged.clear();
        Log.d("--Multidearranged",MulitDecanting_arranged.toString());

  //      for(int i=0 ; i<=MulitDecantings.size();i++){
            Iterator it = MulitDecantings.entrySet().iterator();
            if(!it.hasNext()){
                MulitDecanting_arranged.clear();
            }
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(MulitDecantings.get(pair.getKey())!= null){
                    MulitDecanting_arranged.put(""+count, (com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum) pair.getValue());
                    count++;
                }
               // it.remove(); // avoids a ConcurrentModificationException
            }


     //   }
    }

    private void alertDialog() {
        try {

            new AlertDialog.Builder(this).setTitle("Please Confirm").setMessage("Are you sure to assign load to vehicle no: " + " " + CustomData.getInstance().vehicleDetails.getVehicleNo() + "\n")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AssignLoadVehicle();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
//            new EZDialog.Builder(context)
//
//                    .setTitle("Please Confirm!")
//                    .setMessage("Are you sure to assign load to vehicle no: " + " " + CustomData.getInstance().vehicleDetails.getVehicleNo() + "\n"
////                        +"Pickup Location:"+pickupArrayList.get(pickup).getPickupLocation()+
////                "\n"+"Destination: "+decantingArrayList.get(decanting).getDecantingSite()+"\n"
////                                +"Load Time:"+etLoadTime.getText().toString()+"\n"+"Decanting Time: "+etDecantingTime.getText().toString()+"\n"
////                                +"Commodity:"+commodityArrayList.get(0).getCommodityName()
//                    )
//                    .setPositiveBtnText("Yes")
//                    .setNegativeBtnText("No")
//                    .setCancelableOnTouchOutside(false)
//                    .OnPositiveClicked(() -> )
//                    .OnNegativeClicked(() -> {
//                    })
//                    .build();
        } catch (Exception ex) {

        }

    }

    private final OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
//            Toast.makeText(context, "Item on position " + position + " : " + simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLocation() + " Selected", Toast.LENGTH_SHORT).show();
            Datum d = simpleArrayListAdapterPickup.getselectedItem();
            pickup = d.getPickupLocationID();

            // pickup = simpleArrayListAdapterPickup.getmBackupStrings().get(position-1).getPickupLocationID();
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };
    private final OnItemSelectedListener mOnItemSelectedListenerDecenting = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
//            Toast.makeText(context, "Item on position " + position + " : " + mSimpleArrayListAdapterDecanting.getItem(position-1).getDecantingSite() + " Selected", Toast.LENGTH_SHORT).show();

            com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d = mSimpleArrayListAdapterDecanting.getselectedItem();
            decanting = d.getDecantingSiteID();
            // decanting = mSimpleArrayListAdapterDecanting.getmBackupStrings().get(position-1).getDecantingSiteID();
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };
    private final OnItemSelectedListener mOnItemSelectedListenerCommodity = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
            //
            commodity = mSimpleArrayListAdapterCommodities.getselectedItem().getCommodityID();
//            commodity = mSimpleArrayListAdapterCommodities.getmBackupStrings().get(position-1).getCommodityID();
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };

    private void getPickupLocations() {
        NetworkConsume.getInstance().ShowProgress(context);
        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token", ""));
        NetworkConsume.getInstance().getAuthAPI().getPickupLocationLov().enqueue(new Callback<PickupLocationPojo>() {
            @Override
            public void onResponse(Call<PickupLocationPojo> call, Response<PickupLocationPojo> response) {

                NetworkConsume.getInstance().hideProgress();
                if (response.isSuccessful()) {
                    pickupArrayList.addAll(response.body().getData());
                    simpleArrayListAdapterPickup = new SimpleArrayListAdapterPickup(AssignLoadActivity.this, pickupArrayList);
                    searchableSpinnerPickup1.setAdapter(simpleArrayListAdapterPickup);
                    getDecantings();
                }
            }

            @Override
            public void onFailure(Call<PickupLocationPojo> call, Throwable t) {
                NetworkConsume.getInstance().hideProgress();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDecantings() {

        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token", ""));
        NetworkConsume.getInstance().getAuthAPI().getDecantingSites().enqueue(new Callback<DecantingSitesPojo>() {
            @Override
            public void onResponse(Call<DecantingSitesPojo> call, Response<DecantingSitesPojo> response) {
                if (response.isSuccessful()) {
                    decantingArrayList.addAll(response.body().getData());
                    mSimpleArrayListAdapterDecanting.notifyDataSetChanged();
                    getCommoidites();
                }
            }

            @Override
            public void onFailure(Call<DecantingSitesPojo> call, Throwable t) {
                NetworkConsume.getInstance().hideProgress();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCommoidites() {

        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token", ""));
        NetworkConsume.getInstance().getAuthAPI().getCommodityLov().enqueue(new Callback<CommoditiesPojo>() {
            @Override
            public void onResponse(Call<CommoditiesPojo> call, Response<CommoditiesPojo> response) {
                NetworkConsume.getInstance().hideProgress();
                if (response.isSuccessful()) {
                    commodityArrayList.addAll(response.body().getData());
                    // mySpinner.setItems(commodityArrayList);

                    mSimpleArrayListAdapterCommodities.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CommoditiesPojo> call, Throwable t) {
                NetworkConsume.getInstance().hideProgress();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void datePicker(final TextView etDecantingTime, TextView etLoadTime) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();

        if (isDecenting) {
            if (etDecantingTime.getHint().toString().equals("Decanting Time") && etDecantingTime.getText().toString().equals("")) {

                if (etLoadTime.getText().toString() != "") {
                    String input = etLoadTime.getText().toString();
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
                    loadtimeDate = LocalDateTime.parse(input, formatter);
                    loadtimeDate.toDateTime().getMillis();
                    mYear = loadtimeDate.getYear();
                    mMonth = loadtimeDate.getMonthOfYear();
                    mDay = loadtimeDate.getDayOfMonth();
                } else {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }

            } else {
                String input = etDecantingTime.getText().toString();
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
                localDate = LocalDateTime.parse(input, formatter);
                Log.d("localeMonth", "" + localDate.getDayOfMonth());
                mYear = localDate.getYear();
                mMonth = localDate.getMonthOfYear();
                mDay = localDate.getDayOfMonth();
                mHour = localDate.getHourOfDay();
                mMinute = localDate.getMinuteOfHour();
            }

        } else {
            if (etLoadTime.getHint().toString().equals("Load Time") && etLoadTime.getText().toString().equals("")) {
                String str_loadtime = etLoadTime.getText().toString();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            } else {
                String input = etLoadTime.getText().toString();
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");

                localDate = LocalDateTime.parse(input, formatter);
                Log.d("localeMonth", "" + localDate.getDayOfMonth());
                mYear = localDate.getYear();
                mMonth = localDate.getMonthOfYear();
                mDay = localDate.getDayOfMonth();
                mHour = localDate.getHourOfDay();
                mMinute = localDate.getMinuteOfHour();


            }
        }


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date_time = (month) + 1 + "/" + dayOfMonth + "/" + year;
                timePicker(etDecantingTime, etLoadTime);
            }
        }, mYear, mMonth - 1, mDay);
        if (isDecenting) {
            if (loadtimeDate != null) {
                datePickerDialog.getDatePicker().setMinDate(loadtimeDate.toDateTime().getMillis());
            } else {
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            }
        } else {
            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

        }

        datePickerDialog.show();
    }

    private void timePicker(TextView etDecantingTime, TextView etLoadtime) {
        // Get Current Time
        if (isDecenting) {
            if (etDecantingTime.getHint().toString().equals("Decanting Time") && etDecantingTime.getText().toString().equals("")) {

                if (etLoadtime.getText().toString() != "") {

                    mHour = loadtimeDate.getHourOfDay();
                    mMinute = loadtimeDate.getMinuteOfHour();
                } else {
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                }
            }
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        mHour = hourOfDay;
                        mMinute = minute;
                        String curTime = String.format("%02d:%02d", mHour, mMinute);
                        if (isDecenting) {
                            etDecantingTime.setText(date_time + " " + curTime);
                        } else {
                            etLoadtime.setText(date_time + " " + curTime);

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } else {
            if (etLoadTime.getHint().toString().equals("Load Time") && etLoadTime.getText().toString().equals("")) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        mHour = hourOfDay;
                        mMinute = minute;
                        String curTime = String.format("%02d:%02d", mHour, mMinute);
                        if (isDecenting) {


                            etDecantingTime.setText(date_time + " " + curTime);
                        } else {
                            etLoadtime.setText(date_time + " " + curTime);

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public boolean checkMultiLoads() {

        for(int i=0;i<MulitDecanting_arranged.size();i++) {

            if (MulitDecanting_arranged.get(""+ i).getDecantingSiteID() == null) {
                checkfields = false;
                return checkfields;
            }
            else{

            Log.d("--layoutmulti", String.valueOf(layout_multidecanting.getChildCount()));
            TextView decantingtime = layout_multidecanting.getChildAt(i).findViewById(R.id.eDecantingTime);
            EditText edt_compartmentno = layout_multidecanting.getChildAt(i).findViewById(R.id.edtCompartno);
            SearchableSpinner SearchableSpinnerCommodity = layout_multidecanting.getChildAt(i).findViewById(R.id.SearchableSpinnerCommodity);
            SearchableSpinner SearchableSpinnerDecanting = layout_multidecanting.getChildAt(i).findViewById(R.id.SearchableSpinnerDecenting);
            TextView edt_loadweight = layout_multidecanting.getChildAt(i).findViewById(R.id.edtloadWeight);

            String loadtxt = edt_loadweight.getText().toString();
            Log.d("--loadtxt", loadtxt);
            if (!MulitDecanting_arranged.get("" + i).getDecantingSiteID().equals(0)
                    && !decantingtime.getText().toString().equals("") && !edt_compartmentno.getText().toString().equals("")
                    && !edt_loadweight.getText().toString().equals("")) {
                checkfields = true;
                if (SearchableSpinnerCommodity != null || SearchableSpinnerCommodity.getSelectedItem() != null || SearchableSpinnerDecanting != null || SearchableSpinnerDecanting.getSelectedItem() != null) {
                    multidecant = true;
                    multicommodity = true;
                } else {
                    multidecant = false;
                    multicommodity = false;
                    break;
                }

            } else {
                checkfields = false;
                break;

            }


        }
    }
        return checkfields;
}


    private void AssignLoadVehicle(){
//        NetworkConsume.getInstance().ShowProgress(context);
//        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token",""));
//        NetworkConsume.getInstance().getAuthAPI().AssignLoad(""+commodity, ""+decanting,etDecantingTime.getText().toString(),etLoadTime.getText().toString(),""+pickup,""+CustomData.getInstance().vehicleDetails.getVehicleID())
//                .enqueue(new Callback<AssignLoadPojo>() {
//                    @Override
//                    public void onResponse(Call<AssignLoadPojo> call, Response<AssignLoadPojo> response) {
//                        NetworkConsume.getInstance().hideProgress();
//                        if (response.isSuccessful()){
//                            Toast.makeText(context, "Load is assigned successfully!", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }else {
//                            new CustomToast().errorToast(context,linearLayout,"Unable to assign load to this vehicle");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<AssignLoadPojo> call, Throwable t) {
//                        NetworkConsume.getInstance().hideProgress();
//                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                checkfields=false;
        HashMap data = new HashMap();
if(pickup!=0 && CustomData.getInstance().vehicleDetails.getVehicleID()!=0 &&
        !etLoadTime.getText().toString().equals("") &&
        decanting!=0 && !etDecantingTime.getText().toString().equals("") && !CompartmentNo1.getText().toString().equals("")&& !LoadWeight1.getText().toString().equals(""))
{

    data.put("PickupLocationID", "" + pickup);
    data.put("VehicleID", "" + CustomData.getInstance().vehicleDetails.getVehicleID());
    data.put("LoadTime", etLoadTime.getText().toString());
 //   if(!olddecantingSites.containsKey(decanting)){

    data.put("LoadDecantingSites[0].DecantingSiteID", "" + decanting);
    data.put("LoadDecantingSites[0].DecantingTime", etDecantingTime.getText().toString());
    data.put("LoadDecantingSites[0].LoadCommodities[0].CompartmentNo", CompartmentNo1.getText().toString());
    data.put("LoadDecantingSites[0].LoadCommodities[0].CommodityLoad", LoadWeight1.getText().toString());
    data.put("LoadDecantingSites[0].LoadCommodities[0].CommodityID",""+commodityArrayList.get(searchableSpinnerCommodity1.getSelectedPosition()-1).getCommodityID());
  //  olddecantingSites.put(String.valueOf(decanting),String.valueOf(0));
   // }
    checkfields=true;
//    if(mySpinner.getSelectedItems().size()!=0)
//    {
//        for (int k=0;k<mySpinner.getSelectedItems().size();k++)
//        {
//            data.put("LoadDecantingSites[0].LoadCommodities["+k+"].CommodityID",""+mySpinner.getSelectedItems().get(k).getCommodityID());
//        }
//    }
//    else {
//        checkfields=false;
//    }


}


        for(int i=0;i<MulitDecantings.size();i++)
        {

           // MultiSelectionSpinner multiSelectionCommodities= layout_multidecanting.getChildAt(i).findViewById(R.id.spn_items);

            TextView decantingtime= layout_multidecanting.getChildAt(i).findViewById(R.id.eDecantingTime);
            EditText edt_compartmentno= layout_multidecanting.getChildAt(i).findViewById(R.id.edtCompartno);
            SearchableSpinner  SearchableSpinnerCommodity=  layout_multidecanting.getChildAt(i).findViewById(R.id.SearchableSpinnerCommodity);
            TextView edt_loadweight= layout_multidecanting.getChildAt(i).findViewById(R.id.edtloadWeight);
//            if(!MulitDecantings.get(""+i).getDecantingSiteID().equals(0)
//                    && !decantingtime.getText().toString().equals("") && !edt_compartmentno.getText().toString().equals("")
//                    && !edt_loadweight.getText().toString().equals(""))
//            {
//            checkfields=true;
//            }

       //    if(!olddecantingSites.containsKey( MulitDecantings.get(""+i).getDecantingSiteID())){
                data.put("LoadDecantingSites["+(i+1)+"].DecantingSiteID",""+MulitDecantings.get(""+i).getDecantingSiteID());
                data.put("LoadDecantingSites["+(i+1)+"].DecantingTime",decantingtime.getText().toString());
                data.put("LoadDecantingSites["+(i+1)+"].LoadCommodities[" + 0 + "].CompartmentNo",edt_compartmentno.getText().toString());
                data.put("LoadDecantingSites["+(i+1)+"].LoadCommodities[" + 0 + "].CommodityLoad",edt_loadweight.getText().toString());
                int  commodityposition=SearchableSpinnerCommodity.getSelectedPosition();
                data.put("LoadDecantingSites[" + (i + 1) + "].LoadCommodities[" + 0 + "].CommodityID", "" + commodityArrayList.get(commodityposition-1));
         //       olddecantingSites.put(MulitDecantings.get(""+i).getDecantingSiteID().toString(),String.valueOf(i+1));
  //          }
          //  else{
           //     int olddecantingSiteIDPos = Integer.valueOf(olddecantingSites.get(MulitDecantings.get(""+i).getDecantingSiteID()));
         //       data.put("LoadDecantingSites["+olddecantingSiteIDPos+"].DecantingSiteID",""+MulitDecantings.get(""+i).getDecantingSiteID());
//                data.put("LoadDecantingSites["+(i+1)+"].DecantingTime",decantingtime.getText().toString());
//                data.put("LoadDecantingSites["+(i+1)+"].LoadCommodities[" + 0 + "].CompartmentNo",edt_compartmentno.getText().toString());
//                data.put("LoadDecantingSites["+(i+1)+"].LoadCommodities[" + 0 + "].CommodityLoad",edt_loadweight.getText().toString());
//                int  commodityposition=SearchableSpinnerCommodity.getSelectedPosition();
//                data.put("LoadDecantingSites[" + (i + 1) + "].LoadCommodities[" + 0 + "].CommodityID", "" + commodityArrayList.get(commodityposition-1));
      //      }



            //            if(multiSelectionCommodities.getSelectedItems().size()>0) {
//                for (int j = 0; j < multiSelectionCommodities.getSelectedItems().size(); j++) {
//                    if( multiSelectionCommodities.getSelectedItems().get(j).getCommodityID()!=0)
//                    {
//                        data.put("LoadDecantingSites[" + (i + 1) + "].LoadCommodities[" + j + "].CommodityID", "" + multiSelectionCommodities.getSelectedItems().get(j).getCommodityID());
//
//                    }
//                    else
//                    {
//                        checkfields=false;
//                    }
//
//                }
//            }
//            else
//            {
//                checkfields=false;
//            }


        }
        if(checkfields) {
            NetworkConsume.getInstance().ShowProgress(this);

            JSONObject jsonObject = new JSONObject(data);
            Log.d("--jsondataAssignLoad",jsonObject.toString());
        httpvolley.stringrequestpost("api/LoadAssigns/AssignLoad", Request.Method.POST, data, this);
        }
        else
        {
            Toast.makeText(AssignLoadActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("Status"))
            {
                if(jsonObject.get("Status").equals(true))
                {
                    Toast.makeText(this,"Load Assigned",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();

    }
}


