package com.gl.revival

import android.opengl.GLES20
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import javax.microedition.khronos.opengles.GL10

object GpuHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (p.gpuDriverMode == SpoofMode.PASS_THROUGH.code) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.opengl.GLES20", lpparam.classLoader,
                "glGetString", Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val name = param.args[0] as Int
                        when (name) {
                            GL10.GL_RENDERER -> if (p.gpuRenderer.isNotEmpty()) param.result = p.gpuRenderer
                            GL10.GL_VENDOR -> if (p.gpuVendor.isNotEmpty()) param.result = p.gpuVendor
                            GL10.GL_VERSION -> if (p.gpuVersion.isNotEmpty()) param.result = p.gpuVersion
                        }
                    }
                }
            )
            GLLog.i("GpuHooks", "Applied EGL hooks for ${p.gpuRenderer}")
        } catch (e: Exception) {
            GLLog.e("GpuHooks", "EGL hook failed: ${e.message}")
        }
        if (p.gpuDriverMode == SpoofMode.VULKAN_SPOOF.code || p.gpuDriverMode == SpoofMode.BOTH.code) {
            GLLog.w("GpuHooks", "Vulkan spoofing requires native library; not implemented in this build")
        }
    }
}
