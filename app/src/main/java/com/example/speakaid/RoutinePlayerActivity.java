package com.example.speakaid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoutinePlayerActivity extends AppCompatActivity {

    TextView txtStep;
    Button btnNext;

    List<String> steps;
    int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_player);

        txtStep = findViewById(R.id.txtStep);
        btnNext = findViewById(R.id.btnNext);

        // Dummy steps
        steps = new ArrayList<>();
        steps.add("Wake up");
        steps.add("Brush teeth");
        steps.add("Get dressed");
        steps.add("Eat breakfast");

        showStep();

        btnNext.setOnClickListener(v -> {
            currentStep++;

            if (currentStep < steps.size()) {
                showStep();
            } else {
                txtStep.setText("Routine Completed ✅");
                btnNext.setEnabled(false);
            }
        });
    }

    void showStep() {
        txtStep.setText(steps.get(currentStep));
    }
}