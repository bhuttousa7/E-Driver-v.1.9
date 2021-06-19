package com.telogix.telogixcaptain.driver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.CustomToast;
import com.telogix.telogixcaptain.activities.BaseActivity;
import com.telogix.telogixcaptain.activities.RecoverPasswordActivity;
import com.telogix.telogixcaptain.activities.SignupActivity;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.utils.Drivers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class SecondDriverLoginActivity extends BaseActivity implements response_interface {
    EditText etName, etPassword;
    Button btnLogin;
    TextView txtForgotPswd;
    Context context;
    LinearLayout linearLayout;
    private Button btnLogin2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_driver_login);
        context = this;
        // Views binding
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin2 = findViewById(R.id.btnLogin2);

        txtForgotPswd = findViewById(R.id.txtForgotPswd);
        linearLayout = findViewById(R.id.mainLL);

        TextView txtandroidID=findViewById(R.id.txtandroidID);
        txtandroidID.setText("Device ID : "+ new Constant(this).getAndroid_id());
        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SecondDriverLoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        //   etName.setText("talhadriver");
        // etName.setText("talhadriver");;
////////
        //  etPassword.setText("123456");
        // onClick events
        if(singleton.getsharedpreference(this).getString("token","")!="")
        {
            String response= singleton.getsharedpreference(SecondDriverLoginActivity.this).getString("userData", "");
            if(response!="") {
                Gson gson = new Gson();
                TokenPojo datum = gson.fromJson(response, TokenPojo.class);
                if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {

//                    new LogBook(this).updateLogBook("Driver Logged In");
//                    Intent intent = new Intent(SecondDriverLoginActivity.this, DriverMainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                } else {
//                    Intent intent = new Intent(SecondDriverLoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                }
            }
        }


        btnLogin.setOnClickListener(v -> validate());
        txtForgotPswd.setOnClickListener(v -> {
            Intent intent = new Intent(SecondDriverLoginActivity.this, RecoverPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_second_driver_login;
    }

    private void validate() {
        if (etName.getText().toString().equals("")) {
            new CustomToast().errorToast(context, linearLayout, "Please enter the username!");
        } else if (etPassword.getText().toString().equals("")) {
            new CustomToast().errorToast(context, linearLayout, "Please enter the password!");
        } else {
            login();
        }
    }

    private void login() {
        try {
            NetworkConsume.getInstance().ShowProgress(SecondDriverLoginActivity.this);
            HashMap data = new HashMap();
            data.put("grant_type", "password");
            data.put("UserName", etName.getText().toString());
            data.put("Password", etPassword.getText().toString());

            httpvolley.stringrequestpost("token", Request.Method.POST, data, this);

        }catch (Exception ex){
            NetworkConsume.getInstance().hideProgress();
        }
//        NetworkConsume.getInstance().ShowProgress(SecondDriverLoginActivity.this);
//        NetworkConsume.getInstance().setAccessKey("asd");
//        NetworkConsume.getInstance().getAuthAPI().getToken("password",etName.getText().toString(),etPassword.getText().toString()).enqueue(new Callback<TokenPojo>() {
//            @Override
//            public void onResponse(Call<TokenPojo> call, Response<TokenPojo> response) {
//                NetworkConsume.getInstance().hideProgress();
//                if (response.isSuccessful()){
//                    if (response.body() != null) {
//                        Gson gson = new Gson();
//                        String userJson = gson.toJson(response.body());
//                        singleton.getsharedpreference_editor(SecondDriverLoginActivity.this).putString("token1",response.body().getAccessToken()).commit();
//
//                        NetworkConsume.getInstance().setDefaults("token",response.body().getAccessToken(),SecondDriverLoginActivity.this);
//                        NetworkConsume.getInstance().setDefaults("userData",userJson,SecondDriverLoginActivity.this);
//                        Intent intent = new Intent(SecondDriverLoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//
//                    }
//                }else {
//                    new CustomToast().errorToast(context, linearLayout, "User credentials invalid!");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TokenPojo> call, Throwable t) {
//                NetworkConsume.getInstance().hideProgress();
//            }
//        });
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        Log.d("response",""+response);
        if (response != null && response != "") {

            JSONObject userJson = new JSONObject(response);
            String accesstoken = "";

            if (userJson.has("access_token")) {
                Gson gson = new Gson();
                TokenPojo datum = gson.fromJson(response, TokenPojo.class);
                Paper.book().write(Constants.KEY_ROLE_ID, datum.getRoleID());

                if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {
                accesstoken = userJson.getString("access_token").trim();
                singleton.getsharedpreference_editor(SecondDriverLoginActivity.this).putString("token", accesstoken).commit();
//                            NetworkConsume.getInstance().setDefaults("token",accesstoken,SecondDriverLoginActivity.this);
//                            NetworkConsume.getInstance().setDefaults("userData",response,SecondDriverLoginActivity.this);
                 Paper.init(getApplicationContext());
                 Paper.book().read("drivers");

                   ArrayList<HashMap<String,String>> drivers=new ArrayList();
                   drivers= Paper.book().read("drivers");

                   if(drivers!=null) {
                       HashMap driverhash = new HashMap();
                       driverhash.put("Driver", accesstoken);
                       Drivers.setSecondDriver(getApplicationContext(),driverhash);



                   }






                    singleton.getsharedpreference_editor(SecondDriverLoginActivity.this).putString("userData", response).commit();
            SaveDriver();
                    Intent intent = new Intent(SecondDriverLoginActivity.this, DriverMainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                }
//                else {
//                    Intent intent = new Intent(SecondDriverLoginActivity.this, MainActivity.class);
//                    OneSignal.sendTag("userID", "1");
//
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                }
            }


        } else {
            new CustomToast().errorToast(context, linearLayout, "User credentials invalid!");
        }
    }
    private void SaveDriver() {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/DriverAttendance/SaveTimeIn?DeviceID="+new Constant(this).getAndroid_id(), Request.Method.POST, data, this);



    }

    @Override
    public void onError(VolleyError Error) {
        Log.d("Error",""+Error);
        Toast.makeText(this, "Invalid Credentials2", Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().hideProgress();
    }
}
