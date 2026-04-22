package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ParentModeActivity extends AppCompatActivity {

    FrameLayout btnAddRoutineFrame, btnAddScriptFrame, btnAddPhraseFrame, btnResetAll, btnExport;
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
        btnExport = findViewById(R.id.btnExport);
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
            Toast.makeText(this, "All daily progress & badges have been reset!", Toast.LENGTH_SHORT).show();
        });

        if (btnExport != null) {
            btnExport.setOnClickListener(v -> exportData());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        btnExit.setOnClickListener(v -> finish());
    }

    private void exportData() {
        StringBuilder data = new StringBuilder("SpeakAid Exported Data\n\n");
        try (Cursor cursor = db.getAllChatHistory()) { // Use the new method to get all chat history
            data.append("Chat History:\n");
            if (cursor.getCount() > 0) {
                // Append header for chat history columns
                data.append(String.format("%-5s | %-15s | %-50s | %-10s | %-5s | %s\n", 
                                        "ID", "Room ID", "Message", "Sender", "Sent", "Timestamp"));
                data.append("----------------------------------------------------------------------------------------------------\n");
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String roomId = cursor.getString(1);
                    String message = cursor.getString(2);
                    String senderName = cursor.getString(3);
                    boolean isSent = cursor.getInt(4) == 1;
                    long timestamp = cursor.getLong(5);

                    // Format timestamp for readability
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTimestamp = sdf.format(new Date(timestamp));

                    data.append(String.format("%-5d | %-15s | %-50s | %-10s | %-5b | %s\n", 
                                            id, roomId, message, senderName, isSent, formattedTimestamp));
                }
            } else {
                data.append("No chat data available.\n");
            }
        } catch (Exception e) {
            data.append("Error retrieving chat data: " + e.getMessage() + "\n");
        }

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SpeakAidExport.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data.toString());
            Toast.makeText(this, "Exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
            
            String iconName = "ic_happy";
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
