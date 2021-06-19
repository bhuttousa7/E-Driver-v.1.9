package com.telogix.telogixcaptain.Pojo;

import java.util.List;

public class Hazards {
    public Integer getHazardID() {
        return hazardID;
    }

    public void setHazardID(Integer hazardID) {
        this.hazardID = hazardID;
    }

    public String getHazardName() {
        return hazardName;
    }

    public void setHazardName(String hazardName) {
        this.hazardName = hazardName;
    }

    public String getHazardType() {
        return hazardType;
    }

    public void setHazardType(String hazardType) {
        this.hazardType = hazardType;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

    public List<HazardDetail> getHazardDetails() {
        return hazardDetails;
    }

    public void setHazardDetails(List<HazardDetail> hazardDetails) {
        this.hazardDetails = hazardDetails;
    }

    private Integer hazardID;
    private String hazardName;
    private String hazardType;
    private Object location;
    private Object latitude;
    private Object longitude;
    private Object detail;
    private List<HazardDetail> hazardDetails = null;


}



