package com.gl.revival;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class GpuPanel extends Activity {
    private EditText etRenderer, etVendor, etVersion;
    private Spinner spDriver;
    private ProfileManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = new ProfileManager(this);
        buildUi();
        loadCurrent();
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(iOSColorPalette.systemBackground(this));
        int gap = 24;

        TextView title = new TextView(this);
        title.setText("GPU Spoofing");
        title.setTextSize(22f);
        title.setTextColor(iOSColorPalette.label(this));
        root.addView(title);

        etRenderer = makeField("Renderer override (e.g. Adreno 650)");
        etVendor   = makeField("Vendor override (e.g. Qualcomm)");
        etVersion  = makeField("Version override (e.g. OpenGL ES 3.2)");

        root.addView(label("Renderer"));
        root.addView(etRenderer);
        root.addView(pad(gap));

        root.addView(label("Vendor"));
        root.addView(etVendor);
        root.addView(pad(gap));

        root.addView(label("Version string"));
        root.addView(etVersion);
        root.addView(pad(gap));

        root.addView(label("Driver behavior"));
        spDriver = new Spinner(this);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"Pass-through", "Spoof EGL", "Spoof Vulkan", "Spoof both"});
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDriver.setAdapter(ad);
        root.addView(spDriver);
        root.addView(pad(gap));

        Button btn = new Button(this);
        btn.setText("Apply to active profile");
        btn.setBackgroundColor(iOSColorPalette.systemBlue(this));
        btn.setTextColor(0xFFFFFFFF);
        btn.setOnClickListener(v -> save());
        root.addView(btn);

        sv.addView(root);
        setContentView(sv);
    }

    private EditText makeField(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(iOSColorPalette.label(this));
        e.setHintTextColor(iOSColorPalette.secondaryLabel(this));
        e.setBackgroundColor(iOSColorPalette.secondarySystemBackground(this));
        e.setPadding(24, 24, 24, 24);
        return e;
    }

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(14f);
        t.setTextColor(iOSColorPalette.secondaryLabel(this));
        return t;
    }

    private ViewGroup pad(int h) {
        LinearLayout p = new LinearLayout(this);
        p.setLayoutParams(new LinearLayout.LayoutParams(-1, h));
        return p;
    }

    private void loadCurrent() {
        Profile p = pm.getActiveProfile();
        if (p == null) return;
        etRenderer.setText(p.gpuRenderer);
        etVendor.setText(p.gpuVendor);
        etVersion.setText(p.gpuVersion);
        spDriver.setSelection(p.gpuDriverMode);
    }

    private void save() {
        Profile p = pm.getActiveProfile();
        if (p == null) {
            Toast.makeText(this, "No active profile", Toast.LENGTH_SHORT).show();
            return;
        }
        p.gpuRenderer = etRenderer.getText().toString().trim();
        p.gpuVendor   = etVendor.getText().toString().trim();
        p.gpuVersion  = etVersion.getText().toString().trim();
        p.gpuDriverMode = spDriver.getSelectedItemPosition();
        pm.saveProfile(p);
        Toast.makeText(this, "GPU config saved", Toast.LENGTH_SHORT).show();
        GLLog.i("GpuPanel", "Updated profile " + p.id);
    }
}
