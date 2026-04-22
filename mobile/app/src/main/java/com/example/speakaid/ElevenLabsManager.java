package com.example.speakaid;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ElevenLabsManager {
    // API Key is pulled from BuildConfig, which is populated from local.properties
    private static final String API_KEY = BuildConfig.ELEVENLABS_API_KEY;
    private static final String VOICE_ID = "EXAVITQu4vr4xnSDxMaL";

    interface ElevenLabsApi {
        @POST("v1/text-to-speech/{voice_id}")
        Call<ResponseBody> textToSpeech(
            @Header("xi-api-key") String apiKey, 
            @Path("voice_id") String voiceId, 
            @Body RequestBody body
        );
    }

    public void speak(Context context, String text) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            Log.e("ElevenLabsDebug", "API KEY IS NULL OR EMPTY");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.elevenlabs.io/").build();
        ElevenLabsApi api = retrofit.create(ElevenLabsApi.class);
        
        String json = "{\"text\": \"" + text + "\", \"model_id\": \"eleven_multilingual_v2\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        new Thread(() -> {
            try {
                Call<ResponseBody> call = api.textToSpeech(API_KEY, VOICE_ID, body);
                Response<ResponseBody> response = call.execute();
                
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        playAudio(context, responseBody.byteStream());
                    }
                } else {
                    String error = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                    Log.e("ElevenLabsDebug", "Error code: " + response.code() + " Error: " + error);
                }
            } catch (Exception e) {
                Log.e("ElevenLabsDebug", "Exception: " + e.getMessage());
            }
        }).start();
    }

    private void playAudio(Context context, InputStream inputStream) {
        try {
            java.io.File tempFile = java.io.File.createTempFile("tts", "mp3", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = inputStream.read(buffer)) > 0) fos.write(buffer, 0, len);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build());
            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            
            // Release after playback
            mediaPlayer.setOnCompletionListener(mp -> mp.release());
        } catch (IOException e) {
            Log.e("ElevenLabsDebug", "Playback Error: " + e.getMessage());
        }
    }
}
