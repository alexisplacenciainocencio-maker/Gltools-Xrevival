package com.gl.revival

import android.content.res.Resources
import android.util.DisplayMetrics
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object ResolutionHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (p.resolutionScale <= 0 || p.resolutionScale >= 100) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.content.res.Resources", lpparam.classLoader,
                "getDisplayMetrics",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val dm = param.result as? DisplayMetrics ?: return
                        val f = p.resolutionScale / 100f
                        dm.widthPixels = (dm.widthPixels * f).toInt()
                        dm.heightPixels = (dm.heightPixels * f).toInt()
                        dm.density *= f
                        dm.scaledDensity *= f
                        dm.xdpi *= f
                        dm.ydpi *= f
                    }
                }
            )
            GLLog.i("ResolutionHooks", "Applied scale: ${p.resolutionScale}%")
        } catch (e: Exception) {
            GLLog.e("ResolutionHooks", "Hook failed: ${e.message}")
        }
    }
}
