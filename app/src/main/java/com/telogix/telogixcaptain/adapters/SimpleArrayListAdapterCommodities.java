package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Pojo.CommoditiesPojo.Datum;

import java.util.ArrayList;

import gr.escsoft.michaelprimez.searchablespinner.interfaces.ISpinnerSelectedView;
import gr.escsoft.michaelprimez.searchablespinner.tools.UITools;

public class SimpleArrayListAdapterCommodities extends ArrayAdapter<Datum> implements Filterable, ISpinnerSelectedView {

    private final Context mContext;
    private int pos;

    public ArrayList<Datum> getmBackupStrings() {
        return mBackupStrings;
    }

    private final ArrayList<Datum> mBackupStrings;
    private ArrayList<Datum> mStrings;
    private final StringFilter mStringFilter = new StringFilter();

    public SimpleArrayListAdapterCommodities(Context context, ArrayList<Datum> strings) {
        super(context, R.layout.view_list_item);
        mContext = context;
        mStrings = strings;
        mBackupStrings = strings;
    }

    @Override
    public int getCount() {
        return mStrings == null ? 0 : mStrings.size()+ 1;
    }



    @Override
    public long getItemId(int position) {
        if (mStrings == null && position > 0)
            return mStrings.get(position-1).getCommodityID();
        else
            return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (position == 0) {
            view = getNoSelectionView();
        } else {
            view = View.inflate(mContext, R.layout.view_list_item, null);
            ImageView letters = view.findViewById(R.id.ImgVw_Letters);
            TextView dispalyName = view.findViewById(R.id.TxtVw_DisplayName);
            letters.setImageDrawable(getTextDrawable(mStrings.get(position-1).getCommodityName()));
            dispalyName.setText(mStrings.get(position-1).getCommodityName());
        }
        return view;
    }
    public Datum getselectedItem() {
        int i =pos;
        return mStrings.get(i-1);
    }

    @Override
    public View getSelectedView(int position) {
        View view = null;
        pos=position;
        if (position == 0) {
            view = getNoSelectionView();
        } else {
            view = View.inflate(mContext, R.layout.view_list_item, null);
            ImageView letters = view.findViewById(R.id.ImgVw_Letters);
            TextView dispalyName = view.findViewById(R.id.TxtVw_DisplayName);
            letters.setImageDrawable(getTextDrawable(mStrings.get(position-1).getCommodityName()));
            dispalyName.setText(mStrings.get(position-1).getCommodityName());
        }
        return view;
    }

    @Override
    public View getNoSelectionView() {
        View view = View.inflate(mContext, R.layout.view_list_no_selection_item, null);
        return view;
    }

    private TextDrawable getTextDrawable(String displayName) {
        TextDrawable drawable = null;
        if (!TextUtils.isEmpty(displayName)) {
            int color2 = ColorGenerator.MATERIAL.getColor(displayName);
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .textColor(Color.WHITE)
                    .toUpperCase()
                    .endConfig()
                    .round()
                    .build(displayName.substring(0, 1), color2);
        } else {
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .endConfig()
                    .round()
                    .build("?", Color.GRAY);
        }
        return drawable;
    }

    @Override
    public Filter getFilter() {
        return mStringFilter;
    }

    public class StringFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            String charString = constraint.toString();
            if (TextUtils.isEmpty(constraint)) {
                filterResults.count = mBackupStrings.size();
                filterResults.values = mBackupStrings;
                return filterResults;
            }
            final ArrayList<Datum> filterStrings = new ArrayList<>();
            for (Datum text : mBackupStrings) {
                if (text.getCommodityName().toLowerCase().contains(charString.toLowerCase())) {
                    filterStrings.add(text);
                }
            }
            filterResults.count = filterStrings.size();
            filterResults.values = filterStrings;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mStrings = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    }

    private class ItemView {
        public ImageView mImageView;
        public TextView mTextView;
    }

    public enum ItemViewType {
        ITEM, NO_SELECTION_ITEM
    }
}