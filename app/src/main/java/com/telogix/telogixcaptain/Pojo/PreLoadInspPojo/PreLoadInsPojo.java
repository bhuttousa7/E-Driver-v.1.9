package com.telogix.telogixcaptain.Pojo.PreLoadInspPojo;

import java.util.HashMap;

public class PreLoadInsPojo {
    private Integer inspectionListID;
    private Integer inspectionTypeID;
    private String inspectionType;
    private String inspectionTitle;
    private String inspectionTitleUr;
    private Boolean isRequired;
    private Integer orderNumber;
    private Integer priorityNumber;
    private boolean NoSelected=false;
    private boolean YesSelected=false;
    private boolean ignored=true;

    public HashMap getInspectionDetails() {
        return InspectionDetails;
    }

    public void setInspectionDetails(HashMap inspectionDetails) {
        InspectionDetails = inspectionDetails;
    }

    private HashMap InspectionDetails=new HashMap();
    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }


    public boolean isNoSelected() {
        return NoSelected;
    }

    public void setNoSelected(boolean noSelected) {
        NoSelected = noSelected;
    }

    public boolean isYesSelected() {
        return YesSelected;
    }

    public void setYesSelected(boolean yesSelected) {
        YesSelected = yesSelected;
    }

    //    public boolean isIsselected() {
//        return Isselected;
//    }
//
//    public void setIsselected(boolean isselected) {
//        Isselected = isselected;
//    }
//
//    private boolean Isselected=false;



    public Integer getInspectionListID() {
        return inspectionListID;
    }

    public void setInspectionListID(Integer inspectionListID) {
        this.inspectionListID = inspectionListID;
    }

    public Integer getInspectionTypeID() {
        return inspectionTypeID;
    }

    public void setInspectionTypeID(Integer inspectionTypeID) {
        this.inspectionTypeID = inspectionTypeID;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getInspectionTitle() {
        return inspectionTitle;
    }

    public void setInspectionTitle(String inspectionTitle) {
        this.inspectionTitle = inspectionTitle;
    }

    public String getInspectionTitleUr() {
        return inspectionTitleUr;
    }

    public void setInspectionTitleUr(String inspectionTitleUr) {
        this.inspectionTitleUr = inspectionTitleUr;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }


}

