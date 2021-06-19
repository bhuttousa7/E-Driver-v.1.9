package com.telogix.telogixcaptain.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.telogix.telogixcaptain.R;

import org.json.JSONException;

import java.io.FileInputStream;

public class zoomedImageActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_image);
        ImageView zoomedImage=findViewById(R.id.zoomedImage);


        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            zoomedImage.setImageBitmap(bmp);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_zoomed_image;
    }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
