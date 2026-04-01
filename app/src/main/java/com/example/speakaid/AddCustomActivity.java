package com.example.speakaid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddCustomActivity extends AppCompatActivity {

    TextView txtHeader;
    EditText editTitle, editSteps;
    FrameLayout btnSaveFrame;
    Button btnSimplifyAI;
    ImageButton btnBack;
    String type;

    // Securely loaded from local.properties via BuildConfig
    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom);

        txtHeader = findViewById(R.id.txtHeader);
        editTitle = findViewById(R.id.editTitle);
        editSteps = findViewById(R.id.editSteps);
        btnSaveFrame = findViewById(R.id.btnSaveFrame);
        btnSimplifyAI = findViewById(R.id.btnSimplifyAI);
        btnBack = findViewById(R.id.btnBack);

        type = getIntent().getStringExtra("type");

        if ("routine".equals(type)) {
            txtHeader.setText("Add Custom Routine");
            editTitle.setHint("Routine Title (e.g., Laundry)");
        } else {
            txtHeader.setText("Add Custom Script");
            editTitle.setHint("Script Title (e.g., Ordering Food)");
        }

        btnBack.setOnClickListener(v -> finish());
        btnSimplifyAI.setOnClickListener(v -> simplifyWithAI());
        btnSaveFrame.setOnClickListener(v -> saveToDatabase());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void simplifyWithAI() {
        String originalSteps = editSteps.getText().toString().trim();
        if (originalSteps.isEmpty()) {
            Toast.makeText(this, "Please enter some steps first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty() || GEMINI_API_KEY.equals("unused")) {
            Toast.makeText(this, "API Key error. Please Clean and Rebuild project.", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("AI is simplifying your steps...");
        pd.setCancelable(false);
        pd.show();

        // Use gemini-1.5-flash (Standard high-speed model)
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String prompt = "You are an assistant helping neurodivergent users. " +
                "Rewrite the following list of steps to be extremely simple, direct, and easy to follow. " +
                "Use 'Plain Language'. Keep the steps as a list. Do not add conversational text or intro/outro. " +
                "Steps:\n" + originalSteps;

        Content content = new Content.Builder().addText(prompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    String simplifiedText = result.getText();
                    if (simplifiedText != null) {
                        editSteps.setText(simplifiedText.trim());
                        Toast.makeText(AddCustomActivity.this, "Steps simplified!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Log.e("GeminiError", "Error: " + t.toString());
                    Toast.makeText(AddCustomActivity.this, "AI Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }, executor);
    }

    private void saveToDatabase() {
        String title = editTitle.getText().toString().trim();
        String stepsText = editSteps.getText().toString().trim();

        if (title.isEmpty() || stepsText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] stepsArray = stepsText.split("\n");
        
        try (DBHelper dbHelper = new DBHelper(this)) {
            if ("routine".equals(type)) {
                long routineId = dbHelper.insertRoutine(title);
                for (int i = 0; i < stepsArray.length; i++) {
                    if (!stepsArray[i].trim().isEmpty()) {
                        dbHelper.insertStep(routineId, stepsArray[i].trim(), i + 1);
                    }
                }
                Toast.makeText(this, "Routine Saved!", Toast.LENGTH_SHORT).show();
            } else {
                long scriptId = dbHelper.insertScript(title);
                for (int i = 0; i < stepsArray.length; i++) {
                    if (!stepsArray[i].trim().isEmpty()) {
                        dbHelper.insertScriptStep(scriptId, stepsArray[i].trim(), i + 1);
                    }
                }
                Toast.makeText(this, "Script Saved!", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
