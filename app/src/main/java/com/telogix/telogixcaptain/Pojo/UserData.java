package com.telogix.telogixcaptain.Pojo;

import java.io.Serializable;

public class UserData implements Serializable {

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserRoles() {
        return UserRoles;
    }

    public void setUserRoles(String userRoles) {
        UserRoles = userRoles;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String typeID) {
        TypeID = typeID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getHaulierName() {
        return HaulierName;
    }

    public void setHaulierName(String haulierName) {
        HaulierName = haulierName;
    }

    public String getDecantingSiteName() {
        return DecantingSiteName;
    }

    public void setDecantingSiteName(String decantingSiteName) {
        DecantingSiteName = decantingSiteName;
    }

    public int getExcelID() {
        return ExcelID;
    }

    public void setExcelID(int excelID) {
        ExcelID = excelID;
    }

    //               "ID": 2,
//                       "ExcelID": 1002,
//                       "UserName": "dummy",
//                       "Email": "dummy@dummy.com",
//                       "Password": "dummy12345",
//                       "UserRoles": "Retailer",
//                       "TypeID": 265,
//                       "isChecked": true,
//                       "isSaved": false,
//                       "AddOn": "2021-03-12T12:47:00",
//                       "AddBy": null,
//                       "DecantingSiteName": "TANDO MOHAMMAD KHAN FS",
//                       "HaulierName": null
    private int ExcelID;
    private int ID;
    private String UserName;
    private String Email;
    private String Password;
    private String UserRoles;
    private String  TypeID;
    private boolean isChecked;
    private boolean isSaved;
    private String HaulierName;
    private String DecantingSiteName;



}

