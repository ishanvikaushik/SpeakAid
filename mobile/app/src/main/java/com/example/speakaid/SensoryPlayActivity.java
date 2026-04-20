package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SensoryPlayActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_sensory_hub);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        findViewById(R.id.btnGrounding).setOnClickListener(v -> 
            startActivity(new Intent(this, GroundingActivity.class))
        );

        findViewById(R.id.btnZenCanvas).setOnClickListener(v -> 
            startActivity(new Intent(this, ZenCanvasActivity.class))
        );

        findViewById(R.id.btnFidgetSpinner).setOnClickListener(v -> 
            startActivity(new Intent(this, FidgetSpinnerActivity.class))
        );
    }
}
