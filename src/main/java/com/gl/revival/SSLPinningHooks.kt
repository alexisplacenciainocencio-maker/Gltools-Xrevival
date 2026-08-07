package com.gl.revival

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

/**
 * Bypass básico de SSL Certificate Pinning.
 * Técnica estándar en módulos Xposed para permitir interceptación HTTPS.
 *
 * Advertencia: reduce la seguridad de la conexión. Solo para análisis local.
 */
object SSLPinningHooks {

    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != Config.TARGET_PKG) return
        GLLog.i("SSLPinningHooks", "Applying SSL pinning bypass to ${Config.TARGET_PKG}")

        bypassTrustManager(lpparam)
        bypassOkHttpPinning(lpparam)
        bypassWebViewSSL(lpparam)
    }

    private fun bypassTrustManager(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val trustAll = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAll, java.security.SecureRandom())

            // Hook SSLContext.getDefault() para devolver nuestro contexto
            XposedHelpers.findAndHookMethod(
                "javax.net.ssl.SSLContext", lpparam.classLoader,
                "getDefault",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = sslContext
                        GLLog.d("SSLPinningHooks", "SSLContext.getDefault() bypassed")
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("SSLPinningHooks", "TrustManager bypass failed: ${e.message}")
        }
    }

    private fun bypassOkHttpPinning(lpparam: XC_LoadPackage.LoadPackageParam) {
        // OkHttp usa CertificatePinner para pinning
        val pinnerClasses = arrayOf(
            "okhttp3.CertificatePinner",
            "com.squareup.okhttp.CertificatePinner"
        )
        pinnerClasses.forEach { className ->
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                    ?: return@forEach
                clazz.declaredMethods.forEach { method ->
                    if (method.name == "check" || method.name == "matches") {
                        try {
                            XposedHelpers.findAndHookMethod(
                                clazz, method.name, *method.parameterTypes,
                                object : XC_MethodHook() {
                                    override fun beforeHookedMethod(param: MethodHookParam) {
                                        param.result = null // No-op, bypass check
                                        GLLog.d("SSLPinningHooks", "Bypassed $className.${method.name}")
                                    }
                                }
                            )
                        } catch (_: Exception) { }
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun bypassWebViewSSL(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.webkit.WebViewClient", lpparam.classLoader,
                "onReceivedSslError",
                "android.webkit.WebView",
                "android.webkit.SslErrorHandler",
                "android.net.http.SslError",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val handler = param.args[1]
                        XposedHelpers.callMethod(handler, "proceed")
                        param.result = null
                        GLLog.d("SSLPinningHooks", "WebView SSL error auto-proceeded")
                    }
                }
            )
        } catch (e: Exception) {
            GLLog.w("SSLPinningHooks", "WebView SSL bypass failed: ${e.message}")
        }
    }
}
