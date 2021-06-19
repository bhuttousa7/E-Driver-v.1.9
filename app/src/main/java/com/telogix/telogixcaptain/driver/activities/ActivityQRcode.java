package com.telogix.telogixcaptain.driver.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.BaseActivity;
import com.telogix.telogixcaptain.driver.fragments.DriverMapFragment;

import org.json.JSONException;

public class ActivityQRcode extends BaseActivity {

    private CodeScanner codeScanner;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        CodeScannerView scannerView =  findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);





        // Parameters (default values)
        codeScanner.setCamera(CodeScanner.CAMERA_BACK); // or CAMERA_FRONT or specific camera id
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);  // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE ); // or CONTINUOUS
        codeScanner.setScanMode(ScanMode.SINGLE);  // or CONTINUOUS or PREVIEW
        codeScanner.setAutoFocusEnabled(true); // Whether to enable auto focus or not
        codeScanner.setFlashEnabled(false); // Whether to enable flash or not
//        getFragmentManager().popBackStack();
//        setResult(RESULT_OK);
//        Intent i = new Intent();
//        i.putExtra("result", "" + true);
//        finish();
        // Callbacks
        codeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull Result result) {

                try {

                    getFragmentManager().popBackStack();
                    boolean found=false;
    for(int i = 0; i< DriverMapFragment.CurrentFuelStop.getDecantStaffList().size(); i++){
       if(DriverMapFragment.CurrentFuelStop.getDecantStaffList().get(i).getQRCode().equals(result.getText())){
           found=true;
           break;
       }

    }
    if(found){                    setResult(RESULT_OK);}
    else
    {
        setResult(RESULT_CANCELED);
    }
                    Intent i = new Intent();
                    i.putExtra("result", "" + found );

                   finish();
                }catch (Exception ex)
                {

                }
            }
        });
        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {

                getFragmentManager().popBackStack();
                Intent i = new Intent();
                setResult(RESULT_CANCELED);
                i.putExtra("result", "" + false);
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }else {
                codeScanner.startPreview();
            }
        }
        else{
            codeScanner.startPreview();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_qrcode;
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                codeScanner.startPreview();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
