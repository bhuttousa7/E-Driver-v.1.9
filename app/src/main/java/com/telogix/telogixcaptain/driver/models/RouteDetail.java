
package com.telogix.telogixcaptain.driver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RouteDetail implements Serializable {

    @SerializedName("RouteDetailID")
    @Expose
    public String routeDetailID;
    @SerializedName("RouteID")
    @Expose
    public String routeID;
    @SerializedName("RouteType")
    @Expose
    public String routeType;
    @SerializedName("RouteTypeID")
    @Expose
    public String routeTypeID;
    @SerializedName("Name")
    @Expose
    public Object name;
    @SerializedName("Longitude")
    @Expose
    public Double longitude;
    @SerializedName("Latitude")
    @Expose
    public Double latitude;
    @SerializedName("OrderNumber")
    @Expose
    public Integer orderNumber;
    @SerializedName("GoogleAddress")
    @Expose
    public Object googleAddress;
    @SerializedName("Detail")
    @Expose
    public Object detail;

}
