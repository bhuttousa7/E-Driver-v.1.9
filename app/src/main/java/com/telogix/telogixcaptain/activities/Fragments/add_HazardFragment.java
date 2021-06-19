package com.telogix.telogixcaptain.activities.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;
import com.telogix.telogixcaptain.BuildConfig;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.activities.MapActivity;
import com.telogix.telogixcaptain.activities.zoomedImageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class add_HazardFragment extends Fragment implements Serializable, OnMapReadyCallback, response_interface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Retrofit retrofitClient;
    CountDownTimer countDownTimer;
    // TODO: Rename and change types of parameters

    ArrayList<MyResponse> imagesResponseList = new ArrayList<>();
    LinearLayout recordLayout;
    private ImageButton play, record;

    TextView countdown_recording;
    private ProgressBar playing_audio_progressBar;

    private Timer mRecordingtimer;
    private int seconds;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    private boolean mRecording;
    private String mFilename;
    int MAXTIME = 30000;
    int RQS_RECORDING = 1;
    private MediaPlayer mpintro;
    private String recordedAudio;
    private static final int TAKE_PICTURE = 2;
    private static final int HAZARD_LOC = 3;

    private Uri imageUri;
    LinearLayout layoutImages;
    ArrayList<Uri> imagesArray = new ArrayList<>();
    Spinner spnHazardtype;
    ArrayList hazardtype_list = new ArrayList();
    private ImageButton imgButtonAdd;
    MapView mapView;
    EditText edt_locationtext;
    EditText edt_hazarddesc;
    Button btn_addHazard;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private double latitude, longitude;
    private EditText edt_hazardName;
    private Geocoder geocoder;
    private Bitmap globalBitmap=null;

    public add_HazardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment add_HazardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static add_HazardFragment newInstance(String param1, String param2) {
        add_HazardFragment fragment = new add_HazardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView));
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_hazards, container, false);
        getActivity().setTitle("Add Hazard");
        bindViews(view);
        random = new Random();
        populateSpinner();
        imgButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  renderImageView();
                if (checkPermission()) {
                    if (imagesArray != null) {
                        if (imagesArray.size() < 3) {
                            takePhoto();
                        } else {
                            Toast.makeText(getContext(), "Only three photos are allowed", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                requestPermission();
            }

            }
        });
             record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRecording == true) {
                    try {
                        mediaRecorder.stop();
                    } catch (Exception ex) {
                        NetworkConsume.getInstance().hideProgress();
                    }
                    record.setBackground(getResources().getDrawable(R.drawable.microphone));
                    mRecording = false;
                    play.setVisibility(View.VISIBLE);
                } else {
                    if (checkPermission()) {
                        play.setVisibility(View.INVISIBLE);
                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                        MediaRecorderReady();

                        try {

                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            //showProgressforRecording();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            NetworkConsume.getInstance().hideProgress();
                            e.printStackTrace();
                        } catch (IOException e) {
                            NetworkConsume.getInstance().hideProgress();
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        record.setBackground(getResources().getDrawable(R.drawable.stop));
                        mRecording = true;

                        Toast.makeText(getContext(), "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                }

            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startPlaying();
                try {


                    if (!mRecording) {
                        if (AudioSavePathInDevice != "") {


                            mpintro = MediaPlayer.create(getContext(), Uri.parse(AudioSavePathInDevice));
                            mpintro.setLooping(false);
                            mpintro.start();
                            play.setBackground(getResources().getDrawable(R.drawable.stop));
                            mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    play.setBackground(getResources().getDrawable(R.drawable.btnplay));
                                }
                            });
                        } else {

                            Toast.makeText(getContext(), "unable to play", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    NetworkConsume.getInstance().hideProgress();
                    Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
                }
            }
        });
        return view;
    }

    private void bindViews(View view) {
        recordLayout = view.findViewById(R.id.layout_recording);

        layoutImages= view.findViewById(R.id.layout_hazardimages);
        play = view.findViewById(R.id.imageBtn_play);
        record = view.findViewById(R.id.imgbtn_record);
        imgButtonAdd = view.findViewById(R.id.imgbtnAddimages);
        playing_audio_progressBar = view.findViewById(R.id.progressBar2);
        countdown_recording = view.findViewById(R.id.textTiming);
        spnHazardtype = view.findViewById(R.id.spinner_hazardtype);
        edt_locationtext = view.findViewById(R.id.edt_Location);
        edt_hazarddesc = view.findViewById(R.id.edt_hazarddesc);
        edt_hazardName = view.findViewById(R.id.edt_hazardName);
        btn_addHazard = view.findViewById(R.id.btn_addHazard);

        btn_addHazard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(globalBitmap!=null){
                        NetworkConsume.getInstance().ShowProgress(getContext());
                        uploadToServer(globalBitmap, AudioSavePathInDevice);
                    }


                   // addHazardRequest();


            }
        });
    }

    private void addHazardRequest() {
        if (!edt_hazardName.getText().toString().equals("") && !edt_locationtext.getText().toString().equals("")) {
            ArrayList<HazardDetails> params = new ArrayList<>();
            for (int i = 0; i < imagesResponseList.size(); i++) {
                HazardDetails hd = new HazardDetails();
                hd.setImageUrl(imagesResponseList.get(i).getFileLink());
                hd.setVoiceNoteUrl(imagesResponseList.get(i).getVoiceNoteUrl());
                params.add(hd);
            }

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("HazardName", edt_hazardName.getText().toString());
            hashMap.put("HazardTypeID", "" + (spnHazardtype.getSelectedItemPosition() + 1));
            hashMap.put("Location", ""+edt_locationtext.getText().toString());
            hashMap.put("Latitude", "" + latitude);
            hashMap.put("Longitude", "" + longitude);
            hashMap.put("Detail",edt_hazarddesc.getText().toString());
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i).getImageUrl() != "") {
                    hashMap.put("HazardDetails[" + i + "].ImageUrl", params.get(i).getImageUrl());

                }

                if (params.get(i).getVoiceNoteUrl() != "") {
                    hashMap.put("HazardDetails[" + i + "].VoiceNoteUrl", params.get(i).getVoiceNoteUrl());

                }


            }

            if (hashMap.containsKey("HazardDetails[0].ImageUrl") &&
                    hashMap.containsKey("Location") && hashMap.containsKey("HazardTypeID") && hashMap.containsKey("HazardName")) {
               // NetworkConsume.getInstance().ShowProgress(getContext());
                httpvolley.stringrequestpost("api/Hazards/AddHazard", Request.Method.POST, hashMap, add_HazardFragment.this);

            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                NetworkConsume.getInstance().hideProgress();
            }

        } else {
            Toast.makeText(getContext(), "Input all fields", Toast.LENGTH_SHORT).show();
            NetworkConsume.getInstance().hideProgress();
        }
    }
    private void populateSpinner() {
        HashMap hashMap = new HashMap();
        NetworkConsume.getInstance().ShowProgress(getContext());
        httpvolley.stringrequestpost("api/HazardType/GetHazardTypeForDDL",Request.Method.GET,hashMap,this);

    }

    private void renderImageView(Bitmap bitmap, Uri uri) {
        ImageView img = new ImageView(getContext());
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Write file
                    NetworkConsume.getInstance().ShowProgress(getContext());
                    String filename = "bitmap.png";
                    FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);

                    //Cleanup
                    stream.close();
                    bitmap.recycle();

                    //Pop intent
                    NetworkConsume.getInstance().hideProgress();
                    Intent in1=new Intent(getContext(), zoomedImageActivity.class);
                    in1.putExtra("image", filename);
                    startActivity(in1);
                } catch (Exception e) {
                    NetworkConsume.getInstance().hideProgress();
                    e.printStackTrace();
                }


            }
        });
        globalBitmap=bitmap;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100));
        layoutParams.setMargins(5, 5, 5, 5);
        img.setImageBitmap(bitmap);

        img.setLayoutParams(layoutParams);
        if(layoutImages.getChildCount()>1){
            layoutImages.removeAllViews();
            layoutImages.addView(imgButtonAdd);
        }
//
        layoutImages.addView(img);
        imagesArray.add(uri);

      //  uploadToServer(bitmap, AudioSavePathInDevice);


    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, CAMERA}, RequestPermissionCode);
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getContext(),
                CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getContext(),
                READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageUri;

                getActivity().getContentResolver().notifyChange(selectedImage, null);
//                Toast.makeText(getContext(), "" + selectedImage, Toast.LENGTH_SHORT).show();
                ContentResolver cr = getActivity().getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);

                    renderImageView(bitmap, imageUri);
//                    Toast.makeText(getContext(), selectedImage.toString(),
//                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    NetworkConsume.getInstance().hideProgress();
                    Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
            }
        }
        if(data!=null) {
            if (requestCode == RQS_RECORDING) {

                if (resultCode == RESULT_OK) {

                    // Great! User has recorded and saved the audio file
                    mRecording = false;
                    play.setEnabled(true);
                    recordedAudio = data.getStringExtra("result");


                    Log.d("debug", "Saved Path::" + recordedAudio);


                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Oops! User has canceled the recording / back button
                }

           }
//           else if (requestCode == TAKE_PICTURE) {
//                if (resultCode == RESULT_OK) {
//                    Uri selectedImage = imageUri;
//
//                    getActivity().getContentResolver().notifyChange(selectedImage, null);
////                Toast.makeText(getContext(), "" + selectedImage, Toast.LENGTH_SHORT).show();
//                    ContentResolver cr = getActivity().getContentResolver();
//                    Bitmap bitmap;
//                    try {
//                        bitmap = android.provider.MediaStore.Images.Media
//                                .getBitmap(cr, selectedImage);
//
//                        renderImageView(bitmap, imageUri);
////                    Toast.makeText(getContext(), selectedImage.toString(),
////                            Toast.LENGTH_LONG).show();
//
//                    } catch (Exception e) {
//                        Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT)
//                                .show();
//                        Log.e("Camera", e.toString());
//                    }
//                }
//            }
          else if (requestCode == HAZARD_LOC) {
                if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                    //String latitude = data.getStringExtra("latitude");
                  //  String longitude = data.getStringExtra("longitude");
                    latitude=Double.parseDouble(data.getStringExtra("latitude"));
                    longitude=Double.parseDouble(data.getStringExtra("longitude"));
                    //  Toast.makeText(getContext(), latitude, Toast.LENGTH_LONG).show();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("Start"));
                  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(  new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                            .zoom(15.5f)
                            .bearing(0)
                            .tilt(25)
                            .build()));
                    geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                    try {
                        List<Address> listaddress = geocoder.getFromLocation(latitude, longitude, 1);
                        for (int i = 0; i < listaddress.size(); i++) {
                            edt_locationtext.setText("" + listaddress.get(i).getAddressLine(0));

                        }
                    } catch (IOException e) {
                        NetworkConsume.getInstance().hideProgress();
                        e.printStackTrace();
                    }
//                    edt_locationtext.setText(latitude + "," + longitude);
                }
            }
        }
    }

    public void takePhoto() {
        checkPermission();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");

        imageUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void showProgressforRecording() {
        seconds = 0;
        playing_audio_progressBar.setMax(30 * 1000);
        playing_audio_progressBar.setProgress(0);
        playing_audio_progressBar.setProgress(0);

        mRecordingtimer = new Timer();
        mRecordingtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mRecording) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (playing_audio_progressBar.getProgress() < MAXTIME) {
                                playing_audio_progressBar.setProgress(playing_audio_progressBar.getProgress() + 1000);
                                seconds++;
                                countdown_recording.setVisibility(View.VISIBLE);
                                countdown_recording.setText("" + (seconds > 9 ? "00:" + seconds : "00:0" + seconds));
                            } else {
                                countdown_recording.setVisibility(View.GONE);
                                mediaRecorder.stop();
                                record.setBackground(getResources().getDrawable(R.drawable.microphone));
                                mRecording = false;
                                play.setVisibility(View.VISIBLE);

                            }
                        }
                    });
                }
            }
        }, 1000, 1000);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(getContext(), MapActivity.class);
                startActivityForResult(i, HAZARD_LOC);
            }
        });
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if (myLocation != null) {
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
            }
        }

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
        if(response!="")
        {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("Status")) {

                if (jsonObject.get("Status").equals(true) && jsonObject.get("Message").equals("Hazard Saved"))
                {
                    Toast.makeText(getContext(),"Hazard added successfuly",Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
                else if(jsonObject.get("Status").equals(true) && jsonObject.get("Message").equals("HazardTypes"))
                {
                    JSONArray data=jsonObject.getJSONArray("Data");
                    hazardtype_list.clear();
                    for(int i=0;i<data.length();i++)
                    {
                        hazardtype_list.add(data.getJSONObject(i).get("TypeName"));
                    }
                    spnHazardtype.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,hazardtype_list));
                }
            }
        }
        else
        {
            Toast.makeText(getContext(),"invalid request",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
        Toast.makeText(getContext(),"something went wrong",Toast.LENGTH_LONG).show();
    }

    public class MyResponse {

        @SerializedName("Parameter")
        private String parameter;
        @SerializedName("FileLink")
        private String fileLink;

        public String getVoiceNoteUrl() {
            return VoiceNoteUrl;
        }

        public void setVoiceNoteUrl(String voiceNoteUrl) {
            VoiceNoteUrl = voiceNoteUrl;
        }

        private String VoiceNoteUrl = "";

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public String getFileLink() {
            return fileLink;
        }

        public void setFileLink(String fileLink) {
            this.fileLink = fileLink;
        }
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }


    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            NetworkConsume.getInstance().hideProgress();
            e.printStackTrace();
        }
    }

    public interface UploadAPIs {
        @Multipart
        @POST("/api/FileUpload/PostImage")
        Call<List<MyResponse>> uploadImage(@Part MultipartBody.Part file, @Part MultipartBody.Part Audio, @Part("name") RequestBody requestBody);

        @Multipart
        @POST("/api/FileUpload/PostImage")
        Call<List<MyResponse>> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    }

    private void uploadToServer(Bitmap bitmap, String mRecordedFilePath) {
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
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", "img" + System.currentTimeMillis(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call<List<MyResponse>> call;
        if (mRecordedFilePath != null && mRecordedFilePath != "") {

            File audioFile = new File(mRecordedFilePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("audio/*"), audioFile);
            MultipartBody.Part audioPart = MultipartBody.Part.createFormData("AudioComment", audioFile.getName(), reqFile);


            call = uploadAPIs.uploadImage(part, audioPart, description);
        } else if (bitmap == null && mRecordedFilePath != "")
        {
            File audioFile = new File(mRecordedFilePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("audio/*"), audioFile);
            MultipartBody.Part audioPart = MultipartBody.Part.createFormData("AudioComment", audioFile.getName(), reqFile);
            call = uploadAPIs.uploadImage(audioPart, description);
        }
        else {
            call = uploadAPIs.uploadImage(part, description);
        }
        call.enqueue(new Callback<List<MyResponse>>() {
            @Override
            public void onResponse(Call<List<MyResponse>> call, Response<List<MyResponse>> response) {
                Log.d("imageresponse", "" + response.body().get(0).fileLink);
                if (response.code() == 201) {
                    if (response.body() != null) {
                        if (response.body().size() > 0) {
                            for (int i = 0; i < response.body().size(); i++) {
                                MyResponse imagesPathResponse = new MyResponse();
                                imagesPathResponse.setFileLink(response.body().get(i).fileLink);
                                imagesPathResponse.setParameter(response.body().get(i).parameter);

                                imagesResponseList.add(imagesPathResponse);
                            }
                            addHazardRequest();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyResponse>> call, Throwable t) {
                Log.d("imageresponse error", "" + t);
            }
        });
    }

    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}

class HazardDetails {
    String ImageUrl;
    String VoiceNoteUrl;
    String Latitude;
    String Longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    String Detail;

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getVoiceNoteUrl() {
        return VoiceNoteUrl;
    }

    public void setVoiceNoteUrl(String voiceNoteUrl) {
        VoiceNoteUrl = voiceNoteUrl;
    }

    public String getHazardName() {
        return HazardName;
    }

    public void setHazardName(String hazardName) {
        HazardName = hazardName;
    }

    public String getHazardTypeID() {
        return HazardTypeID;
    }

    public void setHazardTypeID(String hazardTypeID) {
        HazardTypeID = hazardTypeID;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    String HazardName;
    String HazardTypeID;
    String Location;
}
