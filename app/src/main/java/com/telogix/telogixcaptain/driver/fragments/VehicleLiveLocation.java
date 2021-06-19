package com.telogix.telogixcaptain.driver.fragments;

import java.io.Serializable;

class VehicleLiveLocation implements Serializable {
    String latlng;
    String vehicleNo;
    String vehicleName;
    String bearing;
    String time;
    String deviceId;
    String haulierID;

    public float getSpeedinKph() {
        return speedinKph;
    }

    public void setSpeedinKph(float speedinKph) {
        this.speedinKph = speedinKph;
    }

    float speedinKph;

    public String getHaulierID() {
        return haulierID;
    }

    public void setHaulierID(String haulierID) {
        this.haulierID = haulierID;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setTime(String time){this.time = time;}
    public String getTime(){ return time;}

}
