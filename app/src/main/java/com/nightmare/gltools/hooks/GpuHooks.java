package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class GpuHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getSpoofMode() == null) return;

            // EGL spoofing: android.opengl.GLES20.glGetString(GL_RENDERER)
            findClass("android.opengl.GLES20").hook {
                injectMember {
                    method {
                        name = "glGetString"
                        param(IntType)
                    }
                    afterHook {
                        int name = args(0).int();
                        if (name == 0x1F01) { // GL_RENDERER
                            String spoof = p.getGpuRenderer();
                            if (spoof != null && !spoof.isEmpty()) {
                                result = spoof;
                                GLLog.i("GpuHooks", "GL_RENDERER spoofed: " + spoof);
                            }
                        } else if (name == 0x1F02) { // GL_VENDOR
                            String vendor = p.getGpuVendor();
                            if (vendor != null && !vendor.isEmpty()) {
                                result = vendor;
                            }
                        }
                    }
                }
            }

            // Vulkan spoofing vía Build props nativas
            findClass("android.os.Build").hook {
                injectMember {
                    field { name = "BOARD" }
                    replaceAny {
                        return@replaceAny p.getBoard();
                    }
                }
                injectMember {
                    field { name = "HARDWARE" }
                    replaceAny {
                        return@replaceAny p.getHardware();
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("GpuHooks", "Error en hooks de GPU", t);
        }
    }
}
