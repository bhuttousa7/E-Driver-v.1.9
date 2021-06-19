
package com.telogix.telogixcaptain.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenPojo implements Parcelable {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("haulierID")
    @Expose
    private String haulierID;
    @SerializedName("roleID")
    @Expose
    private String roleID;
    @SerializedName(".issued")
    @Expose
    private String issued;
    @SerializedName(".expires")
    @Expose
    private String expires;

    protected TokenPojo(Parcel in) {
        accessToken = in.readString();
        tokenType = in.readString();
        if (in.readByte() == 0) {
            expiresIn = null;
        } else {
            expiresIn = in.readInt();
        }
        userName = in.readString();
        haulierID = in.readString();
        roleID = in.readString();
        issued = in.readString();
        expires = in.readString();
    }

    public static final Creator<TokenPojo> CREATOR = new Creator<TokenPojo>() {
        @Override
        public TokenPojo createFromParcel(Parcel in) {
            return new TokenPojo(in);
        }

        @Override
        public TokenPojo[] newArray(int size) {
            return new TokenPojo[size];
        }
    };

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getHaulierID() {
        return haulierID;
    }

    public void setHaulierID(String roleID) {
        this.haulierID = haulierID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accessToken);
        parcel.writeString(tokenType);
        parcel.writeInt(expiresIn);
        parcel.writeString(userName);
        parcel.writeString(haulierID);
        parcel.writeString(roleID);
        parcel.writeString(issued);
        parcel.writeString(expires);

    }
}
