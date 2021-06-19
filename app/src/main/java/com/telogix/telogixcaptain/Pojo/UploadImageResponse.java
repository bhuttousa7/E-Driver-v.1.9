package com.telogix.telogixcaptain.Pojo;

import com.google.gson.annotations.SerializedName;

public class UploadImageResponse {

        @SerializedName("Parameter")
         String parameter;
        @SerializedName("FileLink")
        public String fileLink;

        public String getVoiceNoteUrl() {
            return VoiceNoteUrl;
        }

        public void setVoiceNoteUrl(String voiceNoteUrl) {
            VoiceNoteUrl = voiceNoteUrl;
        }

        private String VoiceNoteUrl = "";

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public String getFileLink() {
            return fileLink;
        }

        public void setFileLink(String fileLink) {
            this.fileLink = fileLink;
        }

}
