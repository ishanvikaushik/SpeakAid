package com.example.speakaid;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ZenCanvasActivity extends AppCompatActivity {

    private ScratchView scratchView;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensory_play);

        scratchView = findViewById(R.id.scratchView);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> scratchView.reset());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Zen Canvas");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
