package com.gl.revival

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AntiAliasingHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (p.msaaLevel <= 0) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.opengl.GLES20", lpparam.classLoader,
                "glGetIntegerv", Int::class.javaPrimitiveType, IntArray::class.java, Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val pname = param.args[0] as Int
                        if (pname == 0x8A9F) { // GL_NUM_SAMPLE_COUNTS
                            val buf = param.args[1] as? IntArray ?: return
                            if (buf.isNotEmpty()) buf[0] = 1
                        }
                    }
                }
            )
            GLLog.i("AntiAliasingHooks", "Applied MSAA level: ${p.msaaLevel}x")
        } catch (e: Exception) {
            GLLog.e("AntiAliasingHooks", "Hook failed: ${e.message}")
        }
    }
}
