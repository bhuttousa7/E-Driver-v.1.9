
package com.telogix.telogixcaptain.driver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.telogix.telogixcaptain.Pojo.RouteDetail;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    public Integer getLoadID() {
        return LoadID;
    }

    public void setLoadID(Integer loadID) {
        LoadID = loadID;
    }

    public Integer getRouteAssignID() {
        return RouteAssignID;
    }

    public void setRouteAssignID(Integer routeAssignID) {
        RouteAssignID = routeAssignID;
    }

    public boolean isCheckListApproved() {
        return IsCheckListApproved;
    }

    public void setCheckListApproved(boolean checkListApproved) {
        IsCheckListApproved = checkListApproved;
    }

    @SerializedName("IsCheckListApproved")
    @Expose
    public boolean IsCheckListApproved;
    @SerializedName("RouteAssignID")
    @Expose
    public Integer RouteAssignID;
    @SerializedName("LoadID")
    @Expose
    public Integer LoadID;
    @SerializedName("VehicleID")
    @Expose
    public Integer vehicleID;
    @SerializedName("VehicleNo")
    @Expose
    public String vehicleNo;
    @SerializedName("StatusID")
    @Expose
    public Integer statusID;
    @SerializedName("VehicleStatus")
    @Expose
    public String vehicleStatus;
    @SerializedName("RouteID")
    @Expose
    public String routeID;
    @SerializedName("RouteName")
    @Expose
    public String routeName;
    @SerializedName("RouteAddress")
    @Expose
    public String routeAddress;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("IsActive")
    @Expose
    public Object isActive;
    @SerializedName("OriginLong")
    @Expose
    public Double originLong;
    @SerializedName("OriginLat")
    @Expose
    public Double originLat;
    @SerializedName("DestinationLong")
    @Expose
    public Double destinationLong;
    @SerializedName("DestinationLat")
    @Expose
    public Double destinationLat;
    @SerializedName("RouteDetails")
    @Expose
    public List<RouteDetail> routeDetails = null;

}
