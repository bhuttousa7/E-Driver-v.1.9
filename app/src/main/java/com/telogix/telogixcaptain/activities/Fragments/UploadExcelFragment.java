package com.telogix.telogixcaptain.activities.Fragments;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Pojo.ExcelPojo;
import com.telogix.telogixcaptain.Pojo.singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadExcelFragment extends Fragment {


    private String displayName;
    private String[] extension;
    private String selectedFilePath;
    private File myFile;
    private Random random;
    public UploadExcelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_upload_excel, container, false);
        Button uploadExcel=v.findViewById(R.id.btn_uploadexcel);
        random = new Random();
        uploadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");

                startActivityForResult(intent,1);
            }
        });
        return v;
    }
    public static byte[] read(Context context, String file) throws IOException {
        byte[] ret = null;

        if (context != null) {
            try {
                InputStream inputStream = context.openFileInput(file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                int nextByte = inputStream.read();
                while (nextByte != -1) {
                    outputStream.write(nextByte);
                    nextByte = inputStream.read();
                }

                ret = outputStream.toByteArray();

            } catch (FileNotFoundException ignored) { }
        }

        return ret;
    }

    public class FileChooser {

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @author paulburke
         */
        public  String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public  String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public  boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public  boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public  boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }

    private void uploadFile(Uri uri) {
        // Map is used to multipart the file using okhttp3.RequestBody



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        byte[] bbytes=null;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = is.read(buf)))
                baos.write(buf, 0, n);
             bbytes  = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
if(bbytes!=null)
{
    //myFile
    RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),bbytes);
    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file","excel_"+random.nextInt(999999)+".xlsx", requestBody);
    Log.d("--filetoUpload",String.valueOf(fileToUpload));
    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), "excel");

    ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + singleton.getsharedpreference(getContext()).getString("token",""));
    Call<ExcelPojo> call = getResponse.uploadFile(headers,fileToUpload, filename);
    NetworkConsume.getInstance().ShowProgress(getContext());
    call.enqueue(new Callback<ExcelPojo>() {


        @Override
        public void onResponse(Call<ExcelPojo> call, Response<ExcelPojo> response) {
            try {
                NetworkConsume.getInstance().hideProgress();
                String gsonObj = new Gson().toJson(response.body());
                if (!gsonObj.equals(null)) {
                    Log.d("response", gsonObj);
                    try {
                        JSONObject jsonObject = new JSONObject(gsonObj);
                        if (jsonObject.has("Data")) {
                            JSONArray array = jsonObject.getJSONArray("Data");
                            if (array.length() > 0) {
                                Fragment fragment = new Excel_ListFragment();
                                Bundle b = new Bundle();
                                b.putString("jsonData", gsonObj);

                                ChangeFragment(new Excel_ListFragment(), b);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception ex){

            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            NetworkConsume.getInstance().hideProgress();
        }
    });
}
else
{
    Toast.makeText(getContext(),"unable to upload file",Toast.LENGTH_SHORT).show();
}

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Upload Excel");
    }

    interface ApiConfig {
        @Multipart
        @POST("/api/LoadAssigns/PostExcel")
        Call<ExcelPojo> uploadFile(@HeaderMap Map<String, String> token
                , @Part MultipartBody.Part file, @Part("File") RequestBody name);
    }
    void ChangeFragment(Fragment fragment, Bundle b){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        fragment.setArguments(b);
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {


            case 1: {
                try {


                    if (resultCode == RESULT_OK) {
                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();
                        displayName = null;
                        Cursor cursor = null;
                        StringBuilder txt = new StringBuilder();
                        Uri filePathUri;
                        if (uriString.contains("media")) {

                            String[] proj = {MediaStore.Images.Media.DATA};
                            cursor = getContext().getContentResolver().query(uri,proj, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            filePathUri = Uri.parse(cursor.getString(column_index));
                            displayName = filePathUri.getLastPathSegment();

                        } else if (uriString.startsWith("content://")) {

                            try {

                                cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }

                        if (displayName != null) {
                            if (displayName.contains(".")) {
                                extension = displayName.split("\\.");
                                displayName = extension[0];
                            }

                            if (extension[1].equals("xlsx")) {


                                try {
                                    InputStream is = getContext().getContentResolver().openInputStream(uri);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                    String line;

                                    while ((line = br.readLine()) != null) {
                                        txt.append(line);
                                        txt.append('\n');
                                    }


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                new AlertDialog.Builder(getContext())
                                        .setTitle("Confirmation")
                                        .setMessage("Are you sure you want to upload this file?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                uploadFile(uri);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        }).show();

                            } else {
                                Toast.makeText(getContext(), "Not a valid excel file", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getContext(), "Not a valid excel file", Toast.LENGTH_SHORT).show();
                    Log.d("error",""+ex);
                }

            }
            }


    }


}