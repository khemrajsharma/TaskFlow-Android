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
import com.ks.taskflow.core.utils.R
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Worker for scheduling and showing reminders.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val CHANNEL_ID = "TaskFlow_Reminders"
        private const val REMINDER_ID_KEY = "reminder_id"
        private const val TASK_ID_KEY = "task_id"
    }
    
    /**
     * Performs the background check for upcoming reminders and shows notifications.
     */
    override suspend fun doWork(): Result {
        // Extract parameters
        val reminderId = workerParams.inputData.getString(REMINDER_ID_KEY)
        val taskId = workerParams.inputData.getString(TASK_ID_KEY)
        
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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TaskFlow Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for your tasks"
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
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
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
    }
} 