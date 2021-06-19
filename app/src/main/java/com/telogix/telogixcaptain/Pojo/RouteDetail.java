package com.telogix.telogixcaptain.Pojo;

import java.io.Serializable;

public class RouteDetail implements Serializable {
    private String routeDetailID;
    private String routeID;
    private String routeType;
    private String name;
    private Double longitude;
    private Double latitude;
    private Integer orderNumber;
    private String googleAddress;
    private Object detail;
    private String RouteTypeID;
    public String getRouteTypeID() {
        return RouteTypeID;
    }
private boolean notified=false;
    public void setRouteTypeID(String routeTypeID) {
        RouteTypeID = routeTypeID;
    }



    public String getRouteDetailID() {
        return routeDetailID;
    }

    public void setRouteDetailID(String routeDetailID) {
        this.routeDetailID = routeDetailID;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
