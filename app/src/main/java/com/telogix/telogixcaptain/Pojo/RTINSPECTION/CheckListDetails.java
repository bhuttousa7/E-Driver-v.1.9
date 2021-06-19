package com.telogix.telogixcaptain.Pojo.RTINSPECTION;

public class CheckListDetails {

    private Integer inspectionID;
    private String inspectionTitle;
    private String inspectionTitleUr;
    private String condition;
    private Boolean isChecked;
    private Object detail;
    private Object voiceNoteUrl;
    private Object imageUrl;

    public Integer getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(Integer inspectionID) {
        this.inspectionID = inspectionID;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

    public Object getVoiceNoteUrl() {
        return voiceNoteUrl;
    }

    public void setVoiceNoteUrl(Object voiceNoteUrl) {
        this.voiceNoteUrl = voiceNoteUrl;
    }

    public Object getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }


}