package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class TextureHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getTextureQuality() <= 0) return;

            findClass("android.opengl.GLES20").hook {
                injectMember {
                    method {
                        name = "glTexImage2D"
                        param(IntType, IntType, IntType, IntType, IntType, IntType, IntType, IntType, AnyType)
                    }
                    beforeHook {
                        int quality = p.getTextureQuality();
                        if (quality < 100) {
                            int w = args(3).int();
                            int h = args(4).int();
                            float scale = quality / 100f;
                            args(3).set((int)(w * scale));
                            args(4).set((int)(h * scale));
                            GLLog.i("TextureHooks", "Textura escalada: " + w + "x" + h + " -> " + (int)(w*scale) + "x" + (int)(h*scale));
                        }
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("TextureHooks", "Error en hooks de textura", t);
        }
    }
}
