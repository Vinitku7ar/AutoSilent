package com.example.autosilent.util


import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager

class AudioManagerHelper(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val prefs: SharedPreferences =
        context.getSharedPreferences("autosilent_prefs", Context.MODE_PRIVATE)

    fun enableSilent() {
        // Save current mode before overwriting it, so we can restore later
        val currentMode = audioManager.ringerMode
        prefs.edit().putInt("previous_ringer_mode", currentMode).apply()

        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    fun restoreNormal() {
        val previousMode = prefs.getInt("previous_ringer_mode", AudioManager.RINGER_MODE_NORMAL)
        audioManager.ringerMode = previousMode
    }
}