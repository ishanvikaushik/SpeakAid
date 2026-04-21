package com.example.speakaid;

import android.database.Cursor;
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
    private DBHelper db;

    // TODO: Update this with your actual Render URL
    private static final String SERVER_URL = "https://speakaid.onrender.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_chat);

        db = new DBHelper(this);
        roomId = getIntent().getStringExtra("roomId");
        userRole = getIntent().getStringExtra("userRole");
        
        if (roomId == null) roomId = "General";
        if (userRole == null) userRole = "User";

        initViews();
        loadHistory();
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

    private void loadHistory() {
        try (Cursor cursor = db.getChatHistory(roomId)) {
            while (cursor.moveToNext()) {
                String text = cursor.getString(2);
                String sender = cursor.getString(3);
                boolean isSent = cursor.getInt(4) == 1;
                messages.add(new Message(text, isSent, sender));
            }
            adapter.notifyDataSetChanged();
            if (messages.size() > 0) recyclerMessages.scrollToPosition(messages.size() - 1);
        }
    }

    private void setupSocket() {
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket", "polling"}; 
            options.forceNew = true;
            mSocket = IO.socket(SERVER_URL, options);
        } catch (URISyntaxException e) {
            Log.e("ChatActivity", "Socket URL error", e);
            return;
        }

        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
            mSocket.emit("joinRoom", roomId);
            Toast.makeText(this, "Connected as " + userRole, Toast.LENGTH_SHORT).show();
        }));

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(() -> {
            viewConnectionStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFC107)); // Yellow
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
            String sender = data.optString("senderName", "Other");
            
            // Save to DB
            db.saveChatMessage(roomId, text, sender, false);
            
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
                
                // Save to DB
                db.saveChatMessage(roomId, text, userRole, true);
                
                addMessage(text, true, "Me");
                editMessage.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Still connecting...", Toast.LENGTH_SHORT).show();
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
