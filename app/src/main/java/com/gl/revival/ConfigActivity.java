package com.nightmare.gltools;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.nightmare.gltools.spoof.SpoofMode;
import com.nightmare.gltools.ui.iOSColorPalette;

public class ConfigActivity extends AppCompatActivity {

    private EditText nameField, ramField, resField, msaaField, texField, anisoField;
    private Spinner spoofSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iOSColorPalette.apply(this);

        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        scroll.addView(layout);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("⚙️ Editor de Perfil");
        title.setTextSize(22f);
        title.setTextColor(iOSColorPalette.label());
        layout.addView(title);

        nameField = addField(layout, "Nombre del perfil");
        ramField = addField(layout, "RAM (MB) — 0 = sin cambio");
        resField = addField(layout, "Resolución % (50-100)");
        msaaField = addField(layout, "MSAA (0,2,4,8,16)");
        texField = addField(layout, "Calidad textura % (1-100)");
        anisoField = addField(layout, "Anisotropía (0,2,4,8,16)");

        // Spoof mode spinner
        TextView spoofLabel = new TextView(this);
        spoofLabel.setText("Modo de spoofing GPU");
        spoofLabel.setTextColor(iOSColorPalette.secondaryLabel());
        spoofLabel.setPadding(0, 16, 0, 4);
        layout.addView(spoofLabel);

        spoofSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        for (SpoofMode m : SpoofMode.values()) adapter.add(m.getDisplayName());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spoofSpinner.setAdapter(adapter);
        layout.addView(spoofSpinner);

        Button saveBtn = new Button(this);
        saveBtn.setText("💾 Guardar perfil");
        saveBtn.setOnClickListener(v -> saveProfile());
        layout.addView(saveBtn);

        loadDefaults();
    }

    private EditText addField(LinearLayout parent, String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setTextColor(iOSColorPalette.label());
        et.setHintTextColor(iOSColorPalette.tertiaryLabel());
        et.setPadding(0, 16, 0, 16);
        parent.addView(et);
        return et;
    }

    private void loadDefaults() {
        Profile p = ProfileManager.getActive();
        if (p == null) p = Profile.getDefault();
        nameField.setText(p.getName());
        ramField.setText(p.getRamMb() > 0 ? String.valueOf(p.getRamMb()) : "");
        resField.setText(p.getResolutionPercent() != 100 ? String.valueOf(p.getResolutionPercent()) : "");
        msaaField.setText(p.getMsaaLevel() > 0 ? String.valueOf(p.getMsaaLevel()) : "");
        texField.setText(p.getTextureQuality() != 100 ? String.valueOf(p.getTextureQuality()) : "");
        anisoField.setText(p.getAnisoLevel() > 0 ? String.valueOf(p.getAnisoLevel()) : "");
        if (p.getSpoofMode() != null) spoofSpinner.setSelection(p.getSpoofMode().ordinal());
    }

    private void saveProfile() {
        Profile p = new Profile();
        p.setName(nameField.getText().toString().trim());
        try { p.setRamMb(Integer.parseInt(ramField.getText().toString())); } catch (Exception e) {}
        try { p.setResolutionPercent(Integer.parseInt(resField.getText().toString())); } catch (Exception e) {}
        try { p.setMsaaLevel(Integer.parseInt(msaaField.getText().toString())); } catch (Exception e) {}
        try { p.setTextureQuality(Integer.parseInt(texField.getText().toString())); } catch (Exception e) {}
        try { p.setAnisoLevel(Integer.parseInt(anisoField.getText().toString())); } catch (Exception e) {}
        p.setSpoofMode(SpoofMode.values()[spoofSpinner.getSelectedItemPosition()]);

        ProfileManager.saveProfile(p);
        ProfileManager.setActive(p);
        finish();
    }
}
