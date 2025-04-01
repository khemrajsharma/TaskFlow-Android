package com.ks.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ks.taskflow.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for reminder operations.
 */
@Dao
interface ReminderDao {
    
    /**
     * Gets all reminders as a Flow.
     */
    @Query("SELECT * FROM reminders ORDER BY time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>
    
    /**
     * Gets all reminders for a specific task.
     */
    @Query("SELECT * FROM reminders WHERE taskId = :taskId ORDER BY time ASC")
    fun getRemindersByTaskId(taskId: String): Flow<List<ReminderEntity>>
    
    /**
     * Gets reminders that are upcoming (from now until the specified future time).
     */
    @Query("SELECT * FROM reminders WHERE time BETWEEN :fromTime AND :toTime AND isEnabled = 1 ORDER BY time ASC")
    fun getUpcomingReminders(fromTime: String, toTime: String): Flow<List<ReminderEntity>>
    
    /**
     * Gets a reminder by its ID.
     */
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?
    
    /**
     * Gets a reminder by its ID as a Flow.
     */
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderByIdFlow(id: String): Flow<ReminderEntity?>
    
    /**
     * Inserts a reminder.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)
    
    /**
     * Inserts multiple reminders.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)
    
    /**
     * Updates a reminder's enabled status.
     */
    @Query("UPDATE reminders SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateReminderStatus(id: String, isEnabled: Boolean)
    
    /**
     * Updates a reminder.
     */
    @Update
    suspend fun updateReminder(reminder: ReminderEntity)
    
    /**
     * Deletes a reminder.
     */
    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
    
    /**
     * Deletes a reminder by its ID.
     */
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: String)
    
    /**
     * Deletes all reminders for a specific task.
     */
    @Query("DELETE FROM reminders WHERE taskId = :taskId")
    suspend fun deleteRemindersByTaskId(taskId: String)
} 