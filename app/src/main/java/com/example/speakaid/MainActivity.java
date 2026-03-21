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