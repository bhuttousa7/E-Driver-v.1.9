package com.telogix.telogixcaptain.driver.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class Drivers {


    public static ArrayList getDrivers(Context context){
        Paper.init(context);
        ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
        driverslist=Paper.book().read("drivers");
        return driverslist;
    }
    public static int  getDriverCount(Context context){
        Paper.init(context);
        ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
        driverslist=Paper.book().read("drivers");
        if(driverslist!=null)
        {
            return driverslist.size();
        }
        else
        {
            return 0;
        }
    }
    public static String  getFirstDriverName(Context context){
        Paper.init(context);
        try {


            ArrayList<HashMap<String, String>> driverslist = new ArrayList<>();
            driverslist=Paper.book().read("drivers");

            if (driverslist != null) {
                if (driverslist.size() > 0) {
                    return "" + driverslist.get(0).get("DriverName");
                }
            } else {
                return "";
            }
            return "";
        }catch (Exception ex)
        {
            return "";
        }
    }
    public static String  getSecondDriverName(Context context){
        try{
            Paper.init(context);
            ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
            driverslist=Paper.book().read("drivers");

            if(driverslist!=null)
            {
                if(driverslist.size()>1)
                {
                    return ""+driverslist.get(1).get("DriverName");
                }
            }
            else
            {
                return "";
            }
            return "";
        }catch (Exception ex)
        {
            return "";
        }
    }
    public static String  getFirstDriverToken(Context context){
        Paper.init(context);
        try {


            ArrayList<HashMap<String, String>> driverslist = new ArrayList<>();
            driverslist=Paper.book().read("drivers");

            if (driverslist != null) {
                if (driverslist.size() > 0) {
                    return "" + driverslist.get(0).get("Driver");
                }
            } else {
                return "";
            }
            return "";
        }catch (Exception ex)
        {
            return "";
        }
    }
    public static String  getSecondDriverToken(Context context){
        try{
        Paper.init(context);
        ArrayList<HashMap<String,String>> driverslist=new ArrayList<>();
            driverslist=Paper.book().read("drivers");

            if(driverslist!=null)
        {
            if(driverslist.size()>1)
            {
                return ""+driverslist.get(1).get("Driver");
            }
        }
        else
        {
            return "";
        }
        return "";
    }catch (Exception ex)
    {
        return "";
    }
    }
    public static boolean  setFirstDriver(Context context,HashMap<String,String> driverHash){
        try{
            Paper.init(context);
            ArrayList<HashMap<String,String>> driverslist;
            driverslist=Paper.book().read("drivers");

            if(driverslist==null)
            {
                driverslist=new ArrayList<>();
                driverslist.add(driverHash);
               Paper.book().write("drivers",driverslist);
               return true;
            }
            else
            {
                if(driverslist.size()<2) {
                    driverslist = new ArrayList<>();
                    driverslist.add(driverHash);
                    Paper.book().write("drivers", driverslist);
                }
                return true;
            }

        }catch (Exception ex)
        {
            return false;
        }
    }
    public static boolean  setSecondDriver(Context context,HashMap<String,String> driverHash){
        try{
            Paper.init(context);
            ArrayList<HashMap<String,String>> driverslist;
            driverslist=Paper.book().read("drivers");

            if(driverslist!=null)
            {
                if(driverslist.size()==1) {

                    driverslist.add(driverHash);
                    Paper.book().write("drivers", driverslist);
                    return true;
                }

            }

            return false;
        }catch (Exception ex)
        {
            return false;
        }
    }
    public static boolean  setSecondDriverName(Context context,HashMap<String,String> driverHash){
        try{
            Paper.init(context);
            ArrayList<HashMap<String,String>> driverslist;
            driverslist=Paper.book().read("drivers");

            if(driverslist!=null)
            {
                if(driverslist.size()==2) {

                    driverslist.remove(1);
                    driverslist.add(driverHash);
                    Paper.book().write("drivers", driverslist);
                    return true;
                }

            }

            return false;
        }catch (Exception ex)
        {
            return false;
        }
    }

}
