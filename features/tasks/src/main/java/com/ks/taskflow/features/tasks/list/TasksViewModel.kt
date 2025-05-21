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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the tasks list screen.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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
    
    // Loading state
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    // Create a combined flow of all filters
    private val _filtersFlow = combine(
        _filterCompleted,
        _filterPriority,
        _filterCategory,
        _searchQuery
    ) { completed, priority, category, query ->
        FilterParams(
            completed = completed,
            priority = priority,
            category = category,
            query = query
        )
    }
    
    // Task data with filters applied
    private val _tasksFlow = _filtersFlow.flatMapLatest { filters ->
        getTasksUseCase().map { tasks ->
            tasks.filter { task ->
                (filters.completed == null || task.completed == filters.completed) &&
                (filters.priority == null || task.priority == filters.priority) &&
                (filters.category == null || task.category == filters.category) &&
                (filters.query.isBlank() || task.title.contains(filters.query, ignoreCase = true) || 
                    task.description.contains(filters.query, ignoreCase = true))
            }
        }
    }
    
    /**
     * Combined state representing the UI state and task list.
     */
    val uiState: StateFlow<TasksUiState> = combine(
        _tasksFlow,
        _filtersFlow,
        _isLoading,
        _errorMessage
    ) { tasks, filters, isLoading, errorMessage ->
        _isLoading.value = false
        
        TasksUiState(
            tasks = tasks,
            isLoading = isLoading,
            errorMessage = errorMessage,
            filterCompleted = filters.completed,
            filterPriority = filters.priority,
            filterCategory = filters.category,
            searchQuery = filters.query
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
        viewModelScope.launch {
            _searchQuery.value = query
        }
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
 * Data class to hold filter parameters.
 */
private data class FilterParams(
    val completed: Boolean?,
    val priority: Priority?,
    val category: TaskCategory?,
    val query: String
)

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