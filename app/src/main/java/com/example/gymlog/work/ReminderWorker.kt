package com.example.gymlog.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gymlog.GymLogApplication

//runs in the background, counts the workouts of the last 7 days in room and shows a notification
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        //the application context is the GymLogApplication so the same repository is used as in the screens
        val app = applicationContext as GymLogApplication
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val count = app.workoutRepository.countWorkoutsSince(sevenDaysAgo)
        NotificationHelper.showReminder(applicationContext, count)
        return Result.success()
    }
}