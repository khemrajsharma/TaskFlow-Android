package com.ks.taskflow.core.utils.reminder

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ks.taskflow.core.utils.worker.ReminderConstants
import com.ks.taskflow.core.utils.worker.ReminderWorker
import com.ks.taskflow.domain.model.Reminder
import dagger.Lazy
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Utility class for scheduling reminders with WorkManager.
 */
class ReminderScheduler @Inject constructor(
    private val workManagerProvider: Lazy<WorkManager>
) {
    private val workManager get() = workManagerProvider.get()
    
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
            .putString(ReminderConstants.REMINDER_ID_KEY, reminder.id)
            .putString(ReminderConstants.TASK_ID_KEY, reminder.taskId)
            .putBoolean(ReminderConstants.IS_REPEATING_KEY, reminder.isRepeating)
            .putString(ReminderConstants.REPEAT_INTERVAL_KEY, reminder.repeatInterval?.value)
            .build()
        
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag(ReminderConstants.TAG_REMINDER)
            .addTag(reminder.id)
            .apply {
                if (reminder.isRepeating) {
                    addTag(ReminderConstants.TAG_REPEATING)
                }
            }
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
            .putString(ReminderConstants.TASK_ID_KEY, taskId)
            .build()
        
        val refreshWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .addTag(ReminderConstants.TAG_REFRESH)
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
            .addTag(ReminderConstants.TAG_CHECK)
            .build()
        
        workManager.enqueue(checkWorkRequest)
    }
} 