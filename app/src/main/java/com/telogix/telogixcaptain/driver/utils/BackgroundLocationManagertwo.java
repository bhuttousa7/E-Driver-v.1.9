package com.telogix.telogixcaptain.driver.utils;

import android.content.Context;
import android.content.Intent;

public class BackgroundLocationManagertwo {


    private Intent intent;

    private BackgroundLocationManagertwo() {}

    private static class LazyHolder {
        private static final BackgroundLocationManagertwo instance = new BackgroundLocationManagertwo();
    }

    public static BackgroundLocationManagertwo getInstance() {
        return LazyHolder.instance;
    }

    public void startService(Context context) {
        if (intent == null) {
            intent = new Intent(context, FusedLocationServiceAllTime.class);
        }
        context.startService(intent);
    }

    public void stopService(Context context) {
        if (intent != null) {
            context.stopService(intent);
        }
    }
}