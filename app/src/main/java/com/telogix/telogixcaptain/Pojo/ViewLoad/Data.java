package com.telogix.telogixcaptain.Pojo.ViewLoad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private Integer loadID;
    private String pickupLocation;
    private Integer pickupLocationID;
    private String company;
    private Integer companyID;
    private String vehicle;
    private Integer vehicleID;
    private String loadTime;
    private List<LoadDecantingSite> loadDecantingSites = null;
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getLoadID() {
        return loadID;
    }

    public void setLoadID(Integer loadID) {
        this.loadID = loadID;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Integer getPickupLocationID() {
        return pickupLocationID;
    }

    public void setPickupLocationID(Integer pickupLocationID) {
        this.pickupLocationID = pickupLocationID;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public Integer getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(Integer vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public List<LoadDecantingSite> getLoadDecantingSites() {
        return loadDecantingSites;
    }

    public void setLoadDecantingSites(List<LoadDecantingSite> loadDecantingSites) {
        this.loadDecantingSites = loadDecantingSites;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}