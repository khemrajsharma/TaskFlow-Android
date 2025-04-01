package com.ks.taskflow.domain.usecase.reminder

import com.ks.taskflow.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for deleting a reminder.
 */
class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Deletes a reminder by its ID.
     *
     * @param id The ID of the reminder to delete.
     */
    suspend operator fun invoke(id: String) {
        reminderRepository.deleteReminder(id)
    }
} 