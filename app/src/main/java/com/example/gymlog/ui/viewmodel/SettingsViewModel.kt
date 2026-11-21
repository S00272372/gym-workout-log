package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.gymlog.data.SettingsRepository
import com.example.gymlog.data.ThemeMode
import com.example.gymlog.work.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    //the repository already exposes StateFlows so they are passed straight through
    val themeMode: StateFlow<ThemeMode> = repository.themeMode
    val reminderEnabled: StateFlow<Boolean> = repository.reminderEnabled

    //state of the one time test reminder, null while it has never been started
    val testStatus: StateFlow<WorkInfo.State?> = scheduler.observeTestStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onThemeSelected(mode: ThemeMode) {
        repository.setThemeMode(mode)
    }

    //saves the choice and starts or cancels the daily work
    fun onReminderToggled(enabled: Boolean) {
        repository.setReminderEnabled(enabled)
        if (enabled) scheduler.scheduleDaily() else scheduler.cancelDaily()
    }

    fun onSendTestReminder() {
        scheduler.sendTestReminder()
    }
}