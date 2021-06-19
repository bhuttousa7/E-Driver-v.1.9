package com.telogix.telogixcaptain.activities.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.UsersExcelPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static android.app.Activity.RESULT_OK;

public class UploadUserFragment extends Fragment implements response_interface {
    private Button btn_uploaduserexcel;
    private String fileName = null;
    private String[] extension = null;
    private Random random;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_upload_users, container, false);
        bindViews(v);
        return v;
    }

    private void bindViews(View v) {

        btn_uploaduserexcel = v.findViewById(R.id.btn_uploaduserexcel);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_uploaduserexcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {


            case 1:
                try{

                    if(resultCode == RESULT_OK){

                        Uri uri = data.getData();
                        Path path = Paths.get(String.valueOf(uri));
                        String uriString = uri.toString();
                        File mFile = new File(uri.toString());
                        Cursor cursor = null;
                        Uri filePathUri;
                        StringBuilder txt = new StringBuilder();

                        try {


                            if(uriString.contains("media")){

                            String [] proj = {MediaStore.Images.Media.DATA};
                            cursor = getContext().getContentResolver().query(uri,proj,null,null,null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            filePathUri = Uri.parse(cursor.getString(column_index));
                            fileName = filePathUri.getLastPathSegment();



                            }
                            else if(uriString.contains("content://")){
                                cursor = getContext().getContentResolver().query(uri,null,null,null,null);
                                if(cursor != null && cursor.moveToFirst()){
                                    fileName  = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            }
                            else if(uriString.startsWith("file://")){
                                fileName = mFile.getName();
                            }

                            if(fileName != null){

                                if(fileName.contains(".")){
                                    extension = fileName.split("\\.");
                                    fileName = extension[0];
                                }
                                if(extension[1].equals("xlsx")){

                                    InputStream is = getContext().getContentResolver().openInputStream(uri);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                    String line;

                                    while((line = br.readLine()) != null){

                                        txt.append(line);
                                        txt.append("\n");

                                    }


                                    new AlertDialog.Builder(getContext()).
                                            setTitle("Message")
                                            .setMessage("Are you sure you want to upload this file?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    uploadFile(uri);

                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();

                                }
                                else{

                                    Toast.makeText(getContext(),"Not a valid Excel File",Toast.LENGTH_SHORT).show();

                                }



                            }




                        } catch (Exception e) {
                            Log.i("--Exception: ",e.toString());
                        }
                        finally{

                            cursor.close();

                        }


                    }


                }
                catch(Exception e){

                }





        }
    }

    private void uploadFile(Uri uri){
        NetworkConsume.getInstance().ShowProgress(getContext());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] byteArray = null;
        try{

            InputStream is = getContext().getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int n;
            while(-1 !=(n = is.read(buffer))){
                baos.write(buffer,0,n);
            }
            byteArray = baos.toByteArray();

        }
        catch(Exception e){


        }
        if(byteArray != null){
            random = new Random();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),byteArray);
            MultipartBody.Part filetoUpload = MultipartBody.Part.createFormData("file",
                    "UserCredentials_"+random.nextInt(999999999)+".xlsx",requestBody);
            BulkUserUpload getResponse = AppConfig.getRetrofit().create(BulkUserUpload.class);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"),"excel");

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + singleton.getsharedpreference(getContext()).getString("token",""));
            retrofit2.Call<UsersExcelPojo> call = getResponse.uploadFile(headers,filetoUpload,filename);
            call.enqueue(new Callback<UsersExcelPojo>() {
                @Override
                public void onResponse(retrofit2.Call<UsersExcelPojo> call, Response<UsersExcelPojo> response) {
                    NetworkConsume.getInstance().hideProgress();
                    String gsonObj = new Gson().toJson(response.body());
                    try{
                        if(!gsonObj.equals(null)){

                            JSONObject jsonObject = new JSONObject(gsonObj);
                            if (jsonObject.has("Data")) {
                                JSONArray array = jsonObject.getJSONArray("Data");
                                if (array.length() > 0) {

                                    Fragment fragment = new UserListFragment();
                                    Bundle b = new Bundle();
                                    b.putString("userExcelData", gsonObj);
                                    ChangeFragment(fragment,b);


                                }
                            }

                        }

                    }
                    catch(Exception e){




                    }

                }

                @Override
                public void onFailure(retrofit2.Call<UsersExcelPojo> call, Throwable t) {

                }
            });

        }

    }

    private interface BulkUserUpload{
        @Multipart
        @POST("api/Account/BulkUserUpload")
        Call<UsersExcelPojo> uploadFile(@HeaderMap Map<String,String> token,
                                        @Part MultipartBody.Part file, @Part("File") RequestBody name);
    }
    private void registerUser(HashMap userData) {

        httpvolley.stringrequestpost("api/Account/Register", Request.Method.POST, userData, this );
    }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
    public void ChangeFragment(Fragment fragment, Bundle b){

        fragment.setArguments(b);
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.main_content,fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();



    }
}