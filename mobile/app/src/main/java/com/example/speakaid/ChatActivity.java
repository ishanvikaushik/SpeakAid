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
    private String userRole;

    // Replace with your actual Render URL
    private static final String SERVER_URL = "https://speakaid.onrender.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_chat);

        roomId = getIntent().getStringExtra("roomId");
        userRole = getIntent().getStringExtra("userRole");
        
        if (roomId == null) roomId = "General";
        if (userRole == null) userRole = "User";

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

        txtRoomName.setText("Chat: " + roomId);
        btnBack.setOnClickListener(v -> finish());

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
        
        // Initial state: connecting
        Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
        }));

        mSocket.on(Socket.EVENT_DISCONNECT, args -> runOnUiThread(() -> {
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252)); // Red
        }));

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(() -> {
            Log.e("ChatActivity", "Connection Error: " + args[0]);
            // Often happens if Render is sleeping
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFC107)); // Yellow
        }));

        mSocket.on("receiveMessage", onNewMessage);

        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        try {
            String text = data.getString("text");
            String sender = data.optString("senderName", "Other");
            addMessage(text, false, sender);
        } catch (JSONException e) {
            Log.e("ChatActivity", "JSON Parse error", e);
        }
    });

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        if (mSocket != null && mSocket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("room", roomId);
                data.put("text", text);
                data.put("senderName", userRole);
                mSocket.emit("chatMessage", data);
                
                addMessage(text, true, "Me");
                editMessage.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Still connecting... please wait", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMessage(String text, boolean isSent, String sender) {
        messages.add(new Message(text, isSent, sender));
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
