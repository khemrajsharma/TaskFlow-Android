package com.ks.taskflow.core.permission.di

import android.content.Context
import com.ks.taskflow.core.permission.NotificationPermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {
    
    @Provides
    @Singleton
    fun provideNotificationPermissionManager(
        @ApplicationContext context: Context
    ): NotificationPermissionManager {
        return NotificationPermissionManager(context)
    }
} 