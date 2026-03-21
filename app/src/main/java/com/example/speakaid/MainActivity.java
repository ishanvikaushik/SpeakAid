package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnRoutines, btnScripts;

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
        }

        if (db.isStepTableEmpty()) {

            db.insertStep(1, "Wake up", 1);
            db.insertStep(1, "Brush teeth", 2);
            db.insertStep(1, "Get dressed", 3);

            db.insertStep(2, "Pack school bag", 1);
            db.insertStep(2, "Wear uniform", 2);

            db.insertStep(3, "Brush teeth", 1);
            db.insertStep(3, "Go to bed", 2);
        }
        btnRoutines = findViewById(R.id.btnRoutines);
        btnScripts = findViewById(R.id.btnScripts);

        btnRoutines.setOnClickListener(v -> {
            startActivity(new Intent(this, RoutineListActivity.class));
        });

        btnScripts.setOnClickListener(v -> {
            startActivity(new Intent(this, ScriptListActivity.class));
        });
    }
}