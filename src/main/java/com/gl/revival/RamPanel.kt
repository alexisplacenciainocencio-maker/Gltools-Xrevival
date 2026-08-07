package com.gl.revival

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class RamPanel : Activity() {
    private lateinit var etMb: EditText
    private lateinit var sbMb: SeekBar
    private lateinit var tvValue: TextView
    private val pm by lazy { ProfileManager(this) }

    companion object {
        private const val MIN_MB = 512
        private const val MAX_MB = 32768
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        loadCurrent()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(iOSColorPalette.systemBackground(this@RamPanel))
        }

        root.addView(TextView(this).apply {
            text = "RAM Override"
            textSize = 22f
            setTextColor(iOSColorPalette.label(this@RamPanel))
        })

        tvValue = TextView(this).apply {
            textSize = 32f
            setTextColor(iOSColorPalette.label(this@RamPanel))
            gravity = android.view.Gravity.CENTER
        }
        root.addView(tvValue)

        sbMb = SeekBar(this).apply {
            max = MAX_MB - MIN_MB
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                    val v = MIN_MB + p
                    tvValue.text = "$v MB"
                    etMb.setText(v.toString())
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        root.addView(sbMb)

        etMb = EditText(this).apply {
            hint = "Exact value in MB"
            setTextColor(iOSColorPalette.label(this@RamPanel))
            setHintTextColor(iOSColorPalette.secondaryLabel(this@RamPanel))
            setBackgroundColor(iOSColorPalette.secondarySystemBackground(this@RamPanel))
            setPadding(24, 24, 24, 24)
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        root.addView(etMb)

        root.addView(Button(this).apply {
            text = "Apply to active profile"
            setBackgroundColor(iOSColorPalette.systemBlue(this@RamPanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { save() }
        })

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun loadCurrent() {
        val mb = (pm.activeProfile?.takeIf { it.ramMb > 0 }?.ramMb ?: 4096)
            .let { GLUtils.clamp(it, MIN_MB, MAX_MB) }
        sbMb.progress = mb - MIN_MB
        tvValue.text = "$mb MB"
        etMb.setText(mb.toString())
    }

    private fun save() {
        val p = pm.activeProfile ?: run {
            Toast.makeText(this, "No active profile", Toast.LENGTH_SHORT).show()
            return
        }
        val v = etMb.text.toString().trim().toIntOrNull() ?: run {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
            return
        }
        p.ramMb = GLUtils.clamp(v, MIN_MB, MAX_MB)
        pm.saveProfile(p)
        Toast.makeText(this, "RAM set to ${p.ramMb} MB", Toast.LENGTH_SHORT).show()
        GLLog.i("RamPanel", "Updated profile ${p.id} RAM=${p.ramMb}")
    }
}
