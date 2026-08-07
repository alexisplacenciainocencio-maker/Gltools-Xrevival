package com.gl.revival

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GLLog {
    private val buffer = mutableListOf<String>()
    private const val MAX_LINES = 1000
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    private fun log(level: String, tag: String, msg: String) {
        val line = "${sdf.format(Date())} [$level] $tag: $msg"
        synchronized(buffer) {
            buffer.add(line)
            if (buffer.size > MAX_LINES) buffer.removeAt(0)
        }
        android.util.Log.println(levelToInt(level), tag, msg)
    }

    private fun levelToInt(level: String): Int = when (level) {
        "V" -> android.util.Log.VERBOSE
        "D" -> android.util.Log.DEBUG
        "I" -> android.util.Log.INFO
        "W" -> android.util.Log.WARN
        "E" -> android.util.Log.ERROR
        else -> android.util.Log.INFO
    }

    @JvmStatic
    fun v(tag: String, msg: String) = log("V", tag, msg)

    @JvmStatic
    fun d(tag: String, msg: String) = log("D", tag, msg)

    @JvmStatic
    fun i(tag: String, msg: String) = log("I", tag, msg)

    @JvmStatic
    fun w(tag: String, msg: String) = log("W", tag, msg)

    @JvmStatic
    fun e(tag: String, msg: String) = log("E", tag, msg)

    @JvmStatic
    fun dump(): String = synchronized(buffer) { buffer.joinToString("
") }

    @JvmStatic
    fun clear() = synchronized(buffer) { buffer.clear() }
}
