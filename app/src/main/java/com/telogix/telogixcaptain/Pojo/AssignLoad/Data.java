
package com.telogix.telogixcaptain.Pojo.AssignLoad;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("LoadID")
    @Expose
    private Integer loadID;
    @SerializedName("VehicleID")
    @Expose
    private Integer vehicleID;
    @SerializedName("VehicleNo")
    @Expose
    private String vehicleNo;
    @SerializedName("PickUpLocationID")
    @Expose
    private Integer pickUpLocationID;
    @SerializedName("PickupLocation")
    @Expose
    private String pickupLocation;
    @SerializedName("DecantingSiteID")
    @Expose
    private Integer decantingSiteID;
    @SerializedName("DecantingSite")
    @Expose
    private String decantingSite;
    @SerializedName("LoadTime")
    @Expose
    private Object loadTime;
    @SerializedName("DecantingTime")
    @Expose
    private String decantingTime;

    public Integer getLoadID() {
        return loadID;
    }

    public void setLoadID(Integer loadID) {
        this.loadID = loadID;
    }

    public Integer getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(Integer vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Integer getPickUpLocationID() {
        return pickUpLocationID;
    }

    public void setPickUpLocationID(Integer pickUpLocationID) {
        this.pickUpLocationID = pickUpLocationID;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Integer getDecantingSiteID() {
        return decantingSiteID;
    }

    public void setDecantingSiteID(Integer decantingSiteID) {
        this.decantingSiteID = decantingSiteID;
    }

    public String getDecantingSite() {
        return decantingSite;
    }

    public void setDecantingSite(String decantingSite) {
        this.decantingSite = decantingSite;
    }

    public Object getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Object loadTime) {
        this.loadTime = loadTime;
    }

    public String getDecantingTime() {
        return decantingTime;
    }

    public void setDecantingTime(String decantingTime) {
        this.decantingTime = decantingTime;
    }

}
