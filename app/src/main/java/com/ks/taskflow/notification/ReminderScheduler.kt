package com.ks.taskflow.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ks.taskflow.domain.model.Reminder
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Scheduler for task reminders using WorkManager.
 */
class ReminderScheduler @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedule a reminder using WorkManager.
     * @param reminder The reminder to schedule.
     * @return true if the reminder was scheduled, false otherwise.
     */
    fun scheduleReminder(reminder: Reminder): Boolean {
        if (!reminder.isEnabled) return false
        
        val now = LocalDateTime.now()
        if (reminder.time.isBefore(now)) return false
        
        // Calculate delay until reminder time
        val delayMillis = Duration.between(now, reminder.time).toMillis()
        if (delayMillis <= 0) return false
        
        // Create input data with reminder ID
        val inputData = Data.Builder()
            .putString(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .build()
        
        // Create work request
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(reminder.id)
            .build()
        
        // Enqueue work request
        workManager.enqueueUniqueWork(
            reminder.id,
            ExistingWorkPolicy.REPLACE,
            reminderWorkRequest
        )
        
        return true
    }
    
    /**
     * Cancel a scheduled reminder.
     * @param reminderId The ID of the reminder to cancel.
     */
    fun cancelReminder(reminderId: String) {
        workManager.cancelUniqueWork(reminderId)
    }
    
    /**
     * Reschedule a reminder.
     * @param reminder The reminder to reschedule.
     * @return true if the reminder was rescheduled, false otherwise.
     */
    fun rescheduleReminder(reminder: Reminder): Boolean {
        cancelReminder(reminder.id)
        return if (reminder.isEnabled) {
            scheduleReminder(reminder)
        } else {
            true
        }
    }
} 