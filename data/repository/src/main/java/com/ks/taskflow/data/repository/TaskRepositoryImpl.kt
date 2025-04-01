package com.ks.taskflow.data.repository

import com.ks.taskflow.data.local.dao.TaskDao
import com.ks.taskflow.data.local.entity.TaskEntity
import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import com.ks.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Implementation of the TaskRepository interface.
 */
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }
    
    override fun getTaskByIdFlow(id: String): Flow<Task?> {
        return taskDao.getTaskByIdFlow(id).map { it?.toDomain() }
    }
    
    override fun getTasksByCompletionStatus(completed: Boolean): Flow<List<Task>> {
        return taskDao.getTasksByCompletionStatus(completed).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> {
        return taskDao.getTasksByPriority(priority.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> {
        return taskDao.getTasksByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getUpcomingTasks(): Flow<List<Task>> {
        val currentDate = LocalDateTime.now().format(dateTimeFormatter)
        return taskDao.getUpcomingTasks(currentDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getOverdueTasks(): Flow<List<Task>> {
        val currentDate = LocalDateTime.now().format(dateTimeFormatter)
        return taskDao.getOverdueTasks(currentDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun saveTask(task: Task) {
        val taskEntity = TaskEntity.fromDomain(task)
        taskDao.insertTask(taskEntity)
    }
    
    override suspend fun updateTaskCompletion(taskId: String, completed: Boolean) {
        val modifiedAt = LocalDateTime.now().format(dateTimeFormatter)
        taskDao.updateTaskCompletion(taskId, completed, modifiedAt)
    }
    
    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTaskById(taskId)
    }
    
    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
} 