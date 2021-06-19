package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Interfaces.InspectionRemarks;
import com.telogix.telogixcaptain.Pojo.PreLoadInspPojo.PreLoadInsPojo;
import com.telogix.telogixcaptain.activities.InspectionRemarksActivity;

import java.util.ArrayList;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class PreloadInspAdapter extends BaseAdapter {

    public static ArrayList<PreLoadInsPojo> Data;
    Context context;
    String vehicleID;
    public static int pos = -1;
    public static int REQUESTCODE = 1;
    InspectionRemarks inspectionRemarks;

    com.telogix.telogixcaptain.Interfaces.onClickInterface onClickInterface;

    public PreloadInspAdapter(ArrayList<PreLoadInsPojo> Data, Context context) {
        PreloadInspAdapter.Data = Data;
        this.context = context;
        if(context instanceof InspectionRemarks){

            this.inspectionRemarks = (InspectionRemarks) context;
        }
        else {
            this.inspectionRemarks=(InspectionRemarks) context;
        }

        // this.onClickInterface = onClickInterface;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.preloadinspectdesign, null, false);
        TextView PickupLocation, TrailerCode, Commodity, DecantingTime, DecantingSite, CommodityLoad;
        TriStateToggleButton triStateButton = itemView.findViewById(R.id.tstb_1);

        triStateButton.setMidColor(Color.GRAY);
        triStateButton.setToggleMid();
//
//       triStateButton.setOffColor(Color.RED);
//        triStateButton.setMidColor(Color.GRAY);
//        triStateButton.setOnColor(0x4CAF50);

        CardView cardView;
        Button btn_adddetail;
        CheckBox checkBoxYes, checkBoxNo;
//        cardView = itemView.findViewById(R.id.cardView);
        PickupLocation = itemView.findViewById(R.id.txtPickupLocation);
        checkBoxYes = itemView.findViewById(R.id.checkboxYes);
        checkBoxNo = itemView.findViewById(R.id.checkboxNo);
        btn_adddetail = itemView.findViewById(R.id.btn_adddetail);
        TextView textmandatory=itemView.findViewById(R.id.txtmandatory);
        if(Data.get(position).getIsRequired())
        {
            textmandatory.setText("*");
        }
        else
        {
            textmandatory.setText("");
        }
        PickupLocation.setText(Data.get(position).getInspectionTitleUr());
        btn_adddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InspectionRemarksActivity.class);
                ((AppCompatActivity) context).startActivityForResult(intent, REQUESTCODE);
            }
        });


        if (Data.get(position).isYesSelected()) {
            triStateButton.setToggleOn();
//            checkBoxYes.setChecked(true);
//            checkBoxNo.setChecked(false);
            btn_adddetail.setVisibility(View.GONE);
        } else if (Data.get(position).isNoSelected()) {
            btn_adddetail.setVisibility(View.VISIBLE);
            triStateButton.setToggleOff();
            triStateButton.setOffColor(R.color.colorAccent);
//            checkBoxYes.setChecked(false);
//            checkBoxNo.setChecked(true);

        }
        else
        {
            btn_adddetail.setVisibility(View.GONE);
//            triStateButton.setToggleMid();
//            triStateButton.setMidColor(Color.GRAY);
            // triStateButton.setToggleMid(true);
//            checkBoxYes.setChecked(false);
//            checkBoxNo.setChecked(false);

        }
        triStateButton.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean b, int i) {
                switch (toggleStatus) {
                    case off: {
                        inspectionRemarks.onNoPressed(position,true,false,false);
                        pos = position;
                        Intent intent = new Intent(context, InspectionRemarksActivity.class);
                        ((AppCompatActivity) context).startActivityForResult(intent, REQUESTCODE);

                        break;
                    }
                    case mid: {
                        inspectionRemarks.onNoPressed(position,false,true,false);

                        break;}
                    case on: {
                        inspectionRemarks.onYesPressed(position,true,false,false);

                        break;}
                }
            }
        });
//        checkBoxYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                boolean ignored = false;
//                if (isChecked) {
//                    ignored = false;
//                    if (checkBoxNo.isChecked()) {
//                        checkBoxNo.setChecked(false);
//                    }
//                }
//                if(!checkBoxYes.isChecked() &&!checkBoxNo.isChecked()){
//                    ignored=true;
//                }
//                else
//                {
//                    ignored=false;
//                }
//                inspectionRemarks.onYesPressed(position, isChecked, ignored,checkBoxNo.isChecked());
//            }
//        });
//        checkBoxNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                boolean ignored = false;
//
//                if (isChecked) {
//                    ignored = false;
//                    if (checkBoxYes.isChecked()) {
//                        checkBoxYes.setChecked(false);
//                    }
//                }
//                if (!checkBoxYes.isChecked() && !checkBoxNo.isChecked()) {
//                    ignored = true;
//                } else {
//                    ignored = false;
//                }
//                inspectionRemarks.onNoPressed(position, isChecked, ignored,checkBoxYes.isChecked());
//            }
//        });

//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(checkBox.isChecked())
//                {
//                   PreLoadInsPojo p= Data.get(position);
//                   p.setIsselected(false);
//                   Data.get(position).setIsselected(p.isIsselected());
//
//                }
//                else
//                {
//
//                    Data.get(position).setIsselected(true);
//
//                }
//            }
//        });
        return itemView;
    }


}
