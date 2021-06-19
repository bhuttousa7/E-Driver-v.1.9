package com.telogix.telogixcaptain.activities.Fragments;

import android.content.Context;

import com.telogix.telogixcaptain.Utils.connection;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = connection.Baseurl;

//    private static final String BASE_URL = connection.Baseurl;

   // private static final String BASE_URL = "http://telogix.mangotechsoftwares.com";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
