package com.ks.taskflow.data.repository.di

import com.ks.taskflow.data.repository.ReminderRepositoryImpl
import com.ks.taskflow.data.repository.SettingsRepositoryImpl
import com.ks.taskflow.data.repository.TaskRepositoryImpl
import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.SettingsRepository
import com.ks.taskflow.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds the TaskRepository implementation.
     */
    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
    
    /**
     * Binds the ReminderRepository implementation.
     */
    @Singleton
    @Binds
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
} 