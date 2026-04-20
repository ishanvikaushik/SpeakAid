package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChatEntryActivity extends AppCompatActivity {

    private EditText editRoomId;
    private RadioGroup rgRole;
    private Button btnJoinChat;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_chat_entry);

        editRoomId = findViewById(R.id.editRoomId);
        rgRole = findViewById(R.id.rgRole); // Make sure to add this RadioGroup to your XML
        btnJoinChat = findViewById(R.id.btnJoinChat);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnJoinChat.setOnClickListener(v -> {
            String roomId = editRoomId.getText().toString().trim();
            if (roomId.isEmpty()) {
                Toast.makeText(this, "Please enter a Room ID", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgRole.getCheckedRadioButtonId();
            String userRole = "User";
            if (checkedId == R.id.rbTeacher) {
                userRole = "Teacher";
            } else if (checkedId == R.id.rbCaregiver) {
                userRole = "Caregiver";
            }

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("roomId", roomId);
            intent.putExtra("userRole", userRole);
            startActivity(intent);
        });
    }
}
