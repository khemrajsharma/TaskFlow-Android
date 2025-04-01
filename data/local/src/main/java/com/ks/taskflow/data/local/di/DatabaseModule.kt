package com.ks.taskflow.data.local.di

import android.content.Context
import androidx.room.Room
import com.ks.taskflow.data.local.TaskFlowDatabase
import com.ks.taskflow.data.local.dao.ReminderDao
import com.ks.taskflow.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TaskFlowDatabase {
        return Room.databaseBuilder(
            context,
            TaskFlowDatabase::class.java,
            TaskFlowDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    /**
     * Provides the TaskDao instance.
     */
    @Singleton
    @Provides
    fun provideTaskDao(database: TaskFlowDatabase): TaskDao {
        return database.taskDao()
    }
    
    /**
     * Provides the ReminderDao instance.
     */
    @Singleton
    @Provides
    fun provideReminderDao(database: TaskFlowDatabase): ReminderDao {
        return database.reminderDao()
    }
} 