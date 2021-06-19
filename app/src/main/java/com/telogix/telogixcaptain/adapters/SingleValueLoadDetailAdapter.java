package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.telogix.telogixcaptain.R;

import java.util.HashMap;

public class SingleValueLoadDetailAdapter extends BaseAdapter {
    Context context;
    HashMap singleLoadDetailList;
    LayoutInflater layoutInflater;
    String key = null;
    boolean loadDecanted = false;

    public SingleValueLoadDetailAdapter(Context context, HashMap singleLoadDetailList){
        this.context = context;
        this.singleLoadDetailList = singleLoadDetailList;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return (singleLoadDetailList.size() * 2);
    }

    @Override
    public Object getItem(int i) {
        return singleLoadDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = layoutInflater.inflate(R.layout.singlevalueload_detail_item,null,false);
        TextView singleloadtextview = view1.findViewById(R.id.singleloadtextview);


            if(i%2 == 0){
                if(i == 0){

                    key = String.valueOf(singleLoadDetailList.keySet().toArray()[i]);
                    String text = key+":";
                    SpannableStringBuilder spannableKey = new SpannableStringBuilder (text);
                    spannableKey.setSpan(new StyleSpan(Typeface.BOLD),0,text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    singleloadtextview.setText(spannableKey);
                }
                else{
                    key = String.valueOf(singleLoadDetailList.keySet().toArray()[i/2]);
                    String text = key+":";
                    SpannableStringBuilder spannableKey = new SpannableStringBuilder (text);
                    spannableKey.setSpan(new StyleSpan(Typeface.BOLD),0,text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    singleloadtextview.setText(spannableKey);
                }

            }
            else{
                String value = String.valueOf(singleLoadDetailList.get(key));
//                if(key.equals("IsLoad Decanted") && value.equals("false")){
//                    loadDecanted = false;
//                }
//                else if(key.equals("IsLoad Decanted") && value.equals("true")){
//                    loadDecanted = true;
//                }
//                if(loadDecanted == false && key.equals("Decanted By")){
//                    singleloadtextview.setText("none");
//                }
//                else if(loadDecanted == false && key.equals("Decanted Time")){
//                    singleloadtextview.setText("none");
//                }
//                else{
                    singleloadtextview.setText(value);
 //               }

            }




        return view1;
    }
}
