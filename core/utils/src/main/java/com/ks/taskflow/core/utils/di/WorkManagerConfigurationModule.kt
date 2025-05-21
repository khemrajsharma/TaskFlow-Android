package com.ks.taskflow.core.utils.di

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerConfigurationModule {

    @Provides
    @Singleton // Configuration should be a singleton
    fun provideWorkManagerConfiguration(workerFactory: HiltWorkerFactory): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            // You can add other WorkManager configurations here if needed,
            // e.g., .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }
} 