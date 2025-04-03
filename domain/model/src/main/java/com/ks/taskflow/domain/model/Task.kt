package com.ks.taskflow.domain.model

import java.time.LocalDateTime

/**
 * Represents a task in the TaskFlow app.
 */
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val dueDate: LocalDateTime? = null,
    val completed: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Enum representing task priority levels.
 */
enum class Priority(val value: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2)
}

/**
 * Enum representing task categories.
 */
enum class TaskCategory(val value: String) {
    PERSONAL("personal"),
    WORK("work"),
    SHOPPING("shopping"),
    HEALTH("health"),
    FINANCE("finance"),
    OTHER("other")
} 