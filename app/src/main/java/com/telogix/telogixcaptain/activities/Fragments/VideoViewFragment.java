package com.telogix.telogixcaptain.activities.Fragments;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.telogix.telogixcaptain.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoViewFragment extends Fragment {


    public VideoViewFragment() {
        // Required empty public constructor
    }
ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_video_view, container, false);

        progressBar=v.findViewById(R.id.progressBar3);


        if(getArguments().getString("type","")!="")
        {
            String url=url=getArguments().getString("url","");
            if(getArguments().getString("type","").equals("pdf"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }else
            {
                VideoView videoView=v.findViewById(R.id.videoView);
                videoView.setVideoURI(Uri.parse(getArguments().getString("url","")));
                videoView.setVisibility(View.VISIBLE);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        Toast.makeText(getContext(),"Cant play this video",Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                        return false;
                    }
                });
                videoView.start();
                MediaController mediaController=new MediaController(getContext());
                videoView.setMediaController(mediaController);
                DisplayMetrics metrics = new DisplayMetrics(); getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                params.width =  metrics.widthPixels*2;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                videoView.setLayoutParams(params);

            }
        }

        return v;
    }

}
