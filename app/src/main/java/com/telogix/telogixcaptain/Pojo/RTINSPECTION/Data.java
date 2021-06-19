package com.telogix.telogixcaptain.Pojo.RTINSPECTION;

import java.util.List;

public class Data {

    private Integer checkID;
    private Integer vehicleID;
    private String vehicleNo;
    private String inspectionDate;
    private Boolean isApproved;
    private List<CheckListDetails> checkListDetails = null;

    public Integer getCheckID() {
        return checkID;
    }

    public void setCheckID(Integer checkID) {
        this.checkID = checkID;
    }

    public Integer getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(Integer vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public List<CheckListDetails> getCheckListDetails() {
        return checkListDetails;
    }

    public void setCheckListDetails(List<CheckListDetails> checkListDetails) {
        this.checkListDetails = checkListDetails;
    }


}