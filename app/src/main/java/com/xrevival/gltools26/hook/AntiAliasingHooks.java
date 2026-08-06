package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class AntiAliasingHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getMsaaLevel() <= 0) return;

            // GLES20.glGetIntegerv(GL_SAMPLES, ...) override
            findClass("android.opengl.GLES20").hook {
                injectMember {
                    method {
                        name = "glGetIntegerv"
                        param(IntType, IntArrayType)
                    }
                    beforeHook {
                        int pname = args(0).int();
                        if (pname == 0x80A9) { // GL_SAMPLES
                            int[] arr = args(1);
                            arr[0] = p.getMsaaLevel();
                            GLLog.i("AntiAliasingHooks", "MSAA spoofed: " + p.getMsaaLevel() + "x");
                        }
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("AntiAliasingHooks", "Error en hooks de AA", t);
        }
    }
}
