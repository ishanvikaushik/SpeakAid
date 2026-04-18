package com.example.speakaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
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
    boolean isCompleted = false;

    TextToSpeech tts;
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
        int scriptId = getIntent().getIntExtra("scriptId", -1);

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
                btnNextText.setText(getString(R.string.next));//btnNextText.setText("NEXT");
                showStep();
            } else if (currentStep > 0) {
                currentStep--;
                showStep();
            }
        });

        btnClose.setOnClickListener(v -> finish());

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                showStep();
            }
        });
    }

    private void showStep() {
        if (steps.isEmpty()) return;
        String text = steps.get(currentStep);
        txtStep.setText(text.toUpperCase());
        txtProgress.setText((currentStep + 1) + "/" + steps.size());

        int progress = (int) (((float) (currentStep + 1) / steps.size()) * 100);
        progressIndicator.setProgress(progress, true);

        btnPrevFrame.setVisibility(currentStep == 0 ? View.INVISIBLE : View.VISIBLE);
        
        applyEmotionalCoding(text);

        if (tts != null && prefs.getBoolean("sound", true)) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void applyEmotionalCoding(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("smile") || lower.contains("happy") || lower.contains("friend")) {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
        } else if (lower.contains("attention") || lower.contains("look") || lower.contains("calm")) {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_blue_light, getTheme()));
        } else {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_green_light, getTheme()));
        }
    }

    private void completeScript() {
        isCompleted = true;
       // txtStep.setText("SUPER STAR!");
       // btnNextText.setText("FINISH");
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
                //runOnUiThread(() -> txtTransition.setText("Next part in... " + finalI));
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
