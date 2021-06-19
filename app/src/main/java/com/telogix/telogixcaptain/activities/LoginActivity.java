package com.telogix.telogixcaptain.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Log.LogBook;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.CustomToast;
import com.telogix.telogixcaptain.driver.Constant;
import com.telogix.telogixcaptain.driver.activities.DriverMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends BaseActivity implements response_interface {
    EditText etName, etPassword;
    Button btnLogin;
    TextView txtForgotPswd;
    Context context;
    LinearLayout linearLayout;
    private Button btnLogin2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        if(singleton.getsharedpreference(context).getBoolean("LoggedInFromAnotherAccount",false) == true) {
            AnotherDeviceLoggedIn();
        }
        SaveDevice();

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        // onClick events
        if(singleton.getsharedpreference(this).getString("token","")!="")
        {
            String token  = singleton.getsharedpreference(this).getString("token","");
            String response= singleton.getsharedpreference(LoginActivity.this).getString("userData", "");
           if(response!="") {
               Gson gson = new Gson();
               TokenPojo datum = gson.fromJson(response, TokenPojo.class);
               if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {

                   new LogBook(this).updateLogBook("Driver Logged In");
                   Intent intent = new Intent(LoginActivity.this, DriverMainActivity.class);
                   startActivity(intent);
                   finish();
                   overridePendingTransition(R.anim.right_enter, R.anim.left_out);
               } else {
                  // sendNotiTokenServer(token);
                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                   startActivity(intent);
                   finish();
                   overridePendingTransition(R.anim.right_enter, R.anim.left_out);
               }
           }
        }
        Paper.init(this);
        ArrayList<HashMap<String,String>> drivers=new ArrayList<>();
        drivers=Paper.book().read("drivers");
        if(drivers!=null)
        {

        }

        btnLogin.setOnClickListener(v -> validate());
        txtForgotPswd.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoverPasswordActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void AnotherDeviceLoggedIn() {
        NetworkConsume.getInstance().ClearSharedPrefs(context);
        singleton.getsharedpreference_editor(context).putBoolean("LoggedInFromAnotherAccount",false).commit();
        new AlertDialog.Builder(context).setTitle("Another Device is currently logged in with this account").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    //    private void UploadDeviceID() {
//
//        HashMap data = new HashMap();
//        //  data.put("DeviceID",new Constant(this).getAndroid_id());
//        httpvolley.stringrequestpost("api/VehicleRideDrivers/StartJourney?DeviceID="+new Constant(this).getAndroid_id(), Request.Method.GET, data, this);
//
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
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
            NetworkConsume.getInstance().ShowProgress(LoginActivity.this);
            HashMap data = new HashMap();
            data.put("grant_type", "password");
            data.put("UserName", etName.getText().toString());
            data.put("Password", etPassword.getText().toString());



            httpvolley.stringrequestpost("token", Request.Method.POST, data, this);

        }catch (Exception ex){
            NetworkConsume.getInstance().hideProgress();
        }
//        NetworkConsume.getInstance().ShowProgress(LoginActivity.this);
//        NetworkConsume.getInstance().setAccessKey("asd");
//        NetworkConsume.getInstance().getAuthAPI().getToken("password",etName.getText().toString(),etPassword.getText().toString()).enqueue(new Callback<TokenPojo>() {
//            @Override
//            public void onResponse(Call<TokenPojo> call, Response<TokenPojo> response) {
//                NetworkConsume.getInstance().hideProgress();
//                if (response.isSuccessful()){
//                    if (response.body() != null) {
//                        Gson gson = new Gson();
//                        String userJson = gson.toJson(response.body());
//                        singleton.getsharedpreference_editor(LoginActivity.this).putString("token1",response.body().getAccessToken()).commit();
//
//                        NetworkConsume.getInstance().setDefaults("token",response.body().getAccessToken(),LoginActivity.this);
//                        NetworkConsume.getInstance().setDefaults("userData",userJson,LoginActivity.this);
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
        Log.d("--myresponse",response);
        if (response != null && response != "") {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("ResponseCode")){
                if(jsonObject.get("ResponseCode").equals(2501)){

                        String userData = singleton.getsharedpreference(LoginActivity.this).getString("userData", "");
                        Gson gson = new Gson();
                        TokenPojo datum = gson.fromJson(userData, TokenPojo.class);

                        if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {
                            Intent intent = new Intent(LoginActivity.this, DriverMainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                        } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                OneSignal.sendTag("userID", "1");
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.right_enter, R.anim.left_out);

                        }




                }
                if(jsonObject.get("ResponseCode").equals(2504)){

                    new AlertDialog.Builder(this).setTitle("Account already logged in").setMessage("Do you want to logout from all other devices?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NetworkConsume.getInstance().ShowProgress(context);
                                    logOut("");
                                    getNotiToken();
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }


            }
            if(jsonObject.has("ResponseCode")) {
                if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.SAVEDEVICE))) {

                } }
            else {
                    JSONObject userJson = new JSONObject(response);
                    String accesstoken = "";
                    if (userJson.has("access_token")) {

                        accesstoken = userJson.getString("access_token").trim();
                        ArrayList<HashMap<String, String>> drivers = new ArrayList();
                        HashMap driverhash = new HashMap();
                        driverhash.put("Driver", accesstoken);
                        drivers.add(driverhash);

                        singleton.getsharedpreference_editor(LoginActivity.this).putString("token", accesstoken).commit();

                        Paper.init(getApplicationContext());
                        Paper.book().write("drivers", drivers);

//                        NetworkConsume.getInstance().setDefaults("token",accesstoken,LoginActivity.this);
//                        NetworkConsume.getInstance().setDefaults("userData",response,LoginActivity.this);

                        singleton.getsharedpreference_editor(LoginActivity.this).putString("userData", response).commit();
                        Gson gson = new Gson();
                        TokenPojo datum = gson.fromJson(response, TokenPojo.class);
                        Paper.book().write(Constants.KEY_ROLE_ID, datum.getRoleID());
                        Paper.book().write(Constants.KEY_HAULIER_ID, datum.getHaulierID());


                        if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {
                            SaveDriver();
                            getNotiToken();

                        } else {
                            getNotiToken();
                        }
                    }

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

    private synchronized void logOut(String Token) {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());

        httpvolley.stringsynchronousrequest("api/UserToken/ExpireToken", Request.Method.PUT, data, this);

    }
    private void sendNotiTokenServer(String Token) {

        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());

        httpvolley.stringrequestpost("api/UserToken/SaveToken?Token="+Token, Request.Method.GET, data, this);

    }
    private synchronized String getNotiToken()
    {
        final String[] tok = {""};
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notitoken", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        singleton.getsharedpreference_editor(context).putString("notitoken",token).commit();
                        tok[0] =token;
                        sendNotiTokenServer(token);
                        Log.w("notitoken", ""+token, task.getException());

                        // Log and toast




                    }
                });
        return tok[0];
    }
    private void SaveDevice() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notitoken", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        HashMap data = new HashMap();
                        data.put("DeviceNo",new Constant(LoginActivity.this).getAndroid_id());
                        data.put("Token",token);
                        httpvolley.stringrequestpost("api/UserToken/SaveDevice", Request.Method.POST, data, LoginActivity.this);

                        Log.w("notitoken", ""+token, task.getException());

                        // Log and toast




                    }
                });

        //  data.put("DeviceID",new Constant(this).getAndroid_id());

    }
    @Override
    public void onError(VolleyError Error) {
        Log.d("--Error",""+Error);
        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        NetworkConsume.getInstance().hideProgress();
    }
}
