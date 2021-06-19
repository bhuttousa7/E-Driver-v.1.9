package com.telogix.telogixcaptain.driver;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class Constant {
    String android_id;

    public Constant(Context context){
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("AndroidID",android_id);

    }
    public String getAndroid_id() {
        return android_id;
    }


}
