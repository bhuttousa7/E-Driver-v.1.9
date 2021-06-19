package com.telogix.telogixcaptain.Pojo;

public class UsersExcelPojo {



    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public UserData[] getData() {
        return Data;
    }

    public void setData(UserData[] data) {
        Data = data;
    }

    private UserData[] Data;
    private String Message,Status;
}
