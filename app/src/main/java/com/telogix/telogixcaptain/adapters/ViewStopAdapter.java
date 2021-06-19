package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.telogix.telogixcaptain.Pojo.ViewStop.StopPojo;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.ViewStopOnMapFragment;

import java.util.ArrayList;

public class ViewStopAdapter extends BaseAdapter {
    Context context;
    ArrayList<StopPojo>  stoplist;
    LayoutInflater layoutInflater;
    TextView stop_title,stop_description,stop_position,stop_duration,stop_time;
    ImageView viewStopOnMap;


    public  ViewStopAdapter(Context context, ArrayList<StopPojo> stoplist){
        this.context = context;
        this.stoplist = stoplist;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return stoplist.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = layoutInflater.inflate(R.layout.layout_viewstop,null,false);
        LinearLayout ll_multiplestops = v.findViewById(R.id.ll_multiplestops);

        View multipleStopsView = layoutInflater.inflate(R.layout.multiplestops,null,false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0,5,0,0);
        multipleStopsView.setLayoutParams(layoutParams);
        stop_title = multipleStopsView.findViewById(R.id.stop_title);
        stop_time = multipleStopsView.findViewById(R.id.stop_time);
        stop_duration = multipleStopsView.findViewById(R.id.stop_duration);
        stop_position = multipleStopsView.findViewById(R.id.stop_position);
        stop_description = multipleStopsView.findViewById(R.id.stop_description);
        viewStopOnMap = multipleStopsView.findViewById(R.id.viewStopOnMap);
        stop_title.setText("Reason: "+stoplist.get(position).getStopTitle());
        stop_time.setText("StopTime: "+stoplist.get(position).getStopTime());
        stop_duration.setText("Duration: "+stoplist.get(position).getStopDuration() +"min");

       String latlng = stoplist.get(position).getStopPosition().toString().replace(":","");
        String text = "Location: "+latlng;
        Spannable spannableLatLng = new SpannableString(text);
        spannableLatLng.setSpan(new ForegroundColorSpan(Color.BLUE),(text.length() - latlng.length()),text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableLatLng.setSpan(new UnderlineSpan(),(text.length() - latlng.length()),text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stop_position.setText(spannableLatLng, TextView.BufferType.SPANNABLE);
        stop_position.setClickable(true);
        stop_position.setFocusable(true);


       // stop_position.setText("Location: "+stoplist.get(position).getStopPosition().toString().replace(":",""));
        if(stoplist.get(position).getStopDescription() != "null"){
        stop_description.setText("Description: "+stoplist.get(position).getStopDescription());}
        else{
            stop_description.setText("Description: -");
        }
        stop_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b=new Bundle();

                    b.putParcelable("stop_coordinates",stoplist.get(position).getStopPosition());

                  Fragment fragment = new ViewStopOnMapFragment();
                  fragment.setArguments(b);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content, fragment,"ViewStop_On_MapFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                        .commit();

            }
        });

        ll_multiplestops.addView(multipleStopsView);




        return v;
    }

}
