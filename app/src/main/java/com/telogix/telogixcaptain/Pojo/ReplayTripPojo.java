
package com.telogix.telogixcaptain.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplayTripPojo {


    public int getRouteAssignID() {
        return RouteAssignID;
    }

    public void setRouteAssignID(int routeAssignID) {
        RouteAssignID = routeAssignID;
    }

    public Integer getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(Integer vehicleID) {
        this.vehicleID = vehicleID;
    }

    public Integer getLoadID() { return loadID; }

    public void setLoadID(Integer loadID) { this.loadID = loadID; }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getPickupLocationName() {
        return pickupLocationName;
    }

    public void setPickupLocationName(String pickupLocationName) {
        this.pickupLocationName = pickupLocationName;
    }

    public String getLastDecantingSiteName() {
        return lastDecantingSiteName;
    }

    public void setLastDecantingSiteName(String lastDecantingSiteName) {
        this.lastDecantingSiteName = lastDecantingSiteName;
    }

    public boolean isLoadDecanted() {
        return isLoadDecanted;
    }

    public void setLoadDecanted(boolean loadDecanted) {
        isLoadDecanted = loadDecanted;
    }

    @SerializedName("RouteAssignID")
    @Expose
    private int RouteAssignID;
    @SerializedName("VehicleID")
    @Expose
    private Integer vehicleID;
    @SerializedName("LoadTime")
    @Expose
    private String loadTime;
    @SerializedName("LoadID")
    @Expose
    private Integer loadID;
    @SerializedName("RouteName")
    @Expose
    private String routeName;
    @SerializedName("PickupLocationName")
    @Expose
    private String pickupLocationName;
    @SerializedName("LastDecantingSiteName")
    @Expose
    private String lastDecantingSiteName;
    @SerializedName("IsLoadDecanted")
    @Expose
    private boolean isLoadDecanted;



}
