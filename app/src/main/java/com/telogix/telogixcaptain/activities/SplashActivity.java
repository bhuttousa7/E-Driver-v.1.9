package com.telogix.telogixcaptain.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Interfaces.response_interface;

import org.json.JSONException;

public class SplashActivity extends BaseActivity implements response_interface {

    TextView poweredBy;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView splashImage = findViewById(R.id.splashImg);
        poweredBy = findViewById(R.id.poweredBy);
        String versionName = null;
        try {
            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            Log.d("--versionName:",versionName);
            poweredBy.setTextColor(Color.WHITE);
            poweredBy.setText(poweredBy.getText()+"\n version: "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        if(singleton.getsharedpreference(this).getString("token","")!="")
//        {
//            String response= singleton.getsharedpreference(this).getString("userData", "");
//            if(response!="") {
//                Gson gson = new Gson();
//                TokenPojo datum = gson.fromJson(response, TokenPojo.class);
//                if (UserRole.getRole(datum.getRoleID()) == UserRole.DRIVER) {
//                    Intent intent = new Intent(this, DriverMainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                } else {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                }
//            }
//        }
        splashImage.postDelayed(() -> {
            String model = NetworkConsume.getInstance().getDefaults("userData", SplashActivity.this);

            if (model == null || model.equals("")) {

                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
            else {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        }, 3000);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onResponse(String response) throws JSONException {
    Log.d("response",response);
    }

    @Override
    public void onError(VolleyError Error) {
Log.d("responseerror",""+Error);
    }
}
