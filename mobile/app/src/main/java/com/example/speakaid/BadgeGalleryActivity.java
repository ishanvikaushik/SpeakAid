package com.example.speakaid;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class BadgeGalleryActivity extends AppCompatActivity {

    private GridLayout badgeGrid;
    private TextView txtEarnedCount, txtLockedCount;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_gallery);

        db = new DBHelper(this);
        badgeGrid = findViewById(R.id.badgeGrid);
        txtEarnedCount = findViewById(R.id.txtEarnedCount);
        txtLockedCount = findViewById(R.id.txtLockedCount);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadBadges();
    }

    private void loadBadges() {
        // 1. Get earned badge counts
        Map<String, Integer> earnedBadges = new HashMap<>();
        try (Cursor cursor = db.getAllBadges()) {
            while (cursor.moveToNext()) {
                earnedBadges.put(cursor.getString(1), cursor.getInt(2));
            }
        }

        int earnedCount = 0;
        int lockedCount = 0;

        // 2. Get all possible routines (available badges)
        try (Cursor cursor = db.getRoutines()) {
            while (cursor.moveToNext()) {
                String routineTitle = cursor.getString(1);
                int count = earnedBadges.getOrDefault(routineTitle, 0);
                
                if (count > 0) earnedCount++;
                else lockedCount++;

                addBadgeToGrid(routineTitle, count);
            }
        }

        // Update stats card
        txtEarnedCount.setText(String.valueOf(earnedCount));
        txtLockedCount.setText(String.valueOf(lockedCount));
    }

    private void addBadgeToGrid(String name, int count) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_badge, null);
        
        ImageView img = view.findViewById(R.id.imgBadge);
        TextView txtName = view.findViewById(R.id.txtBadgeName);
        TextView txtCount = view.findViewById(R.id.txtBadgeCount);

        txtName.setText(name);

        // Assigning specific images based on routine name
        int badgeIcon = R.drawable.ic_yes; // Default
        String lowerName = name.toLowerCase();

        if (lowerName.contains("morning")) badgeIcon = R.drawable.ic_happy;
        else if (lowerName.contains("school")) badgeIcon = R.drawable.ic_scripts;
        else if (lowerName.contains("night")) badgeIcon = R.drawable.ic_toilet;
        else if (lowerName.contains("brush")) badgeIcon = R.drawable.ic_water;
        
        img.setImageResource(badgeIcon);

        if (count > 0) {
            // Unlocked state
            img.setColorFilter(null);
            img.setAlpha(1.0f);
            txtCount.setText("Earned x" + count);
            txtCount.setTextColor(0xFFF57F17); // Orange
            txtName.setTextColor(0xFF333333);
        } else {
            // Locked state (Grayed out)
            img.setAlpha(0.15f);
            img.setColorFilter(0xFFAAAAAA, android.graphics.PorterDuff.Mode.SRC_ATOP);
            txtCount.setText("Locked");
            txtCount.setTextColor(0xFFBDBDBD);
        }

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(16, 16, 16, 16);
        view.setLayoutParams(params);

        badgeGrid.addView(view);
    }
}
