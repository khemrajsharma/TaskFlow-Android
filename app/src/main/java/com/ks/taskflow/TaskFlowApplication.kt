package com.ks.taskflow

import android.app.Application
import com.ks.taskflow.manager.ReminderManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main application class for TaskFlow.
 */
@HiltAndroidApp
class TaskFlowApplication : Application() {
    
    @Inject
    lateinit var reminderManager: ReminderManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize reminder management
        reminderManager.initialize()
    }
}