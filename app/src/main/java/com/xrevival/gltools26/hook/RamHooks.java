package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.highcapable.yukihookapi.hook.type.java.LongType;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class RamHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getRamMb() <= 0) return;

            // P1: Spoof total RAM vía ActivityManager.getMemoryInfo()
            findClass("android.app.ActivityManager").hook {
                injectMember {
                    method {
                        name = "getMemoryInfo"
                        param("android.app.ActivityManager\$MemoryInfo")
                    }
                    afterHook {
                        long spoofedBytes = (long) p.getRamMb() * 1024L * 1024L;
                        field {
                            name = "totalMem"
                            type = LongType
                        }.set(instance, spoofedBytes);
                        GLLog.i("RamHooks", "RAM spoofed: " + p.getRamMb() + " MB");
                    }
                }
            }

            // P5: registerRuntime hook para apps que leen memoria nativamente
            findClass("dalvik.system.VMRuntime").hook {
                injectMember {
                    method { name = "registerNativeAllocation" }
                    beforeHook {
                        // No-op: evita crash en apps con alloc tracking agresivo
                    }
                }
                injectMember {
                    method { name = "registerNativeFree" }
                    beforeHook {
                        // No-op
                    }
                }
            }

            // Runtime.totalMemory() / maxMemory()
            findClass("java.lang.Runtime").hook {
                injectMember {
                    method { name = "maxMemory" }
                    replaceAny {
                        long spoofed = (long) p.getRamMb() * 1024L * 1024L;
                        GLLog.d("RamHooks", "maxMemory() spoofed: " + spoofed);
                        return@replaceAny spoofed;
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("RamHooks", "Error en hooks de RAM", t);
        }
    }
}
