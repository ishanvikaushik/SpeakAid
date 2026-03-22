package com.example.speakaid;

public class Routine {
    public int id;
    public String title;
    public int lastStep;
    public String completedDate;

    public Routine(int id, String title, int lastStep, String completedDate) {
        this.id = id;
        this.title = title;
        this.lastStep = lastStep;
        this.completedDate = completedDate;
    }
}