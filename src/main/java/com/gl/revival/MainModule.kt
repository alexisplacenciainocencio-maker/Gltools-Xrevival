package com.gl.revival

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XposedBridge

/**
 * Entry point del módulo Xposed.
 * Orden de ejecución: hooks de GLTools original primero (estado base),
 * luego hooks del motor Revival (perfil activo).
 * Cada grupo de hooks está envuelto en try-catch individual para evitar
 * que un fallo en un hook aísle el resto del módulo.
 */
class MainModule : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            GLLog.i("MainModule", "handleLoadPackage: ${lpparam.packageName}")

            // ============================================================
            // FASE 1: Hooks del target original GLTools (estado base)
            // Se ejecutan primero para establecer el estado premium/red
            // antes de que otros módulos actúen.
            // ============================================================
            safeApply("PremiumHooks") { PremiumHooks.apply(lpparam) }
            safeApply("NetworkHooks") { NetworkHooks.apply(lpparam) }
            safeApply("WebViewHooks") { WebViewHooks.apply(lpparam) }
            safeApply("SSLPinningHooks") { SSLPinningHooks.apply(lpparam) }

            // ============================================================
            // FASE 2: Hooks del motor Revival (perfil activo)
            // Solo si hay un perfil activo y habilitado.
            // ============================================================
            safeApply("RevivalProfile") {
                val pm = ProfileManager(null)
                val p = pm.activeProfile
                if (p != null && p.enabled) {
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_GPU, true))
                        safeApply("GpuHooks") { GpuHooks.apply(lpparam, p) }
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_RAM, true))
                        safeApply("RamHooks") { RamHooks.apply(lpparam, p) }
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_RESOLUTION, true))
                        safeApply("ResolutionHooks") { ResolutionHooks.apply(lpparam, p) }
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_AA, true))
                        safeApply("AntiAliasingHooks") { AntiAliasingHooks.apply(lpparam, p) }
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_TEXTURE, true))
                        safeApply("TextureHooks") { TextureHooks.apply(lpparam, p) }
                    if (Config.isHookEnabled(null, Config.KEY_HOOK_ANISO, true))
                        safeApply("AnisotropicHooks") { AnisotropicHooks.apply(lpparam, p) }
                }
            }

            GLLog.i("MainModule", "All hooks applied successfully for ${lpparam.packageName}")
        } catch (e: Exception) {
            GLLog.e("MainModule", "Global hook failure: ${e.message}")
            XposedBridge.log("[GLRevival] FATAL: $e")
        }
    }

    private fun safeApply(tag: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            GLLog.e("MainModule", "[$tag] Hook group failed: ${e.message}")
            XposedBridge.log("[GLRevival] [$tag] ERROR: $e")
        }
    }
}
