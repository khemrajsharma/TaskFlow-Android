package com.ks.taskflow.core.navigation

/**
 * Contains the navigation destinations for the app.
 */
object TaskFlowDestinations {
    const val TASKS_ROUTE = "tasks"
    const val TASK_DETAIL_ROUTE = "task_detail"
    const val TASK_DETAIL_ARG_TASK_ID = "taskId"
    const val TASK_DETAIL_FULL_ROUTE = "$TASK_DETAIL_ROUTE/{$TASK_DETAIL_ARG_TASK_ID}"
    const val TASK_EDIT_ROUTE = "task_edit"
    const val TASK_EDIT_ARG_TASK_ID = "taskId"
    const val TASK_EDIT_FULL_ROUTE = "$TASK_EDIT_ROUTE/{$TASK_EDIT_ARG_TASK_ID}"
    
    const val REMINDERS_ROUTE = "reminders"
    const val REMINDER_DETAIL_ROUTE = "reminder_detail"
    const val REMINDER_DETAIL_ARG_REMINDER_ID = "reminderId"
    const val REMINDER_DETAIL_FULL_ROUTE = "$REMINDER_DETAIL_ROUTE/{$REMINDER_DETAIL_ARG_REMINDER_ID}"
    const val REMINDER_EDIT_ROUTE = "reminder_edit"
    const val REMINDER_EDIT_ARG_REMINDER_ID = "reminderId"
    const val REMINDER_EDIT_ARG_TASK_ID = "taskId"
    const val REMINDER_EDIT_FULL_ROUTE = "$REMINDER_EDIT_ROUTE/{$REMINDER_EDIT_ARG_REMINDER_ID}?taskId={$REMINDER_EDIT_ARG_TASK_ID}"
    
    const val SETTINGS_ROUTE = "settings"
} 