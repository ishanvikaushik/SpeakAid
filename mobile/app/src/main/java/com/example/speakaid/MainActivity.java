package com.example.speakaid;

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

    View btnRoutines, btnScripts, btnSettings, btnCommunicate, btnSensory, btnHelp;
    TextView btnParentMode;
    SharedPreferences prefs;
    String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        
        currentTheme = prefs.getString("theme", "classic");
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
        btnHelp = findViewById(R.id.btnHelp);
        btnParentMode = findViewById(R.id.btnParentMode);

        btnRoutines.setOnClickListener(v -> startActivity(new Intent(this, RoutineListActivity.class)));
        btnScripts.setOnClickListener(v -> startActivity(new Intent(this, ScriptListActivity.class)));
        btnCommunicate.setOnClickListener(v -> startActivity(new Intent(this, CommunicateActivity.class)));
        btnSensory.setOnClickListener(v -> startActivity(new Intent(this, SensoryPlayActivity.class)));
        btnHelp.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        btnParentMode.setOnClickListener(v -> showPasscodeDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String savedTheme = prefs.getString("theme", "classic");
        if (!savedTheme.equals(currentTheme)) {
            recreate();
        }
    }

    private void showPasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caregiver Login");
        builder.setMessage("Select your mode:");

        builder.setPositiveButton("Caregiver Control", (dialog, which) -> {
            showCaregiverPasscodeDialog();
        });

        builder.setNeutralButton("Communication Hub", (dialog, which) -> {
            startActivity(new Intent(MainActivity.this, ChatEntryActivity.class));
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showCaregiverPasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Passcode");

        String savedPasscode = prefs.getString("passcode", "1234");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (input.getText().toString().equals(savedPasscode)) {
                startActivity(new Intent(MainActivity.this, ParentModeActivity.class));
            } else {
                Toast.makeText(this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void seedData(DBHelper db) {
        db.insertRoutine("Morning Routine");
        db.insertRoutine("School Routine");
        db.insertRoutine("Night Routine");

        db.insertStep(1, "Wake up", 1);
        db.insertStep(1, "Brush teeth", 2);
        db.insertStep(1, "Get dressed", 3);

        db.insertStep(2, "Pack school bag", 1);
        db.insertStep(2, "Wear uniform", 2);

        db.insertStep(3, "Brush teeth", 1);
        db.insertStep(3, "Go to bed", 2);

        db.insertScript("Say Hello");
        db.insertScript("Ask for Help");

        db.insertScriptStep(1, "Look at the person", 1);
        db.insertScriptStep(1, "Smile if comfortable", 2);
        db.insertScriptStep(1, "Say hello", 3);
        db.insertScriptStep(1, "Ask how they are", 4);

        db.insertScriptStep(2, "Look at the person", 1);
        db.insertScriptStep(2, "Get their attention", 2);
        db.insertScriptStep(2, "Ask politely", 3);
        db.insertScriptStep(2, "Explain what you need", 4);
    }
}
