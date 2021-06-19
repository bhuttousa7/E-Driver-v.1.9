package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.Hazards;
import com.telogix.telogixcaptain.Utils.connection;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;
    private Button play,record;
    private final boolean Playing=false;
    private MediaPlayer mpintro;
    private ImageView imageHazard;
    private Marker markerShowingInfoWindow;
    public CustomInfoAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {

//        if(imageHazard!=null)
//        {
//            Hazards infoWindowData = (Hazards) marker.getTag();
//            if(infoWindowData!=null) {
//
//
//                if (infoWindowData.getHazardDetails().size() > 0) {
//                    if (infoWindowData.getHazardDetails().get(0).getImageUrl() != "" && infoWindowData.getHazardDetails().get(0).getImageUrl() != null) {
//
//                        imageHazard.setVisibility(View.VISIBLE);
//                        String url = connection.Baseurl + infoWindowData.getHazardDetails().get(0).getImageUrl();
//
//                        Picasso.with(context).load(url).error(R.drawable.placeholder).into(imageHazard, new Callback() {
//                            @Override
//                            public void onSuccess() {
//
//                                Toast.makeText(context, "loaded", Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                            @Override
//                            public void onError() {
//
//                            }
//                        });
//                    } else {
//                        imageHazard.setVisibility(View.INVISIBLE);
//                    }
//                }
//            }
//        }
        return null;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    @Override
    public View getInfoContents(Marker marker) {
   markerShowingInfoWindow=marker;
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.hazardpopup_design, null);

        TextView hazardName = view.findViewById(R.id.hazardName);
        TextView hazardType = view.findViewById(R.id.hazardType);
         imageHazard = view.findViewById(R.id.imageHazard);
        TextView hazardDetail = view.findViewById(R.id.hazardDetail);
        TextView hazardLocation = view.findViewById(R.id.hazardLocation);
        play = view.findViewById(R.id.play);
      //  hazardType.setText(marker.getSnippet());
// Recording Component

        Hazards infoWindowData = (Hazards) marker.getTag();
        if(infoWindowData!=null) {


            hazardName.setText(infoWindowData.getHazardName());
            hazardType.setText(infoWindowData.getHazardType());
            hazardDetail.setText(infoWindowData.getDetail().toString());
            hazardDetail.setText(infoWindowData.getLocation().toString());
            if(infoWindowData.getHazardDetails().size()>0) {
                if(infoWindowData.getHazardDetails().get(0).getImageUrl()!="" &&infoWindowData.getHazardDetails().get(0).getImageUrl()!=null) {

                    imageHazard.setVisibility(View.VISIBLE);
                    String url= connection.Baseurl+ infoWindowData.getHazardDetails().get(0).getImageUrl();
                    Glide.with(context).load(url).fitCenter().apply(new RequestOptions().override(dpToPx(150), 150)).placeholder(R.drawable.placeholder).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(context,"Failed to load image",Toast.LENGTH_SHORT).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //Toast.makeText(context,"ready",Toast.LENGTH_SHORT).show();
                            if (markerShowingInfoWindow.isInfoWindowShown() && markerShowingInfoWindow != null)
                            {
                                markerShowingInfoWindow.hideInfoWindow();
                                markerShowingInfoWindow.showInfoWindow();
                            }
                            return false;
                        }
                    }).into(imageHazard);

                }
                else
                {
                    imageHazard.setVisibility(View.INVISIBLE);
                }
               // checkAudioAvailability(infoWindowData);
            }

        }

        play.setVisibility(View.VISIBLE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startPlaying();
                Hazards infoWindowData = (Hazards) marker.getTag();
                if(infoWindowData.getHazardDetails().size()>0) {
                    if(infoWindowData.getHazardDetails().get(0).getImageUrl()!="" &&infoWindowData.getHazardDetails().get(0).getImageUrl()!=null) {
                        String url= connection.Baseurl+ infoWindowData.getHazardDetails().get(0).getImageUrl();
                      Picasso.with(context).load(url).error(R.drawable.placeholder).into(imageHazard);
                        //Picasso.get().load(connection.Baseurl+ infoWindowData.getHazardDetails().get(0).getImageUrl()).into(imageHazard);
                    }
                }
                String  AudioSavePathInDevice="";
                if(infoWindowData.getHazardDetails().size()>0) {
                    if(infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl()!="" && infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl()!=null) {

                        AudioSavePathInDevice= infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl(); }
                }
                try {
                    if (!Playing) {
                        if (AudioSavePathInDevice != "") {


                            mpintro = MediaPlayer.create(context, Uri.parse(AudioSavePathInDevice));
                            mpintro.setLooping(false);
                            mpintro.start();
                            play.setBackground(context.getResources().getDrawable(R.drawable.stop));
                            mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    play.setBackground(context.getResources().getDrawable(R.drawable.btnplay));
                                }
                            });
                        } else {

                            Toast.makeText(context, "No audio", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        if(mpintro!=null)
                        {
                            if(mpintro.isPlaying())
                            {
                                mpintro.stop();

                            }
                        }
                    }
                } catch (Exception ex) {
                    // Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
                }
            }
        });


        return view;
    }

    private void checkAudioAvailability(Hazards infoWindowData) {

       String  AudioSavePathInDevice="";
        if(infoWindowData.getHazardDetails().size()>0) {
            if(infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl()!="" && infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl()!=null) {
                play.setVisibility(View.VISIBLE);
                AudioSavePathInDevice= infoWindowData.getHazardDetails().get(0).getVoiceNoteUrl(); }
            else {
                play.setVisibility(View.GONE);
                markerShowingInfoWindow.hideInfoWindow();
                markerShowingInfoWindow.showInfoWindow();
            }
        }
        else
        {   imageHazard.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
        try {
            if (!Playing) {
                if (AudioSavePathInDevice != "") {


                    mpintro = MediaPlayer.create(context, Uri.parse(AudioSavePathInDevice));
                    mpintro.setLooping(false);
                    mpintro.start();
                    play.setBackground(context.getResources().getDrawable(R.drawable.stop));
                    mpintro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            play.setBackground(context.getResources().getDrawable(R.drawable.btnplay));
                        }
                    });
                } else {

                  //  Toast.makeText(context, "No audio", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if(mpintro!=null)
                {
                    if(mpintro.isPlaying())
                    {
                        mpintro.stop();

                    }
                }
            }
        } catch (Exception ex) {
            // Log.d("error", "" + NetworkConsume.getInstance().get_accessToken(getActivity()));
        }
    }
}
