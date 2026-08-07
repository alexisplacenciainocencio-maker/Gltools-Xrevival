package com.gl.revival

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class GpuPanel : Activity() {
    private lateinit var etRenderer: EditText
    private lateinit var etVendor: EditText
    private lateinit var etVersion: EditText
    private lateinit var spDriver: Spinner
    private val pm by lazy { ProfileManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        loadCurrent()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(iOSColorPalette.systemBackground(this@GpuPanel))
        }

        root.addView(TextView(this).apply {
            text = "GPU Spoofing"
            textSize = 22f
            setTextColor(iOSColorPalette.label(this@GpuPanel))
        })

        etRenderer = makeField("Renderer override (e.g. Adreno 650)")
        etVendor = makeField("Vendor override (e.g. Qualcomm)")
        etVersion = makeField("Version override (e.g. OpenGL ES 3.2)")

        root.addView(label("Renderer"))
        root.addView(etRenderer)
        root.addView(pad(24))

        root.addView(label("Vendor"))
        root.addView(etVendor)
        root.addView(pad(24))

        root.addView(label("Version string"))
        root.addView(etVersion)
        root.addView(pad(24))

        root.addView(label("Driver behavior"))
        spDriver = Spinner(this).apply {
            adapter = ArrayAdapter(this@GpuPanel, android.R.layout.simple_spinner_item,
                arrayOf("Pass-through", "Spoof EGL", "Spoof Vulkan", "Spoof both")).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
        root.addView(spDriver)
        root.addView(pad(24))

        root.addView(Button(this).apply {
            text = "Apply to active profile"
            setBackgroundColor(iOSColorPalette.systemBlue(this@GpuPanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { save() }
        })

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun makeField(hint: String): EditText = EditText(this).apply {
        this.hint = hint
        setTextColor(iOSColorPalette.label(this@GpuPanel))
        setHintTextColor(iOSColorPalette.secondaryLabel(this@GpuPanel))
        setBackgroundColor(iOSColorPalette.secondarySystemBackground(this@GpuPanel))
        setPadding(24, 24, 24, 24)
    }

    private fun label(text: String): TextView = TextView(this).apply {
        this.text = text
        textSize = 14f
        setTextColor(iOSColorPalette.secondaryLabel(this@GpuPanel))
    }

    private fun pad(h: Int): LinearLayout = LinearLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(-1, h)
    }

    private fun loadCurrent() {
        val p = pm.activeProfile ?: return
        etRenderer.setText(p.gpuRenderer)
        etVendor.setText(p.gpuVendor)
        etVersion.setText(p.gpuVersion)
        spDriver.setSelection(p.gpuDriverMode)
    }

    private fun save() {
        val p = pm.activeProfile ?: run {
            Toast.makeText(this, "No active profile", Toast.LENGTH_SHORT).show()
            return
        }
        p.gpuRenderer = etRenderer.text.toString().trim()
        p.gpuVendor = etVendor.text.toString().trim()
        p.gpuVersion = etVersion.text.toString().trim()
        p.gpuDriverMode = spDriver.selectedItemPosition
        pm.saveProfile(p)
        Toast.makeText(this, "GPU config saved", Toast.LENGTH_SHORT).show()
        GLLog.i("GpuPanel", "Updated profile ${p.id}")
    }
}
