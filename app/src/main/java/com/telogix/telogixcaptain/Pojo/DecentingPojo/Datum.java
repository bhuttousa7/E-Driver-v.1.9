
package com.telogix.telogixcaptain.Pojo.DecentingPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum {
    public Integer getLoadDecantingSiteID() {
        return LoadDecantingSiteID;
    }

    public void setLoadDecantingSiteID(Integer loadDecantingSiteID) {
        LoadDecantingSiteID = loadDecantingSiteID;
    }

    public Integer getLoadCommodityID() {
        return LoadCommodityID;
    }

    public void setLoadCommodityID(Integer loadCommodityID) {
        LoadCommodityID = loadCommodityID;
    }

    public Integer getCommodityID() {
        return CommodityID;
    }

    public void setCommodityID(Integer commodityID) {
        CommodityID = commodityID;
    }

    public String getCommodity() {
        return Commodity;
    }

    public void setCommodity(String commodity) {
        Commodity = commodity;
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

    private Integer LoadCommodityID;
    private Integer CommodityID;
    private String Commodity;
    private String CompartmentNo;
    private String CommodityLoad;



    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    private String ImageUrl="";

    public ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> commoditieslist() {
        return commoditieslist;
    }

    public void setcommoditieslist(ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> datumArrayList) {
        this.commoditieslist = datumArrayList;
    }
    public List<DecantStaffPojo> getDecantStaffList() {
        return decantStaffList;
    }

    public void setDecantStaffList(List<DecantStaffPojo> decantStaffList) {
        this.decantStaffList = decantStaffList;
    }

    private List<DecantStaffPojo> decantStaffList = null;
    private ArrayList<com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum> commoditieslist=new ArrayList<>();
    @SerializedName("LoadDecantingSiteID")
    @Expose
    private Integer LoadDecantingSiteID;
    @SerializedName("DecantingSiteID")
    @Expose
    private Integer decantingSiteID;
    @SerializedName("DecantingSiteName")
    @Expose
    private String decantingSiteName;
    @SerializedName("Detail")
    @Expose
    private String Detail="";
    @SerializedName("DecantingLongitude")
    @Expose
    private double DecantingLongitude;
    @SerializedName("DecantingLatitude")
    @Expose
    private double DecantingLatitude;

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    private boolean isNotified=false;

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public double getDecantingLongitude() {
        return DecantingLongitude;
    }

    public void setDecantingLongitude(double decantingLongitude) {
        DecantingLongitude = decantingLongitude;
    }

    public double getDecantingLatitude() {
        return DecantingLatitude;
    }

    public void setDecantingLatitude(double decantingLatitude) {
        DecantingLatitude = decantingLatitude;
    }

    public Integer getDecantingSiteID() {
        return decantingSiteID;
    }

    public void setDecantingSiteID(Integer decantingSiteID) {
        this.decantingSiteID = decantingSiteID;
    }

    public String getDecantingSite() {
        return decantingSiteName;
    }

    public void setDecantingSite(String decantingSite) {
        this.decantingSiteName = decantingSite;
    }

}
