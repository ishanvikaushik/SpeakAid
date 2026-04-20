package com.example.speakaid;

public class ScriptStep {
    public int id;
    public int scriptId;
    public String text;
    public int stepOrder;

    public ScriptStep(int id, int scriptId, String text, int stepOrder) {
        this.id = id;
        this.scriptId = scriptId;
        this.text = text;
        this.stepOrder = stepOrder;
    }
}