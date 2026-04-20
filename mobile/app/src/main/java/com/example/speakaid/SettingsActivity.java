package com.example.speakaid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private MaterialSwitch switchSound, switchVibration, switchMotion;
    private FrameLayout btnChangePasscodeFrame;
    private EditText editPhone;
    private Button btnSavePhone;
    private ImageButton btnBack;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        ThemeHelper.applyTheme(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchSound = findViewById(R.id.switchSound);
        switchVibration = findViewById(R.id.switchVibration);
        switchMotion = findViewById(R.id.switchMotion);
        btnChangePasscodeFrame = findViewById(R.id.btnChangePasscodeFrame);
        btnBack = findViewById(R.id.btnBack);
        editPhone = findViewById(R.id.editPhone);
        btnSavePhone = findViewById(R.id.btnSavePhone);

        // Load saved values
        switchSound.setChecked(prefs.getBoolean("sound", true));
        switchVibration.setChecked(prefs.getBoolean("vibration", false));
        switchMotion.setChecked(prefs.setChecked(prefs.getBoolean("motion", false)));
        editPhone.setText(prefs.getString("emergency_phone", ""));

        // Save changes for switches
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("sound", isChecked).apply()
        );

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("vibration", isChecked).apply()
        );

        switchMotion.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("motion", isChecked).apply()
        );

        // Save phone number
        btnSavePhone.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("emergency_phone", phone).apply();
                Toast.makeText(this, "Emergency contact saved!", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePasscodeFrame.setOnClickListener(v -> showChangePasscodeDialog());
        btnBack.setOnClickListener(v -> finish());
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
}
