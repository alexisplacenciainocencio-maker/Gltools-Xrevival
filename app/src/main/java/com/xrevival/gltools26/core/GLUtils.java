package com.gl.revival;

import android.opengl.GLES20;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public final class GLUtils {
    private GLUtils() {}

    public static String glGetStringNative(int name) {
        return GLES20.glGetString(name);
    }

    public static int parseGlVersionMajor(String version) {
        if (version == null || version.isEmpty()) return 2;
        try {
            String[] parts = version.trim().split("\s+");
            String num = parts[0];
            if (num.startsWith("OpenGL ES ")) num = num.substring(10);
            return Integer.parseInt(num.substring(0, 1));
        } catch (Exception e) {
            GLLog.w("GLUtils", "parseGlVersionMajor fallback: " + e.getMessage());
            return 2;
        }
    }

    public static boolean isEglContextReady() {
        try {
            EGL10 egl = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            return dpy != null && dpy != EGL10.EGL_NO_DISPLAY;
        } catch (Exception e) {
            return false;
        }
    }

    public static String sanitizeRenderer(String raw) {
        if (raw == null) return "Unknown";
        return raw.replaceAll("[^a-zA-Z0-9 \-_()/.]", "").trim();
    }

    public static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static float clampf(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static int nextPowerOfTwo(int x) {
        if (x <= 1) return 1;
        return Integer.highestOneBit(x - 1) << 1;
    }

    public static String hex(int color) {
        return String.format("#%08X", color);
    }
}
