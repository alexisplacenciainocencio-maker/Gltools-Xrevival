package com.gl.revival

import org.json.JSONObject

data class Profile(
    var id: String = "",
    var name: String = "Unnamed",
    var enabled: Boolean = true,
    var gpuRenderer: String = "",
    var gpuVendor: String = "",
    var gpuVersion: String = "",
    var gpuDriverMode: Int = 0,
    var ramOverride: Boolean = false,
    var ramMb: Int = 4096,
    var resolutionScale: Int = 100,
    var msaaLevel: Int = 0,
    var textureQuality: Int = 2,
    var anisotropyLevel: Int = 0
) {
    fun toJson(): JSONObject =
        try {
            JSONObject().apply {
                put("id", id)
                put("name", name)
                put("enabled", enabled)
                put("gpuRenderer", gpuRenderer)
                put("gpuVendor", gpuVendor)
                put("gpuVersion", gpuVersion)
                put("gpuDriverMode", gpuDriverMode)
                put("ramOverride", ramOverride)
                put("ramMb", ramMb)
                put("resolutionScale", resolutionScale)
                put("msaaLevel", msaaLevel)
                put("textureQuality", textureQuality)
                put("anisotropyLevel", anisotropyLevel)
            }
        } catch (e: Exception) {
            GLLog.w("Profile", "toJson error: ${e.message}")
            JSONObject()
        }

    companion object {
        @JvmStatic
        fun fromJson(o: JSONObject): Profile =
            try {
                Profile(
                    id = o.optString("id", ""),
                    name = o.optString("name", "Unnamed"),
                    enabled = o.optBoolean("enabled", true),
                    gpuRenderer = o.optString("gpuRenderer", ""),
                    gpuVendor = o.optString("gpuVendor", ""),
                    gpuVersion = o.optString("gpuVersion", ""),
                    gpuDriverMode = o.optInt("gpuDriverMode", 0),
                    ramOverride = o.optBoolean("ramOverride", false),
                    ramMb = o.optInt("ramMb", 4096),
                    resolutionScale = o.optInt("resolutionScale", 100),
                    msaaLevel = o.optInt("msaaLevel", 0),
                    textureQuality = o.optInt("textureQuality", 2),
                    anisotropyLevel = o.optInt("anisotropyLevel", 0)
                )
            } catch (e: Exception) {
                GLLog.w("Profile", "fromJson error: ${e.message}")
                Profile()
            }
    }
}
