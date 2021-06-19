package com.telogix.telogixcaptain.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Utils.connection;

import org.json.JSONException;

import java.util.Arrays;

public class AutocompleteActivity extends BaseActivity {


    private TextView txtView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete);

        txtView = findViewById(R.id.txtView);

        // Initialize Places.

        Places.initialize(getApplicationContext(), connection.GoogleAPIKEY);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                txtView.setText(place.getName()+","+place.getId());

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("error",""+status);
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_autocomplete;
    }

    @Override
    public void onResponse(String response) throws JSONException {

    }

    @Override
    public void onError(VolleyError Error) {

    }
}
