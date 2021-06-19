package com.telogix.telogixcaptain.Pojo.ViewLoad;

import java.util.HashMap;
import java.util.Map;

public class LoadCommodity {

    private Integer loadDecantingSiteID;
    private Integer loadCommodityID;
    private Integer commodityID;
    private String commodity;
    private String compartmentNo;
    private Object commodityLoad;
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getLoadDecantingSiteID() {
        return loadDecantingSiteID;
    }

    public void setLoadDecantingSiteID(Integer loadDecantingSiteID) {
        this.loadDecantingSiteID = loadDecantingSiteID;
    }

    public Integer getLoadCommodityID() {
        return loadCommodityID;
    }

    public void setLoadCommodityID(Integer loadCommodityID) {
        this.loadCommodityID = loadCommodityID;
    }

    public Integer getCommodityID() {
        return commodityID;
    }

    public void setCommodityID(Integer commodityID) {
        this.commodityID = commodityID;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCompartmentNo() {
        return compartmentNo;
    }

    public void setCompartmentNo(String compartmentNo) {
        this.compartmentNo = compartmentNo;
    }

    public Object getCommodityLoad() {
        return commodityLoad;
    }

    public void setCommodityLoad(Object commodityLoad) {
        this.commodityLoad = commodityLoad;
    }


}