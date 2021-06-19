package com.telogix.telogixcaptain.Pojo.ViewCheckList;

public class CheckListPojo {

    public String getInspectionDate() {
        return InspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        InspectionDate = inspectionDate;
    }

    public String getInspectionTitle() {
        return InspectionTitle;
    }

    public void setInspectionTitle(String inspectionTitle) {
        InspectionTitle = inspectionTitle;
    }

    public String getInspectionTitleUr() {
        return InspectionTitleUr;
    }

    public void setInspectionTitleUr(String inspectionTitleUr) {
        InspectionTitleUr = inspectionTitleUr;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getInspectionTypeID() {
        return InspectionTypeID;
    }

    public void setInspectionTypeID(int inspectionTypeID) {
        InspectionTypeID = inspectionTypeID;
    }

    public String getDecantingSiteName() {
        return DecantingSiteName;
    }

    public void setDecantingSiteName(String decantingSiteName) {
        DecantingSiteName = decantingSiteName;
    }

    public String getJourneyManagerName() {
        return JourneyManagerName;
    }

    public void setJourneyManagerName(String journeyManagerName) {
        JourneyManagerName = journeyManagerName;
    }

    private String InspectionDate,InspectionTitle,InspectionTitleUr,Condition,DecantingSiteName,JourneyManagerName;
    private boolean isChecked;
    private int InspectionTypeID;

    public CheckListPojo() {
    }


}
