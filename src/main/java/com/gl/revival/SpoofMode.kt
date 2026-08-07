package com.gl.revival

enum class SpoofMode(
    val code: Int,
    val label: String,
    val description: String
) {
    PASS_THROUGH(0, "Pass-through", "No spoofing applied"),
    EGL_SPOOF(1, "EGL Spoof", "Override EGL renderer strings"),
    VULKAN_SPOOF(2, "Vulkan Spoof", "Override Vulkan device properties"),
    BOTH(3, "EGL + Vulkan", "Spoof both APIs"),
    SHIZUKU(4, "Shizuku", "Use Shizuku for elevated hooks");

    companion object {
        @JvmStatic
        fun fromCode(code: Int): SpoofMode =
            values().find { it.code == code } ?: PASS_THROUGH
    }
}
