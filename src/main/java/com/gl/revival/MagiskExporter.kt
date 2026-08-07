package com.gl.revival

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class MagiskExporter : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pm = ProfileManager(this)
        val p = pm.activeProfile
        if (p == null) {
            GLLog.w("MagiskExporter", "No active profile to export")
            stopSelf()
            return START_NOT_STICKY
        }
        generateModule(p)
        return START_NOT_STICKY
    }

    private fun generateModule(p: Profile) {
        val dir = File(Environment.getExternalStorageDirectory(), "GLRevival-Magisk")
        dir.mkdirs()
        try {
            FileOutputStream(File(dir, "module.prop")).use { fos ->
                OutputStreamWriter(fos).use { w ->
                    w.write("id=glrevival
")
                    w.write("name=GLTools 2026X Revival
")
                    w.write("version=2026.1.0
")
                    w.write("versionCode=20260100
")
                    w.write("author=GLRevivalTeam
")
                    w.write("description=GPU/RAM spoofing module
")
                }
            }
            FileOutputStream(File(dir, "post-fs-data.sh")).use { fos ->
                OutputStreamWriter(fos).use { w ->
                    w.write("#!/system/bin/sh
")
                    w.write("# GLTools Revival Magisk module
")
                    w.write("# Generated from profile: ${p.name}
")
                    w.write("# RAM override: ${p.ramMb}MB
")
                    w.write("# Renderer: ${p.gpuRenderer}
")
                    w.write("# This script is a stub; actual spoofing is done via Xposed
")
                }
            }
            GLLog.i("MagiskExporter", "Module written to ${dir.absolutePath}")
        } catch (e: Exception) {
            GLLog.e("MagiskExporter", "Export failed: ${e.message}")
        }
    }
}
