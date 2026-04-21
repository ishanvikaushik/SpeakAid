package com.example.speakaid;

import android.database.Cursor;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
        // 1. Get earned badge counts from DB
        Map<String, Integer> earnedBadges = new HashMap<>();
        try (Cursor cursor = db.getAllBadges()) {
            while (cursor.moveToNext()) {
                earnedBadges.put(cursor.getString(1).toLowerCase(), cursor.getInt(2));
            }
        }

        String[] fixedKeywords = {"morning", "night", "school", "laundry"};
        String[] displayTitles = {"Morning Routine", "Night Routine", "School Routine", "Laundry Routine"};
        int[] fixedIcons = {R.drawable.morning, R.drawable.night, R.drawable.school, R.drawable.laundry};

        int earnedCountTotal = 0;
        badgeGrid.removeAllViews();

        // 2. Add fixed badges with flexible matching
        for (int i = 0; i < fixedKeywords.length; i++) {
            String keyword = fixedKeywords[i];
            int count = 0;
            
            // Check if any earned routine title contains the keyword
            for (Map.Entry<String, Integer> entry : earnedBadges.entrySet()) {
                if (entry.getKey().contains(keyword)) {
                    count += entry.getValue();
                }
            }

            if (count > 0) earnedCountTotal++;
            addBadgeToGrid(displayTitles[i], count, fixedIcons[i]);
        }

        // 3. Handle Custom Routine Badge
        int customCount = 0;
        for (Map.Entry<String, Integer> entry : earnedBadges.entrySet()) {
            boolean isFixed = false;
            for (String kw : fixedKeywords) {
                if (entry.getKey().contains(kw)) {
                    isFixed = true;
                    break;
                }
            }
            if (!isFixed) {
                customCount += entry.getValue();
            }
        }
        
        if (customCount > 0) earnedCountTotal++;
        addBadgeToGrid("Custom Routine", customCount, R.drawable.custom);

        // Update stats
        txtEarnedCount.setText(String.valueOf(earnedCountTotal));
        txtLockedCount.setText(String.valueOf(5 - earnedCountTotal));
    }

    private void addBadgeToGrid(String name, int count, int iconRes) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_badge, null);
        
        ImageView img = view.findViewById(R.id.imgBadge);
        TextView txtName = view.findViewById(R.id.txtBadgeName);
        TextView txtCount = view.findViewById(R.id.txtBadgeCount);

        txtName.setText(name);
        img.setImageResource(iconRes);

        if (count > 0) {
            // Unlocked: Full color
            img.clearColorFilter();
            img.setAlpha(1.0f);
            txtCount.setText("Earned x" + count);
            txtCount.setTextColor(0xFFF57F17);
            txtName.setTextColor(0xFF333333);
        } else {
            // Locked: Grayscale and faint
            img.setAlpha(0.3f);
            
            // Apply a grayscale filter instead of a solid tint
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0); 
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            img.setColorFilter(filter);
            
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
