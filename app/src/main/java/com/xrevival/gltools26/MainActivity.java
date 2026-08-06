package com.nightmare.gltools;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nightmare.gltools.ui.panels.ShizukuPanel;
import com.nightmare.gltools.ui.panels.ProfilePanel;
import com.nightmare.gltools.ui.panels.GpuPanel;
import com.nightmare.gltools.ui.panels.RamPanel;
import com.nightmare.gltools.ui.iOSColorPalette;
import com.nightmare.gltools.magisk.MagiskExporter;

public class MainActivity extends AppCompatActivity {

    private LinearLayout flowRow;  // corregido: era null en onCreate anterior

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar tema iOS dark semantic antes de setContentView
        iOSColorPalette.apply(this);

        ScrollView scroll = new ScrollView(this);
        flowRow = new LinearLayout(this);
        flowRow.setOrientation(LinearLayout.VERTICAL);
        flowRow.setPadding(32, 32, 32, 32);
        scroll.addView(flowRow);
        setContentView(scroll);

        // Header
        TextView header = new TextView(this);
        header.setText("GLTools 2026 — XRevival");
        header.setTextSize(24f);
        header.setTextColor(iOSColorPalette.label());
        flowRow.addView(header);

        // Panels
        flowRow.addView(new GpuPanel(this));
        flowRow.addView(new RamPanel(this));
        flowRow.addView(new ProfilePanel(this));
        flowRow.addView(new ShizukuPanel(this));

        // Footer export
        TextView exportBtn = new TextView(this);
        exportBtn.setText("📦 Exportar perfil a Magisk");
        exportBtn.setTextColor(iOSColorPalette.tint());
        exportBtn.setPadding(0, 24, 0, 0);
        exportBtn.setOnClickListener(v -> MagiskExporter.export(this));
        flowRow.addView(exportBtn);
    }

    public LinearLayout getFlowRow() {
        return flowRow;
    }
}
