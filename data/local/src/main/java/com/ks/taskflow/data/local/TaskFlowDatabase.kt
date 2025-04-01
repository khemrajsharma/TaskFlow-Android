package com.ks.taskflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ks.taskflow.data.local.converter.DateTimeConverters
import com.ks.taskflow.data.local.dao.ReminderDao
import com.ks.taskflow.data.local.dao.TaskDao
import com.ks.taskflow.data.local.entity.ReminderEntity
import com.ks.taskflow.data.local.entity.TaskEntity

/**
 * Room database for the TaskFlow application.
 */
@Database(
    entities = [
        TaskEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class TaskFlowDatabase : RoomDatabase() {
    
    /**
     * Returns the DAO for task operations.
     */
    abstract fun taskDao(): TaskDao
    
    /**
     * Returns the DAO for reminder operations.
     */
    abstract fun reminderDao(): ReminderDao
    
    companion object {
        const val DATABASE_NAME = "taskflow.db"
    }
} 