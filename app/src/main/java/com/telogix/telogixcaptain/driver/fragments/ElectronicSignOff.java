package com.telogix.telogixcaptain.driver.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.UploadImageResponse;
import com.telogix.telogixcaptain.activities.Fragments.NetworkClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * A simple {@link Fragment} subclass.
 */
public class ElectronicSignOff extends Fragment implements response_interface {

    ImageButton cross;
    private SignaturePad mSignaturePad;
    private String vehicleID, routeAssignID;
    private boolean signed = false;

    public ElectronicSignOff() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_electronic_sign_off, container, false);
        getActivity().setTitle("Electronic SignOff");
        Button btn_start_ride = v.findViewById(R.id.btn_start_ride);
        cross = v.findViewById(R.id.imgcross);
        if (getArguments().getString("VehicleID", "---") != "---") {
            vehicleID = getArguments().getString("VehicleID");

        }
        if (getArguments().getString("routeAssignID", "---") != "---") {
            routeAssignID = getArguments().getString("routeAssignID");

        }
        mSignaturePad = v.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                signed = true;
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                signed = false;
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
            }
        });
        btn_start_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                if (signed) {
                    uploadToServer(mSignaturePad.getSignatureBitmap());
                } else {
                    Toast.makeText(getContext(), "Signature Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }


    public interface UploadAPIs {

        @Multipart
        @POST("/api/FileUpload/PostImage")
        Call<List<UploadImageResponse>> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    }

    private void uploadToServer(Bitmap bitmap) {

        Retrofit retrofit = NetworkClient.getRetrofitClient(getContext());
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);

        //Create a file object using file path
        //File file = new File(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] bytes = stream.toByteArray();

        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), bytes);

        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", "signature" + System.currentTimeMillis(), fileReqBody);
        //Create request body with text description and text media type

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        Call<List<UploadImageResponse>> call;

        call = uploadAPIs.uploadImage(part, description);

        call.enqueue(new Callback<List<UploadImageResponse>>() {
            @Override
            public void onResponse(Call<List<UploadImageResponse>> call, Response<List<UploadImageResponse>> response) {
//                Log.d("imageresponse", "" + response.body().get(0).fileLink);


                if (response.code() == 201) {
                    if (response.body() != null) {
                        if (response.body().size() > 0) {

                            NetworkConsume.getInstance().ShowProgress(getContext());
                            signOFFRequest(response.body().get(0).fileLink, vehicleID, routeAssignID);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<UploadImageResponse>> call, Throwable t) {
                Log.d("imageresponse error", "" + t);
            }
        });
    }

    private void signOFFRequest(String URL, String vehicleID, String routeAssignID) {

        HashMap data = new HashMap();
        data.put("VehicleID", vehicleID);
        data.put("RouteAssignID", routeAssignID);
        data.put("SignOffUrl", URL);
        httpvolley.stringrequestpost("api/DriverSignOff/AddSignOff", Request.Method.POST, data, this);
    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                if (jsonObject.get("ResponseCode").equals(1)) {
                    Toast.makeText(getContext(),"SignOff Successful",Toast.LENGTH_SHORT).show();
                  //  NotificationsManager.sendNotification(getContext(),"Vehicle "+vehicleID+" Signed off.","Vehicle "+vehicleID+" Signed off.",""+responseObj.data.vehicleID);

                    getActivity().onBackPressed();
                   //getActivity().onBackPressed();
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
