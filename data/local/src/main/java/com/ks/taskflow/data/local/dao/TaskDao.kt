package com.ks.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ks.taskflow.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for task operations.
 */
@Dao
interface TaskDao {
    
    /**
     * Gets all tasks as a Flow.
     */
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Gets all tasks with a specific completion status.
     */
    @Query("SELECT * FROM tasks WHERE completed = :completed ORDER BY dueDate ASC, createdAt DESC")
    fun getTasksByCompletionStatus(completed: Boolean): Flow<List<TaskEntity>>
    
    /**
     * Gets all tasks with a specific priority.
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC, createdAt DESC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>
    
    /**
     * Gets all tasks with a specific category.
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dueDate ASC, createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>
    
    /**
     * Gets tasks with due dates that are upcoming (today or later).
     */
    @Query("SELECT * FROM tasks WHERE dueDate >= :currentDate AND completed = 0 ORDER BY dueDate ASC")
    fun getUpcomingTasks(currentDate: String): Flow<List<TaskEntity>>
    
    /**
     * Gets tasks with due dates that are overdue (before today and not completed).
     */
    @Query("SELECT * FROM tasks WHERE dueDate < :currentDate AND completed = 0 ORDER BY dueDate ASC")
    fun getOverdueTasks(currentDate: String): Flow<List<TaskEntity>>
    
    /**
     * Gets a task by its ID.
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?
    
    /**
     * Gets a task by its ID as a Flow.
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: String): Flow<TaskEntity?>
    
    /**
     * Inserts a task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    /**
     * Inserts multiple tasks.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    /**
     * Updates a task's completion status.
     */
    @Query("UPDATE tasks SET completed = :completed, modifiedAt = :modifiedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: String, completed: Boolean, modifiedAt: String)
    
    /**
     * Updates a task.
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Deletes a task.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Deletes a task by its ID.
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
    
    /**
     * Searches for tasks that match the query string in title or description.
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dueDate ASC, createdAt DESC")
    fun searchTasks(query: String): Flow<List<TaskEntity>>
}