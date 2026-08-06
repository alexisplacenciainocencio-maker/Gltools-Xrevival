package com.nightmare.gltools.ui.panels;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.nightmare.gltools.shizuku.ShizukuBridge;
import com.nightmare.gltools.ui.iOSColorPalette;
import com.nightmare.gltools.utils.GLLog;

public class ShizukuPanel extends LinearLayout {

    private final TextView statusLabel;

    public ShizukuPanel(@NonNull Context ctx) {
        super(ctx);
        // Validación defensiva: nunca operar con contexto nulo
        if (ctx == null) {
            throw new IllegalArgumentException("ShizukuPanel requiere contexto no nulo");
        }

        setOrientation(VERTICAL);
        setPadding(0, 24, 0, 0);

        TextView title = new TextView(ctx);
        title.setText("🔐 Shizuku Bridge");
        title.setTextSize(18f);
        title.setTextColor(iOSColorPalette.label());
        addView(title);

        statusLabel = new TextView(ctx);
        statusLabel.setTextColor(iOSColorPalette.secondaryLabel());
        statusLabel.setPadding(0, 8, 0, 8);
        addView(statusLabel);

        Button checkBtn = new Button(ctx);
        checkBtn.setText("Verificar estado");
        checkBtn.setOnClickListener(v -> refreshStatus(ctx));
        addView(checkBtn);

        Button reqBtn = new Button(ctx);
        reqBtn.setText("Solicitar permiso");
        reqBtn.setOnClickListener(v -> {
            if (ctx != null) {
                ShizukuBridge.requestPermission(ctx, (requestCode, isGranted) -> {
                    refreshStatus(ctx);
                });
            }
        });
        addView(reqBtn);

        refreshStatus(ctx);
    }

    private void refreshStatus(@NonNull Context ctx) {
        boolean avail = ShizukuBridge.isAvailable(ctx);
        statusLabel.setText(avail ? "✅ Shizuku activo" : "❌ Shizuku no disponible");
        statusLabel.setTextColor(avail ? iOSColorPalette.systemGreen() : iOSColorPalette.systemRed());
    }
}
