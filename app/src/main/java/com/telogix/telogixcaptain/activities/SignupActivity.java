package com.telogix.telogixcaptain.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Enums.ResponseCode;
import com.telogix.telogixcaptain.Pojo.DecentingPojo.Datum;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SignupActivity extends BaseActivity {

    ArrayList userRoleslist = new ArrayList();
    EditText edtusername, edtEmail, edtPass, edtConfirmpass;
    Button btnRegister;
    private Spinner spinner;
    private SearchableSpinner spinnerDecanting_Haulier;
    private ArrayList<Datum> decantingList = new ArrayList();
    private ArrayList<Datum>  haulierList = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign up");
//        userRoleslist.add("SuperAdmin");
        userRoleslist.add("JourneyManager");
        userRoleslist.add("Retailer");
        userRoleslist.add("Driver");

        edtusername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        edtConfirmpass = findViewById(R.id.edtConfirmPass);
        spinner = findViewById(R.id.spinneruserRoles);
        spinnerDecanting_Haulier = findViewById(R.id.spinnerDecanting_Haulier);
        btnRegister = findViewById(R.id.btnRegister);

        spinnerDecanting_Haulier.setPositiveButton("OK");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtusername.getText().toString().equals("") && !edtEmail.getText().toString().equals("") &&
                        !edtPass.getText().toString().equals("") && !edtConfirmpass.getText().toString().equals("")
                ) {
                    if (edtPass.getText().toString().equals(edtConfirmpass.getText().toString())) {
                        registerUser();
                    } else {
                        Toast.makeText(SignupActivity.this, "Password Mismatch", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        spinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, userRoleslist));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) // Journey Manager
                {
                    spinnerDecanting_Haulier.setVisibility(View.VISIBLE);
                    spinnerDecanting_Haulier.setAdapter(new CustomAdapter(SignupActivity.this,R.layout.layout_haulier_list,haulierList));
                } else if (i == 1) // Retailer
                {
                    spinnerDecanting_Haulier.setVisibility(View.VISIBLE);
                    spinnerDecanting_Haulier.setAdapter(new CustomAdapter(SignupActivity.this,R.layout.layout_haulier_list,decantingList));

                } else {
                    spinnerDecanting_Haulier.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerDecanting_Haulier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
           //     Toast.makeText(SignupActivity.this, ""+spinnerDecanting_Haulier.getSelectedItemId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHauliers();
        getUnallocatedDecantings();
    }

    private void getUnallocatedDecantings() {
        //NetworkConsume.getInstance().ShowProgress(this);
        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/DecantingSites/GetDecantingSiteForSignup", Request.Method.GET, data, this);
    }

    private void getHauliers() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap data = new HashMap();
        //  data.put("DeviceID",new Constant(this).getAndroid_id());
        httpvolley.stringrequestpost("api/Hauliers/GetHauliersForDDL", Request.Method.GET, data, this);
    }


    private void registerUser() {
        NetworkConsume.getInstance().ShowProgress(this);
        HashMap data = new HashMap();
        data.put("UserName", edtusername.getText().toString());
        data.put("Email", edtEmail.getText().toString());
//        data.put("CompanyID","1");
        //   data.put("COmpanyID","1");
        data.put("Password", edtPass.getText().toString());
        data.put("ConfirmPassword", edtConfirmpass.getText().toString());
        data.put("UserRoles", spinner.getSelectedItem().toString().trim());
        String driverCheck = spinner.getSelectedItem().toString().trim();
        if(driverCheck != "Driver"){data.put("TypeID", ""+spinnerDecanting_Haulier.getSelectedItemId());}
        httpvolley.stringrequestpost("api/Account/Register", Request.Method.POST, data, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_signup;
    }

    @Override
    public void onResponse(String response) {
        NetworkConsume.getInstance().hideProgress();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("ResponseCode")) {
                if(jsonObject.get("ResponseCode").equals(400)){
                  //  Toast.makeText(getApplicationContext(),"--Response"+jsonObject.toString(),Toast.LENGTH_SHORT).show();
                }
                if (jsonObject.get("ResponseCode").equals(2001)) {
                    Toast.makeText(this, "User Registered", Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.DECANTINGFORSIGNUP))) {
                    JSONArray array = jsonObject.getJSONArray("Data");
                    decantingList = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        Datum datum=new Datum();
                        datum.setDecantingSiteID(array.getJSONObject(i).getInt("ID"));
                        datum.setDecantingSite(array.getJSONObject(i).getString("Name"));
                        decantingList.add(datum);
                    }
                } else if (jsonObject.get("ResponseCode").equals(ResponseCode.getResponseCode(ResponseCode.HAULIERSLIST))) {
                    JSONArray array = jsonObject.getJSONArray("Data");
                    haulierList = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        Datum datum=new Datum();
                        datum.setDecantingSiteID(array.getJSONObject(i).getInt("ID"));
                        datum.setDecantingSite(array.getJSONObject(i).getString("Name"));
                        haulierList.add(datum);
                    }
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onError(VolleyError Error) {
       Log.d("response", "" + Error);
        NetworkConsume.getInstance().hideProgress();
    }

    public class CustomAdapter extends ArrayAdapter {
        ArrayList<Datum> list;
        LayoutInflater layoutInflater;

        public CustomAdapter(@NonNull Context context, int resource,  @NonNull ArrayList<Datum> objects) {
            super(context, resource, objects);
            this.list=objects;
            layoutInflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

//        public CustomAdapter(ArrayList<Datum> list) {
//            this.list = list;
//            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i).getDecantingSite();
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).getDecantingSiteID();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = layoutInflater.inflate(R.layout.layout_haulier_list, null, false);
            TextView Name=v.findViewById(R.id.textView9);
            Name.setText(list.get(position).getDecantingSite());
            return v;

        }
    }
}

