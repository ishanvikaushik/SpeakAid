package com.example.speakaid;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class RoutinePlayerActivity extends AppCompatActivity {

    TextView txtStep, txtProgress, txtTransition, btnNextText;
    FrameLayout btnNextFrame, btnPrevFrame;
    LinearProgressIndicator progressIndicator;
    ImageView imgStep, btnClose;
    
    boolean isCompleted = false;
    TextToSpeech tts;
    int currentStep = 0;
    List<String> steps;
    int routineId;
    String routineTitle;

    SharedPreferences prefs;
    KonfettiView konfettiView;
    DBHelper db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_player);

        routineId = getIntent().getIntExtra("routineId", -1);
        currentStep = getIntent().getIntExtra("startStep", 0);

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
        steps = new ArrayList<>();

        db = new DBHelper(this);
        try (Cursor cursor = db.getSteps(routineId)) {
            while (cursor.moveToNext()) {
                steps.add(cursor.getString(2));
            }
        }
        
        routineTitle = getRoutineTitle(routineId);

        if (currentStep >= steps.size()) {
            currentStep = 0;
        }

        btnNextFrame.setOnClickListener(v -> {
            if (isCompleted) {
                finish();
                return;
            }

            if (currentStep < steps.size() - 1) {
                int nextIndex = currentStep + 1;
                if (prefs.getBoolean("motion", false)) {
                    currentStep = nextIndex;
                    saveProgress();
                    showStep();
                } else {
                    startTransition(nextIndex, steps.get(nextIndex));
                }
            } else {
                completeRoutine();
            }
        });

        btnPrevFrame.setOnClickListener(v -> {
            if (isCompleted) {
                isCompleted = false;
                btnNextText.setText(getString(R.string.next));
                showStep();
            } else if (currentStep > 0) {
                currentStep--;
                saveProgress();
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

    private String getRoutineTitle(int id) {
        Cursor cursor = db.getRoutines();
        String title = "Routine";
        while (cursor.moveToNext()) {
            if (cursor.getInt(0) == id) {
                title = cursor.getString(1);
                break;
            }
        }
        cursor.close();
        return title;
    }

    private void saveProgress() {
        db.updateRoutineProgress(routineId, currentStep);
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
        if (lower.contains("happy") || lower.contains("smile") || lower.contains("good")) {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
        } else if (lower.contains("calm") || lower.contains("wait") || lower.contains("breathe")) {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_blue_light, getTheme()));
        } else {
            imgStep.setColorFilter(getResources().getColor(android.R.color.holo_green_light, getTheme()));
        }
    }

    private void completeRoutine() {
        isCompleted = true;
        
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.markRoutineCompleted(routineId, today);

        txtStep.setText(getString(R.string.routine_complete));
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

        showBadgeEarnedPopup(routineTitle);
    }

    private void showBadgeEarnedPopup(String badgeName) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_badge_earned, null);
        TextView txtName = popupView.findViewById(R.id.txtEarnedBadgeName);
        ImageView imgBadge = popupView.findViewById(R.id.imgEarnedBadge);
        View card = popupView.findViewById(R.id.earnedBadgeCard);
        
        txtName.setText(badgeName);

        // MAP IMAGES TO POPUP
        int badgeRes = R.drawable.ic_yes; // Default
        String lowerName = badgeName.toLowerCase();

        if (lowerName.contains("morning")) badgeRes = R.drawable.morning;
        else if (lowerName.contains("night")) badgeRes = R.drawable.night;
        else if (lowerName.contains("school")) badgeRes = R.drawable.school;
        else if (lowerName.contains("laundry")) badgeRes = R.drawable.laundry;
        else badgeRes = R.drawable.custom;

        imgBadge.setImageResource(badgeRes);
        imgBadge.setColorFilter(null); // Ensure no gray tint in the win popup

        AlertDialog dialog = new AlertDialog.Builder(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                .setView(popupView)
                .create();

        dialog.show();

        card.setCameraDistance(8000 * getResources().getDisplayMetrics().density);
        ObjectAnimator animator = ObjectAnimator.ofFloat(card, View.ROTATION_Y, 0f, 360f);
        animator.setDuration(1200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        popupView.setOnClickListener(v -> dialog.dismiss());
    }

    private void startTransition(int nextIndex, String nextStep) {
        new Thread(() -> {
            for (int i = 3; i >= 1; i--) {
                int finalI = i;
                runOnUiThread(() -> txtTransition.setText(getString(R.string.get_ready, finalI)));
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
            runOnUiThread(() -> {
                currentStep = nextIndex;
                saveProgress();
                txtTransition.setText("");
                showStep();
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
