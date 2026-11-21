package com.example.gymlog.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//the three choices on the settings screen
enum class ThemeMode { System, Light, Dark }

//stores the chosen theme on the device so it is still there after the app is closed
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("gymlog_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    //falls back to following the system if nothing is stored yet
    private fun readThemeMode(): ThemeMode {
        val stored = prefs.getString(KEY_THEME_MODE, null)
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.System
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}