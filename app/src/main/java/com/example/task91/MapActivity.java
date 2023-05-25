package com.example.task91;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private Marker selectedMarker;
    private ItemDatabaseHelper databaseHelper;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the database helper
        databaseHelper = new ItemDatabaseHelper(this);

        // Retrieve all items from the database
        itemList = databaseHelper.getAllItems();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set a long click listener on the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                // Remove previous marker if exists
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }

                // Add a new marker at the selected location
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng);
                selectedMarker = mMap.addMarker(markerOptions);
            }
        });

        // Set the map click listener
        mMap.setOnMarkerClickListener(this);

        // Enable the zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set the initial camera position
        LatLng defaultLocation = new LatLng(-37.840935, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        // Add markers for all items in the database
        addMarkersForItems();
    }

    private void addMarkersForItems() {
        for (Item item : itemList) {
            LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(item.getName()).snippet("Phone Number: " + item.getPhone() + ", Type: " + item.getPostType()));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the corresponding item for the clicked marker
        Item clickedItem = null;
        for (Item item : itemList) {
            LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
            if (latLng.equals(marker.getPosition())) {
                clickedItem = item;
                break;
            }
        }

        // Display the information for the clicked item
        if (clickedItem != null) {
            Toast.makeText(this, "Item Name: " + clickedItem.getName(), Toast.LENGTH_SHORT).show();
        }

        // Return false to allow default marker behavior (e.g., show info window if set)
        return false;
    }

    @Override
    public void onBackPressed() {
        if (selectedMarker != null) {
            // Pass the selected location data back to the calling activity
            LatLng position = selectedMarker.getPosition();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", position.latitude);
            resultIntent.putExtra("longitude", position.longitude);
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database helper
        databaseHelper.close();
    }
}