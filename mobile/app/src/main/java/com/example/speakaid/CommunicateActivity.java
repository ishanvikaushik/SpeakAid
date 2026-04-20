package com.example.speakaid;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class CommunicateActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private GridLayout gridLayout;
    private ImageButton btnBack;
    private DBHelper db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        db = new DBHelper(this);
        gridLayout = findViewById(R.id.gridLayoutCommunicate); // Add this ID to XML

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        setupDefaultCards();
        loadCustomPhrases();
    }

    private void setupDefaultCards() {
        findViewById(R.id.cardFood).setOnClickListener(v -> speak("I want to eat"));
        findViewById(R.id.cardWater).setOnClickListener(v -> speak("I am thirsty"));
        findViewById(R.id.cardToilet).setOnClickListener(v -> speak("I need to use the toilet"));
        findViewById(R.id.cardHelp).setOnClickListener(v -> speak("Please help me"));
        findViewById(R.id.cardYes).setOnClickListener(v -> speak("Yes"));
        findViewById(R.id.cardNo).setOnClickListener(v -> speak("No"));
        findViewById(R.id.cardTired).setOnClickListener(v -> speak("I am tired"));
        findViewById(R.id.cardHappy).setOnClickListener(v -> speak("I am happy"));
    }

    private void loadCustomPhrases() {
        try (Cursor cursor = db.getCustomPhrases()) {
            while (cursor.moveToNext()) {
                String phrase = cursor.getString(1);
                String iconName = cursor.getString(2);
                addCustomCardToGrid(phrase, iconName);
            }
        }
    }

    private void addCustomCardToGrid(String phrase, String iconName) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_custom_communicate, null);
        
        ImageView img = cardView.findViewById(R.id.customIcon);
        TextView txt = cardView.findViewById(R.id.customText);

        txt.setText(phrase);
        
        // Dynamic icon loading
        int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (resId != 0) img.setImageResource(resId);

        cardView.setOnClickListener(v -> speak(phrase));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        
        cardView.setLayoutParams(params);
        gridLayout.addView(cardView);
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (db != null) db.close();
        super.onDestroy();
    }
}
