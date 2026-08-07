package com.gl.revival

import android.opengl.GLES20
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

object GLUtils {
    @JvmStatic
    fun glGetStringNative(name: Int): String = GLES20.glGetString(name)

    @JvmStatic
    fun parseGlVersionMajor(version: String?): Int {
        if (version.isNullOrEmpty()) return 2
        return try {
            val parts = version.trim().split("\s+".toRegex())
            val num = if (parts[0].startsWith("OpenGL ES ")) parts[0].substring(10) else parts[0]
            num.substring(0, 1).toInt()
        } catch (e: Exception) {
            GLLog.w("GLUtils", "parseGlVersionMajor fallback: ${e.message}")
            2
        }
    }

    @JvmStatic
    fun isEglContextReady(): Boolean =
        try {
            val egl = EGLContext.getEGL() as EGL10
            val dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            dpy != null && dpy != EGL10.EGL_NO_DISPLAY
        } catch (_: Exception) { false }

    @JvmStatic
    fun sanitizeRenderer(raw: String?): String =
        raw?.replace("[^a-zA-Z0-9 \-_()/.]".toRegex(), "")?.trim() ?: "Unknown"

    @JvmStatic
    fun clamp(v: Int, min: Int, max: Int): Int = maxOf(min, minOf(max, v))

    @JvmStatic
    fun clampf(v: Float, min: Float, max: Float): Float = maxOf(min, minOf(max, v))

    @JvmStatic
    fun nextPowerOfTwo(x: Int): Int =
        if (x <= 1) 1 else Integer.highestOneBit(x - 1) shl 1

    @JvmStatic
    fun hex(color: Int): String = String.format("#%08X", color)
}
