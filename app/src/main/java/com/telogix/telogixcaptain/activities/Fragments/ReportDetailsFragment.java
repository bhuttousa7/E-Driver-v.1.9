package com.telogix.telogixcaptain.activities.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.MainActivity;
import com.telogix.telogixcaptain.adapters.SingleLoadDetailCardViewAdapter;
import com.telogix.telogixcaptain.adapters.TotalLoadDetailAdapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class ReportDetailsFragment extends Fragment implements response_interface {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    List<HashMap> totaloadDetailsList = new ArrayList<HashMap>();
    List<HashMap> singleloadDetailsList = new ArrayList<HashMap>();
    int count = 0;
    GridView TotalSimpleGrid;
    Context context = getContext();
    RecyclerView cardViewLoadList;
    TextView vehicleNo, durationDialog, fromDate, toDate, header_heading;
    int mYear;
    int mMonth;
    int mDay;
    String date_time = "";
    int mHour;
    int mMinute;
    private boolean mStoragePermissionGranted = false, firstOpened = false;
    private Bundle b;
    private LocalDateTime loadtimeDate;
    private String fDate, tDate;
    private boolean otherSelected = false;
    private ImageButton printReportIcon;
    private JSONObject Message;
    private View v;
    private Bitmap bitmap;
    private String path;
    private File myPath;
    private String imagesUri;
    private int totalHeight, totalWidth;
    private boolean pdfCreated = false;
    private WebView mWebView;
    private ArrayList<String> dSites;
    private String dSite = "All";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static java.time.LocalDateTime getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        Date today = Calendar.getInstance().getTime();
        java.time.LocalDateTime localDateTime = today.toInstant().atZone(dateFormat.getTimeZone().toZoneId()).toLocalDateTime();
        return localDateTime;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_report_details, container, false);
        bindViews(v);
        b = getArguments();
        getActivity().setTitle(b.getString("ReportName"));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((MainActivity) context);
        cardViewLoadList.setLayoutManager(layoutManager);

        return v;

    }

    private void getDecantingSites() {
        HashMap hashMap = new HashMap();
        httpvolley.stringrequestpost("api/DecantingSites/GetDecantingSiteForDDL", Request.Method.GET, hashMap, this);
    }

    private void getReportDetails(int ReportID, String VehicleNo) {
        NetworkConsume.getInstance().ShowProgress(getContext());
        HashMap hashMap = new HashMap();
        if (fromDate != null && toDate != null && otherSelected) {
            fDate = fromDate.getText().toString();
            tDate = toDate.getText().toString();

        }


        httpvolley.stringrequestpost("api/Report/GetReportDetails?ReportID=" + ReportID + "&VehicleNo=" + VehicleNo +
                "&fromDate=" + fDate + "&toDate=" + tDate + "&dsiteName=" + dSite, Request.Method.POST, hashMap, this);
    }

    public void bindViews(View v) {
        vehicleNo = v.findViewById(R.id.vehicleNo);
        cardViewLoadList = v.findViewById(R.id.cardViewLoadList);
        TotalSimpleGrid = v.findViewById(R.id.TotalSimpleGrid);
        durationDialog = v.findViewById(R.id.durationDialog);
        printReportIcon = v.findViewById(R.id.printReportIcon);
        header_heading = v.findViewById(R.id.header_heading);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vehicleNo.setText(b.getString("VehicleNo"));
        getDecantingSites();
        printReportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fDate != null && tDate != null && mStoragePermissionGranted) {
                    PrintPDF(fDate, tDate, b.getInt("ReportID"), b.getString("VehicleNo"));

                } else {
                    if (!mStoragePermissionGranted) {
                        checkStoragePermission();
                    } else {
                        Toast.makeText(getContext(), "Please select a time period", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        durationDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(Html.fromHtml("<font color='#ED1C24'>Select Time</font>"));
                LinearLayout customTimeSelectorDialog = new LinearLayout(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                customTimeSelectorDialog.setLayoutParams(layoutParams);
                customTimeSelectorDialog.setOrientation(LinearLayout.VERTICAL);

                LinearLayout DateSelectorLL = new LinearLayout(getContext());
                LinearLayout.LayoutParams dateSelectorlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                DateSelectorLL.setLayoutParams(dateSelectorlayoutParams);
                DateSelectorLL.setOrientation(LinearLayout.HORIZONTAL);
                DateSelectorLL.setWeightSum(1.0f);
                DateSelectorLL.setPadding(10, 10, 10, 10);

                fromDate = new TextView(getContext());
                fromDate.setHint("From");
                Drawable calender = getContext().getDrawable(R.drawable.ic_baseline_calendar_today_24);
                calender.setBounds(0, 0, 50, 50);
                fromDate.setCompoundDrawables(null, null, calender, null);
                LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                dateParams.setMargins(0, 0, 5, 0);
                fromDate.setLayoutParams(dateParams);
                fromDate.setBackground(getContext().getDrawable(R.drawable.small_roundview));
                fromDate.setPadding(14, 14, 14, 14);

                fromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fdate;
                        fDate = datePicker(fromDate);

                    }
                });


                toDate = new TextView(getContext());
                toDate.setHint("To");
                toDate.setCompoundDrawables(null, null, calender, null);
                toDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                toDate.setBackground(getContext().getDrawable(R.drawable.small_roundview));
                toDate.setPadding(14, 14, 14, 14);

                toDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tDate = datePicker(toDate);
                    }
                });


                DateSelectorLL.addView(fromDate);
                DateSelectorLL.addView(toDate);


                String[] time = {"Last 24 hours", "Today", "Yesterday", "This Week", "LastWeek", "Other"};
                final Spinner spinner = new Spinner(getContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, time);

                spinner.setAdapter(adapter);
                spinner.setPadding(40, 8, 8, 8);
                customTimeSelectorDialog.addView(spinner);

                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, dSites);

                SearchableSpinner decantingSiteSpinner = new SearchableSpinner(getContext());
                decantingSiteSpinner.setAdapter(arrayAdapter);
                decantingSiteSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                decantingSiteSpinner.setPadding(40, 8, 8, 8);
                customTimeSelectorDialog.addView(decantingSiteSpinner);

                builder.setView(customTimeSelectorDialog);

                decantingSiteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        dSite = decantingSiteSpinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        dSite = "All";

                    }
                });

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //durationDialog.setText(spinner.getSelectedItem().toString());
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("other")) {
                            Toast.makeText(getContext(), "Inside: " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            customTimeSelectorDialog.addView(DateSelectorLL);
                            otherSelected = true;

                        } else {
                            customTimeSelectorDialog.removeView(DateSelectorLL);
                        }
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("Last 24 hours")) {


                            otherSelected = false;
                            fDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth() - 1,
                                    getCurrentDate().getHour(), getCurrentDate().getMinute(), getCurrentDate().getSecond());

                            tDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth(),
                                    getCurrentDate().getHour(), getCurrentDate().getMinute(), getCurrentDate().getSecond());


                        }
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("Today")) {
                            otherSelected = false;

                            fDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth(),
                                    00, 00, 00);

                            tDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth(),
                                    23, 59, 59);


                        }
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("Yesterday")) {
                            otherSelected = false;

                            fDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth() - 1,
                                    00, 00, 00);

                            tDate = setDateTimeFormat(getCurrentDate().getYear(),
                                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth() - 1,
                                    23, 59, 59);

                        }
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("This Week")) {
                            otherSelected = false;
                            java.time.LocalDateTime previousMonday = null;
                            if (getCurrentDate().getDayOfWeek() != DayOfWeek.MONDAY) {
                                previousMonday = getCurrentDate().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                            } else {
                                previousMonday = getCurrentDate();
                            }
                            java.time.LocalDateTime nextMonday = getCurrentDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                            fDate = setDateTimeFormat(previousMonday.getYear(),
                                    previousMonday.getMonth().getValue(), previousMonday.getDayOfMonth(),
                                    00, 00, 00);

                            tDate = setDateTimeFormat(nextMonday.getYear(),
                                    nextMonday.getMonth().getValue(), nextMonday.getDayOfMonth(),
                                    23, 59, 59);

                        }
                        if (spinner.getSelectedItem().toString().equalsIgnoreCase("LastWeek")) {
                            otherSelected = false;

                            java.time.LocalDateTime previousMonday = getCurrentDate().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                            java.time.LocalDateTime previous_previousMonday = previousMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

                            fDate = setDateTimeFormat(previous_previousMonday.getYear(),
                                    previous_previousMonday.getMonth().getValue(), previous_previousMonday.getDayOfMonth(),
                                    00, 00, 00);


                            tDate = setDateTimeFormat(previousMonday.getYear(),
                                    previousMonday.getMonth().getValue(), previousMonday.getDayOfMonth(),
                                    23, 59, 59);


                        }
                        durationDialog.setText(fDate + " - " + tDate);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (otherSelected && fromDate.getText().toString() != null && toDate.getText().toString() != null) {
                            durationDialog.setText(fromDate.getText().toString() + " -  " + toDate.getText().toString());
                            getReportDetails(b.getInt("ReportID"), b.getString("VehicleNo"));
                        } else {
                            if (fDate != null && !fDate.isEmpty() && tDate != null && !tDate.isEmpty()) {
//                        if(fromDate.getText().toString()!=null && toDate.getText().toString() != null){

                                getReportDetails(b.getInt("ReportID"), b.getString("VehicleNo"));
                            } else {

                                Toast.makeText(getContext(), "Please enter Date and time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                Button submitButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout.LayoutParams submitButtonLL = (LinearLayout.LayoutParams) submitButton.getLayoutParams();
                submitButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                submitButtonLL.gravity = Gravity.CENTER_HORIZONTAL;
                submitButton.setLayoutParams(submitButtonLL);

            }
        });

        if (!firstOpened) {
            fDate = setDateTimeFormat(getCurrentDate().getYear(),
                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth() - 1,
                    getCurrentDate().getHour(), getCurrentDate().getMinute(), getCurrentDate().getSecond());

            tDate = setDateTimeFormat(getCurrentDate().getYear(),
                    getCurrentDate().getMonth().getValue(), getCurrentDate().getDayOfMonth(),
                    getCurrentDate().getHour(), getCurrentDate().getMinute(), getCurrentDate().getSecond());
            firstOpened = true;
            getReportDetails(b.getInt("ReportID"), b.getString("VehicleNo"));
        }

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        JSONObject jsonObject = new JSONObject(response);
        boolean status = jsonObject.getBoolean("Status");
        int responseCode = jsonObject.getInt("ResponseCode");
        //GetDecantingSiteForDDL
        if (responseCode == 3005) {
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            dSites = new ArrayList<String>();
            dSites.add("All");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("DecantingSiteName") != null) {
                    dSites.add(jsonArray.getJSONObject(i).getString("DecantingSiteName"));
                }

            }

        } else {

            if (status == false) {
                List emptyList = new ArrayList();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, emptyList);
                RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                    }

                    @Override
                    public int getItemCount() {
                        return 0;
                    }
                };
                // TotalSimpleGrid.setVisibility(View.GONE);
                TotalSimpleGrid.setAdapter(arrayAdapter);
                cardViewLoadList.setAdapter(adapter);
                new AlertDialog.Builder(getContext()).setTitle("Response:").setMessage("No Data Found.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            } else {
                JSONObject data = jsonObject.getJSONObject("Data");
                if (data.has("summaryHeader")) {
                    JSONObject header = data.getJSONObject("summaryHeader");
                    totaloadDetailsList.clear();
                    Iterator iterator = header.keys();
                    HashMap headerHashMap = new HashMap();
                    while (iterator.hasNext()) {

                        String key = (String) iterator.next();
                        String value = header.getString(key);
                        headerHashMap.put(key, value);

                    }
                    totaloadDetailsList.add(headerHashMap);
                    TotalSimpleGrid.setVisibility(View.VISIBLE);
                    TotalSimpleGrid.setAdapter(new TotalLoadDetailAdapter(getContext(), totaloadDetailsList));
                } else {
                    totaloadDetailsList.clear();
                    TotalSimpleGrid.setVisibility(View.GONE);
                }

                JSONArray details = data.getJSONArray("details");

                singleloadDetailsList.clear();


                for (int i = 0; i < details.length(); i++) {

                    JSONObject jsonObject1 = details.getJSONObject(i);
                    Iterator it = jsonObject1.keys();
                    HashMap hashMap = new HashMap();
                    while (it.hasNext()) {

                        String key = (String) it.next();
                        String value = jsonObject1.getString(key);
                        hashMap.put(key, value);
                    }
                    singleloadDetailsList.add(hashMap);


                }

//        ViewReportAdapter viewReportAdapter = new ViewReportAdapter(getActivity(),b,totaloadDetailsList,singleloadDetailsList);
//        reportDetailsRecyclerView.setAdapter(viewReportAdapter);


                cardViewLoadList.setAdapter(new SingleLoadDetailCardViewAdapter(getContext(), singleloadDetailsList, responseCode));
            }
        }
        //Get Commodity details
        if (responseCode == 3) {
            header_heading.setVisibility(View.VISIBLE);
            String udata = "Total Qty Decanted";
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            header_heading.setText(content);
        } else {
            header_heading.setVisibility(View.GONE);
            header_heading.setText("");
        }


    }

    @Override
    public void onError(VolleyError Error) {
        totaloadDetailsList.clear();
        singleloadDetailsList.clear();

    }

    public String datePicker(final TextView date) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();


        if (date.getText().toString() != "") {
            String input = date.getText().toString();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
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


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //date_time = (month) + 1 + "/" + dayOfMonth + "/" + year;
                String mon = null, dom = null;
                if (month < 10) {
                    mon = "0" + ((month) + 1);
                } else {
                    mon = "" + ((month) + 1);
                }
                if (dayOfMonth < 10) {
                    dom = "0" + dayOfMonth;
                } else {
                    dom = "" + dayOfMonth;
                }
                date_time = year + "-" + mon + "-" + dom;
                timePicker(date);
            }
        }, mYear, mMonth - 1, mDay);


        datePickerDialog.show();

        return date_time;
    }

    private void timePicker(TextView date) {
        // Get Current Time

        if (date.getText().toString() != "") {

            mHour = loadtimeDate.getHourOfDay();
            mMinute = loadtimeDate.getMinuteOfHour();
        } else {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    mHour = hourOfDay;
                    mMinute = minute;
                    String curTime = String.format("%02d:%02d", mHour, mMinute);
                    date_time = date_time + " " + curTime;
                    date.setText(date_time);

                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    public String setDateTimeFormat(int year, int month, int day, int hour, int min, int sec) {

        String mon, dom;
        if (month < 10) {
            mon = "0" + month;
        } else {
            mon = "" + month;
        }
        if (day < 10) {
            dom = "0" + day;
        } else {
            dom = "" + day;
        }

        String formattedDateTime = "" + year + "-" + mon + "-" + dom + " " + hour + ":" + min + ":" + sec;
        return formattedDateTime;
    }


    private void checkStoragePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
            //PrintPDF(fDate,tDate);
            //   Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {

            requestStoragePermission();

        }

    }

    private void requestStoragePermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to Store generated Report Files  on your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Thanks for enabling the permission", Toast.LENGTH_SHORT).show();

                //do something permission is allowed here....
                mStoragePermissionGranted = true;
                // createPdf();

            } else {

                Toast.makeText(getContext(), "Please allow the Permission", Toast.LENGTH_SHORT).show();


            }
        }

    }

    private void PrintPDF(String from, String to, int ReportID, String VehicleNo) {

        WebView webView = new WebView(getContext());
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                NetworkConsume.getInstance().hideProgress();

                createWebPrintJob(view);

                mWebView = null;
            }
        });


        //updateInDB(from, to);
// NetworkConsume.getInstance().ShowProgress(this);

        String URL = "http://110.93.209.23:8080/Report/Index?fromDate=" +
                from + "&toDate=" + to + "&ReportID=" + ReportID + "&VehicleNo=" + VehicleNo;
        webView.loadUrl(URL);
        mWebView = webView;
// }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

// Get a PrintManager instance
        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = "Document";

// Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

// Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE,
                                500, 500))
                        .build());

// Save the job object for later status checking
//printJobs.add(printJob);
    }


//    private boolean createPdf() {
//        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//        PDFHelper pdfHelper = new PDFHelper(folder,getContext());
//        Bitmap recyclerviewBitmap = getScreenshotFromRecyclerView(cardViewLoadList);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//               pdfCreated = pdfHelper.saveImageToPDF(v, recyclerviewBitmap, "TLWiseLoadDetails_" + getCurrentDate().toString());
//            }
//
//        return pdfCreated;
//    }
//
//
//    public  Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
//        NetworkConsume.getInstance().ShowProgress(getActivity());
//        RecyclerView.Adapter adapter = view.getAdapter();
//        Bitmap bigBitmap = null;
//        if (adapter != null) {
//            int size = adapter.getItemCount();
//            int height = 0;
//            Paint paint = new Paint();
//            int iHeight = 0;
//            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//            // Use 1/8th of the available memory for this memory cache.
//            final int cacheSize = maxMemory / 8;
//            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
//            for (int i = 1; i < size; i++) {
//                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
//                adapter.onBindViewHolder(holder, i);
//                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
//                holder.itemView.setDrawingCacheEnabled(true);
//                holder.itemView.buildDrawingCache();
//                Bitmap drawingCache = holder.itemView.getDrawingCache();
//                if (drawingCache != null) {
//
//                    bitmaCache.put(String.valueOf(i), drawingCache);
//                }
////                holder.itemView.setDrawingCacheEnabled(false);
////                holder.itemView.destroyDrawingCache();
//                height += holder.itemView.getMeasuredHeight();
//            }
//
//            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
//            Canvas bigCanvas = new Canvas(bigBitmap);
//            bigCanvas.drawColor(Color.WHITE);
//
//            for (int i = 1; i < size; i++) {
//                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
//                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
//                iHeight += bitmap.getHeight();
//                bitmap.recycle();
//            }
//
//        }
//        return bigBitmap;
//    }
//
//
//
//    public static Bitmap getBitmapFromView(View v, int width, int height) {
//        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
//        v.draw(c);
//        return b;
//    }
//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float)width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }
//
//    public class PDFHelper {
//
//        private File mFolder;
//        private File mFile;
//        private Context mContext;
//
//        public PDFHelper(File folder, Context context) {
//            this.mContext = context;
//            this.mFolder = folder;
//
//            if(!mFolder.exists())
//                mFolder.mkdirs();
//        }
//
//        public boolean saveImageToPDF(View title, Bitmap bitmap, String filename) {
//            mFile = new File(mFolder, filename + ".pdf");
//            if (!mFile.exists()) {
//                int height = title.getHeight() + bitmap.getHeight();
//                PdfDocument document = new PdfDocument();
//                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), height, 1).create();
//                PdfDocument.Page page = document.startPage(pageInfo);
//                Canvas canvas = page.getCanvas();
//                title.draw(canvas);
//
//                canvas.drawBitmap(bitmap, null, new Rect(0, title.getHeight(), bitmap.getWidth(),bitmap.getHeight()), null);
//
//                document.finishPage(page);
//
//                try {
//                    mFile.createNewFile();
//                    OutputStream out = new FileOutputStream(mFile);
//                    document.writeTo(out);
//                    document.close();
//                    out.close();
//                    new CustomToast().successToast(getContext(), v, "Report PDF Successfully Created");
//                    pdfCreationSuccessful();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    new CustomToast().errorToast(getContext(), v, "Error occured creating PDF file"+e.getMessage());
//                pdfCreated = false;
//                }
//            }
//        return pdfCreated;
//        }
//
//        private void pdfCreationSuccessful() {
//            NetworkConsume.getInstance().hideProgress();
//        }
//    }

}
