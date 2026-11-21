package com.example.gymlog.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//schedules and cancels the reminder work
class ReminderScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    //repeats every 24 hours and the first run is at 18:00
    //unique work means turning the switch on twice never creates two copies
    fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntilEvening(), TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .addTag(DAILY_WORK_NAME)
            .build()
        workManager.enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelDaily() {
        workManager.cancelUniqueWork(DAILY_WORK_NAME)
    }

    //one time demo that runs after 10 seconds so it can be shown in the video
    fun sendTestReminder() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag(TEST_WORK_NAME)
            .build()
        workManager.enqueueUniqueWork(TEST_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    //the state of the demo work: enqueued, running, succeeded and so on
    fun observeTestStatus(): Flow<WorkInfo.State?> =
        workManager.getWorkInfosForUniqueWorkFlow(TEST_WORK_NAME)
            .map { infos -> infos.firstOrNull()?.state }

    //milliseconds from now until the next 18:00
    private fun delayUntilEvening(): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis - now.timeInMillis
    }

    private companion object {
        const val DAILY_WORK_NAME = "daily_workout_reminder"
        const val TEST_WORK_NAME = "test_workout_reminder"
        const val REMINDER_HOUR = 18
    }
}