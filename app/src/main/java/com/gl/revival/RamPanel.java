package com.gl.revival;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.Toast;

public class RamPanel extends Activity {
    private EditText etMb;
    private SeekBar sbMb;
    private TextView tvValue;
    private ProfileManager pm;
    private static final int MIN_MB = 512;
    private static final int MAX_MB = 32768;

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

        TextView title = new TextView(this);
        title.setText("RAM Override");
        title.setTextSize(22f);
        title.setTextColor(iOSColorPalette.label(this));
        root.addView(title);

        tvValue = new TextView(this);
        tvValue.setTextSize(32f);
        tvValue.setTextColor(iOSColorPalette.label(this));
        tvValue.setGravity(android.view.Gravity.CENTER);
        root.addView(tvValue);

        sbMb = new SeekBar(this);
        sbMb.setMax(MAX_MB - MIN_MB);
        sbMb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                int val = MIN_MB + p;
                tvValue.setText(val + " MB");
                etMb.setText(String.valueOf(val));
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        root.addView(sbMb);

        etMb = new EditText(this);
        etMb.setHint("Exact value in MB");
        etMb.setTextColor(iOSColorPalette.label(this));
        etMb.setHintTextColor(iOSColorPalette.secondaryLabel(this));
        etMb.setBackgroundColor(iOSColorPalette.secondarySystemBackground(this));
        etMb.setPadding(24, 24, 24, 24);
        etMb.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        root.addView(etMb);

        Button btn = new Button(this);
        btn.setText("Apply to active profile");
        btn.setBackgroundColor(iOSColorPalette.systemBlue(this));
        btn.setTextColor(0xFFFFFFFF);
        btn.setOnClickListener(v -> save());
        root.addView(btn);

        sv.addView(root);
        setContentView(sv);
    }

    private void loadCurrent() {
        Profile p = pm.getActiveProfile();
        int mb = (p != null && p.ramMb > 0) ? p.ramMb : 4096;
        mb = GLUtils.clamp(mb, MIN_MB, MAX_MB);
        sbMb.setProgress(mb - MIN_MB);
        tvValue.setText(mb + " MB");
        etMb.setText(String.valueOf(mb));
    }

    private void save() {
        Profile p = pm.getActiveProfile();
        if (p == null) {
            Toast.makeText(this, "No active profile", Toast.LENGTH_SHORT).show();
            return;
        }
        int val;
        try {
            val = Integer.parseInt(etMb.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }
        p.ramMb = GLUtils.clamp(val, MIN_MB, MAX_MB);
        pm.saveProfile(p);
        Toast.makeText(this, "RAM set to " + p.ramMb + " MB", Toast.LENGTH_SHORT).show();
        GLLog.i("RamPanel", "Updated profile " + p.id + " RAM=" + p.ramMb);
    }
}
