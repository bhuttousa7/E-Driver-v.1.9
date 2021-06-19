package com.telogix.telogixcaptain.Interfaces;

import com.android.volley.VolleyError;

import org.json.JSONException;

public interface offline_response_interface {

        void onOfflineResponse(String response) throws JSONException;
        void onOfflineError(VolleyError Error);


}
