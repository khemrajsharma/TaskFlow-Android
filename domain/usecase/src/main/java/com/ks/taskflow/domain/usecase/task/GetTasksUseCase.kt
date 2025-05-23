package com.ks.taskflow.domain.usecase.task

import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import com.ks.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for retrieving tasks with optional filtering.
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Gets all tasks as a Flow.
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getTasks()
    }
    
    /**
     * Gets tasks filtered by various parameters.
     */
    operator fun invoke(
        completed: Boolean? = null,
        priority: Priority? = null,
        category: TaskCategory? = null,
        dueDateStart: LocalDateTime? = null,
        dueDateEnd: LocalDateTime? = null,
        searchQuery: String = ""
    ): Flow<List<Task>> {
        var tasks = taskRepository.getTasks()
        
        // Apply filters
        if (completed != null) {
            tasks = taskRepository.getTasksByCompletionStatus(completed)
        }
        
        if (priority != null) {
            tasks = taskRepository.getTasksByPriority(priority)
        }
        
        if (category != null) {
            tasks = taskRepository.getTasksByCategory(category)
        }
        
        if (searchQuery.isNotBlank()) {
            tasks = taskRepository.searchTasks(searchQuery)
        }
        
        return tasks
    }
    
    /**
     * Gets upcoming tasks due within the specified number of days.
     */
    fun getUpcoming(days: Int): Flow<List<Task>> {
        return taskRepository.getUpcomingTasks()
    }
} 