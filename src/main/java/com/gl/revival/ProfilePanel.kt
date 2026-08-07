package com.gl.revival

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class ProfilePanel : Activity() {
    private lateinit var listContainer: LinearLayout
    private val pm by lazy { ProfileManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        refreshList()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(iOSColorPalette.systemBackground(this@ProfilePanel))
        }

        root.addView(TextView(this).apply {
            text = "Profile Manager"
            textSize = 22f
            setTextColor(iOSColorPalette.label(this@ProfilePanel))
        })

        root.addView(Button(this).apply {
            text = "+ New Profile"
            setBackgroundColor(iOSColorPalette.systemGreen(this@ProfilePanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val p = pm.createDefault()
                pm.setActive(p.id)
                refreshList()
                Toast.makeText(this@ProfilePanel, "Created ${p.name}", Toast.LENGTH_SHORT).show()
            }
        })

        root.addView(Button(this).apply {
            text = "Import / Export JSON"
            setBackgroundColor(iOSColorPalette.systemIndigo(this@ProfilePanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                startActivity(Intent(this@ProfilePanel, ConfigActivity::class.java))
            }
        })

        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 24, 0, 0)
        }
        root.addView(listContainer)

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun refreshList() {
        listContainer.removeAllViews()
        val activeId = pm.activeProfileId
        pm.listProfiles().forEach { p ->
            listContainer.addView(makeCard(p, p.id == activeId))
        }
    }

    private fun makeCard(p: Profile, active: Boolean): ViewGroup {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(
                if (active) iOSColorPalette.systemBlueLow(this@ProfilePanel)
                else iOSColorPalette.secondarySystemBackground(this@ProfilePanel)
            )
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                setMargins(0, 0, 0, 16)
            }
        }

        card.addView(TextView(this).apply {
            text = "${if (active) "● " else "○ "}${p.name}"
            textSize = 16f
            setTextColor(iOSColorPalette.label(this@ProfilePanel))
        })

        card.addView(TextView(this).apply {
            text = "${p.gpuRenderer} / ${p.ramMb}MB"
            textSize = 12f
            setTextColor(iOSColorPalette.secondaryLabel(this@ProfilePanel))
        })

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        row.addView(Button(this).apply {
            text = "Activate"
            setOnClickListener {
                pm.setActive(p.id)
                refreshList()
                GLLog.i("ProfilePanel", "Activated ${p.id}")
            }
        })

        row.addView(Button(this).apply {
            text = "Delete"
            setOnClickListener {
                pm.deleteProfile(p.id)
                refreshList()
                GLLog.i("ProfilePanel", "Deleted ${p.id}")
            }
        })

        card.addView(row)
        return card
    }
}
