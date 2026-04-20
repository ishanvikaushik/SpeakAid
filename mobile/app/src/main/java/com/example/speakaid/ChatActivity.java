package com.example.speakaid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private ChatAdapter adapter;
    private List<Message> messages;
    private EditText editMessage;
    private ImageButton btnSend, btnBack;
    private TextView txtRoomName;
    private View viewConnectionStatus;

    private Socket mSocket;
    private String roomId;

    // TODO: Replace with your actual Node.js server URL
    private static final String SERVER_URL = "http://10.0.2.2:3000"; // Default for local Android Emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_chat);

        roomId = getIntent().getStringExtra("roomId");
        if (roomId == null) roomId = "General";

        initViews();
        setupSocket();
    }

    private void initViews() {
        recyclerMessages = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        txtRoomName = findViewById(R.id.txtRoomName);
        viewConnectionStatus = findViewById(R.id.viewConnectionStatus);

        txtRoomName.setText("Room: " + roomId);
        btnBack.setOnClickListener(v -> finish());

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupSocket() {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("ChatActivity", "Socket URL error", e);
            return;
        }

        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
            mSocket.emit("joinRoom", roomId);
        }));

        mSocket.on(Socket.EVENT_DISCONNECT, args -> runOnUiThread(() -> {
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252)); // Red
        }));

        mSocket.on("receiveMessage", onNewMessage);

        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        try {
            String text = data.getString("text");
            // Check if it's from current user or not (simplified for now)
            addMessage(text, false);
        } catch (JSONException e) {
            Log.e("ChatActivity", "JSON Parse error", e);
        }
    });

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        if (mSocket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("room", roomId);
                data.put("text", text);
                mSocket.emit("chatMessage", data);
                
                addMessage(text, true);
                editMessage.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMessage(String text, boolean isSent) {
        messages.add(new Message(text, isSent));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerMessages.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("receiveMessage", onNewMessage);
        }
    }
}
