package com.ks.taskflow.domain.repository

import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for Task operations.
 */
interface TaskRepository {
    
    /**
     * Gets all tasks.
     */
    fun getTasks(): Flow<List<Task>>
    
    /**
     * Gets a task by ID.
     */
    suspend fun getTaskById(id: String): Task?
    
    /**
     * Gets a task by ID as a Flow.
     */
    fun getTaskByIdFlow(id: String): Flow<Task?>
    
    /**
     * Gets tasks filtered by completion status.
     */
    fun getTasksByCompletionStatus(completed: Boolean): Flow<List<Task>>
    
    /**
     * Gets tasks filtered by priority.
     */
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    
    /**
     * Gets tasks filtered by category.
     */
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    
    /**
     * Gets upcoming tasks (today or later, not completed).
     */
    fun getUpcomingTasks(): Flow<List<Task>>
    
    /**
     * Gets overdue tasks (before today, not completed).
     */
    fun getOverdueTasks(): Flow<List<Task>>
    
    /**
     * Saves a task (insert if new, update if existing).
     */
    suspend fun saveTask(task: Task)
    
    /**
     * Updates a task's completion status.
     */
    suspend fun updateTaskCompletion(taskId: String, completed: Boolean)
    
    /**
     * Deletes a task.
     */
    suspend fun deleteTask(taskId: String)
    
    /**
     * Searches tasks by query string.
     */
    fun searchTasks(query: String): Flow<List<Task>>
} 