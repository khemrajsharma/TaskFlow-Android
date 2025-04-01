package com.ks.taskflow.features.tasks.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import com.ks.taskflow.domain.usecase.task.CompleteTaskUseCase
import com.ks.taskflow.domain.usecase.task.DeleteTaskUseCase
import com.ks.taskflow.domain.usecase.task.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for the tasks list screen.
 */
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    // Filters
    private val _filterCompleted = MutableStateFlow<Boolean?>(null)
    private val _filterPriority = MutableStateFlow<Priority?>(null)
    private val _filterCategory = MutableStateFlow<TaskCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    /**
     * Combined state representing the UI state and task list.
     */
    val uiState: StateFlow<TasksUiState> = combine(
        getTasksUseCase(),
        _filterCompleted,
        _filterPriority,
        _filterCategory,
        _searchQuery,
        _isLoading,
        _errorMessage
    ) { tasks, completed, priority, category, query, isLoading, error ->
        _isLoading.value = false
        
        // Apply filters
        val filteredTasks = tasks.filter { task ->
            (completed == null || task.completed == completed) &&
            (priority == null || task.priority == priority) &&
            (category == null || task.category == category) &&
            (query.isBlank() || task.title.contains(query, ignoreCase = true) || 
                task.description.contains(query, ignoreCase = true))
        }
        
        TasksUiState(
            tasks = filteredTasks,
            isLoading = isLoading,
            errorMessage = error,
            filterCompleted = completed,
            filterPriority = priority,
            filterCategory = category,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState(isLoading = true)
    )
    
    /**
     * Sets the completed filter.
     */
    fun setCompletedFilter(completed: Boolean?) {
        _filterCompleted.value = completed
    }
    
    /**
     * Sets the priority filter.
     */
    fun setPriorityFilter(priority: Priority?) {
        _filterPriority.value = priority
    }
    
    /**
     * Sets the category filter.
     */
    fun setCategoryFilter(category: TaskCategory?) {
        _filterCategory.value = category
    }
    
    /**
     * Sets the search query.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Toggles the completion status of a task.
     */
    fun toggleTaskCompletion(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                completeTaskUseCase(taskId, completed)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
            }
        }
    }
    
    /**
     * Deletes a task.
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
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
     * Clear all filters.
     */
    fun clearFilters() {
        _filterCompleted.value = null
        _filterPriority.value = null
        _filterCategory.value = null
        _searchQuery.value = ""
    }
}

/**
 * UI state for the tasks list screen.
 */
data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val filterCompleted: Boolean? = null,
    val filterPriority: Priority? = null,
    val filterCategory: TaskCategory? = null,
    val searchQuery: String = ""
) {
    val isEmpty: Boolean get() = tasks.isEmpty() && !isLoading
    val hasFilters: Boolean get() = filterCompleted != null || filterPriority != null || 
        filterCategory != null || searchQuery.isNotBlank()
} 