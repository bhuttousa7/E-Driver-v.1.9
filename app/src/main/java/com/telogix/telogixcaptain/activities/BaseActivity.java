package com.telogix.telogixcaptain.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity implements response_interface {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                HashMap<String,String> data=new HashMap<>();
                data.put("vErrorMsg",""+paramThrowable.getStackTrace()[2]);
                httpvolley.stringrequestpost("api/VehicleRides/SaveError", Request.Method.POST,data, BaseActivity.this);

            }
        });

    }
    protected abstract int getLayoutResourceId();
}

