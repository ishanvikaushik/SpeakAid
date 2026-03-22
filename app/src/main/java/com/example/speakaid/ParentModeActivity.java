package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ParentModeActivity extends AppCompatActivity {

    Button btnAddRoutine, btnAddScript, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_mode);

        setTitle("Caregiver Mode");

        btnAddRoutine = findViewById(R.id.btnAddRoutine);
        btnAddScript = findViewById(R.id.btnAddScript);
        btnExit = findViewById(R.id.btnExit);

        btnAddRoutine.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCustomActivity.class);
            intent.putExtra("type", "routine");
            startActivity(intent);
        });

        btnAddScript.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCustomActivity.class);
            intent.putExtra("type", "script");
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> finish());
    }
}
