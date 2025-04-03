package com.ks.taskflow.features.reminders.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.usecase.reminder.DeleteReminderUseCase
import com.ks.taskflow.domain.usecase.reminder.GetReminderByIdUseCase
import com.ks.taskflow.domain.usecase.reminder.UpdateReminderStatusUseCase
import com.ks.taskflow.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the reminder detail screen.
 */
@HiltViewModel
class ReminderDetailViewModel @Inject constructor(
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateReminderStatusUseCase: UpdateReminderStatusUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: String = savedStateHandle[TaskFlowDestinations.REMINDER_DETAIL_ARG_REMINDER_ID] ?: ""
    
    private val _uiState = MutableStateFlow(ReminderDetailUiState())
    val uiState: StateFlow<ReminderDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadReminderDetails()
    }
    
    /**
     * Loads the reminder and associated task details.
     */
    private fun loadReminderDetails() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val reminder = getReminderByIdUseCase(reminderId)
                
                if (reminder != null) {
                    // Load associated task
                    val task = getTaskByIdUseCase(reminder.taskId)
                    
                    _uiState.update {
                        it.copy(
                            reminder = reminder,
                            task = task,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Reminder not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error loading reminder: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Toggles the reminder's enabled status.
     */
    fun toggleReminderStatus(isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                updateReminderStatusUseCase(reminderId, isEnabled)
                
                // Update local state immediately for better UX
                _uiState.update {
                    it.copy(
                        reminder = it.reminder?.copy(isEnabled = isEnabled)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update reminder status: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Deletes the current reminder.
     */
    fun deleteReminder() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isDeleting = true) }
                deleteReminderUseCase(reminderId)
                _uiState.update { it.copy(isDeleted = true, isDeleting = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to delete reminder: ${e.message}",
                        isDeleting = false
                    )
                }
            }
        }
    }
    
    /**
     * Clears the error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * UI state for the reminder detail screen.
 */
data class ReminderDetailUiState(
    val reminder: Reminder? = null,
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
) 