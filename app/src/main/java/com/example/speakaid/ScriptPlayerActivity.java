package com.example.speakaid;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;

import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScriptPlayerActivity extends AppCompatActivity {

    TextView txtStep, txtProgress, txtTransition;
    Button btnNext, btnPrev;

    ArrayList<String> steps;
    int currentStep = 0;
    boolean isCompleted = false;

    TextToSpeech tts;
    SharedPreferences prefs;
    Vibrator vibrator;

    KonfettiView konfettiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_player);

        setTitle("Script");

        //  Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtStep = findViewById(R.id.txtStep);
        txtProgress = findViewById(R.id.txtProgress);
        txtTransition = findViewById(R.id.txtTransition);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        konfettiView = findViewById(R.id.confettiView);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        int scriptId = getIntent().getIntExtra("scriptId", -1);

        steps = new ArrayList<>();

        try (DBHelper db = new DBHelper(this);
             Cursor cursor = db.getScriptSteps(scriptId)) {
            while (cursor.moveToNext()) {
                String step = cursor.getString(2);
                steps.add(step);
            }
        }

        //  TTS init
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                showStep();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (steps.isEmpty()) return;

            if (currentStep < steps.size() - 1) {

                int nextIndex = currentStep + 1;
                String nextStep = steps.get(nextIndex);

                if (prefs.getBoolean("motion", false)) {
                    currentStep = nextIndex;
                    showStep();
                } else {
                    startTransition(nextIndex, nextStep);
                }

            } else {
                txtStep.setText("Script Completed ");
                txtProgress.setText("");
                btnNext.setEnabled(false);
                isCompleted = true;

                //  CONFETTI
                if (konfettiView != null) {
                    EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(30);
                    konfettiView.start(
                            new PartyFactory(emitterConfig)
                                    .spread(360)
                                    .setSpeedBetween(5f, 10f)
                                    .timeToLive(2000L)
                                    .colors(Arrays.asList(0xFFFFC107, 0xFF4CAF50, 0xFF2196F3))
                                    .build()
                    );
                }
            }
        });

        btnPrev.setOnClickListener(v -> {

            if (isCompleted) {
                isCompleted = false;
                btnNext.setEnabled(true);
                showStep();
                return;
            }

            if (currentStep > 0) {
                currentStep--;
                showStep();
            }
        });
    }

    void showStep() {
        if (steps.isEmpty()) {
            txtStep.setText("No steps found for this script.");
            txtProgress.setText("");
            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);
            return;
        }

        String text = steps.get(currentStep);

        txtStep.setText(text);
        txtProgress.setText("Step " + (currentStep + 1) + " / " + steps.size());

        btnPrev.setEnabled(currentStep != 0);

        //  TTS
        if (prefs.getBoolean("sound", true)) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        //  Vibration
        if (prefs.getBoolean("vibration", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }
    }

    void startTransition(int nextIndex, String nextStep) {

        new Thread(() -> {
            for (int i = 3; i >= 1; i--) {
                int finalI = i;

                runOnUiThread(() ->
                        txtTransition.setText("Next: " + nextStep + " in " + finalI)
                );

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
