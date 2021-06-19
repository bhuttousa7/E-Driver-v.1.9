
package com.telogix.telogixcaptain.Pojo.pickupPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("PickupLocationID")
    @Expose
    private Integer pickupLocationID;
    @SerializedName("PickupLocationName")
    @Expose
    private String pickupLocationName;
    @SerializedName("Detail")
    @Expose
    private String Detail="";
    @SerializedName("PickupLongitude")
    @Expose
    private double PickupLongitude;
    @SerializedName("PickupLatitude")
    @Expose
    private double PickupLatitude;

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public double getPickupLongitude() {
        return PickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        PickupLongitude = pickupLongitude;
    }

    public double getPickupLatitude() {
        return PickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        PickupLatitude = pickupLatitude;
    }

    public Integer getPickupLocationID() {
        return pickupLocationID;
    }

    public void setPickupLocationID(Integer pickupLocationID) {
        this.pickupLocationID = pickupLocationID;
    }

    public String getPickupLocation() {
        return pickupLocationName;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocationName = pickupLocation;
    }

}
