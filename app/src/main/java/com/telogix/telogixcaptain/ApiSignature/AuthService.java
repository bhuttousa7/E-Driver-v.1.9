package com.telogix.telogixcaptain.ApiSignature;

import com.telogix.telogixcaptain.Pojo.AssignLoad.AssignLoadPojo;
import com.telogix.telogixcaptain.Pojo.AvailableVehicles;
import com.telogix.telogixcaptain.Pojo.CommoditiesPojo.CommoditiesPojo;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.DecantingSitesPojo;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.pickupPojo.PickupLocationPojo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {

    @FormUrlEncoded
    @POST("token")
    Call<TokenPojo> getToken(@Field("grant_type") String type, @Field("UserName") String UserName,
                             @Field("Password") String Password);

    @FormUrlEncoded
    @POST("api/LoadAssigns/AssignLoad")
    Call<AssignLoadPojo> AssignLoad(@Field("CommodityID") String CommodityID, @Field("DecantingSiteID") String DecantingSiteID,
                                    @Field("DEcantingTIme") String decentingTime, @Field("LoadTime") String LoadTime,
                                    @Field("PickupLocationID") String PickupLocationID, @Field("VehicleID") String VehicleID);

    @GET("api/Vehicles/GetAllVehicles")
    Call<AvailableVehicles> getVehicles();


    @GET("api/PickupLocations/GetPickupLocationsForDDL")
    Call<PickupLocationPojo> getPickupLocationLov();

    @GET("api/Commodities/GetCommodityForDDL")
    Call<CommoditiesPojo> getCommodityLov();

    @GET("api/DecantingSites/GetDecantingSiteForDDL")
    Call<DecantingSitesPojo> getDecantingSites();

}
