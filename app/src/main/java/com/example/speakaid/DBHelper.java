package com.example.speakaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "speakaid.db";
    private static final int DB_VERSION = 3; // Incremented to version 3

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Routine table with lastStep and completedDate
        db.execSQL("CREATE TABLE Routine (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "lastStep INTEGER DEFAULT 0, " +
                "completedDate TEXT)"); // Store date as "YYYY-MM-DD"

        // Step table
        db.execSQL("CREATE TABLE Step (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "routineId INTEGER, " +
                "title TEXT, " +
                "stepOrder INTEGER)");

        db.execSQL("CREATE TABLE Script (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT)");

        db.execSQL("CREATE TABLE ScriptStep (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "scriptId INTEGER, " +
                "title TEXT, " +
                "stepOrder INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Routine ADD COLUMN lastStep INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE Routine ADD COLUMN completedDate TEXT");
        }
    }

    // 🔹 Insert Routine
    public long insertRoutine(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("lastStep", 0);
        return db.insert("Routine", null, values);
    }

    // 🔹 Update Progress
    public void updateRoutineProgress(int id, int lastStep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastStep", lastStep);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // 🔹 Mark as Completed for today
    public void markRoutineCompleted(int id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastStep", 0);
        values.put("completedDate", date);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // 🔹 Reset completion status (e.g. if user wants to start again or for next day check)
    public void resetRoutineCompletion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completedDate", (String)null);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // 🔹 Insert Step
    public void insertStep(long routineId, String title, int order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("routineId", routineId);
        values.put("title", title);
        values.put("stepOrder", order);
        db.insert("Step", null, values);
    }

    public long insertScript(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        return db.insert("Script", null, values);
    }

    public void insertScriptStep(long scriptId, String title, int order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("scriptId", scriptId);
        values.put("title", title);
        values.put("stepOrder", order);
        db.insert("ScriptStep", null, values);
    }

    // 🔹 Get all routines
    public Cursor getRoutines() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Routine", null);
    }

    // 🔹 Get steps for a routine
    public Cursor getSteps(int routineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM Step WHERE routineId = " + routineId + " ORDER BY stepOrder",
                null
        );
    }

    public Cursor getScripts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Script", null);
    }

    public Cursor getScriptSteps(int scriptId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM ScriptStep WHERE scriptId = " + scriptId + " ORDER BY stepOrder",
                null
        );
    }

    public boolean isStepTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Step", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
}
