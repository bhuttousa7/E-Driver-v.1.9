package com.telogix.telogixcaptain.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.CommoditiesPojo.CommoditiesPojo;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.DecantingSitesPojo;
import com.telogix.telogixcaptain.Pojo.ViewLoad.Data;
import com.telogix.telogixcaptain.Pojo.ViewLoad.LoadCommodity;
import com.telogix.telogixcaptain.Pojo.ViewLoad.LoadDecantingSite;
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
import org.json.JSONArray;
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
import spencerstudios.com.ezdialoglib.EZDialog;


public class EditLoadActivity extends BaseActivity implements response_interface {

    EditText vehicleName, vehicleType,CompartmentNo1,LoadWeight1;
    TextView etLoadTime;
    Button btnSubmit,btnAddDecating;
    int mYear;
    int mMonth;
    int mDay;
    String date_time = "";
    int mHour;
    int mMinute;
    SearchableSpinner searchableSpinnerPickup1;
    Context context;
    String dateStr, decentingTime,loadtime;
    private SimpleArrayListAdapterPickup simpleArrayListAdapterPickup;
    private SimpleArrayListAdapterDecanting mSimpleArrayListAdapterDecanting;
    private SimpleArrayListAdapterCommodities mSimpleArrayListAdapterCommodities;
    private final int commodity=0;
    private final int decanting =0;
    private int pickup=0;
    String dateTimeFormat = "MM/dd/yyyy mm:hh";
    SimpleDateFormat spf=new SimpleDateFormat(dateTimeFormat);
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    private boolean isDecenting = false;
    LinearLayout linearLayout,layout_multidecanting;
    MultiSelectionSpinner mySpinner;
    ArrayList<Datum> pickupArrayList = new ArrayList<>();
    ArrayList<com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> decantingArrayList = new ArrayList<>();
    ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> commodityArrayList = new ArrayList<>();
    HashMap<String, com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> MulitDecantings=new HashMap<>();
    private LocalDateTime localDate,loadtimeDate;
    private TextView txtpickup;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_load);
        context = this;
        init();
        //   mySpinner.
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_load;
    }

    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Load");
        setSupportActionBar(toolbar);
        // Views binding
        if(getIntent().hasExtra("LoadID"))
        {
            String LoadID=getIntent().getStringExtra("LoadID");
            getLoad(LoadID);
        }

        vehicleName = findViewById(R.id.etVname);
        vehicleName.setEnabled(false);
        vehicleType =  findViewById(R.id.etVtype);
        vehicleType.setEnabled(false);




        etLoadTime = findViewById(R.id.etLoadTime);

        layout_multidecanting=findViewById(R.id.layout_multidecanting);

        btnSubmit =findViewById(R.id.btnSubmit);
        btnAddDecating=findViewById(R.id.btnAddDecating);
        txtpickup=findViewById(R.id.txtpickup);
        searchableSpinnerPickup1=findViewById(R.id.SearchableSpinnerPickup);
        searchableSpinnerPickup1.setVisibility(View.GONE);
        txtpickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickup=0;
                txtpickup.setVisibility(View.GONE);
                searchableSpinnerPickup1.setVisibility(View.VISIBLE);
            }
        });
        btnAddDecating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_decanting();
            }
        });
        getPickupLocations();
        linearLayout =findViewById(R.id.mainLL);
        simpleArrayListAdapterPickup = new SimpleArrayListAdapterPickup(this, pickupArrayList);
        searchableSpinnerPickup1.setAdapter(simpleArrayListAdapterPickup);



        /// multi selection



        // onClick events
        toolbar.setNavigationOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> {


            //
            if (pickup ==0 ){
                Toast.makeText(context, "Please fill out the form!", Toast.LENGTH_SHORT).show();
            }else if (etLoadTime.getText().toString().equals("")){
                Toast.makeText(context, "please select the load Time!", Toast.LENGTH_SHORT).show();
            }else {

                alertDialog();
            }
        });

        searchableSpinnerPickup1.setOnItemSelectedListener(mOnItemSelectedListener);
        etLoadTime.setOnClickListener(v -> {
            isDecenting= false;
           datePicker(etLoadTime,etLoadTime);
        });

    }

    private void add_decanting() {

        View view=getLayoutInflater().inflate(R.layout.assign_multiload,null);

        //
        SearchableSpinner searchablespinnerDecanting = view.findViewById(R.id.SearchableSpinnerDecenting);
        SearchableSpinner searchableSpinnerCommodities = view.findViewById(R.id.SearchableSpinnerCommodity);
        TextView eDecantingTime = view.findViewById(R.id.eDecantingTime);
        ImageButton cross = view.findViewById(R.id.cross);
        SimpleArrayListAdapterDecanting mSimpleArrayListAdapterMultiDecanting= new SimpleArrayListAdapterDecanting(EditLoadActivity.this, decantingArrayList);
        searchablespinnerDecanting.setAdapter(mSimpleArrayListAdapterMultiDecanting);
        SimpleArrayListAdapterCommodities mSimpleArrayListAdapterCommodities= new SimpleArrayListAdapterCommodities(EditLoadActivity.this, commodityArrayList);

        searchableSpinnerCommodities.setAdapter(mSimpleArrayListAdapterCommodities);

        searchablespinnerDecanting.setSelectedItem(decanting);
        searchableSpinnerCommodities.setSelectedItem(commodity);
//        eDecantingTime.setText(etDecantingTime.getText().toString());
        searchableSpinnerCommodities.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        searchablespinnerDecanting.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d= mSimpleArrayListAdapterMultiDecanting.getselectedItem();
                int pos= (Integer)searchablespinnerDecanting.getTag();
                MulitDecantings.put(""+pos,d);

            }

            @Override
            public void onNothingSelected() {

            }
        });
        eDecantingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecenting=true;
                datePicker(eDecantingTime,etLoadTime);
            }
        });
        searchableSpinnerCommodities.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                                    commodityArrayList.get(position-1).getCommodityID();
                int pos= (Integer)searchableSpinnerCommodities.getTag();
                MulitDecantings.get(""+pos).setCommodityID(commodityArrayList.get(position-1).getCommodityID());
                MulitDecantings.get(""+pos).setCommodity(commodityArrayList.get(position-1).getCommodityName());

            }

            @Override
            public void onNothingSelected() {

            }
        });
        // SearchableSpinnerCommodity.setItems(commodityArrayList);

        eDecantingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecenting = true;
                datePicker(eDecantingTime,etLoadTime);
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                removeView(position);
            }
        });
        searchablespinnerDecanting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();

            }
        });
        layout_multidecanting.addView(view);
        int position = layout_multidecanting.indexOfChild(view);
        view.setTag(position);
        MulitDecantings.put(""+position,new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum());
        view.findViewById(R.id.SearchableSpinnerCommodity).setTag(position);
        view.findViewById(R.id.SearchableSpinnerDecenting).setTag(position);

        view.findViewById(R.id.eDecantingTime).setTag(position);
        view.findViewById(R.id.cross).setTag(position);

    }

    private void removeView(int position) {
        try {
            if(layout_multidecanting.getChildCount()==1){

                SearchableSpinner multidecanting = layout_multidecanting.getChildAt(0).findViewById(R.id.SearchableSpinnerDecenting);
                SearchableSpinner SearchableSpinnerCommodity = layout_multidecanting.getChildAt(0).findViewById(R.id.SearchableSpinnerCommodity);
                multidecanting.hideEdit();
                SearchableSpinnerCommodity.hideEdit();
                MulitDecantings.remove("" + position);
                layout_multidecanting.removeView(layout_multidecanting.findViewWithTag(position));

            }
            else {
                SearchableSpinner multidecanting = layout_multidecanting.getChildAt(position).findViewById(R.id.SearchableSpinnerDecenting);
                SearchableSpinner SearchableSpinnerCommodity = layout_multidecanting.getChildAt(position).findViewById(R.id.SearchableSpinnerCommodity);
                multidecanting.hideEdit();
                SearchableSpinnerCommodity.hideEdit();
                MulitDecantings.remove("" + position);
                layout_multidecanting.removeView(layout_multidecanting.findViewWithTag(position));
            }
        }catch (Exception ex){ex.printStackTrace();}
    }



    private void alertDialog(){
        new EZDialog.Builder(context)

                .setTitle("Please Confirm!")
                .setMessage("Are you sure to re-assign load to vehicle no: "+" "+ CustomData.getInstance().vehicleDetails.getVehicleNo()+"\n"

)
                .setPositiveBtnText("Yes")
                .setNegativeBtnText("No")
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(() -> AssignLoadVehicle())
                .OnNegativeClicked(() ->{
                })
                .build();

    }

    private final OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
            Datum d= simpleArrayListAdapterPickup.getselectedItem();
            pickup=d.getPickupLocationID();

        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };

    private void getPickupLocations(){
   //     NetworkConsume.getInstance().ShowProgress(context);
        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token",""));
        NetworkConsume.getInstance().getAuthAPI().getPickupLocationLov().enqueue(new Callback<PickupLocationPojo>() {
            @Override
            public void onResponse(Call<PickupLocationPojo> call, Response<PickupLocationPojo> response) {

                NetworkConsume.getInstance().hideProgress();
                if (response.isSuccessful()){
                    pickupArrayList.addAll(response.body().getData());
                    simpleArrayListAdapterPickup = new SimpleArrayListAdapterPickup(EditLoadActivity.this, pickupArrayList);
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
    private void getDecantings(){

        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token",""));
        NetworkConsume.getInstance().getAuthAPI().getDecantingSites().enqueue(new Callback<DecantingSitesPojo>() {
            @Override
            public void onResponse(Call<DecantingSitesPojo> call, Response<DecantingSitesPojo> response) {
                if (response.isSuccessful()){
                    decantingArrayList.addAll(response.body().getData());
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
    private void getCommoidites(){

        NetworkConsume.getInstance().setAccessKey(singleton.getsharedpreference(context).getString("token",""));
        NetworkConsume.getInstance().getAuthAPI().getCommodityLov().enqueue(new Callback<CommoditiesPojo>() {
            @Override
            public void onResponse(Call<CommoditiesPojo> call, Response<CommoditiesPojo> response) {
                NetworkConsume.getInstance().hideProgress();
                if (response.isSuccessful()){
                    commodityArrayList.addAll(response.body().getData());
                    // mySpinner.setItems(commodityArrayList);


                }
            }

            @Override
            public void onFailure(Call<CommoditiesPojo> call, Throwable t) {
                NetworkConsume.getInstance().hideProgress();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void datePicker(final TextView etDecantingTime, TextView etLoadTime){
        // Get Current Date
        try {
            final Calendar c = Calendar.getInstance();

            if (isDecenting) {
                if (etDecantingTime.getHint().toString().equals("Decanting Time") && etDecantingTime.getText().toString().equals("")) {

                    if (etLoadTime.getText().toString() != "") {
                        String input = etLoadTime.getText().toString();
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
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
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
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
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

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
                    date_time = year + "-" + ((month) + 1) + "-" + dayOfMonth;
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
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }}
    private void timePicker(TextView etDecantingTime,TextView etLoadtime){
        // Get Current Time
        if (isDecenting) {
            if (etDecantingTime.getHint().toString().equals("Decanting Time") && etDecantingTime.getText().toString().equals("")) {

                if(etLoadtime.getText().toString()!="")
                {

                    mHour=loadtimeDate.getHourOfDay();
                    mMinute=loadtimeDate.getMinuteOfHour();
                }
                else {
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
                        String curTime = String.format("%02d:%02d:%02d", mHour, mMinute,0);
                        if (isDecenting) {
                            etDecantingTime.setText(date_time + " " + curTime);
                        } else {
                            etLoadtime.setText(date_time + " " + curTime);

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        else
        {
            if (etLoadTime.getHint().toString().equals("Load Time") && etLoadTime.getText().toString().equals("")) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        mHour = hourOfDay;
                        mMinute = minute;
                        String curTime = String.format("%02d:%02d:%02d", mHour, mMinute,0);
                        if (isDecenting) {


                            etDecantingTime.setText(date_time + " " + curTime);
                        } else {
                            etLoadtime.setText(date_time + " " + curTime);

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    private void getLoad(String LoadID) {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap data = new HashMap();

        httpvolley.stringrequestpost("api/LoadAssigns/GetLoadAssign?id="+LoadID, Request.Method.GET, data, this);

    }
    private void AssignLoadVehicle(){
        try {
            boolean checkfields = false;
            HashMap data = new HashMap();
            if (pickup != 0 && CustomData.getInstance().vehicleDetails.getVehicleID() != 0 &&
                    !etLoadTime.getText().toString().equals("")
            ) {


                data.put("PickupLocationID", "" + pickup);
                data.put("VehicleID", "" + CustomData.getInstance().vehicleDetails.getVehicleID());
                data.put("LoadTime", etLoadTime.getText().toString());
                data.put("LoadID", CustomData.getInstance().vehicleDetails.getLoadID());

                checkfields = true;

            }

            ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> commodities = new ArrayList();
            Iterator r= MulitDecantings.entrySet().iterator();
            for (int i = 0; i < MulitDecantings.size(); i++) {

                Map.Entry<String, com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum> entry = (Map.Entry<String, com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum>) r.next();
                TextView decantingtime = layout_multidecanting.getChildAt(i).findViewById(R.id.eDecantingTime);
                EditText edt_compartmentno = layout_multidecanting.getChildAt(i).findViewById(R.id.edtCompartno);
                SearchableSpinner SearchableSpinnerCommodity = layout_multidecanting.getChildAt(i).findViewById(R.id.SearchableSpinnerCommodity);
                TextView edt_loadweight = layout_multidecanting.getChildAt(i).findViewById(R.id.edtloadWeight);
                //

                String key = entry.getKey();


                //


                if (!MulitDecantings.get(key).getDecantingSiteID().equals(0)
                        && !decantingtime.getText().toString().equals("") && !edt_compartmentno.getText().toString().equals("")
                        && !edt_loadweight.getText().toString().equals("")) {
                    checkfields = true;
                }
                if (MulitDecantings.get(key).getDecantingSiteID() == -1) {
                    checkfields = false;
                    break;
                }

                // Check for same decanting and populating the commodities in the decanting
//            try {
//
//
//
//                for (int j = 0; j < MulitDecantings.size(); j++) {
//                    int decantingSideID = MulitDecantings.get(""+j).getDecantingSiteID();
//                    for (int k = 0; k < MulitDecantings.size(); k++) {
//                        if (decantingSideID == MulitDecantings.get(""+k).getDecantingSiteID()) {
//                            com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum commoditiesPojo = new com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum();
//                            commoditiesPojo.setCommodityID(MulitDecantings.get(""+k).getCommodityID());
//                            commodities.add(commoditiesPojo);
//                            MulitDecantings.get(""+j).setcommoditieslist(commodities);
//                        }
//                    }
//
//commodities=new ArrayList<>();
//                }
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
                data.put("LoadDecantingSites[" + (i) + "].DecantingSiteID", "" + MulitDecantings.get(key).getDecantingSiteID());
                data.put("LoadDecantingSites[" + (i) + "].DecantingTime", decantingtime.getText().toString());
                data.put("LoadDecantingSites[" + (i) + "].LoadCommodities[" + 0 + "].CompartmentNo", edt_compartmentno.getText().toString());
                data.put("LoadDecantingSites[" + (i) + "].LoadCommodities[" + 0 + "].CommodityLoad", edt_loadweight.getText().toString());
                int commodityposition = SearchableSpinnerCommodity.getSelectedPosition();
                if (MulitDecantings.get(key).getCommodityID() == -1) {
                    checkfields = false;
                    break;
                }
                data.put("LoadDecantingSites[" + (i) + "].LoadCommodities[" + 0 + "].CommodityID", "" + MulitDecantings.get(key).getCommodityID());


            }
            if (checkfields) {
                NetworkConsume.getInstance().ShowProgress(this);

                Log.i("editload_data",data.toString());


                httpvolley.stringrequestpost("api/LoadAssigns/EditLoad", Request.Method.POST, data, this);
            } else {
                Toast.makeText(EditLoadActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject1=new JSONObject(response);
        try {


            if (jsonObject1.has("ResponseCode")) {

                if (jsonObject1.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.VIEWLOAD))) {
                    if(jsonObject1.has("Data"))
                    {
                        if(!jsonObject1.getJSONObject("Data").equals(null)){
                            Data ViewLoadData=new Data();
                            JSONObject data = jsonObject1.getJSONObject("Data");
                            ViewLoadData.setLoadID( data.getInt("LoadID"));
                            ViewLoadData.setPickupLocation(data.getString("PickupLocation"));
                            ViewLoadData.setPickupLocationID(data.getInt("PickupLocationID"));
                            ViewLoadData.setCompany(data.getString("Company"));
                            ViewLoadData.setCompanyID(data.getInt("CompanyID"));
                            ViewLoadData.setVehicle( data.getString("Vehicle"));
                            ViewLoadData.setVehicleID(data.getInt("VehicleID"));
                            ViewLoadData.setLoadTime(data.getString("LoadTime"));
                            JSONArray jsonLoadDecantingSites=data.getJSONArray("LoadDecantingSites");
                            ArrayList<LoadDecantingSite> loadDecantingSitesList=new ArrayList<>();
                            for(int i=0;i<jsonLoadDecantingSites.length();i++) {
                                LoadDecantingSite loadDecantingSite = new LoadDecantingSite();
                                loadDecantingSite.setDecantingSite(jsonLoadDecantingSites.getJSONObject(i).getString("DecantingSite"));
                                loadDecantingSite.setDecantingSiteID(jsonLoadDecantingSites.getJSONObject(i).getInt("DecantingSiteID"));
                                loadDecantingSite.setDecantingTime(jsonLoadDecantingSites.getJSONObject(i).getString("DecantingTime"));
                                loadDecantingSite.setLoadDecantingSiteID(jsonLoadDecantingSites.getJSONObject(i).getInt("LoadDecantingSiteID"));
                                loadDecantingSite.setLoadID(jsonLoadDecantingSites.getJSONObject(i).getInt("LoadID"));

                                JSONArray jsonLoadCommodities = jsonLoadDecantingSites.getJSONObject(i).getJSONArray("LoadCommodities");
                                ArrayList<LoadCommodity> loadCommoditiesList=new ArrayList<>();
                                for (int j = 0; j < jsonLoadCommodities.length(); j++)
                                {

                                    LoadCommodity loadCommodity=new LoadCommodity();
                                    loadCommodity.setCommodity(jsonLoadCommodities.getJSONObject(j).getString("Commodity"));
                                    loadCommodity.setCommodityID(jsonLoadCommodities.getJSONObject(j).getInt("CommodityID"));
                                    loadCommodity.setLoadCommodityID(jsonLoadCommodities.getJSONObject(j).getInt("LoadCommodityID"));
                                    loadCommodity.setLoadDecantingSiteID(jsonLoadCommodities.getJSONObject(j).getInt("LoadDecantingSiteID"));
                                    if(!jsonLoadCommodities.getJSONObject(j).get("CompartmentNo").equals(null))
                                    {
                                        loadCommodity.setCompartmentNo(jsonLoadCommodities.getJSONObject(j).getString("CompartmentNo"));

                                    }
                                    if(!jsonLoadCommodities.getJSONObject(j).get("CommodityLoad").equals(null))
                                    {
                                        loadCommodity.setCommodityLoad(jsonLoadCommodities.getJSONObject(j).getString("CommodityLoad"));

                                    }
                                    loadCommoditiesList.add(loadCommodity);
                                    //   loadDecantingSite.setLoadCommodities(loadCommodity);


                                }
                                loadDecantingSite.setLoadCommodities(loadCommoditiesList);
                                loadDecantingSitesList.add(loadDecantingSite);

                                    add_decanting_unedited(loadDecantingSite);

                            }
                            pickup=ViewLoadData.getPickupLocationID();
                            ViewLoadData.setLoadDecantingSites(loadDecantingSitesList);

                            txtpickup.setText(""+ViewLoadData.getPickupLocation());
                            etLoadTime.setText(""+ViewLoadData.getLoadTime().split("T")[0]+" "+ViewLoadData.getLoadTime().split("T")[1]);
                            vehicleName.setText(""+ViewLoadData.getVehicle());

//                    MulitDecantings.put("")

                           // listdecantings.setAdapter(new ViewLoadAdapter(getContext(),loadDecantingSitesList));
                        }
                    }


                }
           else if(jsonObject1.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.EDITLOAD)))
                {
                    hideKeyboardFrom();
                    Toast.makeText(this,"Load Edited",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        }
        catch (Exception ex)
        {
            NetworkConsume.getInstance().hideProgress();
            Log.d("error",""+ex.getLocalizedMessage());
        }
    }
    public  void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void add_decanting_unedited(LoadDecantingSite loadDecantingSite) {


        for(int i=0;i<loadDecantingSite.getLoadCommodities().size();i++) {
            View view=getLayoutInflater().inflate(R.layout.assign_multiload,null);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,5,0,0);
            view.setLayoutParams(params);
            //
            SearchableSpinner searchablespinnerDecanting = view.findViewById(R.id.SearchableSpinnerDecenting);
            SearchableSpinner searchableSpinnerCommodities = view.findViewById(R.id.SearchableSpinnerCommodity);
            TextView txtmultidecanting=view.findViewById(R.id.txtmultidecanting);
            TextView txtmulticommodities=view.findViewById(R.id.txtmulticommodities);

            TextView eDecantingTime = view.findViewById(R.id.eDecantingTime);
            ImageButton cross = view.findViewById(R.id.cross);
            EditText edtCompartno=view.findViewById(R.id.edtCompartno);
            EditText edtloadweight=view.findViewById(R.id.edtloadWeight);
            SimpleArrayListAdapterDecanting mSimpleArrayListAdapterMultiDecanting= new SimpleArrayListAdapterDecanting(EditLoadActivity.this, decantingArrayList);
            searchablespinnerDecanting.setAdapter(mSimpleArrayListAdapterMultiDecanting);
            searchablespinnerDecanting.setVisibility(View.GONE);
            searchableSpinnerCommodities.setVisibility(View.GONE);
            txtmulticommodities.setText(loadDecantingSite.getLoadCommodities().get(i).getCommodity());
            edtloadweight.setText(loadDecantingSite.getLoadCommodities().get(i).getCommodityLoad().toString());
            edtCompartno.setText(loadDecantingSite.getLoadCommodities().get(i).getCompartmentNo());


            txtmultidecanting.setText(loadDecantingSite.getDecantingSite());
//        MulitDecantings.put()
            txtmultidecanting.setVisibility(View.VISIBLE);
            txtmulticommodities.setVisibility(View.VISIBLE);
            edtCompartno.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int pos = (Integer) edtCompartno.getTag();
                    MulitDecantings.get("" + pos).setCompartmentNo(editable.toString());

                }
            });
            edtloadweight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int pos = (Integer) edtloadweight.getTag();
                    MulitDecantings.get("" + pos).setCommodityLoad(editable.toString());

                }
            });
            txtmulticommodities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtmulticommodities.setVisibility(View.GONE);
                    int pos = (int) v.getTag();
                    MulitDecantings.get("" + pos).setCommodityID(-1);
                    searchableSpinnerCommodities.setVisibility(View.VISIBLE);
                }
            });
            txtmultidecanting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtmultidecanting.setVisibility(View.GONE);
                    int pos = (int) v.getTag();

                    MulitDecantings.get("" + pos).setDecantingSiteID(-1);
                    searchablespinnerDecanting.setVisibility(View.VISIBLE);
                }
            });
            SimpleArrayListAdapterCommodities mSimpleArrayListAdapterCommodities = new SimpleArrayListAdapterCommodities(EditLoadActivity.this, commodityArrayList);
            searchableSpinnerCommodities.setAdapter(mSimpleArrayListAdapterCommodities);

            searchablespinnerDecanting.setSelectedItem(decanting);
            searchableSpinnerCommodities.setSelectedItem(commodity);
            eDecantingTime.setText(loadDecantingSite.getDecantingTime().split("T")[0] + " " + loadDecantingSite.getDecantingTime().split("T")[1]);

            searchablespinnerDecanting.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(View view, int position, long id) {
                    com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d = mSimpleArrayListAdapterMultiDecanting.getselectedItem();
                    int pos = (Integer) searchablespinnerDecanting.getTag();
                    //  Toast.makeText(EditLoadActivity.this, "" + pos, Toast.LENGTH_SHORT).show();
                    //MulitDecantings.put(""+pos,d);
                    MulitDecantings.get("" + pos).setDecantingSiteID(d.getDecantingSiteID());
                    MulitDecantings.get("" + pos).setDecantingSite(d.getDecantingSite());

                }

                @Override
                public void onNothingSelected() {

                }
            });
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
                    int pos = (Integer) searchableSpinnerCommodities.getTag();
                    MulitDecantings.get("" + pos).setCommodityID(commodityArrayList.get(position - 1).getCommodityID());
                    MulitDecantings.get("" + pos).setCommodity(commodityArrayList.get(position - 1).getCommodityName());

                    commodityArrayList.get(position - 1).getCommodityID();
                }

                @Override
                public void onNothingSelected() {

                }
            });
            // SearchableSpinnerCommodity.setItems(commodityArrayList);

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
                    removeView(position);
                }
            });


            layout_multidecanting.addView(view);

            int position = layout_multidecanting.indexOfChild(view);
            view.setTag(position);
            com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum d = new com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum();
            d.setDecantingLatitude(0.0);
            d.setDecantingLongitude(0.0);
            d.setDecantingSite(loadDecantingSite.getDecantingSite());
            d.setDecantingSiteID(loadDecantingSite.getDecantingSiteID());
            d.setLoadDecantingSiteID(loadDecantingSite.getLoadDecantingSiteID());

            if (loadDecantingSite.getLoadCommodities().size() > 0) {
                d.setCommodity(loadDecantingSite.getLoadCommodities().get(i).getCommodity());
                d.setLoadCommodityID(loadDecantingSite.getLoadCommodities().get(i).getLoadCommodityID());
                d.setCommodityID(loadDecantingSite.getLoadCommodities().get(i).getCommodityID());
                d.setCommodityLoad("" + loadDecantingSite.getLoadCommodities().get(i).getCommodityLoad());
                d.setCompartmentNo(loadDecantingSite.getLoadCommodities().get(i).getCompartmentNo());
            }

            MulitDecantings.put("" + position, d);
            view.findViewById(R.id.SearchableSpinnerCommodity).setTag(position);
            view.findViewById(R.id.SearchableSpinnerDecenting).setTag(position);
            view.findViewById(R.id.txtmultidecanting).setTag(position);
            view.findViewById(R.id.txtmulticommodities).setTag(position);
            view.findViewById(R.id.edtCompartno).setTag(position);
            view.findViewById(R.id.edtloadWeight).setTag(position);
            view.findViewById(R.id.eDecantingTime).setTag(position);
            view.findViewById(R.id.cross).setTag(position);
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();

    }
}


