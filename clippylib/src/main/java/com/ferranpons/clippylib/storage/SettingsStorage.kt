package com.ferranpons.clippylib.storage

import android.content.Context
import android.content.SharedPreferences

import com.ferranpons.clippylib.AnimationPause

class SettingsStorage(context: Context) {

    private val sharedPreferences: SharedPreferences

    val isSettingsStartOnBoot: Boolean
        get() = sharedPreferences.getBoolean(SETTINGS_START_ON_BOOT, SETTINGS_START_ON_BOOT_DEFAULT)

    var animationPause: AnimationPause
        get() = AnimationPause.getAnimationPauseFromSettings(sharedPreferences.getString(SETTINGS_ANIMATION_PAUSE, SETTINGS_ANIMATION_PAUSE_DEFAULT)!!)
        set(animationPause) = sharedPreferences.edit()
                .putString(SETTINGS_ANIMATION_PAUSE, animationPause.settingsValue)
                .apply()

    init {
        this.sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun setStartOnBoot(activate: Boolean) {
        sharedPreferences.edit()
                .putBoolean(SETTINGS_START_ON_BOOT, activate)
                .apply()
    }

    companion object {

        const val NAME = "settings_storage"

        private const val SETTINGS_START_ON_BOOT = "settings_start_boot"
        private const val SETTINGS_START_ON_BOOT_DEFAULT = false

        const val SETTINGS_ANIMATION_PAUSE = "settings_animation_pause"
        val SETTINGS_ANIMATION_PAUSE_DEFAULT = AnimationPause.Normal.settingsValue
    }


}
