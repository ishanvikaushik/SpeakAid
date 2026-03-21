package com.example.speakaid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoutinePlayerActivity extends AppCompatActivity {

    TextView txtStep;
    int routineId;
    Button btnNext;

    List<String> steps;
    int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_player);

        routineId = getIntent().getIntExtra("routineId", -1);

        txtStep = findViewById(R.id.txtStep);
        btnNext = findViewById(R.id.btnNext);

        //  steps
        steps = new ArrayList<>();

        if (routineId == 1) {
            steps.add("Wake up");
            steps.add("Brush teeth");
            steps.add("Get dressed");
            steps.add("Eat breakfast");
        }
        else if (routineId == 2) {
            steps.add("Pack school bag");
            steps.add("Wear uniform");
            steps.add("Check homework");
        }
        else if (routineId == 3) {
            steps.add("Brush teeth");
            steps.add("Change clothes");
            steps.add("Go to bed");
        }

        showStep();

        btnNext.setOnClickListener(v -> {
            currentStep++;

            if (currentStep < steps.size()) {
                showStep();
            } else {
                txtStep.setText("Routine Completed ");
                btnNext.setEnabled(false);
            }
        });
    }

    void showStep() {
        txtStep.setText(steps.get(currentStep));
    }
}