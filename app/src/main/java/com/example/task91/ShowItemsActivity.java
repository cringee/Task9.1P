package com.example.task91;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ShowItemsActivity extends AppCompatActivity {
    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_lost_and_found_items);

        // Get reference to the items container
        itemsContainer = findViewById(R.id.items_container);

        // Populate the items container with items from the database
        populateItems();
    }




    private void populateItems() {
        // Clear the items container
        itemsContainer.removeAllViews();

        // Get all items from the database
        ItemDatabaseHelper dbHelper = new ItemDatabaseHelper(this);
        List<Item> items = dbHelper.getAllItems();

        // Add each item to the container as a clickable TextView
        for (Item item : items) {
            TextView itemView = new TextView(this);
            itemView.setText(item.getPostType() + ": " + item.getName());
            itemView.setTextSize(18);
            itemView.setPadding(16, 16, 16, 16);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch the RemoveItemActivity with the selected item
                    Intent intent = new Intent(ShowItemsActivity.this, RemoveItemActivity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            });
            itemsContainer.addView(itemView);
        }
    }
}