package com.example.speakaid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "classic");
        switch (theme) {
            case "lavender":
                activity.setTheme(R.style.Theme_Speakaid_Lavender);
                break;
            case "ocean":
                activity.setTheme(R.style.Theme_Speakaid_Ocean);
                break;
            case "sunset":
                activity.setTheme(R.style.Theme_Speakaid_Sunset);
                break;
            default:
                activity.setTheme(R.style.Theme_Speakaid);
                break;
        }
    }
}
