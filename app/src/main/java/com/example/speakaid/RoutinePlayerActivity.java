package com.example.speakaid;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoutinePlayerActivity extends AppCompatActivity {

    TextView txtStep;
    TextView txtProgress;
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
            currentStep++;

            if (currentStep < steps.size()) {
                showStep();
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
    }

    void showStep() {
        txtStep.setText(steps.get(currentStep));
        txtProgress.setText("Step " + (currentStep + 1) + " / " + steps.size());

        btnPrev.setEnabled(currentStep != 0);
    }
}