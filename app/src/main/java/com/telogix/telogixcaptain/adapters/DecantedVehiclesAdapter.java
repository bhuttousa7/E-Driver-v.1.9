package com.telogix.telogixcaptain.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Pojo.Retailer.Datum;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class DecantedVehiclesAdapter extends RecyclerView.Adapter<DecantedVehiclesAdapter.ViewHolder> implements com.telogix.telogixcaptain.Interfaces.response_interface, Filterable {

    Context context;
    ArrayList<Datum> availableVehicles;
    ArrayList<Datum> availableVehiclesFiltered;
    private final ItemSelection itemSelection;
    LinearLayout layoutoptions;
    com.telogix.telogixcaptain.Interfaces.response_interface response_interface;
    public DecantedVehiclesAdapter(Context context, ArrayList<Datum> list, ItemSelection selection){
        this.context = context;
        this.response_interface= (com.telogix.telogixcaptain.Interfaces.response_interface) selection;
        this.availableVehicles = list;
        this.availableVehiclesFiltered = list;
        this.itemSelection = selection;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_vehicle_item,viewGroup,false);
        layoutoptions=view.findViewById(R.id.layoutoptions);
        View line=view.findViewById(R.id.view);
        line.setVisibility(View.GONE);
        return new ViewHolder(view);
    }
    public void hideKeyboardFrom() {

        View view =((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
           // searchView.setIconified(true);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Datum body = availableVehicles.get(position);
        viewHolder.txtVehicleName.setText("Vehicle "+body.getVehicleID());
        viewHolder.txtVehicleType.setText(body.getVehicleType());
        viewHolder.txtHaulier.setVisibility(View.GONE);
        viewHolder.txtVehicleType.setText(body.getVehicleType());
        viewHolder.txtNumber.setText(body.getVehicleNo());
        viewHolder.status.setVisibility(View.GONE);
        viewHolder.btn_inspect.setVisibility(View.GONE);
        String json = NetworkConsume.getInstance().getDefaults("userData",context);
        json= singleton.getsharedpreference(context).getString("userData","");
        Gson gson =  new Gson();

        TokenPojo datum = gson.fromJson(json, TokenPojo.class);
        layoutoptions.setVisibility(View.GONE);
            viewHolder.imgload.setEnabled(true);
            viewHolder.imgload.setImageResource(R.drawable.icon_assignload_small);
            if(UserRole.getRole(datum.getRoleID()).equals(UserRole.RETAILER)) {
                viewHolder.assign.setText("Rate Driver");
            }
            else {
                viewHolder.assign.setText("Load Assigned");
            }
            viewHolder.status.setTextColor(Color.parseColor("#1C821B"));


        viewHolder.assign.setOnClickListener(v -> {

            hideKeyboardFrom();
            itemSelection.onClickCloseSearchView();
            showDialog(position);
        });


    }

    @Override
    public int getItemCount() {
        return availableVehiclesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    availableVehiclesFiltered = availableVehicles;
                } else {
                    ArrayList<Datum> filteredList = new ArrayList<>();
                    for (Datum row : availableVehicles) {
                        if (row.getVehicleNo().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    availableVehiclesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = availableVehiclesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                availableVehiclesFiltered = (ArrayList<Datum>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onResponse(String response) throws JSONException {
     response_interface.onResponse(response);
    }

    @Override
    public void onError(VolleyError Error) {
        response_interface.onError(Error);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVehicleName , txtVehicleType,txtHaulier , txtNumber,status;
        Button assignRoute,btn_inspect;
        ImageView imgload,imgroute,imginspection,imgbriefing;
        LinearLayout linearLayout,layoutload;

        Button assign;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVehicleName=itemView.findViewById(R.id.txtVname);
            txtVehicleType=itemView.findViewById(R.id.txtVtype);
            txtHaulier=itemView.findViewById(R.id.txtHaulierName);
            txtNumber=itemView.findViewById(R.id.txtVnumber);
            status=itemView.findViewById(R.id.vehicleStatus);
            linearLayout=itemView.findViewById(R.id.mainLL);
            assign=itemView.findViewById(R.id.btnAction);
            btn_inspect=itemView.findViewById(R.id.btnInspect);
            layoutload=itemView.findViewById(R.id.layoutload);
            imgload=itemView.findViewById(R.id.imgload);
            imgroute=itemView.findViewById(R.id.imgroute);
            imginspection=itemView.findViewById(R.id.imginspection);
            imgbriefing=itemView.findViewById(R.id.imgbriefing);


        }
    }
    private void showDialog(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.activity_rating, null);
        TextView title=layout.findViewById(R.id.textView16);
        TextView question=layout.findViewById(R.id.textView17);
        title.setText("Rate Driver");
        question.setVisibility(View.GONE);
        RatingBar ratingBar = layout.findViewById(R.id.ratingBar2);
        Switch switch1 = layout.findViewById(R.id.switch1);
        switch1.setVisibility(View.GONE);
        EditText editText2 = layout.findViewById(R.id.editText2);
        final boolean[] checked = {false};
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[0] = isChecked;
            }
        });
        alert.setCancelable(false);
        alert.setView(layout);
        alert.setTitle("");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                SaveRetailerRating(position,ratingBar.getNumStars(), editText2.getText().toString());
                //ResumeRide();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }
    private void SaveRetailerRating(int position,int ratings,String Reviews) {
        HashMap hashMap=new HashMap();
        hashMap.put("LoadDecantingSiteID",""+availableVehicles.get(position).getLoadDecantingSiteID());


        hashMap.put("Rating",""+ratings);
        hashMap.put("Reviews",""+Reviews);
        hashMap.put("IsSameChampion","0");
        httpvolley.stringrequestpost("api/LoadDecantingSite/SaveRetailerRating", Request.Method.POST, hashMap, this);

    }



}
