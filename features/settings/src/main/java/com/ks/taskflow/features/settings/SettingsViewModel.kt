package com.ks.taskflow.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.domain.repository.SettingsRepository
import com.ks.taskflow.domain.usecase.demo.GenerateDummyDataUseCase
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
    private val settingsRepository: SettingsRepository,
    private val generateDummyDataUseCase: GenerateDummyDataUseCase
) : ViewModel() {
    
    private val _dummyDataState = MutableStateFlow(false)
    
    /**
     * UI state for the settings screen.
     */
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getDarkThemeEnabled(),
        settingsRepository.getDynamicColorsEnabled(),
        settingsRepository.getNotificationsEnabled(),
        settingsRepository.getReminderNotificationsEnabled(),
        _dummyDataState
    ) { darkTheme, dynamicColors, notificationsEnabled, reminderNotifications, isDummyDataLoading ->
        SettingsUiState(
            darkThemeEnabled = darkTheme,
            dynamicColorsEnabled = dynamicColors,
            notificationsEnabled = notificationsEnabled,
            reminderNotificationsEnabled = reminderNotifications,
            isDummyDataLoading = isDummyDataLoading
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
            settingsRepository.setDarkThemeEnabled(enabled)
        }
    }
    
    /**
     * Sets the dynamic colors preference.
     */
    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorsEnabled(enabled)
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
            settingsRepository.setReminderNotificationsEnabled(enabled)
        }
    }
    
    /**
     * Generates dummy tasks and reminders data.
     */
    fun generateDummyData() {
        viewModelScope.launch {
            _dummyDataState.value = true
            try {
                generateDummyDataUseCase()
            } finally {
                _dummyDataState.value = false
            }
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
    val reminderNotificationsEnabled: Boolean = true,
    val isDummyDataLoading: Boolean = false
)