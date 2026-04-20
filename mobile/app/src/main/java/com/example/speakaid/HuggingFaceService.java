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
        public String label; // "fear", "anger", "sadness", etc.
        public double score;
    }

    @POST("models/bhadresh-savani/bert-base-uncased-emotion")
    Call<List<List<Response>>> detectEmotion(
        @Header("Authorization") String token,
        @Body Request body
    );
}
