package com.example.speakaid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch switchSound, switchVibration, switchMotion;
    Button btnChangePasscode;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");
        switchSound = findViewById(R.id.switchSound);
        switchVibration = findViewById(R.id.switchVibration);
        switchMotion = findViewById(R.id.switchMotion);
        btnChangePasscode = findViewById(R.id.btnChangePasscode);

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

        btnChangePasscode.setOnClickListener(v -> showChangePasscodeDialog());

        // for back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showChangePasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set New Caregiver Passcode");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Enter 4-digit numeric passcode");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPasscode = input.getText().toString().trim();
            if (newPasscode.length() >= 4) {
                prefs.edit().putString("passcode", newPasscode).apply();
                Toast.makeText(this, "Passcode updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Passcode must be at least 4 digits", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // for back navigation
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
