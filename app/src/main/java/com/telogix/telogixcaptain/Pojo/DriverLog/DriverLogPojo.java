package com.telogix.telogixcaptain.Pojo.DriverLog;

import com.google.android.gms.maps.model.LatLng;

public class DriverLogPojo {

    private String userName;
    private String stopTypeTitle;
    private String vehicleNo;
    private Object startDate;
    private Object resumeDate;
    private String description;
    private Double Latitude;
    private Double Longitude;
    private Double LocationName;
    private int pos;
    Long stop_duration;

    public void setDriverDetails(int pos, String stopTypeTitle, String vehicleNo, Long stop_duration, String description, LatLng coordinates) {
        this.pos = pos;
        this.stopTypeTitle = stopTypeTitle;
        this.vehicleNo = vehicleNo;
        this.description = description;
        this.stop_duration =stop_duration;
    }


    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLocationName() {
        return LocationName;
    }

    public void setLocationName(Double locationName) {
        LocationName = locationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStopTypeTitle() {
        return stopTypeTitle;
    }

    public void setStopTypeTitle(String stopTypeTitle) {
        this.stopTypeTitle = stopTypeTitle;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Object getStartDate() {
        return startDate;
    }

    public void setStartDate(Object startDate) {
        this.startDate = startDate;
    }

    public Object getResumeDate() {
        return resumeDate;
    }

    public void setResumeDate(Object resumeDate) {
        this.resumeDate = resumeDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}