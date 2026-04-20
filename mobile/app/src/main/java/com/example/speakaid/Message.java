package com.example.speakaid;

public class Message {
    private String text;
    private boolean isSent;
    private String senderName;
    private long timestamp;

    public Message(String text, boolean isSent) {
        this(text, isSent, isSent ? "Me" : "Other");
    }

    public Message(String text, boolean isSent, String senderName) {
        this.text = text;
        this.isSent = isSent;
        this.senderName = senderName;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() { return text; }
    public boolean isSent() { return isSent; }
    public String getSenderName() { return senderName; }
    public long getTimestamp() { return timestamp; }
}
