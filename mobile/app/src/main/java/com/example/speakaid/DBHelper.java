package com.example.speakaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "speakaid.db";
    private static final int DB_VERSION = 8; // Incremented for Laundry migration

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Routine (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, lastStep INTEGER DEFAULT 0, completedDate TEXT)");
        db.execSQL("CREATE TABLE Step (id INTEGER PRIMARY KEY AUTOINCREMENT, routineId INTEGER, title TEXT, stepOrder INTEGER)");
        db.execSQL("CREATE TABLE Script (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
        db.execSQL("CREATE TABLE ScriptStep (id INTEGER PRIMARY KEY AUTOINCREMENT, scriptId INTEGER, title TEXT, stepOrder INTEGER)");
        db.execSQL("CREATE TABLE CustomPhrase (id INTEGER PRIMARY KEY AUTOINCREMENT, phrase TEXT, iconName TEXT)");
        db.execSQL("CREATE TABLE DailyBadges (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT UNIQUE, badgeType TEXT)");
        db.execSQL("CREATE TABLE Badges (id INTEGER PRIMARY KEY AUTOINCREMENT, routineTitle TEXT UNIQUE, count INTEGER DEFAULT 0)");
        
        db.execSQL("CREATE TABLE ChatHistory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "roomId TEXT, " +
                "message TEXT, " +
                "senderName TEXT, " +
                "isSent INTEGER, " +
                "timestamp LONG)");

        // Pre-populate Laundry Routine
        long laundryId = insertRoutine(db, "Laundry");
        insertStep(db, laundryId, "Sort Clothes", 0);
        insertStep(db, laundryId, "Add Detergent", 1);
        insertStep(db, laundryId, "Start Machine", 2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) db.execSQL("CREATE TABLE IF NOT EXISTS CustomPhrase (id INTEGER PRIMARY KEY AUTOINCREMENT, phrase TEXT, iconName TEXT)");
        if (oldVersion < 5) db.execSQL("CREATE TABLE IF NOT EXISTS DailyBadges (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT UNIQUE, badgeType TEXT)");
        if (oldVersion < 6) db.execSQL("CREATE TABLE IF NOT EXISTS Badges (id INTEGER PRIMARY KEY AUTOINCREMENT, routineTitle TEXT UNIQUE, count INTEGER DEFAULT 0)");
        if (oldVersion < 7) {
            db.execSQL("CREATE TABLE IF NOT EXISTS ChatHistory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "roomId TEXT, " +
                    "message TEXT, " +
                    "senderName TEXT, " +
                    "isSent INTEGER, " +
                    "timestamp LONG)");
        }
        if (oldVersion < 8) {
             long laundryId = insertRoutine(db, "Laundry");
             insertStep(db, laundryId, "Sort Clothes", 0);
             insertStep(db, laundryId, "Add Detergent", 1);
             insertStep(db, laundryId, "Start Machine", 2);
        }
    }

    // --- Helpers for onCreate/onUpgrade ---
    private long insertRoutine(SQLiteDatabase db, String title) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("lastStep", 0);
        return db.insert("Routine", null, values);
    }

    private void insertStep(SQLiteDatabase db, long routineId, String title, int order) {
        ContentValues values = new ContentValues();
        values.put("routineId", routineId);
        values.put("title", title);
        values.put("stepOrder", order);
        db.insert("Step", null, values);
    }

    // --- Chat Methods ---
    public void saveChatMessage(String roomId, String message, String senderName, boolean isSent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("roomId", roomId);
        values.put("message", message);
        values.put("senderName", senderName);
        values.put("isSent", isSent ? 1 : 0);
        values.put("timestamp", System.currentTimeMillis());
        db.insert("ChatHistory", null, values);
    }

    public Cursor getChatHistory(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ChatHistory WHERE roomId = ? ORDER BY timestamp ASC", new String[]{roomId});
    }

    public Cursor getAllChatHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ChatHistory ORDER BY timestamp ASC", null);
    }

    // --- Routine Methods ---
    public long insertRoutine(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("lastStep", 0);
        return db.insert("Routine", null, values);
    }

    public void updateRoutineProgress(int id, int lastStep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastStep", lastStep);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void markRoutineCompleted(int id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastStep", 0);
        values.put("completedDate", date);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
        awardRoutineBadge(getRoutineTitle(id));
        if (areAllRoutinesCompletedToday(date)) earnBadge(date);
    }

    public void resetRoutineCompletion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completedDate", (String)null);
        values.put("lastStep", 0);
        db.update("Routine", values, "id = ?", new String[]{String.valueOf(id)});
    }

    private String getRoutineTitle(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title FROM Routine WHERE id = ?", new String[]{String.valueOf(id)});
        String title = "Unknown";
        if (cursor.moveToFirst()) title = cursor.getString(0);
        cursor.close();
        return title;
    }

    private boolean areAllRoutinesCompletedToday(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Routine WHERE completedDate != ? OR completedDate IS NULL", new String[]{date});
        cursor.moveToFirst();
        int remaining = cursor.getInt(0);
        cursor.close();
        return remaining == 0;
    }

    private void awardRoutineBadge(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO Badges (routineTitle, count) VALUES (?, 0)", new String[]{title});
        db.execSQL("UPDATE Badges SET count = count + 1 WHERE routineTitle = ?", new String[]{title});
    }

    public Cursor getAllBadges() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM Badges", null);
    }

    public void earnBadge(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("badgeType", "DailyStar");
        db.insertWithOnConflict("DailyBadges", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int getStreak() {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        int streak = 0;
        while (true) {
            String date = sdf.format(cal.getTime());
            Cursor cursor = db.rawQuery("SELECT * FROM DailyBadges WHERE date = ?", new String[]{date});
            if (cursor.getCount() > 0) {
                streak++;
                cal.add(Calendar.DATE, -1);
            } else {
                cursor.close();
                break;
            }
            cursor.close();
        }
        return streak;
    }

    public int getTotalStars() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM DailyBadges", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

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

    public void insertCustomPhrase(String phrase, String iconName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phrase", phrase);
        values.put("iconName", iconName);
        db.insert("CustomPhrase", null, values);
    }

    public Cursor getRoutines() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM Routine", null);
    }

    public Cursor getSteps(int routineId) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM Step WHERE routineId = " + routineId + " ORDER BY stepOrder", null);
    }

    public Cursor getScripts() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM Script", null);
    }

    public Cursor getScriptSteps(int scriptId) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM ScriptStep WHERE scriptId = " + scriptId + " ORDER BY stepOrder", null);
    }

    public Cursor getCustomPhrases() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM CustomPhrase", null);
    }

    public void resetAllRoutines() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastStep", 0);
        values.put("completedDate", (String)null);
        db.update("Routine", values, null, null);
        
        // Reset badges and daily stars
        db.execSQL("DELETE FROM Badges");
        db.execSQL("DELETE FROM DailyBadges");
    }
}
