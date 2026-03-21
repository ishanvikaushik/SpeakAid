package com.example.speakaid;

import android.content.Context;
import android.database.Cursor;
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

        // Routine table
        db.execSQL("CREATE TABLE Routine (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT)");

        // Step table
        db.execSQL("CREATE TABLE Step (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "routineId INTEGER, " +
                "title TEXT, " +
                "stepOrder INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Routine");
        db.execSQL("DROP TABLE IF EXISTS Step");
        onCreate(db);
    }

    // 🔹 Insert Routine
    public void insertRoutine(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Routine (title) VALUES ('" + title + "')");
    }

    // 🔹 Insert Step
    public void insertStep(int routineId, String title, int order) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Step (routineId, title, stepOrder) VALUES (" +
                routineId + ", '" + title + "', " + order + ")");
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
    public boolean isStepTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Step", null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count == 0;
    }
}