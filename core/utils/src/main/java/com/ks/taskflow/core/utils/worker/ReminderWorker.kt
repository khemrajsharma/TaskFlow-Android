package com.ks.taskflow.core.utils.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ks.taskflow.core.permission.NotificationPermissionManager
import com.ks.taskflow.core.utils.R
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.model.RepeatInterval
import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

/**
 * Worker for scheduling and showing reminders.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val notificationPermissionManager: NotificationPermissionManager
) : CoroutineWorker(context, workerParams) {

    /**
     * Performs the background check for upcoming reminders and shows notifications.
     */
    override suspend fun doWork(): Result {
        // Check if notification permission is granted
        if (!notificationPermissionManager.hasNotificationPermission()) {
            return Result.success() // Skip showing notification but mark as success
        }

        // Extract parameters
        val reminderId = workerParams.inputData.getString(ReminderConstants.REMINDER_ID_KEY)
        val taskId = workerParams.inputData.getString(ReminderConstants.TASK_ID_KEY)

        // Check if the reminder is still active
        if (reminderId != null) {
            // Single reminder mode
            val reminder = reminderRepository.getReminderById(reminderId)
            if (reminder != null && reminder.isEnabled) {
                // Get associated task
                val task = taskRepository.getTaskById(reminder.taskId)
                if (task != null && !task.completed) {
                    showNotification(reminder, task.title)
                }
            }
        } else if (taskId != null) {
            // All reminders for a task mode
            val reminders = reminderRepository.getRemindersByTaskId(taskId).first()
            val task = taskRepository.getTaskById(taskId)

            if (task != null && !task.completed) {
                reminders.filter { it.isEnabled }.forEach { reminder ->
                    showNotification(reminder, task.title)
                }
            }
        } else {
            // Check for all upcoming reminders
            val now = LocalDateTime.now()
            val future = now.plusMinutes(15) // Check for reminders due in the next 15 minutes

            val upcomingReminders = reminderRepository.getUpcomingReminders(now, future).first()

            for (reminder in upcomingReminders) {
                if (!reminder.isEnabled) continue

                val task = taskRepository.getTaskById(reminder.taskId) ?: continue
                if (task.completed) continue

                showNotification(reminder, task.title)
            }
        }

        return Result.success()
    }

    /**
     * Shows a notification for a reminder.
     */
    private fun showNotification(reminder: Reminder, taskTitle: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderConstants.CHANNEL_ID,
                ReminderConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = ReminderConstants.CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create pending intent for when the notification is tapped
        val activityIntent = Intent(context, Class.forName("com.ks.taskflow.MainActivity")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", reminder.taskId)
            putExtra("reminderId", reminder.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.hashCode(),
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Format the reminder time
        val formattedTime = reminder.time.format(
            DateTimeFormatter.ofPattern("HH:mm")
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, ReminderConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText(reminder.title)
            .setSubText(formattedTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(reminder.id.hashCode(), notification)

        // If this is a repeating reminder, schedule the next one
        if (reminder.isRepeating && reminder.repeatInterval != null) {
            scheduleNextRepeatingReminder(reminder)
        }
    }

    /**
     * Schedules the next occurrence of a repeating reminder.
     */
    private fun scheduleNextRepeatingReminder(reminder: Reminder) {
        val nextTime = when (reminder.repeatInterval) {
            RepeatInterval.DAILY -> reminder.time.plusDays(1)
            RepeatInterval.WEEKLY -> reminder.time.plusWeeks(1)
            RepeatInterval.MONTHLY -> reminder.time.plusMonths(1)
            else -> return // Unknown or custom interval, don't reschedule
        }

        // Create a new reminder with the updated time
        val nextReminder = reminder.copy(
            time = nextTime
        )

        // Save the updated reminder in a coroutine scope
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            reminderRepository.saveReminder(nextReminder)
        }
    }
} 