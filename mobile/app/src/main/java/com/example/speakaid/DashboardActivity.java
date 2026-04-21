package com.example.speakaid;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtQuote;
    private ImageView imgMascot;
    private View btnUser, btnCaregiver;
    private SharedPreferences prefs;
    private Handler quoteHandler;
    private Runnable quoteRunnable;

    private final String[] quotes = {
            "\"Believe in the magic of your own small steps.\"",
            "\"Every progress, no matter how small, is a victory.\"",
            "\"You are brave, you are strong, you are loved.\"",
            "\"A calm mind is a happy mind.\"",
            "\"Take a deep breath. You're doing great!\"",
            "\"Your unique way is a beautiful way.\""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtQuote = findViewById(R.id.txtQuote);
        imgMascot = findViewById(R.id.imgMascot);
        btnUser = findViewById(R.id.btnUser);
        btnCaregiver = findViewById(R.id.btnCaregiver);

        startMascotAnimation();
        startAutoQuoteRotation();
        setupInteractions();
    }

    private void startAutoQuoteRotation() {
        quoteHandler = new Handler();
        quoteRunnable = new Runnable() {
            @Override
            public void run() {
                // Fade out, change text, fade in
                txtQuote.animate().alpha(0f).setDuration(500).withEndAction(() -> {
                    txtQuote.setText(quotes[new Random().nextInt(quotes.length)]);
                    txtQuote.animate().alpha(1f).setDuration(500).start();
                }).start();
                
                quoteHandler.postDelayed(this, 3000); // 3 seconds interval
            }
        };
        quoteHandler.post(quoteRunnable);
    }

    private void startMascotAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgMascot, View.SCALE_X, 1f, 1.05f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgMascot, View.SCALE_Y, 1f, 1.05f);
        scaleX.setDuration(3000);
        scaleY.setDuration(3000);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }

    private void setupInteractions() {
        btnUser.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        btnCaregiver.setOnClickListener(v -> showCaregiverOptions());
        imgMascot.setOnClickListener(v -> {
            txtQuote.setText(quotes[new Random().nextInt(quotes.length)]);
        });
    }

    private void showCaregiverOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caregiver Mode");
        builder.setPositiveButton("Controls", (dialog, which) -> showPasscodeDialog());
        builder.setNeutralButton("Communication Hub", (dialog, which) ->
                startActivity(new Intent(this, ChatEntryActivity.class)));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Passcode");
        String savedPasscode = prefs.getString("passcode", "1234");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (input.getText().toString().equals(savedPasscode)) {
                startActivity(new Intent(this, ParentModeActivity.class));
            } else {
                Toast.makeText(this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quoteHandler != null) quoteHandler.removeCallbacks(quoteRunnable);
    }
}
