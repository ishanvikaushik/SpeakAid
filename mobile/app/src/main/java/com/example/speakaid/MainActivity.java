package com.example.speakaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private View btnRoutines, btnScripts, btnCommunicate, btnSensory, btnHelp, btnSettings;
    private ImageButton btnBack;
    private ImageView imgStatusMascot;
    private MaterialCardView btnViewBadges;
    private TextView txtStreak, txtBadges;
    private SharedPreferences prefs;
    private DBHelper db;
    private String currentTheme;
    
    private Handler mascotHandler;
    private Runnable mascotRunnable;
    private boolean isAwake = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        currentTheme = prefs.getString("theme", "classic");
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DBHelper(this);

        // Bind views
        btnRoutines = findViewById(R.id.btnRoutines);
        btnScripts = findViewById(R.id.btnScripts);
        btnCommunicate = findViewById(R.id.btnCommunicate);
        btnSensory = findViewById(R.id.btnSensory);
        btnHelp = findViewById(R.id.btnHelp);
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnViewBadges = findViewById(R.id.btnViewBadges);
        imgStatusMascot = findViewById(R.id.imgStatusMascot); // Added ID in XML
        txtStreak = findViewById(R.id.txtStreak);
        txtBadges = findViewById(R.id.txtBadges);

        // Navigation
        btnRoutines.setOnClickListener(v -> startActivity(new Intent(this, RoutineListActivity.class)));
        btnScripts.setOnClickListener(v -> startActivity(new Intent(this, ScriptListActivity.class)));
        btnCommunicate.setOnClickListener(v -> startActivity(new Intent(this, CommunicateActivity.class)));
        btnSensory.setOnClickListener(v -> startActivity(new Intent(this, SensoryPlayActivity.class)));
        btnHelp.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnViewBadges.setOnClickListener(v -> startActivity(new Intent(this, BadgeGalleryActivity.class)));
        
        btnBack.setOnClickListener(v -> finish());

        updateStats();
        startMascotBlinkAnimation();
    }

    private void startMascotBlinkAnimation() {
        mascotHandler = new Handler();
        mascotRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAwake) {
                    imgStatusMascot.setImageResource(R.drawable.blink);
                } else {
                    imgStatusMascot.setImageResource(R.drawable.awake);
                }
                isAwake = !isAwake;
                mascotHandler.postDelayed(this, 2000); // 2 seconds interval
            }
        };
        mascotHandler.post(mascotRunnable);
    }

    private void updateStats() {
        int streak = db.getStreak();
        txtStreak.setText(streak + " Day Streak!");
        int starCount = db.getTotalStars();
        txtBadges.setText("⭐ " + starCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
        String savedTheme = prefs.getString("theme", "classic");
        if (!savedTheme.equals(currentTheme)) recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mascotHandler != null) mascotHandler.removeCallbacks(mascotRunnable);
    }
}
