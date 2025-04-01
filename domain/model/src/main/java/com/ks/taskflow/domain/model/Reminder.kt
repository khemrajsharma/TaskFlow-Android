package com.ks.taskflow.domain.model

import java.time.LocalDateTime

/**
 * Represents a reminder for a task in the TaskFlow app.
 */
data class Reminder(
    val id: String,
    val taskId: String,
    val time: LocalDateTime,
    val title: String,
    val message: String,
    val isRepeating: Boolean = false,
    val repeatInterval: RepeatInterval? = null,
    val isEnabled: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Enum representing reminder repeat intervals.
 */
enum class RepeatInterval(val value: String) {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    CUSTOM("custom")
} 