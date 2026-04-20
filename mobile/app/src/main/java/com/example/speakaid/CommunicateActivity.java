package com.example.speakaid;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class CommunicateActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private CardView cardFood, cardWater, cardToilet, cardHelp, cardYes, cardNo, cardTired, cardHappy;
    private ImageButton btnBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            } else {
                Toast.makeText(this, "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Bind Views
        btnBack = findViewById(R.id.btnBack);
        cardFood = findViewById(R.id.cardFood);
        cardWater = findViewById(R.id.cardWater);
        cardToilet = findViewById(R.id.cardToilet);
        cardHelp = findViewById(R.id.cardHelp);
        cardYes = findViewById(R.id.cardYes);
        cardNo = findViewById(R.id.cardNo);
        cardTired = findViewById(R.id.cardTired);
        cardHappy = findViewById(R.id.cardHappy);

        // Set Click Listeners
        btnBack.setOnClickListener(v -> finish());

        cardFood.setOnClickListener(v -> speak("I want to eat"));
        cardWater.setOnClickListener(v -> speak("I am thirsty"));
        cardToilet.setOnClickListener(v -> speak("I need to use the toilet"));
        cardHelp.setOnClickListener(v -> speak("Please help me"));
        cardYes.setOnClickListener(v -> speak("Yes"));
        cardNo.setOnClickListener(v -> speak("No"));
        cardTired.setOnClickListener(v -> speak("I am tired"));
        cardHappy.setOnClickListener(v -> speak("I am happy"));
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
