package com.nightmare.gltools.shizuku;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dev.rikka.shizuku.Shizuku;
import dev.rikka.shizuku.ShizukuProvider;
import com.nightmare.gltools.utils.GLLog;

public class ShizukuBridge {

    private static final int REQUEST_CODE = 87001;
    private static Boolean cachedAvailable = null;

    /** Verifica si Shizuku está instalado y activo. Null-safe. */
    public static boolean isAvailable(@Nullable Context ctx) {
        if (cachedAvailable != null) return cachedAvailable;
        if (ctx == null) {
            GLLog.w("ShizukuBridge", "Contexto nulo en isAvailable — retornando false");
            return false;
        }
        try {
            boolean avail = Shizuku.pingBinder() && Shizuku.getVersion() >= 10;
            cachedAvailable = avail;
            return avail;
        } catch (Exception e) {
            GLLog.e("ShizukuBridge", "Error verificando disponibilidad", e);
            return false;
        }
    }

    /** Solicita permiso Shizuku con listener null-safe. */
    public static void requestPermission(@NonNull Context ctx, @Nullable Shizuku.OnRequestPermissionResultListener listener) {
        if (!isAvailable(ctx)) {
            GLLog.w("ShizukuBridge", "Shizuku no disponible, omitiendo solicitud");
            return;
        }
        try {
            if (Shizuku.isPreV11() || Shizuku.shouldShowRequestPermissionRationale()) {
                GLLog.i("ShizukuBridge", "Permiso ya concedido o no requerido");
                if (listener != null) listener.onRequestPermissionResult(REQUEST_CODE, true);
            } else {
                Shizuku.addRequestPermissionResultListener(listener);
                Shizuku.requestPermission(REQUEST_CODE);
            }
        } catch (Exception e) {
            GLLog.e("ShizukuBridge", "Error solicitando permiso", e);
            if (listener != null) listener.onRequestPermissionResult(REQUEST_CODE, false);
        }
    }

    /** Ejecuta comando vía Shizuku con validación previa. */
    public static String exec(@Nullable Context ctx, String cmd) {
        if (ctx == null || !isAvailable(ctx)) {
            GLLog.e("ShizukuBridge", "No se puede ejecutar: ctx nulo o Shizuku no disponible");
            return "";
        }
        try {
            java.lang.Process process = Shizuku.newProcess(new String[]{"sh", "-c", cmd}, null, null);
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) out.append(line).append("\n");
            process.waitFor();
            return out.toString().trim();
        } catch (Exception e) {
            GLLog.e("ShizukuBridge", "Exec falló: " + cmd, e);
            return "";
        }
    }

    public static void clearCache() {
        cachedAvailable = null;
    }
}
