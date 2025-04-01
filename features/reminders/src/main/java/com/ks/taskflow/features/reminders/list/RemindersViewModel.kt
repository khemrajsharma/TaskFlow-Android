package com.ks.taskflow.features.reminders.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.usecase.reminder.DeleteReminderUseCase
import com.ks.taskflow.domain.usecase.reminder.GetRemindersUseCase
import com.ks.taskflow.domain.usecase.reminder.UpdateReminderStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the reminders list screen.
 */
@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetRemindersUseCase,
    private val updateReminderStatusUseCase: UpdateReminderStatusUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _filterEnabled = MutableStateFlow<Boolean?>(null)

    /**
     * UI state for the reminders list.
     */
    val uiState: StateFlow<RemindersUiState> = combine(
        getAllRemindersUseCase(),
        _searchQuery,
        _filterEnabled,
        _isLoading,
        _errorMessage
    ) { reminders, query, enabled, isLoading, error ->
        _isLoading.value = false
        
        // Apply filters
        val filteredReminders = reminders.filter { reminder ->
            (query.isEmpty() || reminder.title.contains(query, ignoreCase = true)) &&
            (enabled == null || reminder.isEnabled == enabled)
        }.sortedBy { it.time } // Sort by time
        
        RemindersUiState(
            reminders = filteredReminders,
            isLoading = isLoading,
            errorMessage = error,
            searchQuery = query,
            filterEnabled = enabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RemindersUiState(isLoading = true)
    )
    
    /**
     * Sets the search query.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Sets the enabled filter.
     */
    fun setEnabledFilter(enabled: Boolean?) {
        _filterEnabled.value = enabled
    }
    
    /**
     * Toggles a reminder's enabled status.
     */
    fun toggleReminderStatus(reminderId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                updateReminderStatusUseCase(reminderId, isEnabled)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update reminder: ${e.message}"
            }
        }
    }
    
    /**
     * Deletes a reminder.
     */
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                deleteReminderUseCase(reminderId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete reminder: ${e.message}"
            }
        }
    }
    
    /**
     * Clears the error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clears all filters.
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _filterEnabled.value = null
    }
}

/**
 * UI state for the reminders list.
 */
data class RemindersUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val filterEnabled: Boolean? = null
) {
    val isEmpty: Boolean get() = reminders.isEmpty() && !isLoading
    val hasFilters: Boolean get() = searchQuery.isNotBlank() || filterEnabled != null
} 