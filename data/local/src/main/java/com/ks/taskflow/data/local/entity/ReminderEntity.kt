package com.ks.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ks.taskflow.data.local.converter.DateTimeConverters
import com.ks.taskflow.domain.model.Reminder
import java.time.LocalDateTime

/**
 * Database entity for a reminder.
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("taskId")
    ]
)
@TypeConverters(DateTimeConverters::class)
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val taskId: String,
    val title: String,
    val message: String,
    val time: LocalDateTime,
    val isEnabled: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        /**
         * Converts a domain model Reminder to a database entity ReminderEntity.
         */
        fun fromDomain(reminder: Reminder): ReminderEntity {
            return ReminderEntity(
                id = reminder.id,
                taskId = reminder.taskId,
                title = reminder.title,
                message = reminder.message,
                time = reminder.time,
                isEnabled = reminder.isEnabled,
                createdAt = reminder.createdAt
            )
        }
    }

    /**
     * Converts this database entity to a domain model Reminder.
     */
    fun toDomain(): Reminder {
        return Reminder(
            id = id,
            taskId = taskId,
            title = title,
            message = message,
            time = time,
            isEnabled = isEnabled,
            createdAt = createdAt
        )
    }
} 