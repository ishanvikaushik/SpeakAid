package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ParentModeActivity extends AppCompatActivity {

    FrameLayout btnAddRoutineFrame, btnAddScriptFrame, btnAddPhraseFrame, btnResetAll;
    Button btnExit;
    ImageView btnBack;
    DBHelper db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_mode);

        db = new DBHelper(this);
        btnAddRoutineFrame = findViewById(R.id.btnAddRoutineFrame);
        btnAddScriptFrame = findViewById(R.id.btnAddScriptFrame);
        btnAddPhraseFrame = findViewById(R.id.btnAddPhraseFrame);
        btnResetAll = findViewById(R.id.btnResetAll);
        btnExit = findViewById(R.id.btnExit);
        btnBack = findViewById(R.id.btnBack);

        btnAddRoutineFrame.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCustomActivity.class);
            intent.putExtra("type", "routine");
            startActivity(intent);
        });

        btnAddScriptFrame.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCustomActivity.class);
            intent.putExtra("type", "script");
            startActivity(intent);
        });

        btnAddPhraseFrame.setOnClickListener(v -> showAddPhraseDialog());

        btnResetAll.setOnClickListener(v -> {
            db.resetAllRoutines();
            Toast.makeText(this, "All daily progress has been reset!", Toast.LENGTH_SHORT).show();
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        btnExit.setOnClickListener(v -> finish());
    }

    private void showAddPhraseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_phrase, null);
        builder.setView(view);

        EditText editPhrase = view.findViewById(R.id.editCustomPhrase);
        RadioGroup rgIconPicker = view.findViewById(R.id.rgIconPicker);

        builder.setPositiveButton("SAVE", (dialog, which) -> {
            String phrase = editPhrase.getText().toString().trim();
            int selectedId = rgIconPicker.getCheckedRadioButtonId();
            
            String iconName = "ic_happy"; // Default
            if (selectedId == R.id.rbIconYes) iconName = "ic_yes";
            else if (selectedId == R.id.rbIconHelp) iconName = "ic_help";
            else if (selectedId == R.id.rbIconFood) iconName = "ic_food";
            else if (selectedId == R.id.rbIconWater) iconName = "ic_water";
            else if (selectedId == R.id.rbIconToilet) iconName = "ic_toilet";
            else if (selectedId == R.id.rbIconTired) iconName = "ic_tired";
            else if (selectedId == R.id.rbIconChat) iconName = "ic_chat";

            if (!phrase.isEmpty()) {
                db.insertCustomPhrase(phrase, iconName);
                Toast.makeText(this, "Phrase added successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
