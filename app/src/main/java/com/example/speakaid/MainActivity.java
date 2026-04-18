package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    View btnRoutines, btnScripts, btnSettings, btnCommunicate, btnSensory;
    TextView btnParentMode;
    SharedPreferences prefs;
    String currentTheme,currentLanguage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        
        currentTheme = prefs.getString("theme", "classic");
        currentLanguage=LocaleHelper.getLanguage(this);

        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DBHelper db = new DBHelper(this);

        try (Cursor cursor = db.getScripts()) {
            if (cursor.getCount() == 0) {
                seedData(db);
            }
        }

        btnRoutines = findViewById(R.id.btnRoutines);
        btnScripts = findViewById(R.id.btnScripts);
        btnSettings = findViewById(R.id.btnSettings);
        btnCommunicate = findViewById(R.id.btnCommunicate);
        btnSensory = findViewById(R.id.btnSensory);
        btnParentMode = findViewById(R.id.btnParentMode);

        btnRoutines.setOnClickListener(v -> startActivity(new Intent(this, RoutineListActivity.class)));
        btnScripts.setOnClickListener(v -> startActivity(new Intent(this, ScriptListActivity.class)));
        btnCommunicate.setOnClickListener(v -> startActivity(new Intent(this, CommunicateActivity.class)));
        btnSensory.setOnClickListener(v -> startActivity(new Intent(this, SensoryPlayActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        btnParentMode.setOnClickListener(v -> showPasscodeDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String savedTheme = prefs.getString("theme", "classic");
        String savedLanguage=LocaleHelper.getLanguage(this);

        if (!savedTheme.equals(currentTheme)||!savedLanguage.equals(currentLanguage)) {
            currentTheme=savedTheme;
            currentLanguage=savedLanguage;
            recreate();
        }
    }

    private void showPasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.caregiver_control));

        String savedPasscode = prefs.getString("passcode", "1234");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint(getString(R.string.change_passcode));
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            String enteredPasscode = input.getText().toString();
            if (enteredPasscode.equals(savedPasscode)) {
                startActivity(new Intent(MainActivity.this, ParentModeActivity.class));
            } else {
                Toast.makeText(MainActivity.this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void seedData(DBHelper db) {
        db.insertRoutine(getString(R.string.morning_routine));
        db.insertRoutine(getString(R.string.school_routine));
        db.insertRoutine(getString(R.string.night_routine));

        db.insertStep(1, getString(R.string.wake_up), 1);
        db.insertStep(1, getString(R.string.brush_teeth), 2);
        db.insertStep(1, getString(R.string.get_dressed), 3);

        db.insertStep(2, getString(R.string.pack_bag), 1);
        db.insertStep(2, getString(R.string.wear_uniform), 2);

        db.insertStep(3, getString(R.string.brush_teeth), 1);
        db.insertStep(3, getString(R.string.go_to_bed), 2);

        db.insertScript(getString(R.string.say_hello));
        db.insertScript(getString(R.string.ask_help));

        db.insertScriptStep(1, getString(R.string.look_person), 1);
        db.insertScriptStep(1, getString(R.string.smile_if_comfy), 2);
        db.insertScriptStep(1, getString(R.string.say_hello), 3);
        db.insertScriptStep(1, getString(R.string.ask_how_are), 4);

        db.insertScriptStep(2, getString(R.string.look_person), 1);
        db.insertScriptStep(2, getString(R.string.get_attention), 2);
        db.insertScriptStep(2, getString(R.string.ask_politely), 3);
        db.insertScriptStep(2, getString(R.string.explain_need), 4);
    }
}
