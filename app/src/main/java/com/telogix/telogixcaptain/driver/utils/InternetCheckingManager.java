package com.telogix.telogixcaptain.driver.utils;

import android.content.Context;
import android.content.Intent;

import com.telogix.telogixcaptain.Utils.InternetService;

public class InternetCheckingManager {

    private static Intent intent;

    private InternetCheckingManager() {}

    private static class LazyHolder {
        private static final InternetCheckingManager instance = new InternetCheckingManager();
    }

    public static InternetCheckingManager getInstance() {
        return LazyHolder.instance;
    }

    public void startService(Context context) {
        if (intent == null) {
            intent = new Intent(context, InternetService.class);
            intent.putExtra(InternetService.TAG_INTERVAL, 10);
            // URL to ping
            intent.putExtra(InternetService.TAG_URL_PING, "http://www.google.com");
            // Name of the class that is calling this service
            intent.putExtra(InternetService.TAG_ACTIVITY_NAME, this.getClass().getName());
        }
        context.startService(intent);
    }

    public void stopService(Context context) {
        if (intent != null) {
            context.stopService(intent);
        }
    }
}