package com.telogix.telogixcaptain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;

import org.json.JSONException;

public class RecoverPasswordActivity  extends BaseActivity
{

    EditText etEmail;
    Button btnReset;
    TextView txtback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        // Views binding
        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnResetPswd);
        txtback = findViewById(R.id.txtBack);

        // onClick events
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecoverPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecoverPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_recover_password;
    }


    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
