package com.gl.revival

import android.app.ActivityManager
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object RamHooks {
    @JvmStatic
    fun apply(lpparam: XC_LoadPackage.LoadPackageParam, p: Profile) {
        if (!p.ramOverride || p.ramMb <= 0) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ActivityManager", lpparam.classLoader,
                "getMemoryInfo", ActivityManager.MemoryInfo::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // totalMem is read-only after API 16; hooking runtime values instead
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                "java.lang.Runtime", lpparam.classLoader,
                "totalMemory",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = p.ramMb.toLong() * 1024L * 1024L
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                "java.lang.Runtime", lpparam.classLoader,
                "maxMemory",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = p.ramMb.toLong() * 1024L * 1024L
                    }
                }
            )
            GLLog.i("RamHooks", "Applied RAM hooks: ${p.ramMb}MB")
        } catch (e: Exception) {
            GLLog.e("RamHooks", "Hook failed: ${e.message}")
        }
    }
}
