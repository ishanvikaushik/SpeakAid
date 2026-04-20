package com.example.speakaid;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ZenCanvasActivity extends AppCompatActivity {

    private ScratchView scratchView;
    private Button btnReset;
    private ImageButton btnBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen_canvas);

        scratchView = findViewById(R.id.scratchView);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        btnReset.setOnClickListener(v -> scratchView.reset());
        btnBack.setOnClickListener(v -> finish());
    }
}
