# Xposed / YukiHookAPI
-keep class de.robv.android.xposed.** { *; }
-keep class com.highcapable.yukihookapi.** { *; }
-keep class com.nightmare.gltools.MainModule { *; }
-keep class com.nightmare.gltools.hooks.** { *; }
-keepclassmembers class * {
    @de.robv.android.xposed.XposedHook *;
}
# Shizuku
-keep class dev.rikka.shizuku.** { *; }
# i18n
-keep class com.nightmare.gltools.R$* { *; }
