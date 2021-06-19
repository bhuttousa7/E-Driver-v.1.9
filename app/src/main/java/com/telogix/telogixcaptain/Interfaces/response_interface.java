package com.telogix.telogixcaptain.Interfaces;

import com.android.volley.VolleyError;

import org.json.JSONException;

/**
 * Created by TALHA on 19-Dec-16.
 */

public interface response_interface {

    void onResponse(String response) throws JSONException;
    void onError(VolleyError Error);

}
