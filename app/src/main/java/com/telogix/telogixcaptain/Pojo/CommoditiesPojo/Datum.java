
package com.telogix.telogixcaptain.Pojo.CommoditiesPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("CommodityID")
    @Expose
    private Integer commodityID;
    @SerializedName("CommodityName")
    @Expose
    private String CommodityName;

    public Integer getCommodityID() {
        return commodityID;
    }

    public void setCommodityID(Integer commodityID) {
        this.commodityID = commodityID;
    }

    public String getCommodityName() {
        return CommodityName;
    }

    public void setCommodityName(String commodityName) {
        this.CommodityName = commodityName;
    }

}
