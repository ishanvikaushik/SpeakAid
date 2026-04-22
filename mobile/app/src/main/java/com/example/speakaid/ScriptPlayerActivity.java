package com.example.speakaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class ScriptPlayerActivity extends AppCompatActivity {

    TextView txtStep, txtProgress, txtTransition, btnNextText;
    FrameLayout btnNextFrame, btnPrevFrame;
    LinearProgressIndicator progressIndicator;
    ImageView imgStep, btnClose;

    ArrayList<String> steps;
    int currentStep = 0;
    int scriptId;
    String scriptTitle;
    boolean isCompleted = false;

    ElevenLabsManager elevenLabs = new ElevenLabsManager();
    SharedPreferences prefs;
    KonfettiView konfettiView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_player);

        txtStep = findViewById(R.id.txtStep);
        txtProgress = findViewById(R.id.txtProgress);
        txtTransition = findViewById(R.id.txtTransition);
        btnNextFrame = findViewById(R.id.btnNextFrame);
        btnNextText = findViewById(R.id.btnNextText);
        btnPrevFrame = findViewById(R.id.btnPrevFrame);
        progressIndicator = findViewById(R.id.progressIndicator);
        imgStep = findViewById(R.id.imgStep);
        btnClose = findViewById(R.id.btnClose);
        konfettiView = findViewById(R.id.confettiView);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        scriptId = getIntent().getIntExtra("scriptId", -1);
        scriptTitle = getScriptTitle(scriptId);

        steps = new ArrayList<>();
        try (DBHelper db = new DBHelper(this);
             Cursor cursor = db.getScriptSteps(scriptId)) {
            while (cursor.moveToNext()) {
                steps.add(cursor.getString(2));
            }
        }

        btnNextFrame.setOnClickListener(v -> {
            if (isCompleted) {
                finish();
                return;
            }

            if (steps.isEmpty()) return;
            if (currentStep < steps.size() - 1) {
                int nextIndex = currentStep + 1;
                if (prefs.getBoolean("motion", false)) {
                    currentStep = nextIndex;
                    showStep();
                } else {
                    startTransition(nextIndex, steps.get(nextIndex));
                }
            } else {
                completeScript();
            }
        });

        btnPrevFrame.setOnClickListener(v -> {
            if (isCompleted) {
                isCompleted = false;
                btnNextText.setText(getString(R.string.next));
                showStep();
            } else if (currentStep > 0) {
                currentStep--;
                showStep();
            }
        });

        btnClose.setOnClickListener(v -> finish());
        showStep();
    }

    private String getScriptTitle(int id) {
        try (DBHelper db = new DBHelper(this);
             Cursor cursor = db.getScripts()) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(0) == id) return cursor.getString(1);
            }
        }
        return "Script";
    }

    private void showStep() {
        if (steps.isEmpty()) return;
        String text = steps.get(currentStep);
        txtStep.setText(text.toUpperCase());
        txtProgress.setText((currentStep + 1) + "/" + steps.size());

        int progress = (int) (((float) (currentStep + 1) / steps.size()) * 100);
        progressIndicator.setProgress(progress, true);

        btnPrevFrame.setVisibility(currentStep == 0 ? View.INVISIBLE : View.VISIBLE);
        
        updateStepImage(scriptTitle, currentStep);

        if (prefs.getBoolean("sound", true)) {
            elevenLabs.speak(this, text);
        }
    }

    private void updateStepImage(String scriptName, int stepIndex) {
        int imageRes = android.R.drawable.ic_menu_search;
        String lowerScript = scriptName.toLowerCase();
        
        if (lowerScript.contains("say hello")) {
            int[] helloImages = {R.drawable.look_jpeg, R.drawable.smile_webp, R.drawable.hello_avif, R.drawable.ask_jpeg};
            if (stepIndex < helloImages.length) imageRes = helloImages[stepIndex];
        }
        
        imgStep.setImageResource(imageRes);
        imgStep.setColorFilter(null);
    }

    private void completeScript() {
        isCompleted = true;
        txtStep.setText(getString(R.string.script_complete));
        btnNextText.setText(getString(R.string.finish));
        progressIndicator.setProgress(100);

        if (konfettiView != null) {
            EmitterConfig emitterConfig = new Emitter(2L, TimeUnit.SECONDS).perSecond(30);
            konfettiView.start(new PartyFactory(emitterConfig)
                    .spread(360)
                    .setSpeedBetween(5f, 10f)
                    .timeToLive(2000L)
                    .colors(Arrays.asList(0xFF58CC02, 0xFF1CB0F6, 0xFFFFC800))
                    .build());
        }
    }

    private void startTransition(int nextIndex, String nextStep) {
        new Thread(() -> {
            for (int i = 3; i >= 1; i--) {
                int finalI = i;
                runOnUiThread(() -> txtTransition.setText(getString(R.string.script_transition, finalI)));

                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
            runOnUiThread(() -> {
                currentStep = nextIndex;
                txtTransition.setText("");
                showStep();
            });
        }).start();
    }
}
