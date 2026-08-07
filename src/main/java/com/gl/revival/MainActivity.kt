package com.gl.revival

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        val sv = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 48)
            setBackgroundColor(iOSColorPalette.systemBackground(this@MainActivity))
        }

        root.addView(TextView(this).apply {
            text = "GLTools 2026X Revival"
            textSize = 28f
            setTextColor(iOSColorPalette.label(this@MainActivity))
            gravity = Gravity.CENTER
        })

        root.addView(TextView(this).apply {
            text = "GPU / RAM / Resolution spoofing engine"
            textSize = 14f
            setTextColor(iOSColorPalette.secondaryLabel(this@MainActivity))
            gravity = Gravity.CENTER
        })

        root.addView(pad(32))

        val items = arrayOf(
            "GPU Spoofing" to GpuPanel::class.java.name,
            "RAM Override" to RamPanel::class.java.name,
            "Profile Manager" to ProfilePanel::class.java.name,
            "Shizuku Bridge" to ShizukuPanel::class.java.name,
            "Import / Export" to ConfigActivity::class.java.name,
        )

        items.forEach { (label, className) ->
            root.addView(makeButton(label, className))
            root.addView(pad(12))
        }

        root.addView(Button(this).apply {
            text = "Copy Logs to Clipboard"
            setBackgroundColor(iOSColorPalette.systemGray(this@MainActivity))
            setTextColor(iOSColorPalette.label(this@MainActivity))
            setOnClickListener {
                val cb = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cb.setPrimaryClip(android.content.ClipData.newPlainText("GLRevival Logs", GLLog.dump()))
                Toast.makeText(this@MainActivity, "Logs copied", Toast.LENGTH_SHORT).show()
            }
        })

        sv.addView(root)
        setContentView(sv)
    }

    private fun makeButton(text: String, className: String): Button =
        Button(this).apply {
            this.text = text
            setBackgroundColor(iOSColorPalette.systemBlue(this@MainActivity))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                try {
                    startActivity(Intent(this@MainActivity, Class.forName(className)))
                } catch (_: ClassNotFoundException) {
                    Toast.makeText(this@MainActivity, "Activity not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun pad(h: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(-1, h)
    }
}
