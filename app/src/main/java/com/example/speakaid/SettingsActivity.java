package com.example.speakaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    MaterialSwitch switchSound, switchVibration, switchMotion;
    FrameLayout btnChangePasscodeFrame, btnChangeLang;
    MaterialCardView themeClassic, themeLavender, themeOcean, themeSunset;
    ImageView btnBack;
    SharedPreferences prefs;
// new fn
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

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
        btnChangeLang = findViewById(R.id.btnChangeLang);
        btnBack = findViewById(R.id.btnBack);
        
        themeClassic = findViewById(R.id.themeClassic);
        themeLavender = findViewById(R.id.themeLavender);
        themeOcean = findViewById(R.id.themeOcean);
        themeSunset = findViewById(R.id.themeSunset);

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

        // Theme clicks
        themeClassic.setOnClickListener(v -> saveTheme("classic"));
        themeLavender.setOnClickListener(v -> saveTheme("lavender"));
        themeOcean.setOnClickListener(v -> saveTheme("ocean"));
        themeSunset.setOnClickListener(v -> saveTheme("sunset"));

        btnChangePasscodeFrame.setOnClickListener(v -> showChangePasscodeDialog());

        // Language toggle
        btnChangeLang.setOnClickListener(v -> {
            String currentLang = LocaleHelper.getLanguage(this);
            if ("en".equals(currentLang)) {
                LocaleHelper.persist(this, "kn");
            } else {
                LocaleHelper.persist(this, "en");
            }
            recreate(); 
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void saveTheme(String themeName) {
        prefs.edit().putString("theme", themeName).apply();
        Toast.makeText(this, getString(R.string.theme_updated), Toast.LENGTH_SHORT).show();
        recreate(); 
    }

    private void showChangePasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.change_passcode));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint(getString(R.string.change_passcode));
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
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
