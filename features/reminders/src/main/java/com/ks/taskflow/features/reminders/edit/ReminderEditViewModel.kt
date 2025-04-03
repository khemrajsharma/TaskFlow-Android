package com.ks.taskflow.features.reminders.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.model.RepeatInterval
import com.ks.taskflow.domain.usecase.reminder.GetReminderByIdUseCase
import com.ks.taskflow.domain.usecase.reminder.SaveReminderUseCase
import com.ks.taskflow.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for creating and editing reminders.
 */
@HiltViewModel
class ReminderEditViewModel @Inject constructor(
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val saveReminderUseCase: SaveReminderUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: String? = savedStateHandle[TaskFlowDestinations.REMINDER_EDIT_ARG_REMINDER_ID]
    private val taskId: String? = savedStateHandle[TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID]
    
    private val _uiState = MutableStateFlow(ReminderEditUiState())
    val uiState: StateFlow<ReminderEditUiState> = _uiState.asStateFlow()
    
    init {
        if (reminderId != null) {
            // If we have a reminderId, load the existing reminder
            loadReminder(reminderId)
        } else if (taskId != null && taskId.isNotBlank()) {
            // If we have a taskId, load the task details to use for a new reminder
            loadTaskForNewReminder(taskId)
        }
    }
    
    /**
     * Loads an existing reminder for editing.
     */
    private fun loadReminder(reminderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reminder = getReminderByIdUseCase(reminderId)
                if (reminder != null) {
                    _uiState.update {
                        it.copy(
                            title = reminder.title,
                            message = reminder.message,
                            time = reminder.time,
                            isRepeating = reminder.isRepeating,
                            repeatInterval = reminder.repeatInterval,
                            isEnabled = reminder.isEnabled,
                            taskId = reminder.taskId,
                            isLoading = false,
                            isNewReminder = false
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
     * Loads task details for creating a new reminder.
     */
    private fun loadTaskForNewReminder(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = getTaskByIdUseCase(taskId)
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            title = "Reminder for: ${task.title}",
                            taskId = task.id,
                            time = LocalDateTime.now().plusHours(1),
                            isLoading = false
                        )
                    }
                    validateFields()
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Task not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error loading task: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates the title field in the UI state.
     */
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
        validateFields()
    }
    
    /**
     * Updates the message field in the UI state.
     */
    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message) }
        validateFields()
    }
    
    /**
     * Updates the time field in the UI state.
     */
    fun updateTime(time: LocalDateTime) {
        _uiState.update { it.copy(time = time) }
        validateFields()
    }
    
    /**
     * Updates the repeat settings in the UI state.
     */
    fun updateRepeatSettings(isRepeating: Boolean, repeatInterval: RepeatInterval? = null) {
        _uiState.update {
            it.copy(
                isRepeating = isRepeating,
                repeatInterval = if (isRepeating) repeatInterval ?: RepeatInterval.DAILY else null
            )
        }
        validateFields()
    }
    
    /**
     * Updates the enabled state in the UI state.
     */
    fun updateEnabledState(isEnabled: Boolean) {
        _uiState.update { it.copy(isEnabled = isEnabled) }
        validateFields()
    }
    
    /**
     * Validates the form fields and updates the isValid state.
     */
    private fun validateFields() {
        val state = _uiState.value
        val isValid = state.title.isNotBlank() && 
                      state.taskId.isNotBlank() && 
                      state.time != null &&
                      (!state.isRepeating || state.repeatInterval != null)
                      
        _uiState.update { it.copy(isValid = isValid) }
    }
    
    /**
     * Saves the current reminder.
     */
    fun saveReminder() {
        val state = _uiState.value
        if (!state.isValid || state.taskId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Invalid reminder data") }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            try {
                val reminder = Reminder(
                    id = reminderId ?: UUID.randomUUID().toString(),
                    taskId = state.taskId,
                    title = state.title,
                    message = state.message,
                    time = state.time ?: LocalDateTime.now().plusHours(1),
                    isRepeating = state.isRepeating,
                    repeatInterval = if (state.isRepeating) state.repeatInterval else null,
                    isEnabled = state.isEnabled,
                    createdAt = LocalDateTime.now()
                )
                
                saveReminderUseCase(reminder)
                
                _uiState.update {
                    it.copy(
                        isSaved = true,
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to save reminder: ${e.message}",
                        isSaving = false
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
 * UI state for the reminder edit screen.
 */
data class ReminderEditUiState(
    val title: String = "",
    val message: String = "",
    val time: LocalDateTime? = null,
    val isRepeating: Boolean = false,
    val repeatInterval: RepeatInterval? = null,
    val isEnabled: Boolean = true,
    val taskId: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isValid: Boolean = false,
    val isNewReminder: Boolean = true,
    val errorMessage: String? = null
) 