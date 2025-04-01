package com.ks.taskflow.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user settings.
 */
interface SettingsRepository {
    /**
     * Gets whether dark theme is enabled.
     */
    fun getDarkThemeEnabled(): Flow<Boolean>
    
    /**
     * Sets whether dark theme is enabled.
     */
    suspend fun setDarkThemeEnabled(enabled: Boolean)
    
    /**
     * Gets whether dynamic colors are enabled.
     */
    fun getDynamicColorsEnabled(): Flow<Boolean>
    
    /**
     * Sets whether dynamic colors are enabled.
     */
    suspend fun setDynamicColorsEnabled(enabled: Boolean)
    
    /**
     * Gets whether notifications are enabled.
     */
    fun getNotificationsEnabled(): Flow<Boolean>
    
    /**
     * Sets whether notifications are enabled.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean)
    
    /**
     * Gets whether reminder notifications are enabled.
     */
    fun getReminderNotificationsEnabled(): Flow<Boolean>
    
    /**
     * Sets whether reminder notifications are enabled.
     */
    suspend fun setReminderNotificationsEnabled(enabled: Boolean)
} 