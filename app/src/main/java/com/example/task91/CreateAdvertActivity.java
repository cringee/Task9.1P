package com.example.task91;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class CreateAdvertActivity extends AppCompatActivity {
    private ItemDatabaseHelper databaseHelper;
    private static final int REQUEST_CODE_MAP = 1;
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_a_new_advert);

        // Initialize the database helper
        databaseHelper = new ItemDatabaseHelper(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.edit_text_location);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Retrieve the selected location data from the Place object
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                // Update the EditText with the selected location
                EditText selectedLocationEditText = findViewById(R.id.edit_text_selected_location);
                selectedLocationEditText.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        // Set up submit button click listener
        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reference to the radio group
                RadioGroup radioGroup = findViewById(R.id.radio_group_post_type);

                // Get the ID of the checked radio button
                String postType = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                String name = ((EditText) findViewById(R.id.edit_text_name)).getText().toString();
                String phone = ((EditText) findViewById(R.id.edit_text_phone)).getText().toString();
                String description = ((EditText) findViewById(R.id.edit_text_description)).getText().toString();
                String date = ((EditText) findViewById(R.id.edit_text_date)).getText().toString();
                //String location = ((EditText) findViewById(R.id.edit_text_location)).getText().toString();

                // Validate form
                if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty()) {
                    Toast.makeText(CreateAdvertActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save post to database
                    Item newItem = new Item(postType, name, phone, description, date);
                    newItem.setLatitude(latitude);
                    newItem.setLongitude(longitude);
                    long itemId = databaseHelper.addItem(newItem);
                    newItem.setId(itemId);

                    // Show success message
                    Toast.makeText(CreateAdvertActivity.this, "Post submitted successfully", Toast.LENGTH_SHORT).show();

                    // Clear the form fields
                    ((EditText) findViewById(R.id.edit_text_name)).setText("");
                    ((EditText) findViewById(R.id.edit_text_phone)).setText("");
                    ((EditText) findViewById(R.id.edit_text_description)).setText("");
                    ((EditText) findViewById(R.id.edit_text_date)).setText("");

                    // Return to main activity
                    Intent intent = new Intent(CreateAdvertActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Set up select location button click listener
        Button selectLocationButton = findViewById(R.id.select_location_button);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permission
                if (ContextCompat.checkSelfPermission(CreateAdvertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CreateAdvertActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_MAP);
                } else {
                    // Permission already granted, start location updates
                    startLocationUpdates();
                    updateSelectedLocation();
                }
            }
        });
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    // Get the last known location
                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateSelectedLocation() {
        // Get the address based on latitude and longitude
        Geocoder geocoder = new Geocoder(CreateAdvertActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                // Update the EditText with the selected location
                EditText selectedLocationEditText = findViewById(R.id.edit_text_selected_location);
                selectedLocationEditText.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database helper
        databaseHelper.close();

        // Stop location updates
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}
