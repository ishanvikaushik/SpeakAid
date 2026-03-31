package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SensoryPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensory_hub);

        ThemeHelper.applyTheme(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        findViewById(R.id.btnZenCanvas).setOnClickListener(v -> 
            startActivity(new Intent(this, ZenCanvasActivity.class))
        );

        findViewById(R.id.btnFidgetSpinner).setOnClickListener(v -> 
            startActivity(new Intent(this, FidgetSpinnerActivity.class))
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sensory Hub");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
