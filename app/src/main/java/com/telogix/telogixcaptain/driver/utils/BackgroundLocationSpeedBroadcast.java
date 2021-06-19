package com.telogix.telogixcaptain.driver.utils;


import android.content.Context;
import android.content.Intent;

public class BackgroundLocationSpeedBroadcast {

    private Intent intent;

    private BackgroundLocationSpeedBroadcast() {}

    private static class LazyHolder {
        private static final BackgroundLocationSpeedBroadcast instance = new BackgroundLocationSpeedBroadcast();
    }

    public static BackgroundLocationSpeedBroadcast getInstance() {
        return LazyHolder.instance;
    }

    public void startService(Context context) {
        if (intent == null) {
            intent = new Intent(context, SpeedCheckerBroadcast.class);
        }
        context.startService(intent);
    }

    public void stopService(Context context) {
        if (intent != null) {
            context.stopService(intent);
        }
    }
}