package com.example.speakaid;

public class Message {
    private String text;
    private boolean isSent;
    private long timestamp;

    public Message(String text, boolean isSent) {
        this.text = text;
        this.isSent = isSent;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() { return text; }
    public boolean isSent() { return isSent; }
    public long getTimestamp() { return timestamp; }
}
