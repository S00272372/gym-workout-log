package com.example.gymlog

import android.app.Application
import com.example.gymlog.data.GymLogDatabase
import com.example.gymlog.data.WorkoutRepository

class GymLogApplication : Application() {
    val database: GymLogDatabase by lazy { GymLogDatabase.getDatabase(this) }
    val workoutRepository: WorkoutRepository by lazy { WorkoutRepository(database.workoutDao()) }
}