package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class AnisotropicHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getAnisoLevel() <= 0) return;

            // GLES20.glGetIntegerv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, ...)
            findClass("android.opengl.GLES20").hook {
                injectMember {
                    method {
                        name = "glGetIntegerv"
                        param(IntType, IntArrayType)
                    }
                    beforeHook {
                        int pname = args(0).int();
                        // GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FF
                        if (pname == 0x84FF) {
                            int[] arr = args(1);
                            arr[0] = p.getAnisoLevel();
                            GLLog.i("AnisotropicHooks", "Anisotropía spoofed: " + p.getAnisoLevel() + "x");
                        }
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("AnisotropicHooks", "Error en hooks anisotrópicos", t);
        }
    }
}
