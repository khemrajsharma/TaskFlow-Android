package com.ks.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ks.taskflow.data.local.converter.DateTimeConverters
import java.time.LocalDateTime

/**
 * Database entity for a task.
 */
@Entity(tableName = "tasks")
@TypeConverters(DateTimeConverters::class)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val priority: String, // HIGH, MEDIUM, LOW
    val category: String, // PERSONAL, WORK, SHOPPING, HEALTH, FINANCE, OTHER
    val dueDate: LocalDateTime?,
    val tags: List<String>,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    companion object {
        /**
         * Converts a domain model Task to a database entity TaskEntity.
         */
        fun fromDomain(task: com.ks.taskflow.domain.model.Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                completed = task.completed,
                priority = task.priority.name,
                category = task.category.name,
                tags = task.tags,
                createdAt = task.createdAt,
                modifiedAt = task.modifiedAt
            )
        }
    }

    /**
     * Converts this database entity to a domain model Task.
     */
    fun toDomain(): com.ks.taskflow.domain.model.Task {
        return com.ks.taskflow.domain.model.Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            completed = completed,
            priority = com.ks.taskflow.domain.model.Priority.valueOf(priority),
            category = com.ks.taskflow.domain.model.TaskCategory.valueOf(category),
            tags = tags,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
} 