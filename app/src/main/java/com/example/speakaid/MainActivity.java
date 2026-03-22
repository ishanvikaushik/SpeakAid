package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnRoutines, btnScripts;
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DBHelper db = new DBHelper(this);

        if (db.isStepTableEmpty()) {

            db.insertStep(1, "Wake up", 1);
            db.insertStep(1, "Brush teeth", 2);
            db.insertStep(1, "Get dressed", 3);

            db.insertStep(2, "Pack school bag", 1);
            db.insertStep(2, "Wear uniform", 2);

            db.insertStep(3, "Brush teeth", 1);
            db.insertStep(3, "Go to bed", 2);

            // Scripts
            db.insertScript("Say Hello");
            db.insertScript("Ask for Help");

// Script Steps

// Say Hello (id = 1)
            db.insertScriptStep(1, "Look at the person", 1);
            db.insertScriptStep(1, "Smile if comfortable", 2);
            db.insertScriptStep(1, "Say hello", 3);
            db.insertScriptStep(1, "Ask how they are", 4);

// Ask for Help (id = 2)
            db.insertScriptStep(2, "Look at the person", 1);
            db.insertScriptStep(2, "Get their attention", 2);
            db.insertScriptStep(2, "Ask politely", 3);
            db.insertScriptStep(2, "Explain what you need", 4);
        }


        btnRoutines = findViewById(R.id.btnRoutines);
        btnScripts = findViewById(R.id.btnScripts);
        btnSettings=findViewById(R.id.btnSettings);

        btnRoutines.setOnClickListener(v -> {
            startActivity(new Intent(this, RoutineListActivity.class));
        });

        btnScripts.setOnClickListener(v -> {
            startActivity(new Intent(this, ScriptListActivity.class));
        });
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }
}