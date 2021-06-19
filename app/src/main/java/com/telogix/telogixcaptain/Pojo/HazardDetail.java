package com.telogix.telogixcaptain.Pojo;

public class HazardDetail {
    public Integer getHazardDetailID() {
        return hazardDetailID;
    }

    public void setHazardDetailID(Integer hazardDetailID) {
        this.hazardDetailID = hazardDetailID;
    }

    public Integer getHazardID() {
        return hazardID;
    }

    public void setHazardID(Integer hazardID) {
        this.hazardID = hazardID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoiceNoteUrl() {
        return voiceNoteUrl;
    }

    public void setVoiceNoteUrl(String voiceNoteUrl) {
        this.voiceNoteUrl = voiceNoteUrl;
    }

    private Integer hazardDetailID;
    private Integer hazardID;
    private String imageUrl;
    private String voiceNoteUrl;
}
