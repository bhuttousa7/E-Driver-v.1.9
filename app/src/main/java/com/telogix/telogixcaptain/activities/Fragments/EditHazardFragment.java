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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.telogix.telogixcaptain.BuildConfig;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.activities.zoomedImageActivity;
import com.telogix.telogixcaptain.adapters.SwipeableHazardAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class EditHazardFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    ArrayList<add_HazardFragment.MyResponse> imagesResponseList = new ArrayList<>();
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
    private boolean Playing=false;
    private LinearLayout layout_recording;
    private TextView textHazardtype;
    private LinearLayout images;


    public EditHazardFragment() {
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
        getActivity().setTitle("Hazard Details");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView));
getActivity().setTitle("Edit Hazard");
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onDestroy() {
        try{
            if(mpintro!=null)
            {
                if(mpintro.isPlaying()) {
                    mpintro.stop();
                }
                mpintro.release();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        super.onDestroy();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_hazard, container, false);
        bindViews(view);
        getActivity().setTitle("Hazard Detail");
        random = new Random();
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
                playAudio();
            }
        });
        edt_hazardName.setText( SwipeableHazardAdapter.currentHazard.getHazardName());
        edt_locationtext.setText(SwipeableHazardAdapter.currentHazard.getHazardType());
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
        if(SwipeableHazardAdapter.currentHazard.getLocation()!=null)
        {
            edt_locationtext.setText(SwipeableHazardAdapter.currentHazard.getLocation().toString());
        }
        else
        {
            edt_locationtext.setText("No Location");

        }      if(SwipeableHazardAdapter.currentHazard.getDetail()!=null) {
            edt_hazarddesc.setText(SwipeableHazardAdapter.currentHazard.getDetail().toString());
        }
        else
        {
            edt_hazarddesc.setText("No Description");
        }

        boolean imageadded=false;
        for (int i = 0; i< SwipeableHazardAdapter.currentHazard.getHazardDetails().size(); i++)
        {

            ImageView img = new ImageView(getContext());
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100));
            layoutParams.setMargins(5, 5, 5, 5);
            img.setLayoutParams(layoutParams);
            if(!SwipeableHazardAdapter.currentHazard.getHazardDetails().get(0).getImageUrl().equals("N/A")) {
                Picasso.with(getContext()).load(connection.completeurl(SwipeableHazardAdapter.currentHazard.getHazardDetails().get(i).getImageUrl())).fit().error(R.drawable.placeholder).into(img);

                if(imageadded==false){images.addView(img);
                    imageadded=true;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                //Write file
                                img.buildDrawingCache();
                                Bitmap bmap = img.getDrawingCache();
                                NetworkConsume.getInstance().ShowProgress(getContext());
                                String filename = "bitmap.png";
                                FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                                bmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);

                                //Cleanup
                                stream.close();
                                //        bmap.recycle();

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

                }
            }
            checkAudioAvailability(SwipeableHazardAdapter.currentHazard);
            SwipeableHazardAdapter.currentHazard.getHazardDetails().get(i).getImageUrl();
        }

        return view;
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
    private void playAudio() {
        Hazards infoWindowData = SwipeableHazardAdapter.currentHazard;

        String AudioSavePathInDevice = "";
        if (infoWindowData.getHazardDetails().size() > 1) {
            if (infoWindowData.getHazardDetails().get(1).getImageUrl() != "" && infoWindowData.getHazardDetails().get(1).getImageUrl() != null) {

                AudioSavePathInDevice = infoWindowData.getHazardDetails().get(1).getImageUrl();
            }
        }

        try {
            if (!Playing) {
                if (AudioSavePathInDevice != "") {
                    mpintro = new MediaPlayer();
                    mpintro.setDataSource(connection.Baseurl + AudioSavePathInDevice);
                    mpintro.prepare();

                    mpintro.setLooping(false);
                    mpintro.start();
                    Playing=true;
                    play.setBackground(getContext().getResources().getDrawable(R.drawable.stop));
                    mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            play.setBackground(getContext().getResources().getDrawable(R.drawable.btnplay));
                            Playing = false;
                        }
                    });
                } else {

                    //  Toast.makeText(context, "No audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mpintro != null) {
                    if (mpintro.isPlaying()) {
                        mpintro.stop();
                        Playing=false;
                        play.setBackground(getContext().getResources().getDrawable(R.drawable.btnplay));

                    }
                }
            }
        } catch (Exception ex) {
             Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
        }
    }
    private void checkAudioAvailability(Hazards infoWindowData) {

        String AudioSavePathInDevice = "";
        if (infoWindowData.getHazardDetails().size() > 1) {
            layout_recording.setVisibility(View.VISIBLE);
            if (infoWindowData.getHazardDetails().size() > 1) {
                if (infoWindowData.getHazardDetails().get(1).getImageUrl() != "" && infoWindowData.getHazardDetails().get(1).getImageUrl() != null) {
                    AudioSavePathInDevice = infoWindowData.getHazardDetails().get(1).getImageUrl();
                }

            } else {
                layout_recording.setVisibility(View.GONE);
            }
        }
        else
        {
            layout_recording.setVisibility(View.GONE);
        }


    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
    private void bindViews(View view) {


        textHazardtype = view.findViewById(R.id.txt_hazardtype);
        edt_locationtext = view.findViewById(R.id.edt_Location);
        edt_hazarddesc = view.findViewById(R.id.edt_hazarddesc);
        edt_hazardName = view.findViewById(R.id.edt_hazardName);
        images=view.findViewById(R.id.layout_hazardimages);
        layout_recording = view.findViewById(R.id.layout_recording);
        play=view.findViewById(R.id.imageBtn_play);
        record=view.findViewById(R.id.imgbtn_record);
        record.setVisibility(View.VISIBLE);

        play.setVisibility(View.VISIBLE);
        imgButtonAdd = view.findViewById(R.id.imgbtnAddimages);

    }






    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, CAMERA}, RequestPermissionCode);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(getContext(), MapDetailActivity.class);
                try {
                    i.putExtra("hazarddetail",true);
                    i.putExtra("latitude", SwipeableHazardAdapter.currentHazard.getLatitude().toString());
                    i.putExtra("longitude", SwipeableHazardAdapter.currentHazard.getLongitude().toString());
                    startActivityForResult(i, HAZARD_LOC);

                }
                catch (Exception ex)
                {

                }
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
        mMap.clear();
        if(!SwipeableHazardAdapter.currentHazard.getLatitude().toString().equals("0") && !SwipeableHazardAdapter.currentHazard.getLongitude().toString().equals("0"))
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(SwipeableHazardAdapter.currentHazard.getLatitude().toString())
                    ,Double.parseDouble(SwipeableHazardAdapter.currentHazard.getLongitude().toString()))).icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)).title("Hazard"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(SwipeableHazardAdapter.currentHazard.getLatitude().toString())
                    ,Double.parseDouble(SwipeableHazardAdapter.currentHazard.getLongitude().toString())), 14), 1500, null);
        }
    }






    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}


