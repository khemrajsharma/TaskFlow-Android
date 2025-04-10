package com.ks.taskflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ks.taskflow.manager.ReminderManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main application class for TaskFlow.
 */
@HiltAndroidApp
class TaskFlowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var reminderManager: ReminderManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize reminder management
        reminderManager.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}