package com.ks.taskflow.domain.repository

import com.ks.taskflow.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for Reminder operations.
 */
interface ReminderRepository {
    
    /**
     * Gets all reminders.
     */
    fun getReminders(): Flow<List<Reminder>>
    
    /**
     * Gets a reminder by ID.
     */
    suspend fun getReminderById(id: String): Reminder?
    
    /**
     * Gets a reminder by ID as a Flow.
     */
    fun getReminderByIdFlow(id: String): Flow<Reminder?>
    
    /**
     * Gets reminders for a specific task.
     */
    fun getRemindersByTaskId(taskId: String): Flow<List<Reminder>>
    
    /**
     * Gets upcoming reminders within the specified time range.
     */
    fun getUpcomingReminders(fromTime: LocalDateTime, toTime: LocalDateTime): Flow<List<Reminder>>
    
    /**
     * Gets reminders for the next 24 hours.
     */
    fun getRemindersForNext24Hours(): Flow<List<Reminder>>
    
    /**
     * Gets reminders for the next 7 days.
     */
    fun getRemindersForNextWeek(): Flow<List<Reminder>>
    
    /**
     * Saves a reminder (insert if new, update if existing).
     */
    suspend fun saveReminder(reminder: Reminder)
    
    /**
     * Updates a reminder's enabled status.
     */
    suspend fun updateReminderStatus(reminderId: String, isEnabled: Boolean)
    
    /**
     * Deletes a reminder.
     */
    suspend fun deleteReminder(reminderId: String)
    
    /**
     * Deletes all reminders for a specific task.
     */
    suspend fun deleteRemindersByTaskId(taskId: String)
} 