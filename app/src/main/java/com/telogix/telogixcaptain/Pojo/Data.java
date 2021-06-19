package com.telogix.telogixcaptain.Pojo;

import java.io.Serializable;

public class Data implements Serializable
{
    private String ExcelID;

    private String CommodityID;

    private String DecantingTime;

    private String VehicleID;

    private String TrailerCode;

    private String DecantingSite;

    private String CompartmentNo;

    private String LoadTime;

    private String PickupLocation;

    private String Commodity;

    private String CommodityLoad;

    private String ID;

    private String DecantingSiteID;

    private String PickupLocationID;


    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }

    private boolean IsChecked;

    public boolean isIsselected() {
        return Isselected;
    }

    public void setIsselected(boolean isselected) {
        Isselected = isselected;
    }

    private boolean Isselected;

    public String getExcelID ()
    {
        return ExcelID;
    }

    public void setExcelID (String ExcelID)
    {
        this.ExcelID = ExcelID;
    }

    public String getCommodityID ()
    {
        return CommodityID;
    }

    public void setCommodityID (String CommodityID)
    {
        this.CommodityID = CommodityID;
    }

    public String getDecantingTime ()
    {
        return DecantingTime;
    }

    public void setDecantingTime (String DecantingTime)
    {
        this.DecantingTime = DecantingTime;
    }

    public String getVehicleID ()
    {
        return VehicleID;
    }

    public void setVehicleID (String VehicleID)
    {
        this.VehicleID = VehicleID;
    }

    public String getTrailerCode ()
    {
        return TrailerCode;
    }

    public void setTrailerCode (String TrailerCode)
    {
        this.TrailerCode = TrailerCode;
    }

    public String getDecantingSite ()
    {
        return DecantingSite;
    }

    public void setDecantingSite (String DecantingSite)
    {
        this.DecantingSite = DecantingSite;
    }

    public String getCompartmentNo ()
    {
        return CompartmentNo;
    }

    public void setCompartmentNo (String CompartmentNo)
    {
        this.CompartmentNo = CompartmentNo;
    }

    public String getLoadTime ()
    {
        return LoadTime;
    }

    public void setLoadTime (String LoadTime)
    {
        this.LoadTime = LoadTime;
    }

    public String getPickupLocation ()
    {
        return PickupLocation;
    }

    public void setPickupLocation (String PickupLocation)
    {
        this.PickupLocation = PickupLocation;
    }

    public String getCommodity ()
    {
        return Commodity;
    }

    public void setCommodity (String Commodity)
    {
        this.Commodity = Commodity;
    }

    public String getCommodityLoad ()
    {
        return CommodityLoad;
    }

    public void setCommodityLoad (String CommodityLoad)
    {
        this.CommodityLoad = CommodityLoad;
    }

    public String getID ()
    {
        return ID;
    }

    public void setID (String ID)
    {
        this.ID = ID;
    }

    public String getDecantingSiteID ()
    {
        return DecantingSiteID;
    }

    public void setDecantingSiteID (String DecantingSiteID)
    {
        this.DecantingSiteID = DecantingSiteID;
    }

    public String getPickupLocationID ()
    {
        return PickupLocationID;
    }

    public void setPickupLocationID (String PickupLocationID)
    {
        this.PickupLocationID = PickupLocationID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ExcelID = "+ExcelID+", CommodityID = "+CommodityID+", DecantingTime = "+DecantingTime+", VehicleID = "+VehicleID+", TrailerCode = "+TrailerCode+", DecantingSite = "+DecantingSite+", CompartmentNo = "+CompartmentNo+", LoadTime = "+LoadTime+", PickupLocation = "+PickupLocation+", Commodity = "+Commodity+", CommodityLoad = "+CommodityLoad+", ID = "+ID+", DecantingSiteID = "+DecantingSiteID+", PickupLocationID = "+PickupLocationID+"]";
    }
}
