package com.telogix.telogixcaptain.activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Utils.connection;
import com.telogix.telogixcaptain.activities.MapDetailActivity;
import com.telogix.telogixcaptain.activities.zoomedImageActivity;
import com.telogix.telogixcaptain.adapters.SwipeableHazardAdapter;

import java.io.FileOutputStream;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class HazardDetailFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    public static final int RequestPermissionCode = 1;

    private static final int HAZARD_LOC = 3;


    TextView textHazardtype;
    EditText edt_locationtext;
    EditText edt_hazarddesc;
    LinearLayout layout_recording;
    private GoogleMap mMap;
    private EditText edt_hazardName;
    LinearLayout images;
    private boolean Playing=false;
    private MediaPlayer mpintro;
    private ImageButton play,record;

    public HazardDetailFragment() {
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

        mapFragment.getMapAsync(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_hazard_detail, container, false);
        bindViews(view);
        getActivity().setTitle("Hazard Detail");
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
      edt_hazardName.setText( SwipeableHazardAdapter.currentHazard.getHazardName());
      edt_locationtext.setText(SwipeableHazardAdapter.currentHazard.getHazardType());

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
            // Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
        }
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
        layout_recording=view.findViewById(R.id.layout_recording);
        play=view.findViewById(R.id.imageBtn_play);
        record=view.findViewById(R.id.imgbtn_record);
        record.setVisibility(View.GONE);
        play.setVisibility(View.VISIBLE);


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


