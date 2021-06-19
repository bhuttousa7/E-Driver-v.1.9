package com.telogix.telogixcaptain.driver.utils;

import com.telogix.telogixcaptain.driver.models.DriverVehicleStatusResponse;

import retrofit2.Call;
import retrofit2.http.Header;

public interface IWebInterface {
    Call<DriverVehicleStatusResponse> getVehicleStatusForDriver(@Header("Authorization") String token);

}
