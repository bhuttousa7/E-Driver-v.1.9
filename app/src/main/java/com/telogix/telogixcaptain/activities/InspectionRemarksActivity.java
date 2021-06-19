package com.telogix.telogixcaptain.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.VolleyError;
import com.google.gson.annotations.SerializedName;
import com.telogix.telogixcaptain.BuildConfig;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.activities.Fragments.NetworkClient;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

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

public class InspectionRemarksActivity extends BaseActivity {
    LinearLayout recordLayout, layoutImages;
    private ImageButton play, record, imgButtonAdd;
    private ProgressBar playing_audio_progressBar;
    EditText edt_inspectdesc;
    //
    ArrayList<InspectionRemarksActivity.MyResponse> imagesResponseList = new ArrayList<>();
    ArrayList<Uri> imagesArray = new ArrayList<>();
    TextView countdown_recording;
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
    private Button btn_attachDetail;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_remarks);
        setTitle("Inspection Remarks");
        bindViews();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_inspection_remarks;
    }

    private void bindViews() {
        recordLayout = findViewById(R.id.layout_recording);
        layoutImages = findViewById(R.id.layout_inspectimages);
        play = findViewById(R.id.imageBtn_play);
        record = findViewById(R.id.imgbtn_record);

        imgButtonAdd = findViewById(R.id.imgbtnAddimages);
        playing_audio_progressBar = findViewById(R.id.progressBar2);
        countdown_recording = findViewById(R.id.textTiming);
        edt_inspectdesc = findViewById(R.id.edt_inspectdesc);
        btn_attachDetail = findViewById(R.id.btn_attachDetail);
        imgButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  renderImageView();
                if (checkPermission()) {
                    if (imagesArray != null) {
                        if (imagesArray.size() < 1) {
                            takePhoto();
                        } else {
                            Toast.makeText(InspectionRemarksActivity.this, "Only one photo is allowed", Toast.LENGTH_SHORT).show();
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
                        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap emptybitmap = Bitmap.createBitmap(100, 100, conf);
                        uploadToServer(emptybitmap, AudioSavePathInDevice);
                    } catch (Exception ex) {
                        Toast.makeText(InspectionRemarksActivity.this, "Exception: "+ex, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(InspectionRemarksActivity.this, "onRecordStart:"+e, Toast.LENGTH_SHORT).show();
                            Log.i("--recordStart: ",e.toString());
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(InspectionRemarksActivity.this, "onRecordStart:"+e, Toast.LENGTH_SHORT).show();
                            Log.i("--recordStart: ",e.toString());


                            e.printStackTrace();
                        }

                        record.setBackground(getResources().getDrawable(R.drawable.stop));
                        mRecording = true;

                        Toast.makeText(InspectionRemarksActivity.this, "Recording started",
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


                            mpintro = MediaPlayer.create(InspectionRemarksActivity.this, Uri.parse(AudioSavePathInDevice));
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

                            Toast.makeText(InspectionRemarksActivity.this, "unable to play", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(InspectionRemarksActivity.this));
                }
            }
        });
        btn_attachDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                for(int i=0;i<imagesResponseList.size();i++)
                {
                    if(imagesResponseList.get(i).fileLink.contains("3gp"))
                    {
                        intent.putExtra("voicelink",""+imagesResponseList.get(i).fileLink);

                    }
                    else if(imagesResponseList.get(i).fileLink.contains("jpeg"))
                    {
                        intent.putExtra("link",""+imagesResponseList.get(i).fileLink);

                    }


                }
                intent.putExtra("description",edt_inspectdesc.getText().toString());

                if(imagesResponseList.size()>0 )
                {
//                    if(!edt_inspectdesc.getText().toString().equals("")){
                        setResult(3, intent);
                        finish();
//                    }
//                    else
//                    {Toast.makeText(InspectionRemarksActivity.this,"Add description",Toast.LENGTH_SHORT).show();}
                }
              else
                {Toast.makeText(InspectionRemarksActivity.this,"Add Image or Audio",Toast.LENGTH_SHORT).show();}

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(-1, intent);
        finish();

    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, CAMERA}, RequestPermissionCode);
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        random = new Random();
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    public void takePhoto() {
        checkPermission();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");

        imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(this,
                CAMERA);
        int result3 = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void renderImageView(Bitmap bitmap, Uri uri) {
        ImageView img = new ImageView(this);
        img.setScaleType(ImageView.ScaleType.FIT_XY);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100));
        layoutParams.setMargins(5, 5, 5, 5);
        img.setImageBitmap(bitmap);
        img.setLayoutParams(layoutParams);
        layoutImages.addView(img);
        imagesArray.add(uri);
        if (AudioSavePathInDevice != null) {
            uploadToServer(bitmap, AudioSavePathInDevice);
        } else {
            uploadToServer(bitmap, "");
        }


    }

    private void uploadToServer(Bitmap bitmap, String mRecordedFilePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        InspectionRemarksActivity.UploadAPIs uploadAPIs = retrofit.create(InspectionRemarksActivity.UploadAPIs.class);
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
        // this creates a MUTABLE bitmap
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap emptybitmap = Bitmap.createBitmap(100, 100, conf);
        Call<List<MyResponse>> call = null;
        if (bitmap.sameAs(emptybitmap) && mRecordedFilePath != "") {
            File audioFile = new File(mRecordedFilePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("audio/*"), audioFile);
            MultipartBody.Part audioPart = MultipartBody.Part.createFormData("AudioComment", audioFile.getName(), reqFile);
            call = uploadAPIs.uploadImage(audioPart, description);
        } else if (mRecordedFilePath != "" && !bitmap.sameAs(emptybitmap)) {

            File audioFile = new File(mRecordedFilePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("audio/*"), audioFile);
            MultipartBody.Part audioPart = MultipartBody.Part.createFormData("AudioComment", audioFile.getName(), reqFile);


            call = uploadAPIs.uploadImage(part, audioPart, description);
        } else if (mRecordedFilePath == "" && !bitmap.sameAs(emptybitmap)) {
            call = uploadAPIs.uploadImage(part, description);
        }
        if (call != null) {
            call.enqueue(new Callback<List<InspectionRemarksActivity.MyResponse>>() {
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
    }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }


    public interface UploadAPIs {
        @Multipart
        @POST("/api/FileUpload/PostImage")
        Call<List<MyResponse>> uploadImage(@Part MultipartBody.Part file, @Part MultipartBody.Part Audio, @Part("name") RequestBody requestBody);

        @Multipart
        @POST("/api/FileUpload/PostImage")
        Call<List<MyResponse>> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageUri;

                getContentResolver().notifyChange(selectedImage, null);
//                Toast.makeText(getContext(), "" + selectedImage, Toast.LENGTH_SHORT).show();
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);

                    renderImageView(bitmap, imageUri);
//                    Toast.makeText(getContext(), selectedImage.toString(),
//                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
            }
        }
        if (data != null) {
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


        }
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


}
