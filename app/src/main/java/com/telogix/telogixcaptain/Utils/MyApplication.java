package com.telogix.telogixcaptain.Utils;

import android.app.Application;

import com.onesignal.OneSignal;

import io.paperdb.Paper;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        OneSignal.startInit(this)

                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
               .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }


}
