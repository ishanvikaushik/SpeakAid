package com.example.speakaid;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class RoutinePlayerActivity extends AppCompatActivity {

    TextView txtStep;
    TextToSpeech tts;
    TextView txtProgress;
    TextView txtTransition;
    Button btnPrev;
    int routineId;
    Button btnNext;

    List<String> steps;
    int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_player);

        routineId = getIntent().getIntExtra("routineId", -1);

        txtTransition=findViewById(R.id.txtTransition);
        txtProgress = findViewById(R.id.txtProgress);
        btnPrev = findViewById(R.id.btnPrev);
        txtStep = findViewById(R.id.txtStep);
        btnNext = findViewById(R.id.btnNext);

        //  steps
        steps = new ArrayList<>();

        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getSteps(routineId);

        while (cursor.moveToNext()) {
            String stepTitle = cursor.getString(2); // title column
            steps.add(stepTitle);
        }

        showStep();

        btnNext.setOnClickListener(v -> {

            if (currentStep < steps.size() - 1) {

                int nextStepIndex = currentStep + 1;
                String nextStep = steps.get(nextStepIndex);

                startTransition(nextStepIndex, nextStep);

            } else {
                txtStep.setText("Routine Completed ");
                txtProgress.setText("");
                btnNext.setEnabled(false);
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                showStep();
            }
        });

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }
    void startTransition(int nextIndex, String nextStep) {

        new Thread(() -> {
            for (int i = 3; i >= 1; i--) {
                int finalI = i;
                runOnUiThread(() -> {
                    txtTransition.setText("Next: " + nextStep + " in " + finalI);
                });

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

    void showStep() {
        String currentText = steps.get(currentStep);

        txtStep.setText(currentText);
        txtProgress.setText("Step " + (currentStep + 1) + " / " + steps.size());

        btnPrev.setEnabled(currentStep != 0);

        // Speak step
        if (tts != null) {
            tts.speak(currentText, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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