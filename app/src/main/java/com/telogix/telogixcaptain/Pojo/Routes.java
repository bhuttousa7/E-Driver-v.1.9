package com.telogix.telogixcaptain.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class Routes implements Serializable {
    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteAddress() {
        return routeAddress;
    }

    public void setRouteAddress(String routeAddress) {
        this.routeAddress = routeAddress;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getIsActive() {
        return isActive;
    }

    public void setIsActive(Object isActive) {
        this.isActive = isActive;
    }

    public Double getOriginLong() {
        return originLong;
    }

    public void setOriginLong(Double originLong) {
        this.originLong = originLong;
    }

    public Double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    public Double getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(Double destinationLong) {
        this.destinationLong = destinationLong;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public ArrayList<RouteDetail> getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(ArrayList<RouteDetail> routeDetails) {
        this.routeDetails = routeDetails;
    }

    private String routeID;
    private String routeName;
    private String routeAddress;
    private Object description;
    private Object isActive;
    private Double originLong;
    private Double originLat;
    private Double destinationLong;
    private Double destinationLat;
    private String pickupLocationName;
    private String lastDecatingSiteName;


    private ArrayList<RouteDetail> routeDetails = null;


    public String getPickupLocationName() {
        return pickupLocationName;
    }

    public void setPickupLocationName(String pickupLocationName) {
        this.pickupLocationName = pickupLocationName;
    }

    public String getLastDecatingSiteName() {
        return lastDecatingSiteName;
    }

    public void setLastDecatingSiteName(String lastDecatingSiteName) {
        this.lastDecatingSiteName = lastDecatingSiteName;
    }
}
