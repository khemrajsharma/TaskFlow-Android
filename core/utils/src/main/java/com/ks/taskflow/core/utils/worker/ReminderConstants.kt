package com.ks.taskflow.core.utils.worker

/**
 * Constants related to reminders and notification workers.
 */
object ReminderConstants {
    // Worker keys
    const val REMINDER_ID_KEY = "reminder_id"
    const val TASK_ID_KEY = "task_id"
    const val IS_REPEATING_KEY = "is_repeating"
    const val REPEAT_INTERVAL_KEY = "repeat_interval"
    
    // Channel constants
    const val CHANNEL_ID = "TaskFlow_Reminders"
    const val CHANNEL_NAME = "TaskFlow Reminders"
    const val CHANNEL_DESCRIPTION = "Reminders for your tasks"
    
    // Worker tags
    const val TAG_REMINDER = "reminder"
    const val TAG_REPEATING = "repeating"
    const val TAG_REFRESH = "refresh"
    const val TAG_CHECK = "check"
} 