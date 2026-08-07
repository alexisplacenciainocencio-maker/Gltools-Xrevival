package com.gl.revival

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AnisotropicHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (p.anisotropyLevel <= 0) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.opengl.GLES20", lpparam.classLoader,
                "glGetFloatv", Int::class.javaPrimitiveType, FloatArray::class.java, Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val pname = param.args[0] as Int
                        if (pname == 0x84FF) { // GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT
                            val buf = param.args[1] as? FloatArray ?: return
                            if (buf.isNotEmpty()) buf[0] = minOf(buf[0], p.anisotropyLevel.toFloat())
                        }
                    }
                }
            )
            GLLog.i("AnisotropicHooks", "Applied anisotropy: ${p.anisotropyLevel}x")
        } catch (e: Exception) {
            GLLog.e("AnisotropicHooks", "Hook failed: ${e.message}")
        }
    }
}
