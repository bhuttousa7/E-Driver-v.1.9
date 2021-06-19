package com.telogix.telogixcaptain.Pojo.ViewLoad;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadDecantingSite {

    private Integer loadID;
    private Integer loadDecantingSiteID;
    private Integer decantingSiteID;
    private String decantingSite;
    private String decantingTime;
    private List<LoadCommodity> loadCommodities = null;
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getLoadID() {
        return loadID;
    }

    public void setLoadID(Integer loadID) {
        this.loadID = loadID;
    }

    public Integer getLoadDecantingSiteID() {
        return loadDecantingSiteID;
    }

    public void setLoadDecantingSiteID(Integer loadDecantingSiteID) {
        this.loadDecantingSiteID = loadDecantingSiteID;
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

    public String getDecantingTime() {
        return decantingTime;
    }

    public void setDecantingTime(String decantingTime) {
        this.decantingTime = decantingTime;
    }

    public List<LoadCommodity> getLoadCommodities() {
        return loadCommodities;
    }

    public void setLoadCommodities(List<LoadCommodity> loadCommodities) {
        this.loadCommodities = loadCommodities;
    }


}