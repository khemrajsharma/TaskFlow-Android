package com.ks.taskflow.features.tasks.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import com.ks.taskflow.domain.usecase.task.GetTaskByIdUseCase
import com.ks.taskflow.domain.usecase.task.SaveTaskUseCase
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
 * ViewModel for creating and editing tasks.
 */
@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String? = savedStateHandle[TaskFlowDestinations.TASK_ID_ARG]
    
    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()
    
    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }
    
    /**
     * Loads an existing task for editing.
     */
    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = getTaskByIdUseCase(taskId)
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            priority = task.priority,
                            category = task.category,
                            dueDate = task.dueDate,
                            tags = task.tags.joinToString(", "),
                            isLoading = false,
                            isNewTask = false
                        )
                    }
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
     * Updates the description field in the UI state.
     */
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    /**
     * Updates the priority field in the UI state.
     */
    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }
    
    /**
     * Updates the category field in the UI state.
     */
    fun updateCategory(category: TaskCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    /**
     * Updates the due date field in the UI state.
     */
    fun updateDueDate(dueDate: LocalDateTime?) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }
    
    /**
     * Updates the tags field in the UI state.
     */
    fun updateTags(tags: String) {
        _uiState.update { it.copy(tags = tags) }
    }
    
    /**
     * Validates the form fields.
     */
    private fun validateFields() {
        val isValid = uiState.value.title.isNotBlank()
        _uiState.update { it.copy(isValid = isValid) }
    }
    
    /**
     * Saves the task.
     */
    fun saveTask() {
        val currentState = uiState.value
        
        if (!currentState.isValid) {
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            try {
                val tagList = currentState.tags
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                
                val task = Task(
                    id = taskId ?: UUID.randomUUID().toString(),
                    title = currentState.title,
                    description = currentState.description,
                    completed = false, // Always start as not completed or keep existing state
                    priority = currentState.priority,
                    category = currentState.category,
                    dueDate = currentState.dueDate,
                    tags = tagList,
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
                
                saveTaskUseCase(task)
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Error saving task: ${e.message}",
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
 * UI state for the task edit screen.
 */
data class TaskEditUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val dueDate: LocalDateTime? = null,
    val tags: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isValid: Boolean = false,
    val isNewTask: Boolean = true,
    val errorMessage: String? = null
) 