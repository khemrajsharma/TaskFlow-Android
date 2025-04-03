package com.ks.taskflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ks.taskflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val REMINDER_NOTIFICATIONS = booleanPreferencesKey("reminder_notifications")
    }

    override fun getDarkThemeEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DARK_THEME] == true
        }
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = enabled
        }
    }

    override fun getDynamicColorsEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLORS] != false
        }
    }

    override suspend fun setDynamicColorsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLORS] = enabled
        }
    }

    override fun getNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] != false
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
            // If notifications are disabled, also disable reminder notifications
            if (!enabled) {
                preferences[PreferencesKeys.REMINDER_NOTIFICATIONS] = false
            }
        }
    }

    override fun getReminderNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.REMINDER_NOTIFICATIONS] != false
        }
    }

    override suspend fun setReminderNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_NOTIFICATIONS] = enabled
        }
    }
} 