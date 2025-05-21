package com.ks.taskflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ks.taskflow.manager.ReminderManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

/**
 * Main application class for TaskFlow.
 */
@HiltAndroidApp
class TaskFlowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var reminderManager: ReminderManager

    // Inject a Provider for HiltWorkerFactory
    @Inject
    lateinit var workerFactoryProvider: Provider<HiltWorkerFactory>

    override fun onCreate() {
        super.onCreate()

        // Initialize reminder management
        reminderManager.initialize()
    }

    // Implement the Configuration.Provider interface
    // Construct the Configuration directly here using the HiltWorkerFactory provider.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactoryProvider.get())
            .build()
}