package com.telogix.telogixcaptain.activities.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.telogix.telogixcaptain.R;

public class MapBottomSheetDialog extends BottomSheetDialogFragment {
    ImageButton satellite_type,default_type,terrain_type;
     Switch summary_checkbox,stops_checkbox;
    View view_terrain_map_type,view_default_map_type,view_satellite_map_type;
    TextView default_map_textview,satellite_map_textview,terrain_map_textview;
    Spinner speed_control_spinner;
    LinearLayout summary_ll;
    private ItemClickListener mListener;
    public String TAG="mapBottomSheetDialog";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static MapBottomSheetDialog newInstance(){

        return new MapBottomSheetDialog();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.custom_map_type_layout,container,false);
        satellite_type = v.findViewById(R.id.satellite_type);
        default_type = v.findViewById(R.id.default_type);
        terrain_type = v.findViewById(R.id.terrain_type);
        summary_ll = v.findViewById(R.id.summary_ll);
        summary_checkbox = v.findViewById(R.id.summary_checkbox);
        stops_checkbox = v.findViewById(R.id.stops_checkbox);
        view_default_map_type = v.findViewById(R.id.view_default_map_type);
        view_terrain_map_type = v.findViewById(R.id.view_terrain_map_type);
        view_satellite_map_type = v.findViewById(R.id.view_satellite_map_type);
        default_map_textview = v.findViewById(R.id.default_map_textview);
        satellite_map_textview = v.findViewById(R.id.satellite_map_textview);
        terrain_map_textview = v.findViewById(R.id.terrain_map_textview);
        speed_control_spinner  = v.findViewById(R.id.speed_control_spinner);
        ArrayAdapter<CharSequence> speed_control_adapter  = ArrayAdapter.createFromResource(this.getContext(),R.array.spinnerItems,R.layout.support_simple_spinner_dropdown_item);
        speed_control_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        speed_control_spinner.setAdapter(speed_control_adapter);
        v.setVisibility(View.VISIBLE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getContext().getSharedPreferences("MapBottomSheetDialog",Context.MODE_PRIVATE);
         editor= sharedPreferences.edit();

        getOptionsState();


        speed_control_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String speedtype = adapterView.getItemAtPosition(i).toString();
                editor.putInt("speedtype",i);
                editor.commit();
                mListener.onSpeedAdapterItemClick(speedtype);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        summary_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(summary_checkbox.isChecked() || !summary_checkbox.isChecked()){
                    editor.putBoolean("summary_checkbox",b);
                    mListener.onSummarySwitchClick(b);
                    editor.commit();
                }
            }
        });

        stops_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(stops_checkbox.isChecked() || !stops_checkbox.isChecked()){
                    editor.putBoolean("stops_checkbox",b);
                    mListener.onStopSwitchClick(b);
                    editor.commit();
                }
            }
        });
        satellite_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view_satellite_map_type.setVisibility(View.VISIBLE);
                view_default_map_type.setVisibility(View.GONE);
                view_terrain_map_type.setVisibility(View.GONE);
                satellite_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                terrain_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.BLACK);
                editor.putBoolean("satellite_map_type",true);
                editor.putBoolean("default_map_type",false);
                editor.putBoolean("terrain_map_type",false);

                setOptionsState();

                mListener.onSatelliteMapTypeClick(true);


            }
        });
        default_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view_satellite_map_type.setVisibility(View.GONE);
                view_default_map_type.setVisibility(View.VISIBLE);
                view_terrain_map_type.setVisibility(View.GONE);
                satellite_map_textview.setTextColor(Color.BLACK);
                terrain_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                editor.putBoolean("satellite_map_type",false);
                editor.putBoolean("default_map_type",true);
                editor.putBoolean("terrain_map_type",false);

                setOptionsState();

                mListener.onDefaultMapTypeClick(true);

            }
        });
        terrain_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view_satellite_map_type.setVisibility(View.GONE);
                view_default_map_type.setVisibility(View.GONE);
                view_terrain_map_type.setVisibility(View.VISIBLE);
                terrain_map_textview.setTextColor(Color.parseColor("#ED1C24"));
                satellite_map_textview.setTextColor(Color.BLACK);
                default_map_textview.setTextColor(Color.BLACK);
                editor.putBoolean("satellite_map_type",false);
                editor.putBoolean("default_map_type",false);
                editor.putBoolean("terrain_map_type",true);

                setOptionsState();

                mListener.onTerrainMapTypeClick(true);

            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ItemClickListener )context;
        }
        catch (Exception e){
            Log.i("--bsheetex",e.toString());
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ItemClickListener {
        void onSummarySwitchClick(boolean item);
        void onStopSwitchClick(boolean item);
        void onSatelliteMapTypeClick(boolean item);
        void onDefaultMapTypeClick(boolean item);
        void onTerrainMapTypeClick(boolean item);
        void onSpeedAdapterItemClick(String i);
    }
    public void getOptionsState(){

        if(sharedPreferences.getBoolean("summary_checkbox",false)){
            boolean b = sharedPreferences.getBoolean("summary_checkbox",false);
            summary_checkbox.setChecked(b);
        }
        if(sharedPreferences.getBoolean("stops_checkbox",false)){
            boolean b = sharedPreferences.getBoolean("stops_checkbox",false);
            stops_checkbox.setChecked(b);

        }
        if(sharedPreferences.getString("satellite_map_textview","") != ""){
            String color = sharedPreferences.getString("satellite_map_textview","");
            satellite_map_textview.setTextColor(Color.parseColor(color));


        }
        if(sharedPreferences.getString("terrain_map_textview","") != ""){
            String color = sharedPreferences.getString("terrain_map_textview","");
            terrain_map_textview.setTextColor(Color.parseColor(color));

        }
        if(sharedPreferences.getString("default_map_textview","") != ""){
            String color = sharedPreferences.getString("default_map_textview","");
            default_map_textview.setTextColor(Color.parseColor(color));



        }

        if(sharedPreferences.getInt("view_default_map_type",-1) != -1){
            int visibility_view = sharedPreferences.getInt("view_default_map_type",-1);
            view_default_map_type.setVisibility(visibility_view);
        }
        if(sharedPreferences.getInt("view_satellite_map_type",-1) != -1){
            int visibility_view = sharedPreferences.getInt("view_satellite_map_type",-1);
            view_satellite_map_type.setVisibility(visibility_view);
        }
        if(sharedPreferences.getInt("view_terrain_map_type",-1) != -1){
            int visibility_view = sharedPreferences.getInt("view_terrain_map_type",-1);
            view_terrain_map_type.setVisibility(visibility_view);
        }
        if(sharedPreferences.getInt("speedtype",-1) != -1){
            int pos = sharedPreferences.getInt("speedtype",-1);
            speed_control_spinner.setSelection(pos);
        }
    }
    public void setOptionsState(){


        editor.putInt("view_satellite_map_type",view_satellite_map_type.getVisibility());
        editor.putInt("view_default_map_type",view_default_map_type.getVisibility());
        editor.putInt("view_terrain_map_type",view_terrain_map_type.getVisibility());

        editor.putString("satellite_map_textview", String.format("#%06X", (0xFFFFFF & satellite_map_textview.getCurrentTextColor())));
        editor.putString("terrain_map_textview", String.format("#%06X", (0xFFFFFF & terrain_map_textview.getCurrentTextColor())));
        editor.putString("default_map_textview", String.format("#%06X", (0xFFFFFF & default_map_textview.getCurrentTextColor())));
        editor.commit();

    }
}
