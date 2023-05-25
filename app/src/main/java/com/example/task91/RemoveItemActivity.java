package com.example.task91;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import androidx.appcompat.app.AppCompatActivity;

public class RemoveItemActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private Button removeButton;
    private ItemDatabaseHelper dbHelper;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_the_item);

        // Get references to the views
        nameTextView = findViewById(R.id.name_text_view);
        phoneTextView = findViewById(R.id.phone_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        locationTextView = findViewById(R.id.city_text_view);
        removeButton = findViewById(R.id.remove_button);

        // Get the selected item from the intent
        item = (Item) getIntent().getSerializableExtra("item");

        // Initialize the database helper
        dbHelper = new ItemDatabaseHelper(this);

        // Set the text of the views to the item's properties
        nameTextView.setText("Name: " + item.getName());
        phoneTextView.setText("Phone: " + item.getPhone());
        descriptionTextView.setText("Description: " + item.getDescription());
        dateTextView.setText("Date: " + item.getDate());
        //locationTextView.setText("Location: " + item.getLocation());

        // Set the click listener for the remove button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the database
                dbHelper.removeItem(item);

                // Return to the ShowItemsActivity
                Intent intent = new Intent(RemoveItemActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Clear the references and finish the activity
        dbHelper.close();
        dbHelper = null;
        item = null;
        super.onBackPressed();
    }
}
