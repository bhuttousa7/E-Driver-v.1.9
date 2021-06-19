package com.telogix.telogixcaptain.Pojo.Retailer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("VehicleID")
    @Expose
    private Integer vehicleID;

    public Integer getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(Integer vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDriver() {
        return Driver;
    }

    public void setDriver(String driver) {
        Driver = driver;
    }

    public String getLoadDecantingSiteID() {
        return LoadDecantingSiteID;
    }

    public void setLoadDecantingSiteID(String loadDecantingSiteID) {
        LoadDecantingSiteID = loadDecantingSiteID;
    }

    public String getCommodityName() {
        return CommodityName;
    }

    public void setCommodityName(String commodityName) {
        CommodityName = commodityName;
    }

    public String getCompartmentNo() {
        return CompartmentNo;
    }

    public void setCompartmentNo(String compartmentNo) {
        CompartmentNo = compartmentNo;
    }

    public String getCommodityLoad() {
        return CommodityLoad;
    }

    public void setCommodityLoad(String commodityLoad) {
        CommodityLoad = commodityLoad;
    }

    public String getDecantedTime() {
        return DecantedTime;
    }

    public void setDecantedTime(String decantedTime) {
        DecantedTime = decantedTime;
    }

    public String getDriverRating() {
        return DriverRating;
    }

    public void setDriverRating(String driverRating) {
        DriverRating = driverRating;
    }

    public String getDriverReviews() {
        return DriverReviews;
    }

    public void setDriverReviews(String driverReviews) {
        DriverReviews = driverReviews;
    }

    @SerializedName("VehicleType")
    @Expose
    private String vehicleType;
    @SerializedName("VehicleNo")
    @Expose
    private String vehicleNo;
    @SerializedName("Driver")
    @Expose
    private String Driver;
    @SerializedName("LoadDecantingSiteID")
    @Expose
    private String LoadDecantingSiteID;
    @SerializedName("CommodityName")
    @Expose
    private String CommodityName;
    @SerializedName("CompartmentNo")
    @Expose
    private String CompartmentNo;
    @SerializedName("CommodityLoad")
    @Expose
    private String CommodityLoad;
    @SerializedName("DecantedTime")
    @Expose
    private String DecantedTime;
    @SerializedName("DriverRating")
    @Expose
    private String DriverRating;
    @SerializedName("DriverReviews")
    @Expose
    private String DriverReviews;
}