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

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();
        DBHelper db = new DBHelper(this);

// Insert only if empty (basic check)
        db.insertStep(1, "Wake up", 1);
        db.insertStep(1, "Brush teeth", 2);
        db.insertStep(1, "Get dressed", 3);

        db.insertStep(2, "Pack school bag", 1);
        db.insertStep(2, "Wear uniform", 2);

        db.insertStep(3, "Brush teeth", 1);
        db.insertStep(3, "Go to bed", 2);

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