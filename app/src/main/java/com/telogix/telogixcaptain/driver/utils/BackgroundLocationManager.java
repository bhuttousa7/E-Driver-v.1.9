package com.telogix.telogixcaptain.driver.utils;


import android.content.Context;
import android.content.Intent;

public class BackgroundLocationManager {

    private Intent intent;

    private BackgroundLocationManager() {}

    private static class LazyHolder {
        private static final BackgroundLocationManager instance = new BackgroundLocationManager();
    }

    public static BackgroundLocationManager getInstance() {
        return LazyHolder.instance;
    }

    public void startService(Context context) {
        if (intent == null) {
            intent = new Intent(context, FusedLocationService.class);
        }
        context.startService(intent);
    }

    public void stopService(Context context) {
        if (intent != null) {
            context.stopService(intent);
        }
    }
}