package com.gl.revival

import android.view.View
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Hooks para forzar estado Premium en GLTools original (com.superpaninbros.glng).
 * Reconstrucción propia basada en descripción pública.
 *
 * Mejoras post-análisis:
 * - Eliminado spoofeo de getCoins (no crítico, riesgo de detección).
 * - Ocultación de botón companion por texto del botón (más robusto que solo tag).
 * - Try-catch individual por cada hook para evitar cascada de fallos.
 * - Configuración centralizada vía Config.kt.
 */
object PremiumHooks {

    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != Config.TARGET_PKG) return
        if (!Config.isHookEnabled(null, Config.KEY_HOOK_PREMIUM, true)) return

        GLLog.i("PremiumHooks", "Applying premium bypass to ${Config.TARGET_PKG}")

        hookPremiumStatus(lpparam)
        hookPurchaseVerification(lpparam)
        hookCompanionButton(lpparam)
    }

    private fun hookPremiumStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        Config.PREMIUM_CLASSES.forEach { className ->
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                    ?: return@forEach

                clazz.declaredMethods.forEach { method ->
                    val lower = method.name.lowercase()
                    if (lower in Config.PREMIUM_BOOLEAN_METHODS) {
                        try {
                            XposedHelpers.findAndHookMethod(
                                clazz, method.name, *method.parameterTypes,
                                object : XC_MethodHook() {
                                    override fun afterHookedMethod(param: MethodHookParam) {
                                        param.result = true
                                    }
                                }
                            )
                            GLLog.d("PremiumHooks", "Hooked $className.${method.name} -> true")
                        } catch (e: Exception) {
                            GLLog.w("PremiumHooks", "Failed to hook $className.${method.name}: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                GLLog.w("PremiumHooks", "Class not found or error: $className")
            }
        }
    }

    private fun hookPurchaseVerification(lpparam: XC_LoadPackage.LoadPackageParam) {
        Config.PREMIUM_CLASSES.forEach { className ->
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                    ?: return@forEach

                clazz.declaredMethods.forEach { method ->
                    val lower = method.name.lowercase()
                    if (Config.VERIFY_METHODS.any { lower.contains(it) }) {
                        try {
                            XposedHelpers.findAndHookMethod(
                                clazz, method.name, *method.parameterTypes,
                                object : XC_MethodHook() {
                                    override fun afterHookedMethod(param: MethodHookParam) {
                                        param.result = true
                                    }
                                }
                            )
                            GLLog.d("PremiumHooks", "Hooked verify $className.${method.name} -> true")
                        } catch (e: Exception) {
                            GLLog.w("PremiumHooks", "Failed verify hook: ${e.message}")
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun hookCompanionButton(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Estrategia 1: interceptar setText en TextView para detectar textos de companion
        try {
            XposedHelpers.findAndHookMethod(
                "android.widget.TextView", lpparam.classLoader,
                "setText", CharSequence::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val text = param.args[0]?.toString() ?: return
                        if (Config.COMPANION_BUTTON_TEXTS.any { text.contains(it, ignoreCase = true) }) {
                            val view = param.thisObject as? View ?: return
                            view.visibility = View.GONE
                            GLLog.d("PremiumHooks", "Hid companion button by text: "$text"")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("PremiumHooks", "TextView hook failed: ${e.message}")
        }

        // Estrategia 2: fallback por tag (mantiene compatibilidad con implementación anterior)
        try {
            XposedHelpers.findAndHookMethod(
                "android.view.View", lpparam.classLoader,
                "setVisibility", Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as? View ?: return
                        val tag = view.tag
                        if (tag is String && Config.COMPANION_TAGS.any { tag.contains(it, ignoreCase = true) }) {
                            param.args[0] = View.GONE
                            GLLog.d("PremiumHooks", "Hid companion button by tag: "$tag"")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("PremiumHooks", "View tag hook failed: ${e.message}")
        }
    }
}
