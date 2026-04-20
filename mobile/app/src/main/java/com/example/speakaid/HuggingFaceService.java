package com.example.speakaid;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface HuggingFaceService {
    class Request {
        String inputs;
        Request(String text) { this.inputs = text; }
    }

    class Response {
        public String label; // "joy", "fear", "anger", "sadness", etc.
        public double score;
    }

    // Using a more robust emotion detection model
    @POST("models/j-hartmann/emotion-english-distilroberta-base")
    Call<List<List<Response>>> detectEmotion(
        @Header("Authorization") String token,
        @Body Request body
    );
}
