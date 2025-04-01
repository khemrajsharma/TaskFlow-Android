package com.ks.taskflow.features.tasks.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.usecase.reminder.DeleteReminderUseCase
import com.ks.taskflow.domain.usecase.reminder.GetRemindersUseCase
import com.ks.taskflow.domain.usecase.reminder.UpdateReminderStatusUseCase
import com.ks.taskflow.domain.usecase.task.CompleteTaskUseCase
import com.ks.taskflow.domain.usecase.task.DeleteTaskUseCase
import com.ks.taskflow.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the task detail screen.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val updateReminderStatusUseCase: UpdateReminderStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String = savedStateHandle[TaskFlowDestinations.TASK_DETAIL_ARG_TASK_ID] ?: ""
    
    private val _task = MutableStateFlow<Task?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    /**
     * UI state for the task detail screen.
     */
    val uiState: StateFlow<TaskDetailUiState> = combine(
        _task,
        getRemindersUseCase(taskId),
        _isLoading,
        _errorMessage
    ) { task, reminders, isLoading, error ->
        TaskDetailUiState(
            task = task,
            reminders = reminders,
            isLoading = isLoading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskDetailUiState(isLoading = true)
    )
    
    init {
        loadTask()
    }
    
    /**
     * Loads the task details.
     */
    private fun loadTask() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val task = getTaskByIdUseCase(taskId)
                _task.value = task
                _isLoading.value = false
                
                if (task == null) {
                    _errorMessage.value = "Task not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading task: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggles the completion status of the task.
     */
    fun toggleTaskCompletion() {
        viewModelScope.launch {
            try {
                _task.value?.let { task ->
                    completeTaskUseCase(task.id, !task.completed)
                    // Update the local task state immediately
                    _task.value = task.copy(completed = !task.completed)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
            }
        }
    }
    
    /**
     * Deletes the task and its reminders.
     */
    fun deleteTask() {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                // No need to navigate back here, the caller should handle it
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
            }
        }
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
}

/**
 * UI state for the task detail screen.
 */
data class TaskDetailUiState(
    val task: Task? = null,
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isTaskLoaded: Boolean get() = task != null && !isLoading
    val hasReminders: Boolean get() = reminders.isNotEmpty()
} 