package com.telogix.telogixcaptain.Pojo.ViewStop;

import com.google.android.gms.maps.model.LatLng;

public class StopPojo {
    public String getStopTitle() {
        return stopTitle;
    }

    public void setStopTitle(String stopTitle) {
        this.stopTitle = stopTitle;
    }



    public String getStopDuration() {
        return stopDuration;
    }

    public void setStopDuration(String stopDuration) {
        this.stopDuration = stopDuration;
    }

    public String getStopDescription() {
        return stopDescription;
    }

    public void setStopDescription(String stopDescription) {
        this.stopDescription = stopDescription;
    }

    public LatLng getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(LatLng stopPosition) {
        this.stopPosition = stopPosition;
    }

    public String getStopTime() {
        return StopTime;
    }

    public void setStopTime(String stopTime) {
        StopTime = stopTime;
    }


    private String stopDuration;
    private String stopDescription;
    private LatLng stopPosition;
    private String stopTitle;
    private String StopTime;


}
