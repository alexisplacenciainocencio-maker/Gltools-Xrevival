package com.gl.revival;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;

public class ProfilePanel extends Activity {
    private LinearLayout listContainer;
    private ProfileManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = new ProfileManager(this);
        buildUi();
        refreshList();
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(iOSColorPalette.systemBackground(this));

        TextView title = new TextView(this);
        title.setText("Profile Manager");
        title.setTextSize(22f);
        title.setTextColor(iOSColorPalette.label(this));
        root.addView(title);

        Button btnNew = new Button(this);
        btnNew.setText("+ New Profile");
        btnNew.setBackgroundColor(iOSColorPalette.systemGreen(this));
        btnNew.setTextColor(0xFFFFFFFF);
        btnNew.setOnClickListener(v -> {
            Profile p = pm.createDefault();
            pm.setActive(p.id);
            refreshList();
            Toast.makeText(this, "Created " + p.name, Toast.LENGTH_SHORT).show();
        });
        root.addView(btnNew);

        Button btnImport = new Button(this);
        btnImport.setText("Import / Export JSON");
        btnImport.setBackgroundColor(iOSColorPalette.systemIndigo(this));
        btnImport.setTextColor(0xFFFFFFFF);
        btnImport.setOnClickListener(v -> {
            startActivity(new Intent(this, ConfigActivity.class));
        });
        root.addView(btnImport);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(0, 24, 0, 0);
        root.addView(listContainer);

        sv.addView(root);
        setContentView(sv);
    }

    private void refreshList() {
        listContainer.removeAllViews();
        List<Profile> list = pm.listProfiles();
        String activeId = pm.getActiveProfileId();
        for (Profile p : list) {
            listContainer.addView(makeCard(p, p.id.equals(activeId)));
        }
    }

    private ViewGroup makeCard(Profile p, boolean active) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(active
            ? iOSColorPalette.systemBlueLow(this)
            : iOSColorPalette.secondarySystemBackground(this));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 16);
        card.setLayoutParams(lp);

        TextView name = new TextView(this);
        name.setText((active ? "● " : "○ ") + p.name);
        name.setTextSize(16f);
        name.setTextColor(iOSColorPalette.label(this));
        card.addView(name);

        TextView meta = new TextView(this);
        meta.setText(p.gpuRenderer + " / " + p.ramMb + "MB");
        meta.setTextSize(12f);
        meta.setTextColor(iOSColorPalette.secondaryLabel(this));
        card.addView(meta);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        Button btnUse = new Button(this);
        btnUse.setText("Activate");
        btnUse.setOnClickListener(v -> {
            pm.setActive(p.id);
            refreshList();
            GLLog.i("ProfilePanel", "Activated " + p.id);
        });
        row.addView(btnUse);

        Button btnDel = new Button(this);
        btnDel.setText("Delete");
        btnDel.setOnClickListener(v -> {
            pm.deleteProfile(p.id);
            refreshList();
            GLLog.i("ProfilePanel", "Deleted " + p.id);
        });
        row.addView(btnDel);

        card.addView(row);
        return card;
    }
}
