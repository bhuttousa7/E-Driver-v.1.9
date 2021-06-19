
package com.telogix.telogixcaptain.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("VehicleID")
    @Expose
    private Integer vehicleID;
    @SerializedName("VehicleType")
    @Expose
    private String vehicleType;
    @SerializedName("VehicleNo")
    @Expose
    private String vehicleNo;
    @SerializedName("HaulierID")
    @Expose
    private Integer haulierID;
    @SerializedName("UserLoginID")
    @Expose
    private Object userLoginID;
    @SerializedName("HaulierName")
    @Expose
    private String haulierName;
    @SerializedName("Capacity")
    @Expose
    private String capacity;
    @SerializedName("Compartment")
    @Expose
    private String compartment;
    @SerializedName("StatusID")
    @Expose
    private Integer statusID;
    @SerializedName("VehicleStatus")
    @Expose
    private String vehicleStatus;
//
@SerializedName("LoadID")
@Expose
private String LoadID;
//
@SerializedName("RouteAssignID")
@Expose
private String RouteAssignID;

    public String getRouteID() {
        return RouteID;
    }

    public void setRouteID(String routeID) {
        RouteID = routeID;
    }

    @SerializedName("RouteID")
    @Expose
    private String RouteID;
    public String getLoadID() {
        return LoadID;
    }

    public void setLoadID(String loadID) {
        LoadID = loadID;
    }

    public String getRouteAssignID() {
        return RouteAssignID;
    }

    public void setRouteAssignID(String routeAssignID) {
        RouteAssignID = routeAssignID;
    }

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

    public Integer getHaulierID() {
        return haulierID;
    }

    public void setHaulierID(Integer haulierID) {
        this.haulierID = haulierID;
    }

    public Object getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(Object userLoginID) {
        this.userLoginID = userLoginID;
    }

    public String getHaulierName() {
        return haulierName;
    }

    public void setHaulierName(String haulierName) {
        this.haulierName = haulierName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCompartment() {
        return compartment;
    }

    public void setCompartment(String compartment) {
        this.compartment = compartment;
    }

    public Integer getStatusID() {
        return statusID;
    }

    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

}
