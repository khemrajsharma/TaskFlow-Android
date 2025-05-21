package com.ks.taskflow.manager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ks.taskflow.core.utils.reminder.ReminderScheduler
import com.ks.taskflow.core.utils.worker.ReminderWorker
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for reminder operations and scheduling.
 */
@Singleton
class ReminderManager @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderSchedulerProvider: Lazy<ReminderScheduler>,
    private val workManagerProvider: Lazy<WorkManager>
) {

    private val reminderScheduler get() = reminderSchedulerProvider.get()
    private val workManager get() = workManagerProvider.get()
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Initializes the reminder manager and sets up periodic reminder checks.
     */
    fun initialize() {
        // Set up periodic work to check for reminders
        schedulePeriodicReminderCheck()
        
        // Monitor reminders for changes to update scheduling
        monitorReminders()
    }
    
    /**
     * Schedules a reminder.
     */
    fun scheduleReminder(reminder: Reminder) {
        reminderScheduler.scheduleReminder(reminder)
    }
    
    /**
     * Schedules all reminders for a task.
     */
    fun scheduleRemindersForTask(taskId: String) {
        reminderScheduler.scheduleRemindersForTask(taskId)
    }
    
    /**
     * Cancels a reminder.
     */
    fun cancelReminder(reminderId: String) {
        reminderScheduler.cancelReminder(reminderId)
    }
    
    /**
     * Cancels all reminders for a task.
     */
    fun cancelRemindersForTask(taskId: String) {
        reminderScheduler.cancelRemindersForTask(taskId)
    }
    
    /**
     * Schedules a periodic check for upcoming reminders.
     */
    private fun schedulePeriodicReminderCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()
        
        val periodicCheckRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES // Check every 15 minutes
        )
            .setConstraints(constraints)
            .addTag(PERIODIC_CHECK_TAG)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_CHECK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicCheckRequest
        )
    }
    
    /**
     * Monitors reminders for changes to update scheduling.
     */
    private fun monitorReminders() {
        scope.launch {
            reminderRepository.getReminders().collectLatest { reminders ->
                // Schedule all enabled reminders
                reminders.filter { it.isEnabled }.forEach { reminder ->
                    reminderScheduler.scheduleReminder(reminder)
                }
            }
        }
    }
    
    companion object {
        private const val PERIODIC_CHECK_TAG = "periodic_reminder_check"
        private const val PERIODIC_CHECK_NAME = "reminder_check_work"
    }
} 