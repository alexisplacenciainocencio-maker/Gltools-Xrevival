package com.gl.revival

import android.app.Activity
import android.content.ClipboardManager
import android.content.ClipData
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * Activity de configuración: import/export JSON + toggles individuales
 * para cada grupo de hooks.
 */
class ConfigActivity : Activity() {
    private lateinit var etJson: EditText
    private val pm by lazy { ProfileManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        loadExport()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(iOSColorPalette.systemBackground(this@ConfigActivity))
        }

        root.addView(TextView(this).apply {
            text = "Import / Export"
            textSize = 22f
            setTextColor(iOSColorPalette.label(this@ConfigActivity))
        })

        etJson = EditText(this).apply {
            minLines = 8
            setTextColor(iOSColorPalette.label(this@ConfigActivity))
            setBackgroundColor(iOSColorPalette.secondarySystemBackground(this@ConfigActivity))
            setPadding(16, 16, 16, 16)
        }
        root.addView(etJson)

        root.addView(Button(this).apply {
            text = "Copy to Clipboard"
            setBackgroundColor(iOSColorPalette.systemIndigo(this@ConfigActivity))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val cb = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                cb.setPrimaryClip(ClipData.newPlainText("GLRevival Config", etJson.text.toString()))
                Toast.makeText(this@ConfigActivity, "Copied", Toast.LENGTH_SHORT).show()
            }
        })

        root.addView(Button(this).apply {
            text = "Import from Text"
            setBackgroundColor(iOSColorPalette.systemGreen(this@ConfigActivity))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                try {
                    val arr = JSONArray(etJson.text.toString())
                    (0 until arr.length()).forEach { i ->
                        pm.saveProfile(Profile.fromJson(arr.getJSONObject(i)))
                    }
                    Toast.makeText(this@ConfigActivity, "Imported ${arr.length()} profiles", Toast.LENGTH_SHORT).show()
                    GLLog.i("ConfigActivity", "Imported ${arr.length()} profiles")
                } catch (e: Exception) {
                    Toast.makeText(this@ConfigActivity, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        })

        root.addView(Button(this).apply {
            text = "Save to Downloads"
            setBackgroundColor(iOSColorPalette.systemBlue(this@ConfigActivity))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { saveToFile() }
        })

        root.addView(Button(this).apply {
            text = "Load from Downloads"
            setBackgroundColor(iOSColorPalette.systemOrange(this@ConfigActivity))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { loadFromFile() }
        })

        // Separador
        root.addView(TextView(this).apply {
            text = ""
            layoutParams = LinearLayout.LayoutParams(-1, 32)
        })

        // Sección de toggles de hooks
        root.addView(TextView(this).apply {
            text = "Hook Toggles"
            textSize = 18f
            setTextColor(iOSColorPalette.label(this@ConfigActivity))
        })

        root.addView(makeToggle("Premium Hook", Config.KEY_HOOK_PREMIUM))
        root.addView(makeToggle("Network Hook", Config.KEY_HOOK_NETWORK))
        root.addView(makeToggle("WebView Hook", Config.KEY_HOOK_WEBVIEW))
        root.addView(makeToggle("GPU Hook", Config.KEY_HOOK_GPU))
        root.addView(makeToggle("RAM Hook", Config.KEY_HOOK_RAM))
        root.addView(makeToggle("Resolution Hook", Config.KEY_HOOK_RESOLUTION))
        root.addView(makeToggle("Anti-Aliasing Hook", Config.KEY_HOOK_AA))
        root.addView(makeToggle("Texture Hook", Config.KEY_HOOK_TEXTURE))
        root.addView(makeToggle("Anisotropic Hook", Config.KEY_HOOK_ANISO))

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun makeToggle(label: String, key: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 12, 0, 12)
        }
        val tv = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(iOSColorPalette.label(this@ConfigActivity))
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }
        val sw = Switch(this).apply {
            isChecked = Config.isHookEnabled(this@ConfigActivity, key, true)
            setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                Config.setHookEnabled(this@ConfigActivity, key, checked)
                GLLog.i("ConfigActivity", "$key = $checked")
            }
        }
        row.addView(tv)
        row.addView(sw)
        return row
    }

    private fun loadExport() {
        val arr = JSONArray().apply { pm.listProfiles().forEach { put(it.toJson()) } }
        etJson.setText(arr.toString(2))
    }

    private fun saveToFile() {
        try {
            val f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "glrevival_export.json")
            FileOutputStream(f).use { fos ->
                OutputStreamWriter(fos).use { it.write(etJson.text.toString()) }
            }
            Toast.makeText(this, "Saved to ${f.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFromFile() {
        try {
            val f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "glrevival_export.json")
            FileInputStream(f).use { fis ->
                BufferedReader(InputStreamReader(fis)).use { reader ->
                    etJson.setText(reader.readText())
                }
            }
            Toast.makeText(this, "Loaded ${f.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Load failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
