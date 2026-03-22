package com.example.speakaid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCustomActivity extends AppCompatActivity {

    TextView txtHeader;
    EditText editTitle, editSteps;
    FrameLayout btnSaveFrame;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom);

        txtHeader = findViewById(R.id.txtHeader);
        editTitle = findViewById(R.id.editTitle);
        editSteps = findViewById(R.id.editSteps);
        btnSaveFrame = findViewById(R.id.btnSaveFrame);

        type = getIntent().getStringExtra("type");

        if ("routine".equals(type)) {
            txtHeader.setText("Add Custom Routine");
            editTitle.setHint("Routine Title (e.g., Laundry)");
        } else {
            txtHeader.setText("Add Custom Script");
            editTitle.setHint("Script Title (e.g., Ordering Food)");
        }

        btnSaveFrame.setOnClickListener(v -> saveToDatabase());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void saveToDatabase() {
        String title = editTitle.getText().toString().trim();
        String stepsText = editSteps.getText().toString().trim();

        if (title.isEmpty() || stepsText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] stepsArray = stepsText.split("\n");
        DBHelper dbHelper = new DBHelper(this);

        if ("routine".equals(type)) {
            dbHelper.insertRoutine(title);
            // Get the ID of the newly inserted routine
            int routineId = getLastInsertedId(dbHelper, "Routine");
            for (int i = 0; i < stepsArray.length; i++) {
                dbHelper.insertStep(routineId, stepsArray[i].trim(), i + 1);
            }
            Toast.makeText(this, "Routine Saved!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertScript(title);
            int scriptId = getLastInsertedId(dbHelper, "Script");
            for (int i = 0; i < stepsArray.length; i++) {
                dbHelper.insertScriptStep(scriptId, stepsArray[i].trim(), i + 1);
            }
            Toast.makeText(this, "Script Saved!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private int getLastInsertedId(DBHelper dbHelper, String tableName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
