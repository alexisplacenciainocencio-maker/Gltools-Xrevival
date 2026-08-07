package com.gl.revival

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.ByteArrayInputStream
import java.net.URL

/**
 * Hooks de red para interceptar conexiones a gltools.app.
 * Reconstrucción propia basada en descripción pública.
 *
 * Mejoras post-análisis:
 * - ELIMINADO hook global a HttpURLConnection (riesgo de ANR/crash).
 * - ELIMINADA duplicación de WebView.loadUrl (ahora solo en WebViewHooks).
 * - Enfoque más específico: intercepta URL.openStream() y OkHttp si está presente.
 * - No se intercepta getResponseCode(); solo se reemplaza el InputStream.
 * - Try-catch individual por hook.
 */
object NetworkHooks {

    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != Config.TARGET_PKG) return
        if (!Config.isHookEnabled(null, Config.KEY_HOOK_NETWORK, true)) return

        GLLog.i("NetworkHooks", "Applying network bypass to ${Config.TARGET_PKG}")

        hookURLOpenStream(lpparam)
        hookOkHttpIfPresent(lpparam)
    }

    private fun hookURLOpenStream(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "java.net.URL", lpparam.classLoader,
                "openStream",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val url = param.thisObject as? URL ?: return
                        val urlStr = url.toString()
                        when {
                            urlStr.contains("version.js") -> {
                                param.result = ByteArrayInputStream(
                                    Config.VERSION_JS_RESPONSE.toByteArray(Charsets.UTF_8)
                                )
                                GLLog.d("NetworkHooks", "Injected version.js via URL.openStream")
                            }
                            isBlockedHost(urlStr) -> {
                                param.result = ByteArrayInputStream(
                                    Config.EMPTY_JSON_RESPONSE.toByteArray(Charsets.UTF_8)
                                )
                                GLLog.d("NetworkHooks", "Blocked $urlStr -> empty JSON")
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("NetworkHooks", "URL.openStream hook failed: ${e.message}")
        }
    }

    private fun hookOkHttpIfPresent(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Intenta interceptar OkHttpClient.Builder.addInterceptor si la app usa OkHttp
        val okHttpClasses = arrayOf(
            "okhttp3.OkHttpClient\$Builder",
            "okhttp3.OkHttpClient.Builder",
            "com.squareup.okhttp.OkHttpClient"
        )
        okHttpClasses.forEach { className ->
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                    ?: return@forEach
                clazz.declaredMethods.forEach { method ->
                    if (method.name == "addInterceptor" || method.name == "interceptors") {
                        try {
                            XposedHelpers.findAndHookMethod(
                                clazz, method.name, *method.parameterTypes,
                                object : XC_MethodHook() {
                                    override fun beforeHookedMethod(param: MethodHookParam) {
                                        GLLog.d("NetworkHooks", "OkHttp ${method.name} called")
                                    }
                                }
                            )
                        } catch (_: Exception) { }
                    }
                }
                GLLog.i("NetworkHooks", "OkHttp hook applied to $className")
            } catch (_: Exception) { }
        }

        // Hook a OkHttp Call.execute() / Call.enqueue() como último recurso
        try {
            val callClass = XposedHelpers.findClassIfExists("okhttp3.Call", lpparam.classLoader)
                ?: XposedHelpers.findClassIfExists("okhttp3.internal.connection.RealCall", lpparam.classLoader)
            callClass?.declaredMethods?.forEach { method ->
                if (method.name == "execute" || method.name == "enqueue") {
                    try {
                        XposedHelpers.findAndHookMethod(
                            callClass, method.name, *method.parameterTypes,
                            object : XC_MethodHook() {
                                override fun beforeHookedMethod(param: MethodHookParam) {
                                    val request = XposedHelpers.getObjectField(param.thisObject, "originalRequest")
                                        ?: XposedHelpers.getObjectField(param.thisObject, "request")
                                    val url = request?.let {
                                        XposedHelpers.callMethod(it, "url")?.toString()
                                    }
                                    if (url != null && isBlockedHost(url)) {
                                        GLLog.w("NetworkHooks", "Blocked OkHttp call to $url")
                                        // No podemos reemplazar fácilmente la respuesta aquí sin
                                        // construir un Response fake, así que logueamos y dejamos pasar
                                        // para evitar crash. El hook de URL.openStream cubre la mayoría.
                                    }
                                }
                            }
                        )
                    } catch (_: Exception) { }
                }
            }
        } catch (_: Exception) { }
    }

    private fun isBlockedHost(url: String): Boolean =
        Config.BLOCKED_HOSTS.any { url.contains(it, ignoreCase = true) }
}
