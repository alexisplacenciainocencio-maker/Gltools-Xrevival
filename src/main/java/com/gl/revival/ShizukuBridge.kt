package com.gl.revival

import android.content.Context
import rikka.shizuku.Shizuku

object ShizukuBridge {
    private var bound = false

    @JvmStatic
    fun isReady(): Boolean =
        try { Shizuku.pingBinder() } catch (_: Exception) { false }

    @JvmStatic
    fun hasPermission(): Boolean =
        try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (_: Exception) { false }

    @JvmStatic
    fun requestPermission() {
        try {
            if (!hasPermission() && !Shizuku.shouldShowRequestPermissionRationale()) {
                Shizuku.requestPermission(0)
            }
        } catch (e: Exception) {
            GLLog.w("ShizukuBridge", "Permission request failed: ${e.message}")
        }
    }

    @JvmStatic
    fun bind(ctx: Context?) {
        if (ctx == null) {
            GLLog.w("ShizukuBridge", "bind called with null context")
            return
        }
        try {
            if (isReady() && hasPermission()) {
                bound = true
                GLLog.i("ShizukuBridge", "Bound successfully")
            }
        } catch (e: Exception) {
            GLLog.e("ShizukuBridge", "Bind failed: ${e.message}")
        }
    }

    @JvmStatic
    fun isBound(): Boolean = bound

    @JvmStatic
    fun unbind() {
        bound = false
    }
}
