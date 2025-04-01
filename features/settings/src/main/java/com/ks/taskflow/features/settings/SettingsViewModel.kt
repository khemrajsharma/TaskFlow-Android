package com.ks.taskflow.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.features.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    /**
     * UI state for the settings screen.
     */
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.darkThemeFlow,
        settingsRepository.dynamicColorsFlow,
        settingsRepository.notificationsEnabledFlow,
        settingsRepository.reminderNotificationsFlow
    ) { darkTheme, dynamicColors, notificationsEnabled, reminderNotifications ->
        SettingsUiState(
            darkThemeEnabled = darkTheme,
            dynamicColorsEnabled = dynamicColors,
            notificationsEnabled = notificationsEnabled,
            reminderNotificationsEnabled = reminderNotifications
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )
    
    /**
     * Sets the dark theme preference.
     */
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }
    
    /**
     * Sets the dynamic colors preference.
     */
    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColors(enabled)
        }
    }
    
    /**
     * Sets the notifications enabled preference.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }
    
    /**
     * Sets the reminder notifications preference.
     */
    fun setReminderNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setReminderNotifications(enabled)
        }
    }
}

/**
 * UI state for the settings screen.
 */
data class SettingsUiState(
    val darkThemeEnabled: Boolean = false,
    val dynamicColorsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val reminderNotificationsEnabled: Boolean = true
)