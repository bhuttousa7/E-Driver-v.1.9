package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telogix.telogixcaptain.Pojo.ViewCheckList.CheckListPojo;
import com.telogix.telogixcaptain.R;

import java.util.List;

public class ViewCheckListAdapter extends BaseAdapter {
    Context context;
    List<CheckListPojo> checkListPojoList;
    LayoutInflater layoutinflater;
    TextView inspectionTitleUr,isChecked,condition,
            decantingSite,tv_decanting_site_name;
    String yes,no,text,cond;
    int dclCount = 10;
    LinearLayout header_decanting;

    public ViewCheckListAdapter(Context context, List<CheckListPojo> checkListPojoList){
        this.context = context;
        this.checkListPojoList = checkListPojoList;
        this.layoutinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return checkListPojoList.size();
    }

    @Override
    public Object getItem(int i) {
        return checkListPojoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = layoutinflater.inflate(R.layout.layout_viewchecklist,null,false);
       // LinearLayout ll_multiplechecklist_1 = v.findViewById(R.id.ll_multiplechecklist_1);
        LinearLayout ll_multiplechecklist_2 = v.findViewById(R.id.ll_multiplechecklist_2);

            header_decanting = v.findViewById(R.id.header_decanting);
            decantingSite = v.findViewById(R.id.decantingSite);
            tv_decanting_site_name = v.findViewById(R.id.tv_decanting_site_name);
        if(getItemId(i) % 10 == 0){
            header_decanting.setVisibility(View.VISIBLE);
            tv_decanting_site_name.setVisibility(View.VISIBLE);
            decantingSite.setVisibility(View.VISIBLE);
            decantingSite.setText(checkListPojoList.get(i).getDecantingSiteName());
        }


        if(checkListPojoList.get(i).getInspectionTypeID() == 1){
            header_decanting.setVisibility(View.GONE);
            decantingSite.setVisibility(View.GONE);
            tv_decanting_site_name.setVisibility(View.GONE);
            View preLoadCheckListView= layoutinflater.inflate(R.layout.multiplechecklists,null,false);
            LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0,5,0,0);
            preLoadCheckListView.setLayoutParams(layoutParams);
//            inspectionTitle = preLoadCheckListView.findViewById(R.id.inspectionTitle);
            inspectionTitleUr = preLoadCheckListView.findViewById(R.id.inspectionTitleUr);
            isChecked = preLoadCheckListView.findViewById(R.id.isChecked);
            condition = preLoadCheckListView.findViewById(R.id.condition);
            inspectionTitleUr.setText(checkListPojoList.get(i).getInspectionTitleUr());
            if(checkListPojoList.get(i).isChecked())
            {
                 yes = "ہاں";
                 text = ""+yes;
                Spannable spannableYes = new SpannableString(text);
                spannableYes.setSpan(new ForegroundColorSpan(Color.GREEN),(text.length() - yes.length()),text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                isChecked.setText(spannableYes, TextView.BufferType.SPANNABLE);
            }
            else{
                no = "نہیں";
                 text = ""+no;
                Spannable spannableNo = new SpannableString(text);
                spannableNo.setSpan(new ForegroundColorSpan(Color.RED),(text.length() - no.length()),text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                isChecked.setText(spannableNo, TextView.BufferType.SPANNABLE);
            }

            ll_multiplechecklist_2.addView(preLoadCheckListView);

        }

        if(checkListPojoList.get(i).getInspectionTypeID() == 3) {
            if(getItemId(i) % 10 == 0) {
                header_decanting.setVisibility(View.VISIBLE);
                decantingSite.setVisibility(View.VISIBLE);
                tv_decanting_site_name.setVisibility(View.VISIBLE);
            }
            else{
                decantingSite.setVisibility(View.GONE);
                tv_decanting_site_name.setVisibility(View.GONE);
            }

                View decantingInspectionCheckListView = layoutinflater.inflate(R.layout.multiplechecklists, null, false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 5, 0, 0);
                decantingInspectionCheckListView.setLayoutParams(layoutParams);
                inspectionTitleUr = decantingInspectionCheckListView.findViewById(R.id.inspectionTitleUr);
                isChecked = decantingInspectionCheckListView.findViewById(R.id.isChecked);
                condition = decantingInspectionCheckListView.findViewById(R.id.condition);
                inspectionTitleUr.setText(checkListPojoList.get(i).getInspectionTitleUr());


                if (checkListPojoList.get(i).isChecked()) {
                    yes = "ہاں";
                    text = "" + yes;
                    Spannable spannableYes = new SpannableString(text);
                    spannableYes.setSpan(new ForegroundColorSpan(Color.GREEN), (text.length() - yes.length()), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isChecked.setText(spannableYes, TextView.BufferType.SPANNABLE);
                } else {
                    no = "نہیں";
                    text = "" + no;
                    Spannable spannableYes = new SpannableString(text);
                    spannableYes.setSpan(new ForegroundColorSpan(Color.RED), (text.length() - no.length()), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isChecked.setText(spannableYes, TextView.BufferType.SPANNABLE);
                }
                dclCount++;
                ll_multiplechecklist_2.addView(decantingInspectionCheckListView);

         //   }

        }
        return v;
    }
}
