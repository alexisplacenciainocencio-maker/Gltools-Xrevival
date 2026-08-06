package com.nightmare.gltools.magisk;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.nightmare.gltools.Profile;
import com.nightmare.gltools.utils.GLLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MagiskExporter {

    private static final String MODULE_ID = "gltools_profile";

    public static boolean export(@NonNull Context ctx) {
        Profile p = ProfileManager.getActive();
        if (p == null) {
            GLLog.w("MagiskExporter", "No hay perfil activo para exportar");
            return false;
        }

        File outDir = new File(Environment.getExternalStorageDirectory(), "Download/GLToolsMagisk");
        if (!outDir.exists() && !outDir.mkdirs()) {
            GLLog.e("MagiskExporter", "No se pudo crear directorio de salida");
            return false;
        }

        File moduleDir = new File(outDir, MODULE_ID);
        moduleDir.mkdirs();

        // module.prop con newlines reales (\n literal en String Java = \n en archivo)
        boolean ok1 = writeFile(new File(moduleDir, "module.prop"),
            "id=" + MODULE_ID + "\n" +
            "name=GLTools Profile\n" +
            "version=2026.1.0\n" +
            "versionCode=202601\n" +
            "author=GLTools2026XRevival\n" +
            "description=Perfil GLTools exportado a Magisk\n" +
            "minMagisk=24000\n"
        );

        // service.sh con newlines reales y setprop correctos
        boolean ok2 = writeFile(new File(moduleDir, "service.sh"),
            "#!/system/bin/sh\n" +
            "# GLTools2026 XRevival — Magisk service script\n" +
            "\n" +
            "# GPU spoofing\n" +
            "resetprop ro.hardware.egl " + p.getEglValue() + "\n" +
            "resetprop ro.hardware.vulkan " + p.getVulkanValue() + "\n" +
            "\n" +
            "# RAM spoofing (parche P1)\n" +
            "resetprop ro.build.version.sdk " + p.getSdkValue() + "\n" +
            "\n" +
            "# Registro\n" +
            "log -t GLToolsMagisk \"Perfil aplicado: " + p.getName() + "\"\n"
        );

        // system.prop
        boolean ok3 = writeFile(new File(moduleDir, "system.prop"),
            "# GLTools2026 system props\n" +
            "ro.product.model=" + p.getDeviceModel() + "\n" +
            "ro.product.brand=" + p.getBrand() + "\n" +
            "ro.product.manufacturer=" + p.getManufacturer() + "\n"
        );

        boolean ok4 = writeFile(new File(moduleDir, "install.sh"),
            "#!/system/bin/sh\n" +
            "ui_print \"GLTools Profile Module v2026.1.0\"\n" +
            "ui_print \"Instalando perfil: " + p.getName() + "\"\n" +
            "exit 0\n"
        );

        GLLog.i("MagiskExporter", "Módulo exportado a: " + moduleDir.getAbsolutePath());
        return ok1 && ok2 && ok3 && ok4;
    }

    private static boolean writeFile(File f, String content) {
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(content);
            return true;
        } catch (IOException e) {
            GLLog.e("MagiskExporter", "Error escribiendo " + f.getName(), e);
            return false;
        }
    }
}
