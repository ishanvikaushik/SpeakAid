package com.example.speakaid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HelpActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private EditText editMood;
    private Button btnSubmit;
    private TextView txtStatus;
    private SharedPreferences prefs;
    private HuggingFaceService hfService;

    // Securely loaded from local.properties
    private static final String HF_TOKEN = "Bearer " + BuildConfig.HF_API_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_help);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        editMood = findViewById(R.id.editMood);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtStatus = findViewById(R.id.txtStatus);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Initialize Retrofit for Hugging Face
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-inference.huggingface.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        hfService = retrofit.create(HuggingFaceService.class);

        updateStatus();

        btnSubmit.setOnClickListener(v -> {
            String text = editMood.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Please tell me how you feel.", Toast.LENGTH_SHORT).show();
                return;
            }
            processDistress(text);
        });
    }

    private void updateStatus() {
        if (isOnline()) {
            txtStatus.setText("Online: Advanced AI detection active");
        } else {
            txtStatus.setText("Offline: Simple keyword detection active");
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void processDistress(String text) {
        if (isOnline()) {
            callHuggingFace(text);
        } else {
            offlineCheck(text);
        }
    }

    private void callHuggingFace(String text) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("ANALYZING...");

        hfService.detectEmotion(HF_TOKEN, new HuggingFaceService.Request(text)).enqueue(new Callback<List<List<HuggingFaceService.Response>>>() {
            @Override
            public void onResponse(Call<List<List<HuggingFaceService.Response>>> call, Response<List<List<HuggingFaceService.Response>>> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("TALK TO ME");
                
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<HuggingFaceService.Response> results = response.body().get(0);
                    checkEmotions(results);
                } else {
                    Log.e("HF_ERROR", "Error: " + response.code());
                    offlineCheck(text); // Fallback to offline on API error
                }
            }

            @Override
            public void onFailure(Call<List<List<HuggingFaceService.Response>>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("TALK TO ME");
                offlineCheck(text);
            }
        });
    }

    private void checkEmotions(List<HuggingFaceService.Response> results) {
        boolean distressDetected = false;
        for (HuggingFaceService.Response res : results) {
            // bhadresh-savani/bert-base-uncased-emotion labels: fear, anger, joy, sadness, love, surprise
            if ((res.label.equals("fear") || res.label.equals("anger") || res.label.equals("sadness")) && res.score > 0.7) {
                distressDetected = true;
                break;
            }
        }

        if (distressDetected) {
            triggerAlert();
        } else {
            Toast.makeText(this, "I'm listening. Thank you for sharing.", Toast.LENGTH_LONG).show();
        }
    }

    private void offlineCheck(String text) {
        String lowerText = text.toLowerCase();
        String[] keywords = {"scared", "angry", "help", "stop", "overwhelmed", "hate", "hurt", "kill", "die"};
        
        boolean detected = false;
        for (String word : keywords) {
            if (lowerText.contains(word)) {
                detected = true;
                break;
            }
        }

        if (detected) {
            triggerAlert();
        } else {
            Toast.makeText(this, "I'm here for you. Try taking a deep breath.", Toast.LENGTH_LONG).show();
        }
    }

    private void triggerAlert() {
        Toast.makeText(this, "Distress detected. Notifying caregiver...", Toast.LENGTH_LONG).show();
        
        String phone = prefs.getString("emergency_phone", "");
        if (!phone.isEmpty()) {
            sendSms(phone, "SpeakAid Alert: High distress detected. Please check on the user.");
        }

        // Switch to grounding mode immediately
        Intent intent = new Intent(this, ZenCanvasActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendSms(String phone, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Log.d("SMS", "Alert sent to " + phone);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS alert.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
