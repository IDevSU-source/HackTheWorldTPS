package com.idevsu.mythos

import android.app.Activity
import android.os.Bundle
import org.json.JSONObject

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("mythos", MODE_PRIVATE) }
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val data = JSONObject(assets.open("corpus_manifest.json").bufferedReader().use { it.readText() })
        MythosUi(this, data, prefs).home()
    }
}
