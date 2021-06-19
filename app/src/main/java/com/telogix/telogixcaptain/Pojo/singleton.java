package com.telogix.telogixcaptain.Pojo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.telogix.telogixcaptain.R;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Created by hp on 3/6/2017.
 */

public class singleton {
private static singleton s=new singleton();
   private static RequestQueue requestQueue;
    static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;




    public  static SharedPreferences getsharedpreference(Context context) {
        if(sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences("loggedin", Context.MODE_APPEND);
            return sharedPreferences;
        }
        return sharedPreferences;
    }
    public  static SharedPreferences.Editor getsharedpreference_editor(Context context) {
        if(editor==null) {
            if (sharedPreferences != null) {
                editor = sharedPreferences.edit();
                return editor;
            } else
            {
                getsharedpreference(context);
                return getsharedpreference_editor(context);
            }

        }
        return editor;
    }

    public static singleton getinstance(Context context)
    {
if (s==null)
{
    s=new singleton();
    requestQueue=getrequestqueue(context);
    return s;
}
        return  s;
    }

    public  static RequestQueue getrequestqueue(Context context) {
     if(requestQueue==null) {
         requestQueue = Volley.newRequestQueue(context);
         Log.d("request",requestQueue+"");
     return requestQueue;
     }
        return requestQueue;
    }


    public void dosomething()
{

}




    }
