package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.data.SettingsRepository
import com.example.gymlog.data.ThemeMode
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    //the repository already exposes a StateFlow so it is passed straight through
    val themeMode: StateFlow<ThemeMode> = repository.themeMode

    fun onThemeSelected(mode: ThemeMode) {
        repository.setThemeMode(mode)
    }
}