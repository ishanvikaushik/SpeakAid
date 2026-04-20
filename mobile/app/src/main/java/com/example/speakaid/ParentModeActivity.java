package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ParentModeActivity extends AppCompatActivity {

    FrameLayout btnAddRoutineFrame, btnAddScriptFrame, btnResetAll;
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

        btnResetAll.setOnClickListener(v -> {
            resetAllProgress();
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        btnExit.setOnClickListener(v -> finish());
    }
// new fn, not there originally

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }
    private void resetAllProgress() {
        try (Cursor cursor = db.getRoutines()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                db.updateRoutineProgress(id, 0);
                db.resetRoutineCompletion(id);
            }
        }
        Toast.makeText(this, "All daily progress has been reset!", Toast.LENGTH_SHORT).show();
    }

}
