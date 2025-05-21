package com.ks.taskflow.core.utils.di

import androidx.work.WorkManager
import com.ks.taskflow.core.utils.reminder.ReminderScheduler
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    @Singleton
    fun provideReminderScheduler(workManagerProvider: Lazy<WorkManager>): ReminderScheduler {
        // Pass the Lazy<WorkManager> to ReminderScheduler's constructor
        return ReminderScheduler(workManagerProvider)
    }
} 