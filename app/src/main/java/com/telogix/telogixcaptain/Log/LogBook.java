package com.telogix.telogixcaptain.Log;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telogix.telogixcaptain.Pojo.singleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public  class LogBook {

    Context context;
    public LogBook(Context context)
    {
        this.context=context;
    }
    public void updateLogBook(String Title) {
        Gson gson;
        if (singleton.getsharedpreference(context).getString("logbook", null) != null) {
            ArrayList<HashMap<String,String>> historylist;
            String json = singleton.getsharedpreference(context).getString("logbook", "");
            HashMap<String,String> data=new HashMap<>();
            data.put("title", Title);

            data.put("time", String.format("" +getCurrentDate()));
            gson = new Gson();
            historylist = gson.fromJson(json, new TypeToken<List<HashMap<String,String>>>() {
            }.getType());
            historylist.add(data);
            json = gson.toJson(historylist);
            singleton.getsharedpreference_editor(context).putString("logbook", json).commit();


        } else {

            ArrayList<HashMap<String,String>> historylist=new ArrayList<>();
            HashMap<String,String> data=new HashMap<>();
            data.put("title",Title);
            data.put("time", String.format("" +getCurrentDate()));
            historylist.add(data);
            gson = new Gson();
            String json = gson.toJson(historylist);
            singleton.getsharedpreference_editor(context).putString("logbook", json).commit();
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

}
