package com.example.speakaid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "speakaid.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Routine (id INTEGER PRIMARY KEY, title TEXT)");
        db.execSQL("CREATE TABLE Step (id INTEGER PRIMARY KEY, routineId INTEGER, title TEXT, stepOrder INTEGER)");

        db.execSQL("CREATE TABLE Script (id INTEGER PRIMARY KEY, title TEXT)");
        db.execSQL("CREATE TABLE ScriptStep (id INTEGER PRIMARY KEY, scriptId INTEGER, text TEXT, stepOrder INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Routine");
        db.execSQL("DROP TABLE IF EXISTS Step");
        db.execSQL("DROP TABLE IF EXISTS Script");
        db.execSQL("DROP TABLE IF EXISTS ScriptStep");
        onCreate(db);
    }
}