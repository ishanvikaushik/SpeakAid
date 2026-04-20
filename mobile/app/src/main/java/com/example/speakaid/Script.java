package com.example.speakaid;

public class Script {
    int id;
    String title;

    public Script(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}