package com.example.gymlog

import android.app.Application
import com.example.gymlog.data.GymLogDatabase
import com.example.gymlog.data.SettingsRepository
import com.example.gymlog.data.WorkoutRepository
import com.example.gymlog.work.NotificationHelper
import com.example.gymlog.work.ReminderScheduler

class GymLogApplication : Application() {
    val database: GymLogDatabase by lazy { GymLogDatabase.getDatabase(this) }
    val workoutRepository: WorkoutRepository by lazy { WorkoutRepository(database.workoutDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val reminderScheduler: ReminderScheduler by lazy { ReminderScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        //the channel has to exist before the first notification is shown
        NotificationHelper.createChannel(this)
    }
}