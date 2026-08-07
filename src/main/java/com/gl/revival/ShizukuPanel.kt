package com.gl.revival

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class ShizukuPanel : Activity() {
    private lateinit var tvStatus: TextView
    private lateinit var btnBind: Button
    private lateinit var btnReq: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        refreshStatus()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(iOSColorPalette.systemBackground(this@ShizukuPanel))
        }

        root.addView(TextView(this).apply {
            text = "Shizuku Bridge"
            textSize = 22f
            setTextColor(iOSColorPalette.label(this@ShizukuPanel))
        })

        tvStatus = TextView(this).apply {
            textSize = 16f
            setPadding(0, 16, 0, 16)
        }
        root.addView(tvStatus)

        btnReq = Button(this).apply {
            text = "Request Permission"
            setBackgroundColor(iOSColorPalette.systemOrange(this@ShizukuPanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                ShizukuBridge.requestPermission()
                refreshStatus()
            }
        }
        root.addView(btnReq)

        btnBind = Button(this).apply {
            text = "Bind Service"
            setBackgroundColor(iOSColorPalette.systemBlue(this@ShizukuPanel))
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                ShizukuBridge.bind(this@ShizukuPanel)
                refreshStatus()
            }
        }
        root.addView(btnBind)

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun refreshStatus() {
        val ready = ShizukuBridge.isReady()
        val perm = ShizukuBridge.hasPermission()
        val bound = ShizukuBridge.isBound()
        tvStatus.text = "Ready: $ready
Permission: $perm
Bound: $bound"
        tvStatus.setTextColor(
            if (ready && perm) iOSColorPalette.systemGreen(this)
            else iOSColorPalette.systemRed(this)
        )
    }
}
