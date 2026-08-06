package com.nightmare.gltools;

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker;
import com.nightmare.gltools.hooks.*;
import com.nightmare.gltools.utils.GLLog;

public class MainModule extends YukiBaseHooker {

    @Override
    public void onHook() {
        try {
            GLLog.i("GLTools2026", "=== XRevival v2026.1.0 iniciado ===");
            GLLog.i("GLTools2026", "Target: " + getPackageName());

            // Inicializar gestor de perfiles
            ProfileManager.init(getApplicationContext());

            // Hooks por categoría — cada uno con su propio try/catch interno
            new GpuHooks().onHook();
            new RamHooks().onHook();
            new ResolutionHooks().onHook();
            new AntiAliasingHooks().onHook();
            new TextureHooks().onHook();
            new AnisotropicHooks().onHook();

            GLLog.i("GLTools2026", "Todos los hooks registrados correctamente.");

        } catch (Throwable t) {
            // Try/catch global: evita crash del módulo Xposed
            GLLog.e("GLTools2026", "FATAL: Error global en onHook", t);
        }
    }
}
