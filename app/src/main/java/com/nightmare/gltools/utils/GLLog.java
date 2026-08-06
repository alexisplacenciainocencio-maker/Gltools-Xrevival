package com.nightmare.gltools.utils;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GLLog {

    private static final String GLOBAL_TAG = "GLTools2026";
    private static boolean debug = true;

    public static void setDebug(boolean d) { debug = d; }

    public static void i(@NonNull String tag, @NonNull String msg) {
        Log.i(GLOBAL_TAG, "[" + tag + "] " + msg);
    }

    public static void d(@NonNull String tag, @NonNull String msg) {
        if (debug) Log.d(GLOBAL_TAG, "[" + tag + "] " + msg);
    }

    public static void w(@NonNull String tag, @NonNull String msg) {
        Log.w(GLOBAL_TAG, "[" + tag + "] " + msg);
    }

    public static void e(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        if (t != null) {
            Log.e(GLOBAL_TAG, "[" + tag + "] " + msg, t);
        } else {
            Log.e(GLOBAL_TAG, "[" + tag + "] " + msg);
        }
    }

    public static void e(@NonNull String tag, @NonNull String msg) {
        e(tag, msg, null);
    }
}
