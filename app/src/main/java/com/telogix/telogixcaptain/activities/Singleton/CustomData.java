package com.telogix.telogixcaptain.activities.Singleton;

import com.telogix.telogixcaptain.Pojo.Datum;


public class CustomData {

    public Datum vehicleDetails;


    private static final CustomData ourInstance = new CustomData();

    public static CustomData getInstance() {
        return ourInstance;
    }

    private CustomData() {
    }
}
