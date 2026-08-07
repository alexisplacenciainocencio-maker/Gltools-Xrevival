package com.gl.revival

/**
 * Configuración centralizada del módulo.
 * Todos los valores hardcodeados migrados aquí para facilitar
 * actualizaciones sin recompilación (vía SharedPreferences override).
 */
object Config {
    // Target package de GLTools original
    const val TARGET_PKG = "com.superpaninbros.glng"

    // Dominios bloqueados / interceptados
    val BLOCKED_HOSTS = setOf(
        "gltools.app",
        "www.gltools.app",
        "api.gltools.app",
        "cdn.gltools.app"
    )

    // Clases candidatas para premium spoofing
    val PREMIUM_CLASSES = arrayOf(
        "com.superpaninbros.glng.CoinsManager",
        "com.superpaninbros.glng.billing.CoinsManager",
        "com.superpaninbros.glng.premium.CoinsManager",
        "com.superpaninbros.glng.data.CoinsManager",
        "com.superpaninbros.glng.billing.BillingManager",
        "com.superpaninbros.glng.purchase.PurchaseManager",
        "com.superpaninbros.glng.license.LicenseChecker",
        "com.superpaninbros.glng.subscription.SubscriptionManager"
    )

    // Métodos booleanos de estado premium
    val PREMIUM_BOOLEAN_METHODS = setOf(
        "ispremium", "haspremium", "ispro", "isplus", "isvip",
        "isunlocked", "islicensed", "issubscribed", "haslicense"
    )

    // Métodos de verificación de compra
    val VERIFY_METHODS = setOf(
        "check", "verify", "validate", "authenticate", "confirm"
    )

    // Textos del botón companion a detectar
    val COMPANION_BUTTON_TEXTS = setOf(
        "get companion", "companion", "get premium", "upgrade",
        "buy premium", "unlock", "go pro"
    )

    // Tags de vista asociados al companion (fallback)
    val COMPANION_TAGS = setOf("companion", "premium_btn", "upgrade_btn")

    // Respuesta version.js hardcodeada
    const val VERSION_JS_RESPONSE = """{"version":"1.0","premium":true,"api_ok":true,"revival":"2026.1.0","server_time":${System.currentTimeMillis()}}"""

    // Respuesta JSON vacía para endpoints bloqueados
    const val EMPTY_JSON_RESPONSE = "{}"

    // URLs de feeds muertos a interceptar en WebView
    val DEAD_FEED_PATTERNS = setOf(
        "twitter.com", "x.com", "gltools.app/help",
        "gltools.app/support", "gltools.app/news"
    )

    // Asset path para contenido WebView custom
    const val WEBVIEW_ASSET_BASE = "https://appassets.androidplatform.net/assets/webview/"

    // Niveles de log
    const val LOG_MAX_LINES = 1000

    // Límites RAM
    const val RAM_MIN_MB = 512
    const val RAM_MAX_MB = 32768
    const val RAM_DEFAULT_MB = 4096

    // Flags de activación de hooks (persistibles via SharedPreferences)
    const val PREFS_HOOKS = "glrevival_hooks"
    const val KEY_HOOK_PREMIUM = "hook_premium"
    const val KEY_HOOK_NETWORK = "hook_network"
    const val KEY_HOOK_WEBVIEW = "hook_webview"
    const val KEY_HOOK_GPU = "hook_gpu"
    const val KEY_HOOK_RAM = "hook_ram"
    const val KEY_HOOK_RESOLUTION = "hook_resolution"
    const val KEY_HOOK_AA = "hook_antialiasing"
    const val KEY_HOOK_TEXTURE = "hook_texture"
    const val KEY_HOOK_ANISO = "hook_anisotropic"

    fun isHookEnabled(ctx: android.content.Context?, key: String, default: Boolean = true): Boolean {
        if (ctx == null) return default
        return ctx.getSharedPreferences(PREFS_HOOKS, android.content.Context.MODE_PRIVATE)
            .getBoolean(key, default)
    }

    fun setHookEnabled(ctx: android.content.Context, key: String, enabled: Boolean) {
        ctx.getSharedPreferences(PREFS_HOOKS, android.content.Context.MODE_PRIVATE)
            .edit().putBoolean(key, enabled).apply()
    }
}
