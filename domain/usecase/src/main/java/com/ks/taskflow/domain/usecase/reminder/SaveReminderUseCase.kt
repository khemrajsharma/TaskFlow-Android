package com.ks.taskflow.domain.usecase.reminder

import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving (creating or updating) a reminder.
 */
class SaveReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository
) {
    /**
     * Saves a reminder, generating a new ID if it's a new reminder.
     * Validates that the associated task exists.
     *
     * @param reminder The reminder to save.
     * @return The ID of the saved reminder or null if the associated task does not exist.
     */
    suspend operator fun invoke(reminder: Reminder): String? {
        // Verify the task exists
        val task = taskRepository.getTaskById(reminder.taskId) ?: return null
        
        val reminderToSave = if (reminder.id.isBlank()) {
            reminder.copy(
                id = UUID.randomUUID().toString(),
                createdAt = LocalDateTime.now()
            )
        } else {
            reminder
        }
        
        return reminderRepository.saveReminder(reminderToSave)
    }
} 