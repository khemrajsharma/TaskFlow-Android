package com.ks.taskflow.core.utils.worker

/**
 * Constants related to reminders and notification workers.
 */
object ReminderConstants {
    // Worker keys
    const val REMINDER_ID_KEY = "reminder_id"
    const val TASK_ID_KEY = "task_id"
    
    // Channel constants
    const val CHANNEL_ID = "TaskFlow_Reminders"
    const val CHANNEL_NAME = "TaskFlow Reminders"
    const val CHANNEL_DESCRIPTION = "Reminders for your tasks"
    
    // Worker tags
    const val TAG_REMINDER = "reminder"
    const val TAG_REFRESH = "refresh"
    const val TAG_CHECK = "check"
} 