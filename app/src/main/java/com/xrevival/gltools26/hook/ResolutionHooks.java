package com.nightmare.gltools.hooks;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.ProfileManager;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;

public class ResolutionHooks extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            Profile p = ProfileManager.getActive();
            if (p == null || p.getResolutionPercent() <= 0) return;

            // WindowManager.getDefaultDisplay().getRealMetrics()
            findClass("android.view.Display").hook {
                injectMember {
                    method { name = "getRealMetrics" }
                    afterHook {
                        android.util.DisplayMetrics dm = result();
                        float pct = p.getResolutionPercent() / 100f;
                        dm.widthPixels = (int)(dm.widthPixels * pct);
                        dm.heightPixels = (int)(dm.heightPixels * pct);
                        dm.density = dm.density * pct;
                        dm.densityDpi = (int)(dm.densityDpi * pct);
                        result = dm;
                        GLLog.i("ResolutionHooks", "Resolución escalada al " + p.getResolutionPercent() + "%");
                    }
                }
            }

        } catch (Throwable t) {
            GLLog.e("ResolutionHooks", "Error en hooks de resolución", t);
        }
    }
}
