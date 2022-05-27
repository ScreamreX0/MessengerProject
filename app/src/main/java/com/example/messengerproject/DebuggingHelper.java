package com.example.messengerproject;

import android.util.Log;

import com.example.messengerproject.activities.AuthActivity;

public class DebuggingHelper {
    public static String getDebugCode(Class classObject) {
        if (classObject.getSimpleName().equals(AuthActivity.class.getSimpleName())) {
            return DebugCodes.authActivityDebugCode;
        } else {
            return DebugCodes.globalDebugCode;
        }
    }

    public static void log(Class classObject, String title, String text) {
        Log.v(DebugCodes.globalDebugCode, "" + classObject.getSimpleName() + ": " + title + " - " + text);
    }

    public static void log(Class classObject, String text) {
        Log.v(DebugCodes.globalDebugCode, "" + classObject.getSimpleName() + ": " + text);
    }
}

class DebugCodes {
    public static final String authActivityDebugCode = "authActivityDebugCode";
    public static final String globalDebugCode = "globalDebugCode";
}