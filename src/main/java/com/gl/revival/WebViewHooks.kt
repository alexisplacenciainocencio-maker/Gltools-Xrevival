package com.gl.revival

import android.webkit.WebView
import androidx.webkit.WebViewAssetLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Hooks para interceptar WebView y servir contenido custom vía WebViewAssetLoader.
 * Reconstrucción propia basada en descripción pública.
 *
 * Mejoras post-análisis:
 * - Usa WebViewAssetLoader para servir assets locales con rutas relativas.
 * - HTML/CSS/JS separados en assets/webview/ (modular y mantenible).
 * - Try-catch individual por hook.
 * - No duplica funcionalidad de NetworkHooks (solo maneja WebView interno).
 */
object WebViewHooks {

    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != Config.TARGET_PKG) return
        if (!Config.isHookEnabled(null, Config.KEY_HOOK_WEBVIEW, true)) return

        GLLog.i("WebViewHooks", "Applying WebView hooks to ${Config.TARGET_PKG}")

        hookLoadUrl(lpparam)
        hookLoadData(lpparam)
    }

    private fun hookLoadUrl(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.webkit.WebView", lpparam.classLoader,
                "loadUrl", String::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val url = param.args[0] as? String ?: return
                        if (isDeadFeed(url)) {
                            val webView = param.thisObject as? WebView ?: return
                            injectViaAssetLoader(webView)
                            param.result = null
                            GLLog.d("WebViewHooks", "Intercepted dead URL, injected asset loader content: $url")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("WebViewHooks", "loadUrl hook failed: ${e.message}")
        }
    }

    private fun hookLoadData(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.webkit.WebView", lpparam.classLoader,
                "loadData", String::class.java, String::class.java, String::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val data = param.args[0] as? String ?: return
                        if (isDeadFeed(data)) {
                            val webView = param.thisObject as? WebView ?: return
                            injectViaAssetLoader(webView)
                            param.result = null
                            GLLog.d("WebViewHooks", "Intercepted dead loadData, injected asset loader content")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("WebViewHooks", "loadData hook failed: ${e.message}")
        }
    }

    private fun isDeadFeed(urlOrData: String): Boolean =
        Config.DEAD_FEED_PATTERNS.any { urlOrData.contains(it, ignoreCase = true) }

    private fun injectViaAssetLoader(webView: WebView) {
        try {
            // Configurar WebViewAssetLoader para servir assets locales
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(webView.context))
                .build()

            val client = object : android.webkit.WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: android.webkit.WebResourceRequest
                ): android.webkit.WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                        ?: super.shouldInterceptRequest(view, request)
                }
            }
            webView.webViewClient = client
            webView.loadUrl("${Config.WEBVIEW_ASSET_BASE}webview/index.html")
        } catch (e: Exception) {
            // Fallback si WebViewAssetLoader no está disponible (AndroidX no presente)
            GLLog.w("WebViewHooks", "WebViewAssetLoader failed, using fallback: ${e.message}")
            webView.loadData(FALLBACK_HTML, "text/html", "UTF-8")
        }
    }

    private const val FALLBACK_HTML = """<!DOCTYPE html>
<html><head><meta charset="UTF-8">
<style>
body{font-family:sans-serif;background:#0a0a0a;color:#eee;padding:24px}
h1{color:#0A84FF}
.card{background:#1c1c1e;border-radius:12px;padding:16px;margin:12px 0}
</style>
</head><body>
<h1>GLTools 2026X Revival</h1>
<div class="card">
<p>Original API servers are offline. This interface is provided by the Revival module.</p>
<p>Premium status: <strong style="color:#30D158">ACTIVE</strong></p>
</div>
</body></html>"""
}
