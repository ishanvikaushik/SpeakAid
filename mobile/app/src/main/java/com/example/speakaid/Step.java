package com.example.speakaid;

public class Step {
    public int id;
    public int routineId;
    public String title;
    public int stepOrder;

    public Step(int id, int routineId, String title, int stepOrder) {
        this.id = id;
        this.routineId = routineId;
        this.title = title;
        this.stepOrder = stepOrder;
    }
}