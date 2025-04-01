package com.ks.taskflow.core.utils.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ks.taskflow.core.utils.worker.ReminderWorker
import com.ks.taskflow.domain.model.Reminder
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Utility class for scheduling reminders with WorkManager.
 */
class ReminderScheduler @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedules a reminder notification.
     */
    fun scheduleReminder(reminder: Reminder) {
        if (!reminder.isEnabled) return
        
        val now = LocalDateTime.now()
        if (reminder.time.isBefore(now)) return // Don't schedule past reminders
        
        val delay = Duration.between(now, reminder.time)
        val workRequestName = "reminder_${reminder.id}"
        
        val inputData = Data.Builder()
            .putString(REMINDER_ID_KEY, reminder.id)
            .putString(TASK_ID_KEY, reminder.taskId)
            .build()
        
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag(TAG_REMINDER)
            .addTag(reminder.id)
            .build()
        
        workManager.enqueueUniqueWork(
            workRequestName,
            ExistingWorkPolicy.REPLACE,
            reminderWorkRequest
        )
    }
    
    /**
     * Schedules all reminders for a task.
     */
    fun scheduleRemindersForTask(taskId: String) {
        val inputData = Data.Builder()
            .putString(TASK_ID_KEY, taskId)
            .build()
        
        val refreshWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .addTag(TAG_REFRESH)
            .addTag(taskId)
            .build()
        
        workManager.enqueue(refreshWorkRequest)
    }
    
    /**
     * Cancels a reminder.
     */
    fun cancelReminder(reminderId: String) {
        workManager.cancelUniqueWork("reminder_$reminderId")
    }
    
    /**
     * Cancels all reminders for a task.
     */
    fun cancelRemindersForTask(taskId: String) {
        workManager.cancelAllWorkByTag(taskId)
    }
    
    /**
     * Schedules a periodic check for upcoming reminders.
     */
    fun scheduleReminderCheck() {
        val checkWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .addTag(TAG_CHECK)
            .build()
        
        workManager.enqueue(checkWorkRequest)
    }
    
    companion object {
        private const val TAG_REMINDER = "reminder"
        private const val TAG_REFRESH = "refresh"
        private const val TAG_CHECK = "check"
        private const val REMINDER_ID_KEY = "reminder_id"
        private const val TASK_ID_KEY = "task_id"
    }
} 