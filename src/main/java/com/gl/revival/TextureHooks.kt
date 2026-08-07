package com.gl.revival

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object TextureHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (p.textureQuality < 0 || p.textureQuality > 2) return
        try {
            val maxSizes = intArrayOf(512, 1024, 2048, 4096, 8192)
            val limit = maxSizes[GLUtils.clamp(p.textureQuality, 0, maxSizes.size - 1)]
            XposedHelpers.findAndHookMethod(
                "android.opengl.GLES20", lpparam.classLoader,
                "glGetIntegerv", Int::class.javaPrimitiveType, IntArray::class.java, Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val pname = param.args[0] as Int
                        if (pname == 0x0D33) { // GL_MAX_TEXTURE_SIZE
                            val buf = param.args[1] as? IntArray ?: return
                            if (buf.isNotEmpty()) buf[0] = minOf(buf[0], limit)
                        }
                    }
                }
            )
            GLLog.i("TextureHooks", "Applied texture quality: ${p.textureQuality} (max $limit)")
        } catch (e: Exception) {
            GLLog.e("TextureHooks", "Hook failed: ${e.message}")
        }
    }
}
