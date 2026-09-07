package com.idevsu.tps

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
            setBackgroundColor(Color.BLACK)
        }

        val title = TextView(this).apply {
            text = "TRILLIONS PER SECOND ⚡️"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }

        val subtitle = TextView(this).apply {
            text = "TPS HACK THE WORLD\n\nSYSTEM BOOT SEQUENCE...\nKERNEL_VERSION: TPS_v1.0\nROOT_ACCESS_GRANTED.\n\nA portable reader for the TPS protocol, diagnostics, chapters, and system logs."
            textSize = 16f
            setTextColor(Color.rgb(180, 255, 190))
            setLineSpacing(0f, 1.15f)
        }

        val scroll = ScrollView(this).apply {
            addView(subtitle)
        }

        root.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        setContentView(root)
    }
}
