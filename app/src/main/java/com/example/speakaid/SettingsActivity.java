package com.example.speakaid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch switchSound, switchVibration, switchMotion;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchSound = findViewById(R.id.switchSound);
        switchVibration = findViewById(R.id.switchVibration);
        switchMotion = findViewById(R.id.switchMotion);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Load saved values
        switchSound.setChecked(prefs.getBoolean("sound", true));
        switchVibration.setChecked(prefs.getBoolean("vibration", false));
        switchMotion.setChecked(prefs.getBoolean("motion", false));

        // Save changes
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("sound", isChecked).apply()
        );

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("vibration", isChecked).apply()
        );

        switchMotion.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("motion", isChecked).apply()
        );
    }
}