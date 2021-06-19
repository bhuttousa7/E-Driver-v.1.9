package com.telogix.telogixcaptain.ApiSignature;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.LoginActivity;
import com.telogix.telogixcaptain.activities.MainActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkConsume {

//    public static final String API_ENDPOINT = "http://telogix.mangotechsoftwares.com/";
public static final String API_ENDPOINT = connection.Baseurl+"/";

    private String _accessToken = null;
    private ProgressDialog progressDialog = null;
    private static final String TAG = MainActivity.class.getName();
    private Retrofit _retrofit;
    private Retrofit.Builder _retrofitBuilder = null;
    private final HttpLoggingInterceptor _interceptor;
    private OkHttpClient.Builder _client;
    private final String LATITUDE="Lat";
    private final String LONGITUDE="long";
    private final String POBLATITUDE="PobLat";
    private final String POBLONGITUDE="Poblong";

    public NetworkConsume() {
        _interceptor = new HttpLoggingInterceptor();
        _interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        rebuild();
    }

    public void rebuild() {
        _client = new OkHttpClient.Builder()
                .addInterceptor(_interceptor);

        if (this._accessToken != null) {
            _client.connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS);
            _client.addInterceptor(chain -> {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type","application/json")
                        .addHeader("Authorization", "Bearer " + _accessToken.trim());
                        // <-- this is the important line

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }

        if (_retrofitBuilder == null) {
            _retrofitBuilder = new Retrofit.Builder();
        } else {
            _retrofitBuilder = _retrofit.newBuilder();
        }

        _retrofit = _retrofitBuilder.baseUrl(API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(_client.build())
                .build();
    }
    public static final NetworkConsume getInstance() {
        return SingletonHolder._instance;
    }

    private static final class SingletonHolder {
        protected static final NetworkConsume _instance = new NetworkConsume();
    }

    public AuthService getAuthAPI() {
        return _retrofit.create(AuthService.class);
    }
    public void setAccessKey(String accessToken) {
        this._accessToken = accessToken;

        rebuild();
    }
    public void ShowDialogeY(Context context){

        progressDialog = new ProgressDialog(context, R.style.MyGravity);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();

        progressDialog.setContentView(R.layout.custom_progressdialog);
        progressDialog.setCancelable(true);
    }

    public void ShowProgress(Context context){
        ShowDialogeY(context);
//        progressDialog.show();
    }
    public void hideProgress(){
        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void setBoolean(String key, Boolean value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public String getDefaults(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public String get_accessToken(Context context){
        String token = NetworkConsume.getInstance().getDefaults("token",context);
        return token;
    }
    public Boolean getBoolean(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    public void setLatitude(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LATITUDE, value);
        editor.commit();
    }

    public String getLatitude(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(LATITUDE, "");
    }

    public void setLongitude(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LONGITUDE, value);
        editor.commit();
    }

    public String getLongitude(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(LONGITUDE, "");
    }



    public void setPobLatitude(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(POBLATITUDE, value);
        editor.commit();
    }

    public String getPobLatitude(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(POBLATITUDE, "");
    }

    public void setPobLongitude(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(POBLONGITUDE, value);
        editor.commit();
    }

    public String getPobLongitude(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(POBLONGITUDE, "");
    }


    public void ClearSharedPrefs(Context context){
        SharedPreferences preferences = context.getSharedPreferences(TAG,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }
    public void Logout(Context context){
        if(singleton.getsharedpreference(context).getBoolean("LoggedInFromAnotherAccount",false) == false){
        NetworkConsume.getInstance().ClearSharedPrefs(context);
       callLoginActivity(context);}
        else{
           callLoginActivity(context);
        }
    }
    public void callLoginActivity(Context context){
        Intent main = new Intent(context, LoginActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(main);
        ((Activity)context).finish();
    }
}
