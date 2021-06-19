
package com.telogix.telogixcaptain.driver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DriverVehicleStatusResponse implements Serializable {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @SerializedName("Message")
    @Expose
    public String message;
    @SerializedName("ResponseCode")
    @Expose
    public Integer responseCode;
    @SerializedName("Status")
    @Expose
    public Boolean status;
    @SerializedName("Data")
    @Expose
    public Data data;

}
